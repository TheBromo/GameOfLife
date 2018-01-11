package ch.bbw.model.network.packets;

import java.net.InetAddress;
import java.net.SocketAddress;

public class PacketHandler {

    public void handlePacket(Packet packet) {
        if (packet instanceof TextPacket) {
            TextPacket pm = (TextPacket) packet;
            System.out.println(pm.text);
        }
    }
}
