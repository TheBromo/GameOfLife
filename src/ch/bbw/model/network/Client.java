package ch.bbw.model.network;

import ch.bbw.model.network.packets.Packet;
import ch.bbw.model.network.packets.PacketHandler;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;

public class Client implements Runnable {


    public static final int port = 6666;
    private InetSocketAddress serverAddress;
    private ArrayList<Packet> queue;
    private SocketChannel channel;

    private PacketHandler packetHandler;
    private boolean running;

    public Client(InetSocketAddress serverAddress, PacketHandler packetHandler) {
        this.serverAddress = serverAddress;

        queue = new ArrayList<>();
        this.packetHandler = packetHandler;
        running = true;
    }

    public void queuePacket(Packet packet) {
        queue.add(packet);
    }

    public SocketAddress getServerAddress() {
        return serverAddress;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }

    @Override
    public void run() {
        try {
            channel = SocketChannel.open();
            channel.configureBlocking(false);
            channel.connect(serverAddress);

            System.out.println("Connecting to " + channel.getRemoteAddress().toString());

            Selector selector = Selector.open();
            channel.register(selector, SelectionKey.OP_CONNECT | SelectionKey.OP_READ | SelectionKey.OP_WRITE);

            ByteBuffer readBuffer = ByteBuffer.allocate(2048);
            ByteBuffer writeBuffer = ByteBuffer.allocate(2048);

            while (running) {
                if (selector.selectNow() > 0) {
                    Set<SelectionKey> keys = selector.selectedKeys();

                    for (SelectionKey key : keys) {
                        if (key.isConnectable()) {
                            channel.finishConnect();
                            System.out.println("Connected");
                        } else if (key.isReadable()) {
                            SocketChannel sChannel = (SocketChannel) key.channel();

                            readBuffer.position(0).limit(readBuffer.capacity());
                            sChannel.read(readBuffer);
                            readBuffer.flip();

                            Packet packet = Packet.decompilePacket(readBuffer);
                            packetHandler.handlePacket(packet);
                            System.out.println("Packet received");
                        }
                    }
                    keys.clear();
                }
                if (!queue.isEmpty()) {

                    Iterator<Packet> packetIterator = queue.iterator();
                    while (packetIterator.hasNext()) {
                        System.out.println("Sending Packet...");
                        Packet packet = packetIterator.next();
                        writeBuffer.position(0).limit(writeBuffer.capacity());
                        Packet.compilePacket(packet, writeBuffer);
                        writeBuffer.flip();

                        channel.write(writeBuffer);

                        packetIterator.remove();
                    }
                }
            }
            channel.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
