package serverTest.network;

import serverTest.network.packets.Packet;
import serverTest.network.packets.PacketHandler;
import serverTest.network.packets.TextPacket;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class Server implements Runnable {

    private int port;
    private boolean running;

    private PacketHandler packetHandler;
    private ServerSocketChannel channel;
    private ArrayList<Packet> queue;

    private HashMap<InetAddress, SocketChannel> clients;

    private InetAddress ip;

    public Server(int port) {

        this.port = port;
        running = true;

        packetHandler = new PacketHandler();

        clients = new HashMap<>();
        queue = new ArrayList<>();

        try {
            channel = ServerSocketChannel.open();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void setIp(InetAddress ip) {
        this.ip = ip;
    }

    public HashMap<InetAddress, SocketChannel> getClients() {
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
            channel.bind(new InetSocketAddress(ip, port));
            channel.configureBlocking(false);
            System.out.println();
            Selector selector = Selector.open();
            channel.register(selector, SelectionKey.OP_ACCEPT);

            ByteBuffer readBuffer = ByteBuffer.allocate(2048);
            ByteBuffer writeBuffer = ByteBuffer.allocate(2048);

            while (running) {
                if (selector.selectNow() > 0) {
                    Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
                    while (iterator.hasNext()) {
                        SelectionKey key = iterator.next();
                        if (key.isAcceptable()) {
                            try {

                                System.out.println();

                                SocketChannel sChannel = channel.accept();
                                SocketAddress sender = sChannel.getRemoteAddress();

                                sChannel.configureBlocking(false);
                                sChannel.register(selector, SelectionKey.OP_READ);

                                System.out.println("Server: User found: " + sender);
                                sendHello(((InetSocketAddress) sChannel.getRemoteAddress()).getAddress());
                                clients.put(((InetSocketAddress) sChannel.getRemoteAddress()).getAddress(), sChannel);

                            } catch (ClosedChannelException ex) {
                                System.out.println("Server: User Disconnected");
                            }
                        }
                        if (key.isReadable()) {

                            SocketChannel sChannel = (SocketChannel) key.channel();

                            readBuffer.position(0);
                            readBuffer.limit(readBuffer.capacity());

                            System.out.println();
                            sChannel.read(readBuffer);
                            readBuffer.flip();

                            Packet packet = Packet.decompilePacket(readBuffer);
                            packet.clearTargets();

                            for (InetAddress a : clients.keySet()) {
                                if (!a.equals(clients.get(sChannel))) {
                                    packet.addTarget(a);
                                }
                            }
                            queuePacket(packet);
                        }
                        iterator.remove();
                    }
                }
                if (!queue.isEmpty()) {
                    Iterator<Packet> packetIterator = queue.iterator();
                    while (packetIterator.hasNext()) {
                        Packet packet = packetIterator.next();

                        writeBuffer.position(0);
                        writeBuffer.limit(writeBuffer.capacity());

                        Packet.compilePacket(packet, writeBuffer);

                        writeBuffer.flip();

                        Iterator<InetAddress> targetIterator = packet.getTargets().iterator();

                        while (targetIterator.hasNext()) {

                            SocketAddress target = new InetSocketAddress(targetIterator.next(), port);
                            System.out.println(target);
                            System.out.println();
                            clients.get(target).write(writeBuffer);
                            System.out.println("Server: Sending Packet to: " + target);

                            writeBuffer.flip();
                        }

                        packetIterator.remove();
                    }
                }
            }
        } catch (Exception e) {
        }
    }

    private void sendHello(InetAddress address) {
        Packet packet = new TextPacket("Hello");
        packet.addTarget(address);
        queuePacket(packet);

    }
}
