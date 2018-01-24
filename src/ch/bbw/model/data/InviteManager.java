package ch.bbw.model.data;

import javafx.scene.control.Button;

import java.util.ArrayList;

/**
 * this class is used to manage this invites
 */
public class InviteManager {
    private ArrayList<Invite> sentInvites = new ArrayList<>();
    private ArrayList<Invite> receivedInvites = new ArrayList<>();

    public void addSentInvite(Invite invite) {
        sentInvites.add(invite);
    }

    public void addReceivedInvite(Invite invite) {
        receivedInvites.add(invite);
    }

    /**
     * looks if an invite exists
     * @param invite
     * @return if the invite exists
     */
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

    /**
     * gives back an invite with the according button
     * @param button the button
     * @return the according invite
     */
    public Invite getInviteByButton(Button button){
        for (Invite invite:receivedInvites){
            if (invite.getAcceptButton().equals(button)){
                return invite;
            }
        }
        return null;
    }

    /**
     * gets an invite with it's id
     * @param id
     * @return
     */
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
