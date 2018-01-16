package ch.bbw.model.network.packets;

import ch.bbw.model.utils.CellCoordinates;

import java.nio.ByteBuffer;
import java.util.ArrayList;

public class ActionPacket extends Packet {

    private boolean hasParents;
    private CellCoordinates mainCell;
    private ArrayList<CellCoordinates> parents;

    public ActionPacket(boolean hasParents, CellCoordinates mainCell, ArrayList<CellCoordinates> parents) {
        this.hasParents = hasParents;
        this.mainCell = mainCell;
        this.parents = parents;
    }

    public ActionPacket() {
    }


    public boolean hasParents() {
        return hasParents;
    }

    public CellCoordinates getMainCell() {
        return mainCell;
    }

    public ArrayList<CellCoordinates> getParents() {
        return parents;
    }

    @Override
    public void serialize(ByteBuffer byteBuffer) {
        Packet.writeBoolean(hasParents, byteBuffer);
        Packet.writeCoor(mainCell, byteBuffer);
        if (hasParents) {
            Packet.writeCoor(parents.get(0), byteBuffer);
            Packet.writeCoor(parents.get(1), byteBuffer);
        }

    }

    @Override
    public void deserialize(ByteBuffer byteBuffer) {
        hasParents = Packet.readBoolean(byteBuffer);
        mainCell = Packet.readCoor(byteBuffer);
        if (hasParents) {
            parents.add(Packet.readCoor(byteBuffer));
            parents.add(Packet.readCoor(byteBuffer));
        }

    }
}
