package ch.bbw.model.utils;

public class TurnHandler {
    private boolean isHost, hostsTurn;


    public TurnHandler(boolean isHost) {
        this.isHost = isHost;
    }


    public void newTurn() {
        hostsTurn = !hostsTurn;
    }


    public boolean canPlay() {

        if (hostsTurn && isHost) {
            return true;
        } else return !hostsTurn && !isHost;
    }
}
