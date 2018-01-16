package ch.bbw.model.data;

import javafx.scene.control.Button;

import java.net.InetAddress;

public class Invite {
    private long timeSent, deprecationTime;
    private String recallAddress, name;
    private int id;
    private Button acceptButton;

    public Invite(long timeSent, long deprecationTime, int id, String recallAddress) {
        this.timeSent = timeSent;
        this.id = id;
        this.recallAddress = recallAddress;
        this.deprecationTime = deprecationTime;
    }

    public Invite(long timeSent, long deprecationTime, String recallAddress, int id, InetAddress target) {
        this.timeSent = timeSent;
        this.deprecationTime = deprecationTime;
        this.recallAddress = recallAddress;
        this.id = id;
    }

    public Button getAcceptButton() {
        return acceptButton;
    }

    public void setAcceptButton(Button acceptButton) {
        this.acceptButton = acceptButton;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public long getTimeSent() {
        return timeSent;
    }

    public long getDeprecationTime() {
        return deprecationTime;
    }

    public String getRecallAddress() {
        return recallAddress;
    }

}
