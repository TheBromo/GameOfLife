package ch.bbw.model.network;

import ch.bbw.model.network.packets.Packet;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.ArrayList;
import java.util.Iterator;

public class InviteSender {


    private ArrayList<Packet> sentPackets = new ArrayList<>();
    private ByteBuffer readBuffer, writeBuffer;
    private DatagramChannel socket;
    private Selector selector;
    public static final int port = 8888;

    public InviteSender() throws IOException {

        //prepares the socket
        socket = DatagramChannel.open();
        socket.configureBlocking(false);
        socket.bind(new InetSocketAddress(port));

        //creates a selector for reading incoming Traffic
        selector = Selector.open();
        socket.register(selector, SelectionKey.OP_READ);

        readBuffer = ByteBuffer.allocate(1024);
        writeBuffer = ByteBuffer.allocate(1024);
    }

    public void sendPacket(Packet packet) throws IOException {

        writeBuffer.position(0).limit(writeBuffer.capacity());
        Packet.compilePacket(packet,writeBuffer);
        writeBuffer.flip();

        for (InetSocketAddress address : packet.getTargets()) {


            //sends the data
            socket.send(writeBuffer, address);
        }
    }


     public ArrayList<Packet> readReceivedPacket() throws IOException {
        ArrayList<Packet> packets = new ArrayList<>();
        if (selector.selectNow() > 0) {
            Iterator<SelectionKey> keys = selector.selectedKeys().iterator();
            while (keys.hasNext()) {
                SelectionKey key = keys.next();

                //checks if any packets were received
                if (key.isReadable()) {

                    //prepares buffer for reading
                    readBuffer.position(0).limit(readBuffer.capacity());
                    //gets the address of the sender
                    SocketAddress sender = socket.receive(readBuffer);
                    readBuffer.flip();

                    //the senders InetAddress
                    InetAddress address = ((InetSocketAddress) sender).getAddress();

                    Packet packet = Packet.decompilePacket(readBuffer);
                    packets.add(packet);


                }
                keys.remove();
            }
        }
        return packets;
    }


}
