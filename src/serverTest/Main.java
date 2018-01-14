package serverTest;

import serverTest.network.Client;
import serverTest.network.Server;
import serverTest.network.packets.Packet;
import serverTest.network.packets.PacketHandler;
import serverTest.network.packets.TextPacket;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Client client = null;
        PacketHandler packetHandler = new PacketHandler();
        boolean host = true;
        if (host) {
            Server server = new Server(6666);
            new Thread(server).start();
            try {
                client = new Client(new InetSocketAddress(InetAddress.getLocalHost(), 6666), packetHandler);
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
        } else {
            Scanner scanner = new Scanner(System.in);
            System.out.println("IP: ");
            String ip = scanner.nextLine();
            try {
                client = new Client(new InetSocketAddress(InetAddress.getByName(ip), 6666), packetHandler);
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
        }
        new Thread(client).start();

        Packet packet = new TextPacket("Test");
        client.queuePacket(packet);
    }
}
