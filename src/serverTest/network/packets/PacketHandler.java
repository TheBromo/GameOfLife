package serverTest.network.packets;

import java.util.Observable;

public class PacketHandler extends Observable {

    public void handlePacket(Packet packet) {
        System.out.println("Packet Received");
        if (packet instanceof TextPacket) {
            TextPacket pm = (TextPacket) packet;
            System.out.println(pm.text);
        }
    }
}
