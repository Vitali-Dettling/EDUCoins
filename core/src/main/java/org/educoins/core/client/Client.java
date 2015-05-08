package org.educoins.core.client;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import org.educoins.core.miner.Input;


public class Client {
	
	

	public static void main(String[] args) {

		// Hier muss wohl dann die P2P Komponente her, in Moment greife ich
		// direckt auf die Klasse zu...
		Broadcast broadcast = new Broadcast(new Input());

		broadcast.sendTransaction();
	
		
		
		
	}

	
}
