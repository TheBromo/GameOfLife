package ch.bbw.model.data;

import ch.bbw.model.network.server.Server;
import javafx.scene.control.Button;

import java.net.SocketAddress;

public class User {
    private SocketAddress address;
    private String name;
    private Button button;

    public User(SocketAddress address, Button button) {
        this.address = address;
        this.button = button;
    }

    public SocketAddress getAddress() {
        return address;
    }

    public void setAddress(SocketAddress address) {
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
