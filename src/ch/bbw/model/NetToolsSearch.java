package ch.bbw.model;

import ch.bbw.controller.FXMLLobbyController;
import ch.thecodinglab.nettools.Discovery;
import ch.thecodinglab.nettools.SocketAddress;
import ch.thecodinglab.nettools.WinNative;

import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;

public class NetToolsSearch extends Thread implements Discovery.Callback {

    private ArrayList<InetAddress> addresses = new ArrayList<>();
    private FXMLLobbyController controller;
    private boolean running=true;

    public NetToolsSearch(FXMLLobbyController controller) {
        this.controller = controller;
    }

    /**
     * Gets all the addresses of other players searching over the same Port
     *
     * @return all found Addresses
     */
    public void run() {
        WinNative.loadLibrary(new File("lib/native"));

        Discovery.initailize((short) 12345);
        Discovery.setCallback(this);
        Discovery.search((short) 12345, true);

        try{
            while (running) {
                System.out.println("Searching");
                Discovery.update();
            }
        }finally {
            System.out.println("Discovery Close");

            Discovery.close();

        }

    }

    public void setRunning(boolean running) {
        this.running = running;
    }

    @Override
    public boolean discoveryRequestReceived(SocketAddress socketAddress) {
        return false;
    }

    @Override
    public void discoveryClientFound(SocketAddress socketAddress) {
        try {
            InetAddress addr = InetAddress.getByAddress(socketAddress.getAddress());
            addresses.add(addr);
            controller.addUser(addr);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void discoveryPingResult(SocketAddress socketAddress, int i, boolean b) {

    }
}
