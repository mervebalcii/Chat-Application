/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Client;

import Message.Message;
import Message.Room;
//import chatmsg.prive_msg;
import Server.SClient;
import Message.FirstPage;
import Message.MainPage;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import static Client.Client.sInput;
import Message.FirstPage;
import Message.MainPage;
import Message.PrivateMsg;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import javax.swing.DefaultListModel;
import javax.swing.JOptionPane;
import static Message.Message.Message_Type.prive_msg;

/**
 *
 * 
 */
// serverdan gelecek mesajları dinleyen thread

class Listen extends Thread {
    
    

    public void run() {
        //soket bağlı olduğu sürece dön
        while (Client.socket.isConnected()) {
            try {
                //mesaj gelmesini dinyelen komut
                Message received = (Message) (sInput.readObject());
                
                switch (received.type) {
                    case Name:
                        break;
                   
                    case Text:
                        // herkes ile birlikte yapılan chat kısmı
                        FirstPage.mainP.chat_screen.setText(FirstPage.mainP.chat_screen.getText() + "\n" + received.content.toString());                  
                        break;
                    case update_users:
                        // user listesini güncelleme
                        FirstPage.mainP.online_users((DefaultListModel) received.content);
                        break;                        
                      case room_name:
                        // msg.content == newRoom.name
                        FirstPage.mainP.rooms.lbl_roomName.setText(received.content.toString().toUpperCase());
               
                        break;
                        
                         case room_list:
                        FirstPage.mainP.dlm2.removeAllElements();
                        ArrayList<String> receivedRooms = new ArrayList();
                        receivedRooms = (ArrayList<String>) received.content;
                        for (String item : receivedRooms) {
                            FirstPage.mainP.dlm2.addElement(item);
                        }
                        break;
                    case room_join:
                       
                        FirstPage.mainP.rooms.lbl_roomName.setText(received.content.toString().toUpperCase());
                         FirstPage.mainP.rooms.setVisible(true);
                        break;
                    case prive_msg:
               
                        FirstPage.mainP.priv_rec_msg((PrivateMsg)received.content);
                        break;
                   
                    case File_t:
                       
                        String homePath = System.getProperty("user.home");
                        File receivedFile = new File(homePath + "/Desktop/" + received.content);
                        OutputStream os = new FileOutputStream(receivedFile);
                        byte content[] = (byte[])received.file_cont;
                        os.write(content);
                        break;
                    case File_Notify:
                        FirstPage.mainP.chat_screen.append(received.content.toString() + "\n");
           
                        break;
                        
                         case second_text:
                            FirstPage.mainP.rooms.jTextArea1.append(received.content.toString()+"\n");
                    
                        break;
                         case Leaving:
                        break;
                    case end:
                        break;

                }

            } catch (IOException ex) {

                Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
                Client.Stop();
                break;
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
                Client.Stop();
                break;
            }
        }

    }
}

public class Client {
    
  

    //her clientın bir soketi olmalı
    public static Socket socket;

    
    public static ObjectInputStream sInput;
   
    public static ObjectOutputStream sOutput;
    //serverı dinleme thredi 
    public static Listen listenMe;

    public static void Start(String ip, int port) {
        try {
            // Client Soket nesnesi
            Client.socket = new Socket(ip, port);
            Client.Display("Servera bağlandı");
            
            Client.sInput = new ObjectInputStream(Client.socket.getInputStream());
          
            Client.sOutput = new ObjectOutputStream(Client.socket.getOutputStream());
            Client.listenMe = new Listen();
            Client.listenMe.start();

            //ilk mesaj
            Message msg = new Message(Message.Message_Type.Name);
           
       //     fp.txt_username.getText();
             msg.content =   FirstPage.fp.txt_username.getText();
            Client.Send(msg);
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    //client durdurma fonksiyonu
    public static void Stop() {
        try {
            if (Client.socket != null) {
                Client.listenMe.stop();
                Client.socket.close();
                Client.sOutput.flush();
                Client.sOutput.close();

                Client.sInput.close();
            }
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public static void Display(String msg) {

        System.out.println(msg);

    }

    //mesaj gönderme fonksiyonu
    public static void Send(Message msg) {
        try {
            Client.sOutput.writeObject(msg);
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

}
