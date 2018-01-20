package ch.bbw.model.network.packets;

import java.nio.ByteBuffer;

public class AcceptPacket extends Packet {

    private int id;
    private boolean accepted;
    private String name;


    public AcceptPacket() {
    }

    public AcceptPacket(int id, boolean accepted, String name) {
        this.id = id;
        this.accepted = accepted;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public boolean hasAccepted() {
        return accepted;
    }

    @Override
    public void serialize(ByteBuffer byteBuffer) {
        byteBuffer.putInt(id);
        Packet.writeBoolean(accepted, byteBuffer);
        Packet.writeString(name, byteBuffer);
    }

    @Override
    public void deserialize(ByteBuffer byteBuffer) {
        id = byteBuffer.getInt();
        accepted = Packet.readBoolean(byteBuffer);
        name = Packet.readString(byteBuffer);
    }
}
