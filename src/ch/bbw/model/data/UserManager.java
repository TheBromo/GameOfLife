package ch.bbw.model.data;

import javafx.scene.control.Button;

import java.util.ArrayList;

public class UserManager {
    private ArrayList<User> users = new ArrayList<>();

    public void addUser(User user){
        users.add(user);
    }

    public ArrayList<User> getUsers() {
        return users;
    }

    public User getUserByButton(Button button){
        for (User user : users) {
            if (user.getButton().equals(button)) {
                return user;
            }
        }
        return null;
    }
}
