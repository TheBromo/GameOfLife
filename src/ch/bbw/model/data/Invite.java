package ch.bbw.model.data;

import javafx.scene.control.Button;
import javafx.scene.layout.HBox;

import java.net.InetAddress;

public class Invite {
    private long timeSent, deprecationTime;
    private String recallAddress, name;
    private int id;
    private HBox container;
    private Button acceptButton;
    private InetAddress target;

    public Invite(long timeSent, long deprecationTime, int id, HBox container, String recallAddress) {
        this.timeSent = timeSent;
        this.id = id;
        this.container = container;
        this.recallAddress = recallAddress;
        this.deprecationTime = deprecationTime;
    }

    public Invite(long timeSent, long deprecationTime, String recallAddress, int id, InetAddress target) {
        this.timeSent = timeSent;
        this.deprecationTime = deprecationTime;
        this.recallAddress = recallAddress;
        this.id = id;
        this.target=target;
    }


    public InetAddress getTarget() {
        return target;
    }

    public void setTarget(InetAddress target) {
        this.target = target;
    }

    public HBox getContainer() {
        return container;
    }

    public void setContainer(HBox container) {
        this.container = container;
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

    public void setId(int id) {
        this.id = id;

    }

    public long getTimeSent() {
        return timeSent;
    }

    public void setTimeSent(long timeSent) {
        this.timeSent = timeSent;
    }

    public long getDeprecationTime() {
        return deprecationTime;
    }

    public void setDeprecationTime(long deprecationTime) {
        this.deprecationTime = deprecationTime;
    }

    public String getRecallAddress() {
        return recallAddress;
    }

    public void setRecallAddress(String recallAddress) {
        this.recallAddress = recallAddress;
    }
}
