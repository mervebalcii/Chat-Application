/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Message;

/**
 *
 * @author MERVE
 */
public class Message implements java.io.Serializable {

   

    public static enum Message_Type {
        None, Name, Leaving , Text , end, Start, update_users, prive_msg,room_name,room_join,room_list, list_,File_t,File_Notify,second_text
    }
    public byte[] file_cont;
    public Message_Type type;
    public Object content;

    public Message(Message_Type t) {
        this.type = t;
    }

}