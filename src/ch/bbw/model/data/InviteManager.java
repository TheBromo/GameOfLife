package ch.bbw.model.data;

import javafx.scene.layout.HBox;

import java.util.ArrayList;

public class InviteManager {
    private ArrayList<Invite> sentInvites = new ArrayList<>();
    private ArrayList<Invite> receivedInvites = new ArrayList<>();

    public void addSentInvite(Invite invite) {
        sentInvites.add(invite);
    }

    public void addReceivedInvite(Invite invite) {
        receivedInvites.add(invite);
    }

    public ArrayList<Invite> getReceivedInvites() {
        return receivedInvites;
    }

    public ArrayList<Invite> getSentInvites() {
        return sentInvites;
    }

    public Invite getInviteByContainer(HBox container) {
        for (Invite invite : sentInvites) {
            if (container.equals(invite.getContainer())) return invite;
        }
        for (Invite invite : receivedInvites) {
            if (container.equals(invite.getContainer())) return invite;
        }
        return null;
    }

    public boolean inviteExists(Invite invite) {
        for (Invite secInvite : sentInvites) {
            if (invite.getTimeSent() == secInvite.getTimeSent()) {
                return true;
            }
        }
        for (Invite secInvite : receivedInvites) {
            if (invite.getId() == secInvite.getId()) {
                return true;
            }
        }
        return false;
    }


}
