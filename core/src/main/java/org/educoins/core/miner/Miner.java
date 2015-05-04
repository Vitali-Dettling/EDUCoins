package org.educoins.core.miner;

import java.io.IOException;

public class Miner {

	private static Block block;
	private static BlockChain blockChain;

	public static void main(String[] args) throws IOException {
		
		MinerThread minerThread = new MinerThread();
		minerThread.start();

		//Input inputTransacation = new Input();

		Thread.yield();

	}

	public static class MinerThread extends Thread {

		public void run() {
			
			//Hier Klasse um letzten Block aus der BlocChain, mit daten zu bekommen...
			blockChain = new BlockChain();
			PoW mining = new PoW();	
			Block lastBlock = new Block();
			Block newBlock = lastBlock;
			while (true) {
				
				blockChain.newBlock(newBlock);
				newBlock = mining.startMiningPOW(lastBlock);

				System.out.println("Block");
			}
		}
	}
}







