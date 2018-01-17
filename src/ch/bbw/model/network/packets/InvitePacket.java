package ch.bbw.model.network.packets;

import java.nio.ByteBuffer;

public class InvitePacket extends Packet {

    private String name, recallAdress;
    private long deprecationTime;
    private int id;
    private int fieldSize;

    public InvitePacket(String name, long deprecationTime, int id,int fieldSize, String recallAdress) {
        this.name = name;
        this.deprecationTime = deprecationTime;
        this.id = id;
        this.recallAdress = recallAdress;
        this.fieldSize=fieldSize;
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

    public int getFieldSize() {
        return fieldSize;
    }

    @Override
    public void serialize(ByteBuffer byteBuffer) {
        Packet.writeString(name, byteBuffer);
        byteBuffer.putLong(deprecationTime);
        byteBuffer.putInt(id);
        Packet.writeString(recallAdress, byteBuffer);
        byteBuffer.putInt(fieldSize);
    }

    @Override
    public void deserialize(ByteBuffer byteBuffer) {
        name = readString(byteBuffer);
        deprecationTime = byteBuffer.getLong();
        id = byteBuffer.getInt();
        recallAdress = Packet.readString(byteBuffer);
        fieldSize=byteBuffer.getInt();

    }
}
