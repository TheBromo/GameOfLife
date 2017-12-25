package ch.bbw.model.data;

import javafx.scene.control.Button;

import java.net.InetAddress;

public class User {
    private InetAddress address;
    private String name;
    private Button button;

    public User(InetAddress address, Button button) {
        this.address = address;
        this.button = button;
    }

    public InetAddress getAddress() {
        return address;
    }

    public void setAddress(InetAddress address) {
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Button getButton() {
        return button;
    }

    public void setButton(Button button) {
        this.button = button;
    }
}
