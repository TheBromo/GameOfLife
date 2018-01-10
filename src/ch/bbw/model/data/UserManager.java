package ch.bbw.model.data;

import java.util.ArrayList;

public class UserManager {
    private ArrayList<User> users = new ArrayList<>();

    public void addUser(User user){
        users.add(user);
    }

    public ArrayList<User> getUsers() {
        return users;
    }
}
