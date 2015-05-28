package org.educoins.core;

import org.educoins.core.cryptography.ECDSA;

public class Wallet implements IBlockListener {

	
	private IBlockReceiver blockReceiver;
	private IBlockTransmitter blockTransmitter;
	
	public Wallet(IBlockReceiver blockReceiver, IBlockTransmitter blockTransmitter){
		
		this.blockReceiver = blockReceiver;				
		this.blockTransmitter = blockTransmitter;
		
		this.blockReceiver.addBlockListener(this);
		
	}
			
	
	

	@Override
	public void blockReceived(Block block) {
		
		if(Verifier.verifyBlock(block)){
			
			//TODO[Vitali] Hier Implementierung wo und wie die BlockChain lokal gespeichert werden soll. 
			//P.s. Die jetztige Implemtierung ist nur zum veranschaulichen.
			this.blockTransmitter.transmitBlock(block);
			
			
		}
		else
		{
			System.err.println("INFO: Block was rejected!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! Class Wallet");
			
		}
		
		
	}


	
	
	
	
}
