package org.educoins;

import org.educoins.core.*;
import org.educoins.core.p2p.discovery.DiscoveryException;
import org.educoins.core.p2p.peers.SoloMinerPeer;
import org.educoins.core.store.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

/**
 * Created by dacki on 03.12.15.
 */
public class MinerApplication {
    private static Logger log = LoggerFactory.getLogger(MinerApplication.class);

    protected MinerApplication() {
    }

    public static void main(String[] args) {
        IBlockStore blockStore;
        try {
            blockStore = new LevelDbBlockStore(new File("/tmp/educoin" + System.currentTimeMillis()));
        } catch (BlockStoreException e) {
            log.error("BlockStore could not be initialized", e);
            return;
        }
        BlockChain bc = new BlockChain(BlockReveiverDummy.create(), TransactionReveiverDummy.create(), null, blockStore);
        Miner miner = new Miner(bc);
        SoloMinerPeer peer = new SoloMinerPeer(bc, miner);
        try {
            peer.start();
        } catch (DiscoveryException e) {
            e.printStackTrace();
            log.error("Discovery could not find any peers. Closing now...", e);
        }
    }

    static class BlockReveiverDummy implements IBlockReceiver {

        public static BlockReveiverDummy create() {
            return new BlockReveiverDummy();
        }

        @Override
        public void addBlockListener(IBlockListener blockListener) {

        }

        @Override
        public void removeBlockListener(IBlockListener blockListener) {

        }

        @Override
        public void receiveBlocks() {

        }
    }

    static class TransactionReveiverDummy implements ITransactionReceiver {
        public static TransactionReveiverDummy create() {
            return new TransactionReveiverDummy();
        }

        @Override
        public void addTransactionListener(ITransactionListener transactionListener) {

        }

        @Override
        public void removeTransactionListener(ITransactionListener transactionListener) {

        }

        @Override
        public void receiveTransactions() {

        }
    }
}
