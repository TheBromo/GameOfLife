package ch.bbw.model.network;


import ch.bbw.model.network.packets.Packet;
import ch.bbw.model.network.packets.PacketHandler;
import ch.bbw.model.network.packets.TextPacket;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

public class Server implements Runnable {

    public static final int port = 6666;
    private boolean running;

    private PacketHandler packetHandler;
    private ServerSocketChannel channel;
    private ArrayList<Packet> queue;

    private HashMap<InetSocketAddress, SocketChannel> clients;

    private InetAddress ip;

    public Server() {


        running = true;

        packetHandler = new PacketHandler();

        clients = new HashMap<>();
        queue = new ArrayList<>();


    }

    public void setIp(InetAddress ip) {
        this.ip = ip;
    }

    public HashMap<InetSocketAddress, SocketChannel> getClients() {
        return clients;
    }

    public void queuePacket(Packet packet) {
        queue.add(packet);
    }

    public void setRunning(boolean running) {
        this.running = running;
    }

    @Override
    public void run() {
        try {

            channel = ServerSocketChannel.open();
            channel.configureBlocking(false);
            channel.bind(new InetSocketAddress(InetAddress.getLocalHost(), port));

            System.out.println("Server starting on: " + channel.getLocalAddress().toString());

            Selector selector = Selector.open();
            channel.register(selector, SelectionKey.OP_ACCEPT);

            ByteBuffer readBuffer = ByteBuffer.allocate(2048);
            ByteBuffer writeBuffer = ByteBuffer.allocate(2048);

            while (running) {
                if (selector.selectNow() > 0) {
                    Set<SelectionKey> keys = selector.selectedKeys();

                    for (SelectionKey key : keys) {
                        if (key.isAcceptable()) {
                            try {

                                SocketChannel sChannel = channel.accept();
                                sChannel.configureBlocking(false);
                                SocketAddress sender = sChannel.getRemoteAddress();

                                sChannel.configureBlocking(false);
                                sChannel.register(selector, SelectionKey.OP_READ | SelectionKey.OP_WRITE);

                                System.out.println("Server: Client connected: " + sender);
                                clients.put(((InetSocketAddress) sChannel.getRemoteAddress()), sChannel);

                            } catch (ClosedChannelException ex) {
                                System.out.println("Server: User Disconnected");
                            }
                        } else if (key.isReadable()) {

                            SocketChannel sChannel = (SocketChannel) key.channel();

                            readBuffer.position(0).limit(readBuffer.capacity());
                            sChannel.read(readBuffer);
                            readBuffer.flip();

                            Packet packet = Packet.decompilePacket(readBuffer);
                            packet.clearTargets();

                            for (InetSocketAddress a : clients.keySet()) {
                                if (!a.equals(clients.get(sChannel))) {
                                    packet.addTarget(a);
                                }
                            }
                            queuePacket(packet);
                        }
                    }
                    keys.clear();
                }
                if (!queue.isEmpty()) {
                    Iterator<Packet> packetIterator = queue.iterator();
                    while (packetIterator.hasNext()) {
                        Packet packet = packetIterator.next();

                        writeBuffer.position(0);
                        writeBuffer.limit(writeBuffer.capacity());

                        Packet.compilePacket(packet, writeBuffer);

                        writeBuffer.flip();

                        Iterator<InetSocketAddress> targetIterator = packet.getTargets().iterator();

                        while (targetIterator.hasNext()) {

                            SocketAddress target = targetIterator.next();
                            System.out.println(target);
                            clients.get(target).write(writeBuffer);
                            System.out.println("Server: Sending Packet to: " + target);
                            writeBuffer.flip();
                        }

                        packetIterator.remove();
                    }
                }
            }
            channel.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendHello(InetSocketAddress address) {
        Packet packet = new TextPacket("Hello");
        packet.addTarget(address);
        queuePacket(packet);

    }
}
