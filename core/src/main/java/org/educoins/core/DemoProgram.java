package org.educoins.core;

import java.io.File;

import org.educoins.core.miner.Block;

public class DemoProgram {

	public static void main(String[] args) throws InterruptedException {
		String blockFolderPath = System.getProperty("user.home") + File.separator + "documents" + File.separator
				+ "blocks";

		IBlockReceiver rcvr = new DemoBlockReceiver(blockFolderPath, 5000);
		rcvr.addBlockListener(new IBlockListener() {

			@Override
			public void onBlockReceived(Block block) {
				System.out.println("Block received :-)");
			}
		});
		Thread rcvrThread = new Thread(rcvr);
		rcvrThread.start();

		IBlockTransmitter trmt = new DemoBlockTransmitter(blockFolderPath);

		while (true) {
			Thread.sleep(7000);
			trmt.sendBlock(new Block());
		}
	}

}
