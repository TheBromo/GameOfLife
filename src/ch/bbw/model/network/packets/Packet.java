package ch.bbw.model.network.packets;

import ch.bbw.model.utils.CellCoordinates;

import java.lang.reflect.InvocationTargetException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.util.ArrayList;

/**
 * This Packet system is based on a system by Florian Walter
 */
public abstract class Packet {

    private ArrayList<InetSocketAddress> targets = new ArrayList<>();
    private InetSocketAddress sender;

    public InetSocketAddress getSender() {
        return sender;
    }

    public void setSender(InetSocketAddress sender) {
        this.sender = sender;
    }

    /**
     * writes a string into a bytebuffer
     * @param val the String
     * @param buffer the Bytebuffer
     */
    public static void writeString(String val, ByteBuffer buffer) {
        byte[] data = val.getBytes();
        buffer.putInt(data.length);
        buffer.put(data);
    }

    /**
     * reads a string out of a Bytebuffer
     * @param buffer
     * @return the String
     */
    public static String readString(ByteBuffer buffer) {
        byte[] data = new byte[buffer.getInt()];
        buffer.get(data);
        return new String(data);
    }

    /**
     * reads CellCooordinates
     * @param buffer
     * @return
     */
    public static CellCoordinates readCoor(ByteBuffer buffer) {
        return new CellCoordinates(Packet.readBoolean(buffer), buffer.getInt(), buffer.getInt());
    }

    /**
     * Writes CellCoordinates
     * @param coordinates
     * @param byteBuffer
     */
    public static void writeCoor(CellCoordinates coordinates, ByteBuffer byteBuffer) {
        Packet.writeBoolean(coordinates.isAlive(), byteBuffer);
        byteBuffer.putInt(coordinates.getX());
        byteBuffer.putInt(coordinates.getY());
    }

    /**
     * writes a boolean into a ByteBuffer
     * @param bool
     * @param buffer
     */
    public static void writeBoolean(boolean bool, ByteBuffer buffer) {
        buffer.put((byte) (bool ? 1 : 0));
    }

    /**
     * writes a boolean into a ByteBuffer
     * @param buffer
     * @return
     */
    public static Boolean readBoolean(ByteBuffer buffer) {
        return buffer.get() != 0;
    }

    /**
     * Creates a packet with the according subclass of the PacketClass with the name
     * @param className the class name
     * @return
     */
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

    /**
     * Writes the ClassName as the first Parameter and after that calls the serialize method which is different int every subClass
     * @param packet
     * @param byteBuffer
     */
    public static void compilePacket(Packet packet, ByteBuffer byteBuffer) {
        writeString(packet.getClass().getName(), byteBuffer);
        packet.serialize(byteBuffer);
    }

    /**
     * First creates the packet and then deserializes it
     * @param byteBuffer
     * @return
     */
    public static Packet decompilePacket(ByteBuffer byteBuffer) {
        Packet packet = createPacket(readString(byteBuffer));
        packet.deserialize(byteBuffer);
        return packet;
    }

    public ArrayList<InetSocketAddress> getTargets() {
        return targets;
    }

    public void clearTargets() {
        targets.clear();
    }

    public void addTarget(InetSocketAddress target) {
        targets.add(target);
    }

    /**
     * is used to add data to the bytebuffer
     * @param byteBuffer
     */
    public abstract void serialize(ByteBuffer byteBuffer);

    /**
     * is used to get data out of a bytebuffer
     * @param byteBuffer
     */
    public abstract void deserialize(ByteBuffer byteBuffer);
}
