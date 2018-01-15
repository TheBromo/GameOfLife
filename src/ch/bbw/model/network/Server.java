package ch.bbw.model.network;


import ch.bbw.model.network.packets.NamePacket;
import ch.bbw.model.network.packets.Packet;
import ch.bbw.model.network.packets.SeedPacket;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.*;

public class Server implements Runnable {

    public static final int port = 6666;
    private boolean running;

    private ServerSocketChannel channel;
    private ArrayList<Packet> queue;
    private long seed;

    private HashMap<InetSocketAddress, SocketChannel> clients;

    private InetAddress ip;

    public Server() {

        running = true;
        clients = new HashMap<>();
        queue = new ArrayList<>();
        Random random = new Random();
        seed = random.nextLong();
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

            System.out.println("Server: starting on: " + channel.getLocalAddress().toString());

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


                                Packet packet = new SeedPacket(seed);
                                packet.addTarget((InetSocketAddress) sChannel.getRemoteAddress());
                                queuePacket(packet);

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
                                    System.out.println("Server: added target: " + sChannel.getRemoteAddress());
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
                            clients.get(target).write(writeBuffer);
                            System.out.println("Server: Sending " + packet.getClass().getName() + " to: " + target);
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
        Packet packet = new NamePacket("Hello");
        packet.addTarget(address);
        queuePacket(packet);

    }
}
