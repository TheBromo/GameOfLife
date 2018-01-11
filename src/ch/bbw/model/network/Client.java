package ch.bbw.model.network;

import ch.bbw.model.network.packets.Packet;
import ch.bbw.model.network.packets.PacketHandler;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Iterator;

public class Client implements Runnable {

    private SocketAddress serverAddress;
    private ArrayList<Packet> queue;

    private PacketHandler packetHandler;
    private boolean running;

    public Client(SocketAddress serverAddress) {
        this.serverAddress = serverAddress;
        queue = new ArrayList<>();
        packetHandler = new PacketHandler();
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
            SocketChannel channel = SocketChannel.open();
            channel.configureBlocking(false);

            channel.connect(serverAddress);

            Selector selector = Selector.open();
            channel.register(selector, SelectionKey.OP_CONNECT | SelectionKey.OP_READ);

            ByteBuffer readBuffer = ByteBuffer.allocate(1024);
            ByteBuffer writeBuffer = ByteBuffer.allocate(1024);

            while (running) {
                if (selector.selectNow() > 0) {
                    Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();

                    while (iterator.hasNext()) {
                        SelectionKey key = iterator.next();

                        if (key.isConnectable()){
                            channel.finishConnect();
                        }else if (key.isReadable()){
                            SocketChannel sChannel  =(SocketChannel)key.channel();

                            readBuffer.position(0);
                            readBuffer.limit(readBuffer.capacity());

                            sChannel.read(readBuffer);
                            readBuffer.flip();

                            Packet packet = Packet.decompilePacket(readBuffer);
                            packetHandler.handlePacket(packet);
                        }
                        iterator.remove();
                    }
                }
                if (!queue.isEmpty()) {

                    Iterator<Packet> packetIterator = queue.iterator();
                    while (packetIterator.hasNext()) {
                        Packet packet = packetIterator.next();
                        writeBuffer.position(0).limit(writeBuffer.capacity());

                        Packet.compilePacket(packet, writeBuffer);
                        writeBuffer.flip();

                        channel.write(writeBuffer);

                        packetIterator.remove();
                    }
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
