package ch.bbw.model.network.packets;

import java.nio.ByteBuffer;

public class InvitePacket extends Packet {

    String name, recallAdress;
    long deprecationTime;
    int id;

    public InvitePacket(String name, long deprecationTime, int id, String recallAdress) {
        this.name = name;
        this.deprecationTime = deprecationTime;
        this.id = id;
        this.recallAdress = recallAdress;
    }

    public InvitePacket() {
    }

    public String getName() {
        return name;
    }

    public long getDeprecationTime() {
        return deprecationTime;
    }

    public int getId() {
        return id;
    }

    public String getRecallAdress() {
        return recallAdress;
    }

    @Override
    public void serialize(ByteBuffer byteBuffer) {
        Packet.writeString(name, byteBuffer);
        byteBuffer.putLong(deprecationTime);
        byteBuffer.putInt(id);
        Packet.writeString(recallAdress, byteBuffer);
    }

    @Override
    public void deserialize(ByteBuffer byteBuffer) {
        name = readString(byteBuffer);
        deprecationTime = byteBuffer.getLong();
        id = byteBuffer.getInt();
        recallAdress = Packet.readString(byteBuffer);
    }
}
