package ch.bbw.model.network.packets;

import java.nio.ByteBuffer;

public class NamePacket extends Packet {
    String text;

    public NamePacket() {

    }

    public NamePacket(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    @Override
    public void serialize(ByteBuffer byteBuffer) {
        Packet.writeString(text, byteBuffer);
    }

    @Override
    public void deserialize(ByteBuffer byteBuffer) {
        text = Packet.readString(byteBuffer);
    }
}
