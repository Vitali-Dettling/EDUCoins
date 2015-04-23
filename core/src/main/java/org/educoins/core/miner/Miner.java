package org.educoins.core.miner;

public class Miner {

	private static Block block;

	public static void main(String[] args) {

		block = new Block();

		// Dummy
		block.setVersion(0);
		block.setHashedPrevBlock("Damy value previouse block");
		block.setHashedMerkleRoot("Damy value merkle root");
		block.setTimestamp(0);
		block.setDifficulty(1);

		MinerThread minerThread = new MinerThread();
		minerThread.start();

		Input inputTransacation = new Input();

		Thread.yield();

	}

	public static class MinerThread extends Thread {

		public void run() {

			while (true) {

				new PoW(block);
				//new BlockChain(block);

				System.out.println("Block");

			}
		}
	}

}
