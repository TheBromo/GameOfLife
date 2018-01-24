package ch.bbw.model.data;

import javafx.scene.control.Button;

import java.util.ArrayList;

/**
 * This class manages all found users
 */
public class UserManager {
    private ArrayList<User> users = new ArrayList<>();

    public void addUser(User user){
        users.add(user);
    }

    public ArrayList<User> getUsers() {
        return users;
    }

    public User getUserByButton(Button button){
        //gives back an User with the according button
        for (User user : users) {
            if (user.getButton().equals(button)) {
                return user;
            }
        }
        return null;
    }
}
