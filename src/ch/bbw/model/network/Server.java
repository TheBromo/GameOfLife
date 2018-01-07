package ch.bbw.model.network;

import ch.bbw.model.network.packets.Packet;
import ch.bbw.model.network.packets.PacketHandler;

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

    private HashMap<SocketAddress, SocketChannel> clients;

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

    public HashMap<SocketAddress, SocketChannel> getClients() {
        return clients;
    }

    public void queuePacket(Packet packet) {
        queue.add(packet);
    }

    @Override
    public void run() {
        try {
            channel.bind(new InetSocketAddress(ip, port));
            channel.configureBlocking(false);

            Selector selector = Selector.open();
            channel.register(selector, SelectionKey.OP_ACCEPT);

            ByteBuffer readBuffer = ByteBuffer.allocate(1024);
            ByteBuffer writeBuffer = ByteBuffer.allocate(1024);

            while (running) {
                if (selector.selectNow() > 0) {
                    Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
                    while (iterator.hasNext()) {
                        SelectionKey key = iterator.next();
                        if (key.isAcceptable()) {
                            try {

                                System.out.println("User found");

                                SocketChannel sChannel = channel.accept();
                                SocketAddress sender = sChannel.getRemoteAddress();

                                sChannel.configureBlocking(false);
                                sChannel.register(selector, SelectionKey.OP_READ);

                                clients.put(sChannel.getRemoteAddress(), sChannel);

                            }catch (ClosedChannelException ex){
                                System.out.println("User Disconnected");
                            }
                        }
                        if (key.isReadable()){

                            SocketChannel sChannel = (SocketChannel) key.channel();

                            readBuffer.position(0);
                            readBuffer.limit(readBuffer.capacity());

                            sChannel.read(readBuffer);
                            readBuffer.flip();

                            Packet packet  = Packet.decompilePacket(readBuffer);
                            packet.clearTargets();

                            for(SocketAddress a : clients.keySet()){
                                if(!a.equals(clients.get(sChannel))){
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

                        Iterator<SocketAddress> targetIterator = packet.getTargets().iterator();

                        while (targetIterator.hasNext()) {

                            SocketAddress target = targetIterator.next();
                            System.out.println(target);

                            clients.get(target).write(writeBuffer);

                            writeBuffer.flip();
                        }

                        packetIterator.remove();
                    }
                }
            }
        } catch (Exception e) {
        }
    }
}
