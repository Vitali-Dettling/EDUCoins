package org.educoins.core.miner;

import java.io.IOException;

public class Miner {

	private static Block block;
	private static BlockChain blockChain;

	public static void main(String[] args) throws IOException {
		
		blockChain = new BlockChain();

		block = new Block();

		// Dummy
		block.setVersion(0);
		block.setHashedPrevBlock("Damy value previouse block");
		block.setHashedMerkleRoot("Damy value merkle root");
		block.setTimestamp(0);
		block.setDifficulty(Long.parseLong("1d07fff8", 16));//Max difficulty, because of 0xFFFFFFFF => 4294967295 
		 																   
		 																       

		MinerThread minerThread = new MinerThread();
		minerThread.start();

		//Input inputTransacation = new Input();

		Thread.yield();

	}

	public static class MinerThread extends Thread {

		public void run() {

			while (true) {

				new PoW(block);
				blockChain.addBlock(block);

				System.out.println("Block");

			}
		}
	}

}
