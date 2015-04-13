package org.educoins.core.client;

import java.util.ArrayList;
import java.util.List;

import org.educoins.core.miner.IListen;
import org.educoins.core.miner.Input;

public class Broadcast {

    List<IListen> listlisteners = new ArrayList<IListen>();

    
    public Broadcast(Input minerListen){
    	
    	this.listlisteners = new ArrayList<IListen>();
    	addListener(minerListen);
    	
    }
    
    
    private void addListener(IListen minerListen) {
    	listlisteners.add(minerListen);
    }
    
    

    public void sendTransaction() {

    	// Notify everybody that may be interested.
        for (IListen broadcastTo : listlisteners){
        	broadcastTo.sendTransaction("Transaction send ^^");
        }
    }
	

}
