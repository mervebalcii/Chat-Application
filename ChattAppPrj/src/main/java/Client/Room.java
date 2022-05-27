package Client;

import java.util.ArrayList;

public class Room {
    
    public String name;
    public ArrayList<String> my_user_list = new ArrayList();
     ArrayList<String> my_room_list = new ArrayList<>();
   
    
    public Room(String name, String creatorName){
        this.name = name;
        my_user_list.add(creatorName);
    }
    
    
}
