package ch.bbw.model.network.packets;

import java.lang.reflect.InvocationTargetException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.util.ArrayList;

public abstract class Packet {

    private ArrayList<SocketAddress> targets = new ArrayList<>();

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

    public static Packet createPacket(String className) {
        try {
            Class<Packet> packetClass = Class.forName(className).asSubclass((Class) Packet.class);
            return packetClass.getDeclaredConstructor().newInstance();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static ByteBuffer compilePacket(Packet packet, ByteBuffer byteBuffer) {
        writeString(packet.getClass().getName(), byteBuffer);
        packet.serialize(byteBuffer);
        return byteBuffer;
    }

    public static Packet decompilePacket(ByteBuffer byteBuffer) {
        Packet packet = createPacket(readString(byteBuffer));
        packet.deserialize(byteBuffer);
        return packet;
    }

    public abstract void serialize(ByteBuffer byteBuffer);

    public abstract void deserialize(ByteBuffer byteBuffer);
}
