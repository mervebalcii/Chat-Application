/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Server;

import Client.Room;
import Message.Message;
import Client.Room;
import Message.FirstPage;
import Message.PrivateMsg;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.concurrent.Semaphore;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultListModel;

/**
 *
 * 
 */
//client gelişini dinleme threadi
class ServerThread extends Thread {

    public void run() {
        //server kapanana kadar dinle
        while (!Server.serverSocket.isClosed()) {
            try {
                Server.Display("Client Bekleniyor...");
                // clienti bekleyen satır
                //bir client gelene kadar bekler
                Socket clientSocket = Server.serverSocket.accept();
                //client gelirse bu satıra geçer
                Server.Display("Client Geldi...");
                //gelen client soketinden bir sclient nesnesi oluştur
                //bir adet id de kendimiz verdik
                SClient nclient = new SClient(clientSocket, Server.IdClient);

                Server.IdClient++;
                //clienti listeye ekle.
                Server.Clients.add(nclient);
                //client mesaj dinlemesini başlat
                nclient.listenThread.start();

            } catch (IOException ex) {
                Logger.getLogger(ServerThread.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}

public class Server {

    //server soketi eklemeliyiz
    public static ServerSocket serverSocket;
    public static int IdClient = 0;
    // Serverın dileyeceği port
    public static int port = 0;
    //Serverı sürekli dinlemede tutacak thread nesnesi
    public static ServerThread runThread;
   
    // servere bagli kullanicilar
    public static ArrayList<SClient> Clients = new ArrayList<>();
    // olusturulan odalar
   
    
   public static ArrayList<Room> rooms = new ArrayList();
    public static ArrayList<ChatRoom> chatRooms = new ArrayList();
    
    

    // başlaşmak için sadece port numarası veriyoruz
    public static void Start(int openport) {
        try {
            Server.port = openport;
            Server.serverSocket = new ServerSocket(Server.port);

            Server.runThread = new ServerThread();
            Server.runThread.start();

        } catch (IOException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void Display(String msg) {

        System.out.println(msg);

    }

    public static void send_update_users() {
        // Yeni user katıldığında, tüm userlara gönderilecek mesaj.
        DefaultListModel model = new DefaultListModel();
        for (SClient c : Clients) {
            model.addElement(c.name);
        }
        // Bu mesaj ile kullancıılardaki user listesi güncellenir
        Message msg = new Message(Message.Message_Type.update_users);
        msg.content = model;
        for (SClient c : Clients) {
            Server.Send(c, msg);
        }

    }



    public static void send_all(Message msg) {
        //mesajı main sayfadaki chat ile herkese gönderme
      
        for (SClient c : Clients) {
            Server.Send(c, msg);
        }
    }
    
    /****************************/
    
     public static void Send(Message msg) {
        for (SClient sclient : Clients) {
            try {
                sclient.sOutput.writeObject(msg);
            } catch (IOException ex) {
                Logger.getLogger(SClient.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public static void send_prive_msg(Message msg) {
        // Server'a prive mesajı iletir
        PrivateMsg pmsg = (PrivateMsg) msg.content;

        for (SClient c : Clients) {
            if (c.name.equals(pmsg.getTarget())) {
                Server.Send(c, msg);
                
                break;
            }

        }

    }



    public static void Send(SClient cl, Message msg) {

        try {
            cl.sOutput.writeObject(msg);
        } catch (IOException ex) {
            Logger.getLogger(SClient.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

}
