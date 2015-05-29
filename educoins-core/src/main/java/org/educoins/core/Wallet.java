package org.educoins.core;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

import org.educoins.core.cryptography.ECDSA;


public class Wallet implements IBlockListener {

	
	private IBlockReceiver blockReceiver;
	private IBlockTransmitter blockTransmitter;
	private ECDSA keyPair;
	private Path walletDirectory;
	
	public Wallet(IBlockReceiver blockReceiver, IBlockTransmitter blockTransmitter, ECDSA ecdsa){
		
		this.blockReceiver = blockReceiver;				
		this.blockTransmitter = blockTransmitter;
		
		this.blockReceiver.addBlockListener(this);
		
		this.keyPair = ecdsa;
		
		this.walletDirectory = Paths.get(System.getProperty("user.home") + File.separator + "documents" + File.separator
				+ "educoins" + File.separator + "demo" + File.separator + "wallet");
		
		
		
		
		try {
			getNewKeyPair();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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

	//TODO[Vitali] Right now the private public key is stored in a file but this can change, maybe???
	public String getNewKeyPair() throws IOException{
		
		//TODO [Vitali] Create a own Util for testing??? -> DemoFileDirectory??? Jören klären...
		PrintWriter writer = createNewDirectory();

		writer.println(this.keyPair.getPrivateKey() + this.keyPair.getPublicKey());
		
		return null;
		
	}
	
	
	private PrintWriter createNewDirectory() throws IOException{
		
		if (Files.exists(this.walletDirectory)) {
			Stream<Path> localFiles = Files.list(this.walletDirectory);
			for (Object file : localFiles.toArray()) {
				Files.delete((Path) file);
			}
			localFiles.close();
		}
		
		if (Files.exists(this.walletDirectory) && !Files.isDirectory(this.walletDirectory)) {
			throw new IllegalArgumentException(this.walletDirectory.toString() + " is not a directory");
		}

		if (!Files.exists(this.walletDirectory)) {
			Files.createDirectories(this.walletDirectory);
		}
		
		return new PrintWriter(this.walletDirectory.toString() + ".Ekeys", "UTF-8");
	}

	
	
	
	
}
