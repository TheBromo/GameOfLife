package ch.bbw.model.network;

import ch.bbw.model.network.packets.Packet;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.ArrayList;
import java.util.Iterator;

public class InviteSender {


    private ByteBuffer readBuffer, writeBuffer;
    private DatagramChannel socket;
    private Selector selector;
    public static final int port = 7929;

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

    /**
     * sends a packet to all it's targets
     * @param packet the packet that needs to be sent
     * @throws IOException
     */

    public void sendPacket(Packet packet) throws IOException {
        //prepares the buffer for writing
        writeBuffer.position(0).limit(writeBuffer.capacity());
        //compiles the packet
        Packet.compilePacket(packet,writeBuffer);
        //finishes writing process
        writeBuffer.flip();

        for (InetSocketAddress address : packet.getTargets()) {
            //sends the data
            socket.send(writeBuffer, address);
        }
    }

    /**
     * goes through all received packets and puts them into an ArrayList
     * @return all Packets in an ArrayList
     * @throws IOException
     */
     public ArrayList<Packet> readReceivedPacket() throws IOException {
        ArrayList<Packet> packets = new ArrayList<>();
        if (selector.selectNow() > 0) {
            Iterator<SelectionKey> keys = selector.selectedKeys().iterator();
            while (keys.hasNext()) {
                SelectionKey key = keys.next();

                //checks if any packets were received
                if (key.isReadable()) {
                    System.out.println("invite received");
                    //prepares buffer for reading
                    readBuffer.position(0).limit(readBuffer.capacity());
                    //gets the address of the sender
                    socket.receive(readBuffer);
                    readBuffer.flip();

                    //decompiles packet
                    Packet packet = Packet.decompilePacket(readBuffer);
                    packets.add(packet);
                }
                keys.remove();
            }
        }
        return packets;
    }



}
