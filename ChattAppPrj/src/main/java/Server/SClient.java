/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Server;

import Client.Room;
import Message.Message;
//import chatmsg.prive_msg;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import static java.lang.Thread.sleep;
import java.net.Socket;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * 
 */
public class SClient {

    int id;
    public String name = "NoName";
    Socket soket;
    ObjectOutputStream sOutput;
    ObjectInputStream sInput;
    //clientten gelenleri dinleme threadi
    Listen listenThread;
    //cilent eşleştirme thredi
   
    
    
   

    public SClient(Socket gelenSoket, int id) {
        this.soket = gelenSoket;
        this.id = id;
        try {
            this.sOutput = new ObjectOutputStream(this.soket.getOutputStream());
            this.sInput = new ObjectInputStream(this.soket.getInputStream());
        } catch (IOException ex) {
            Logger.getLogger(SClient.class.getName()).log(Level.SEVERE, null, ex);
        }
        //thread nesneleri
        this.listenThread = new Listen(this);
       

    }

    
    public void Send(Message message) {
        try {
            this.sOutput.writeObject(message);
        } catch (IOException ex) {
            Logger.getLogger(SClient.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    //client dinleme threadi
    //her clientin ayrı bir dinleme thredi var
    class Listen extends Thread {

        SClient TheClient;

        //thread nesne alması için yapıcı metod
        Listen(SClient TheClient) {
            this.TheClient = TheClient;
        }

        public void run() {
            //client bağlı olduğu sürece dönsün
            while (TheClient.soket.isConnected()) {
                try {
                    //mesajı bekleyen kod satırı
                    Message received = (Message) (TheClient.sInput.readObject());
                    //mesaj gelirse bu satıra geçer
                    //mesaj tipine göre işlemlere ayır
                    switch (received.type) {
                        case Name:
                            // User ilk girdigindeki mesaj
                            // User adı kontrollü bir şekilde belirlenir.
                            String uname = received.content.toString();
                            TheClient.name = uname;
                            System.out.println(TheClient.name + "is connected.");
                            Server.send_update_users();
                          
                          
                            break;     
                        case Leaving:
                            // Bir user ayrılırken gönderdiği mesaj
                            // Bu mesajla diğer kullanıcıların listesinden çıkartılır.
                            Server.Clients.remove(TheClient);
                            Server.send_update_users();
                            TheClient.listenThread.stop();
                            TheClient.soket.close();
                            break;
                        case Text:
                            //gelen global mesaj metni tüm kullanıcılara gider
                            Server.send_all(received);
                            break;
                        case prive_msg:
                            Server.send_prive_msg(received);
                            break;
                          case room_name:
                            System.out.println("gelen room name msg: " + received.content.toString());
                            Room newRoom = new Room(received.content.toString(), TheClient.name);
                            Server.rooms.add(newRoom);
                            Message roomMSG = new Message(Message.Message_Type.room_name);
                            roomMSG.content = newRoom.name;
                            Server.Send(this.TheClient, roomMSG);
                            break;

                        case list_:
                            ArrayList<String> usernames = new ArrayList<String>();
                            for (SClient item : Server.Clients) {
                                usernames.add(item.name);
                            }
                            Message mesaj = new Message(Message.Message_Type.list_);
                            mesaj.content = usernames;
                            Server.Send(this.TheClient, mesaj);
                            break;

                        case room_list:
                            ArrayList<String> roomNames = new ArrayList<String>();
                            for (Room item : Server.rooms) {
                                roomNames.add(item.name);
                            }
                            Message roomListMsg = new Message(Message.Message_Type.room_list);
                            roomListMsg.content = roomNames;
                            Server.Send(this.TheClient, roomListMsg);
                            break;

                        case room_join:
                          
                            String tempRoomName = " ";
                            for (Room item : Server.rooms) {
                                if (item.name.equals(received.content)) {
                                    tempRoomName = item.name;
                                    item.my_user_list.add(this.TheClient.name);
                                    break;
                                }
                            }
                            Message roomNameMSG = new Message(Message.Message_Type.room_join);
                            roomNameMSG.content = tempRoomName;
                            Server.Send(this.TheClient, roomNameMSG);
                            break;

                             case File_t:
                            // msg.content == file name
                          
                            Server.Send(received);
                            break;
                        case File_Notify:
                            // msg.content == filename + shared..
                            Server.Send(received);
                            break;
                            
                          case second_text:
                            // msg.content == sended message to room 
                            Message chatMSG = new Message(Message.Message_Type.second_text);
                            chatMSG.content = this.TheClient.name.toUpperCase() + ": " + received.content;
                            Server.Send(chatMSG);                                                      
                            break;

                            
                        case end:
                            break;

                    }

                } catch (EOFException ex) {
                    System.out.println("hata");
                } catch (IOException ex) {
                    Logger.getLogger(SClient.class.getName()).log(Level.SEVERE, null, ex);
                    //client bağlantısı koparsa listeden sil
                    Server.Clients.remove(TheClient);

                } catch (ClassNotFoundException ex) {
                    Logger.getLogger(SClient.class.getName()).log(Level.SEVERE, null, ex);
                    //client bağlantısı koparsa listeden sil
                    Server.Clients.remove(TheClient);
                }
            }

        }
    }



}
