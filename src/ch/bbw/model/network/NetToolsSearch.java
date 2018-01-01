package ch.bbw.model.network;

import ch.thecodinglab.nettools.Discovery;
import ch.thecodinglab.nettools.SocketAddress;
import ch.thecodinglab.nettools.WinNative;

import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Observable;

public class NetToolsSearch extends Observable implements Discovery.Callback {

    private ArrayList<InetAddress> addresses = new ArrayList<>();
    private boolean running = true;


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

        while (running) {
            Discovery.update();
        }

        System.out.println("Discovery Close");

        Discovery.close();


    }

    public ArrayList<InetAddress> getAddresses() {
        return addresses;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }

    @Override
    public boolean discoveryRequestReceived(SocketAddress socketAddress) {
        return true;
    }

    @Override
    public void discoveryClientFound(SocketAddress socketAddress) {
        try {
            InetAddress addr = InetAddress.getByAddress(socketAddress.getAddress());
            addresses.add(addr);
            setChanged();
            notifyObservers(addr);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void discoveryPingResult(SocketAddress socketAddress, int i, boolean b) {

    }
}
