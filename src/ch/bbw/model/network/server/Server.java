package ch.bbw.model.network.server;

import ch.bbw.model.network.packets.Packet;
import ch.bbw.model.network.packets.PacketHandler;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

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

    public void queuePacket(Packet packet){
        queue.add(packet);
    }

    @Override
    public void run() {
        try{
            channel.bind(new InetSocketAddress(ip,port));
            channel.configureBlocking(false);

            Selector selector = Selector.open();
            channel.register(selector, SelectionKey.OP_ACCEPT);



        }catch (Exception e){}
    }
}
