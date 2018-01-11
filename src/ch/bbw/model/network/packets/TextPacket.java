package ch.bbw.model.network.packets;

import java.nio.ByteBuffer;

public class TextPacket extends Packet {
    String text;

    public TextPacket() {

    }

    public TextPacket(String text) {
        this.text = text;
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
