package ch.bbw.model.utils;

public class TurnHandler {
    private boolean isHost, hostsTurn;


    public TurnHandler(boolean isHost) {
        this.isHost = isHost;
        hostsTurn = true;
    }


    public void newTurn() {
        hostsTurn = !hostsTurn;
    }


    public boolean canPlay() {
        return hostsTurn && isHost || !hostsTurn && !isHost;
    }
}
