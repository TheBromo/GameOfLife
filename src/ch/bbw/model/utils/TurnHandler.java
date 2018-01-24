package ch.bbw.model.utils;

/**
 * manages who's turn it is
 */
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
