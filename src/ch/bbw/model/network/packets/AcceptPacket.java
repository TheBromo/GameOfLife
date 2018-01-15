package ch.bbw.model.network.packets;

import java.nio.ByteBuffer;

public class AcceptPacket extends Packet {

    private int id;
    private boolean acceptence;
    private String name;


    public AcceptPacket() {
    }

    public AcceptPacket(int id, boolean accptence, String name) {
        this.id = id;
        this.acceptence = accptence;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public boolean isAcceptence() {
        return acceptence;
    }

    @Override
    public void serialize(ByteBuffer byteBuffer) {
        byteBuffer.putInt(id);
        Packet.writeBoolean(acceptence, byteBuffer);
        Packet.writeString(name, byteBuffer);
    }

    @Override
    public void deserialize(ByteBuffer byteBuffer) {
        id = byteBuffer.getInt();
        acceptence = Packet.readBoolean(byteBuffer);
        name = Packet.readString(byteBuffer);
    }
}
