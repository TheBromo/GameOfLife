package ch.bbw.model.network.packets;

import java.nio.ByteBuffer;

public class InvitePacket extends Packet {
    String name;
    long inviteDuration;
    int id;

    public InvitePacket(String name, long inviteDuration, int id) {
        this.name = name;
        this.inviteDuration = inviteDuration;
        this.id = id;
    }

    public InvitePacket() {
    }


    @Override
    public void serialize(ByteBuffer byteBuffer) {
        Packet.writeString(name, byteBuffer);
        byteBuffer.putLong(inviteDuration);
        byteBuffer.putInt(id);
    }

    @Override
    public void deserialize(ByteBuffer byteBuffer) {
        name = readString(byteBuffer);
        inviteDuration = byteBuffer.getLong();
        id = byteBuffer.getInt();
    }
}
