package ch.bbw.model.network.packets;

import java.nio.ByteBuffer;

public class SeedPacket extends Packet {
    private long seed;

    public SeedPacket() {
    }

    public SeedPacket(long seed) {
        this.seed = seed;
    }

    public long getSeed() {
        return seed;
    }

    @Override
    public void serialize(ByteBuffer byteBuffer) {
        byteBuffer.putLong(seed);
    }

    @Override
    public void deserialize(ByteBuffer byteBuffer) {
        seed = byteBuffer.getLong();
    }
}
