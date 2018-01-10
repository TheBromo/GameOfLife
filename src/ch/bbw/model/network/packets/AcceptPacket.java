package ch.bbw.model.network.packets;

import java.nio.ByteBuffer;

public class AcceptPacket extends Packet{

   private int id;
   private boolean acceptence;

    public AcceptPacket(){}

    public int getId() {
        return id;
    }

    public boolean isAcceptence() {
        return acceptence;
    }

    public AcceptPacket(int id, boolean accptence) {
        this.id = id;
        this.acceptence = accptence;

    }

    @Override
    public void serialize(ByteBuffer byteBuffer) {
        byteBuffer.putInt(id);
        Packet.writeBoolean(acceptence,byteBuffer);
    }

    @Override
    public void deserialize(ByteBuffer byteBuffer) {
        id=byteBuffer.getInt();
        acceptence=Packet.readBoolean(byteBuffer);
    }
}
