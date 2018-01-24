package ch.bbw.model.data;

import javafx.scene.control.Button;

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


    public boolean inviteExists(Invite invite) {
        //checks if an invite exists
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


    public Invite getInviteByButton(Button button){
        //gives back an invite with the according button
        for (Invite invite:receivedInvites){
            if (invite.getAcceptButton().equals(button)){
                return invite;
            }
        }
        return null;
    }

    public Invite getInviteById(int id){
        //gives back an invite with the according id
        for (Invite invite:sentInvites){
            if (invite.getId()==id){
                return invite;
            }
        }
        for (Invite invite:receivedInvites){
            if (invite.getId()==id){
                return invite;
            }
        }
        return null;
    }



}
