package ch.bbw.model.network;

import ch.thecodinglab.nettools.Discovery;
import ch.thecodinglab.nettools.SocketAddress;
import ch.thecodinglab.nettools.WinNative;

import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Observable;
@SuppressWarnings("deprecation")
public class NetToolsSearch extends Observable implements Runnable,Discovery.Callback {

    private boolean running = true;

    /**
     * Gets all the addresses of other players searching over the same Port
     *
     *
     */
    @Override
    public void run() {
        WinNative.loadLibrary(new File("lib/native"));

        Discovery.initailize((short) 12345);
        Discovery.setCallback(this);
        Discovery.search((short) 12345, true);

        while (running) {
            //sends requests to everyone in the network
            Discovery.update();
        }

        System.out.println("Discovery Close");
        Discovery.close();
    }


    public void setRunning(boolean running) {
        this.running = running;
    }

    @Override
    public boolean discoveryRequestReceived(SocketAddress socketAddress) {
        //if a user request is received, answer
        return true;
    }

    @Override
    public void discoveryClientFound(SocketAddress socketAddress) {
        try {
            //send InetAddress to Lobby Controller
            InetAddress addr = InetAddress.getByAddress(socketAddress.getAddress());
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
