package org.educoins.core.client;

import org.educoins.core.miner.Input;

public class Transaction {
	
public static void main(String[] args) {
	
	
			//Hier muss wohl dann die P2P Komponente her, in Moment greife ich direckt auf die Klasse zu...
			Broadcast broadcast = new Broadcast(new Input());
			
			
			broadcast.sendTransaction();
			
	
		}
		
}
