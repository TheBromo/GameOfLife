package serverTest.network.packets;

import java.util.Observable;

public class PacketHandler extends Observable {

    public void handlePacket(Packet packet) {
        if (packet instanceof TextPacket) {
            TextPacket pm = (TextPacket) packet;
            System.out.println(pm.text);
        }
    }
}
