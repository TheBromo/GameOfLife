package ch.bbw.model.network.packets;

import java.nio.ByteBuffer;

public class InvitePacket extends Packet {

    String name;
    long startTime;
    int id;

    public InvitePacket(String name, long startTime, int id) {
        this.name = name;
        this.startTime = startTime;
        this.id = id;
    }

    public InvitePacket() {
    }

    public String getName() {
        return name;
    }

    public long getStartTime() {
        return startTime;
    }

    public int getId() {
        return id;
    }

    @Override
    public void serialize(ByteBuffer byteBuffer) {
        Packet.writeString(name, byteBuffer);
        byteBuffer.putLong(startTime);
        byteBuffer.putInt(id);
    }

    @Override
    public void deserialize(ByteBuffer byteBuffer) {
        name = readString(byteBuffer);
        startTime = byteBuffer.getLong();
        id = byteBuffer.getInt();
    }
}
