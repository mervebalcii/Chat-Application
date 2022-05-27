/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Server;

import java.util.ArrayList;
import javax.swing.DefaultListModel;

/**
 *
 * @author Furu
 */
public class ChatRoom {
    String name;
 
    DefaultListModel<String> msg_list_model = new DefaultListModel<>();

    public ChatRoom(String name) {
        this.name = name;
     
    }
}
