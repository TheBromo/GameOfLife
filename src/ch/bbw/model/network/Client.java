package ch.bbw.model.network;

import ch.bbw.model.network.packets.Packet;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Observable;
import java.util.Set;
@SuppressWarnings("deprecation")
public class Client extends Observable implements Runnable {


    public static final int port = 7563;
    private InetSocketAddress serverAddress;
    private ArrayList<Packet> queue;
    private boolean running;

    public Client(InetSocketAddress serverAddress) {
        this.serverAddress = serverAddress;
        queue = new ArrayList<>();
        running = true;
    }

    /**
     * adds a packet that needs to be sent
     * @param packet
     */
    public void queuePacket(Packet packet) {
        queue.add(packet);
    }


    public void setRunning(boolean running) {
        this.running = running;
    }

    /**
     *
     */
    @Override
    public void run() {
        try {
            //preparing the client
            SocketChannel channel;
            channel = SocketChannel.open();
            channel.configureBlocking(false);
            //connects to the server
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
                            //connected to the server
                            channel.finishConnect();
                            System.out.println("Client: Connected");


                        } else if (key.isReadable()) {
                            //gets channel
                            SocketChannel sChannel = (SocketChannel) key.channel();
                            //reads buffer
                            readBuffer.position(0).limit(readBuffer.capacity());
                            sChannel.read(readBuffer);
                            readBuffer.flip();
                            //decompiles the packet
                            Packet packet = Packet.decompilePacket(readBuffer);
                            setChanged();
                            notifyObservers(packet);
                            System.out.println("Client: Packet received");
                        }
                    }
                    keys.clear();
                }
                if (!queue.isEmpty()) {
                    //Sends all packets
                    Iterator<Packet> packetIterator = queue.iterator();
                    while (packetIterator.hasNext()) {
                        System.out.println("Client: Sending Packet...");
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
