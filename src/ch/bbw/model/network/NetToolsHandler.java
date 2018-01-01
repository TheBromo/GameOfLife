package ch.bbw.model.network;

import ch.bbw.controller.FXMLLobbyController;

public class NetToolsHandler extends Thread {
    private NetToolsSearch observable;

    public NetToolsHandler (FXMLLobbyController observer){
        observable = new NetToolsSearch();
        observable.addObserver(observer);
        observer.initNettools(observable);
    }

    public void run(){
        observable.run();
    }

    public void shutdown(){
        observable.setRunning(false);
    }
}
