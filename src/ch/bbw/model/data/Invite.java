package ch.bbw.model.data;

import javafx.scene.control.Button;

import java.net.InetAddress;

/**
 * This class is used to save a sent or received invite
 */
public class Invite {
    private long timeSent, deprecationTime;
    private String recallAddress, name;
    private int id,fieldSize;
    private Button acceptButton;

    public Invite(long timeSent, long deprecationTime, int id,int fieldSize, String recallAddress) {
        this.timeSent = timeSent;
        this.id = id;
        this.recallAddress = recallAddress;
        this.deprecationTime = deprecationTime;
        this.fieldSize=fieldSize;
    }

    public Invite(long timeSent, long deprecationTime, String recallAddress, int id,int fieldSize) {
        this.timeSent = timeSent;
        this.deprecationTime = deprecationTime;
        this.recallAddress = recallAddress;
        this.id = id;
        this.fieldSize=fieldSize;
    }

    public int getFieldSize() {
        return fieldSize;
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
