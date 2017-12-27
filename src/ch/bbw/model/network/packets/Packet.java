package ch.bbw.model.network.packets;

import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.util.ArrayList;

public abstract class Packet {

    private ArrayList<SocketAddress>targets =new ArrayList<>();

    public abstract void serialize(ByteBuffer byteBuffer);
    public abstract void deserialize(ByteBuffer byteBuffer);



    public static Packet createPacket(String className){
        Class


        return null;
    }
    public static void writeString(String val, ByteBuffer buffer) {
        byte[] data = val.getBytes();
        buffer.putInt(data.length);
        buffer.put(data);
    }


    public static String readString(ByteBuffer buffer) {
        byte[] data = new byte[buffer.getInt()];
        buffer.get(data);
        return new String(data);
    }


}
