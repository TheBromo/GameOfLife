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

public class Inviter {


    boolean startTimeReceived = false;
    private ArrayList<Packet> sentPackets = new ArrayList<>();
    private ByteBuffer readBuffer, writeBuffer;
    private DatagramChannel socket;
    private Selector selector;
    private int port;

    public Inviter(int port) throws IOException {
        this.port=port;

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

        for (InetAddress address:packet.getTargets()) {

            //creates a socket address
            InetSocketAddress socketAddress = new InetSocketAddress( address, port);
            //sends the data
            socket.send(writeBuffer, socketAddress);
        }
    }

    public Packet readReceivedPacket() throws IOException {
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
                    return packet;


                }
                keys.remove();
            }
        }
        return null;
    }


}
