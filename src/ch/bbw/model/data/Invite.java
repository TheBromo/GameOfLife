package ch.bbw.model.data;

import javafx.scene.layout.HBox;

public class Invite {
    private long timeSent,deprecationTime;
    private String target;
    private int id;
    private HBox container;

    public Invite(long timeSent, int id, HBox container) {
        this.timeSent = timeSent;
        this.id = id;
        this.container = container;
    }

    public Invite(long timeSent, long deprecationTime, String target, int id) {
        this.timeSent = timeSent;
        this.deprecationTime = deprecationTime;
        this.target = target;
        this.id = id;
    }

    public void setContainer(HBox container) {
        this.container = container;
    }

    public HBox getContainer() {
        return container;
    }

    public void setId(int id) {
        this.id = id;

    }

    public int getId() {
        return id;
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

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }
}
