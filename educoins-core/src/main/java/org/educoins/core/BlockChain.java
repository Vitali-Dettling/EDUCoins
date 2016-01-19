package org.educoins.core;

import com.google.common.annotations.VisibleForTesting;
import org.educoins.core.p2p.peers.IProxyPeerGroup;
import org.educoins.core.store.*;
import org.educoins.core.transaction.*;
import org.educoins.core.utils.FormatToScientifc;
import org.educoins.core.utils.Sha256Hash;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.stream.Collectors;

@Service
public class BlockChain implements IBlockListener, IPoWListener, ITransactionListener {

    private static final int CHECK_AFTER_BLOCKS = 10;
    private static final int DESIRED_TIME_PER_BLOCK_IN_SEC = 60;
    private static final int IN_SECONDS = 1000;
    private static final int DESIRED_BLOCK_TIME = DESIRED_TIME_PER_BLOCK_IN_SEC * IN_SECONDS * CHECK_AFTER_BLOCKS;
    private static final int SCALE_DECIMAL_LENGTH = 100;
    private static final int RESET_BLOCKS_COUNT = 0;

    private final IBlockStore store;
    private final Logger logger = LoggerFactory.getLogger(BlockChain.class);
    private final Queue<Block> blockQueue = new LinkedBlockingDeque<>();
    private int blockCounter;
    private List<IBlockListener> blockListeners;
    private ITransactionTransmitter transactionTransmitters;
    private List<IBlockListenerMiner> blockListenerMiners;
    private List<ITransactionListener> transactionListeners;
    private List<Transaction> transactions;
    private Verification verification;
    private IProxyPeerGroup remoteProxies;
    private ITransactionFactory transactionFactory;

    @Autowired
    public BlockChain(@NotNull IProxyPeerGroup remoteProxies, @NotNull IBlockStore store) {
        this.store = store;
        this.remoteProxies = remoteProxies;
        this.transactionTransmitters = remoteProxies;

        this.transactionFactory = new TransactionFactory();
        this.blockListeners = new CopyOnWriteArrayList<>();
        this.transactionListeners = new ArrayList<>();
        this.transactions = new ArrayList<>();
        this.blockListenerMiners = new ArrayList<>();
        this.verification = new Verification(this);

        this.blockListeners.add(this.remoteProxies);

        this.blockCounter = RESET_BLOCKS_COUNT;
    }

    @VisibleForTesting
    public static Sha256Hash calcNewDifficulty(Sha256Hash oldDiff, long currentTime, long allBlocksSinceLastTime) {
        BigDecimal oldDifficulty = new BigDecimal(oldDiff.toBigInteger()).setScale(SCALE_DECIMAL_LENGTH,
                BigDecimal.ROUND_HALF_UP);

        BigDecimal actualBlockTime = BigDecimal.valueOf(currentTime - allBlocksSinceLastTime)
                .setScale(SCALE_DECIMAL_LENGTH, BigDecimal.ROUND_HALF_UP);

        // New Difficulty = Old Difficulty * (Actual Time of Last 2016 Blocks /
        // 20160 minutes)
        BigDecimal returnValue = oldDifficulty
                .multiply(actualBlockTime.divide(BigDecimal.valueOf(DESIRED_BLOCK_TIME), BigDecimal.ROUND_HALF_DOWN)
                        .setScale(SCALE_DECIMAL_LENGTH, BigDecimal.ROUND_HALF_UP));

        System.out.println("+++++++++++++++++++++");
        System.out.println("ActualBlockTime: " + actualBlockTime);
        System.out.println("OldDiff: " + FormatToScientifc.format(oldDifficulty, 1) + " | newDiff: "
                + FormatToScientifc.format(returnValue, 1));
        System.out.println("+++++++++++++++++++++");

        return Sha256Hash.wrap(returnValue.toBigInteger().toByteArray());
    }

    @Scheduled(fixedDelay = 5, initialDelay = 15000)
    public void blockProcess() {
//        if (blockQueue.size() == 0)
//            update();

        processNextBlock();
    }

    @Override
    public void blockReceived(Block block) {
        logger.info("Block received: {}", block.hash());
        blockQueue.add(block);
    }

    @Override
    public void foundPoW(Block block) {
        logger.info("Found pow. (Block {})", block.hash().toString());

        blockReceived(block);
        blockListeners.forEach(listener -> listener.blockReceived(block));

        //New round of miner.
        triggerMiners(block);
    }

    private void triggerMiners(Block block) {
        Block newBlock = prepareNewBlock(block, Wallet.getPublicKey());
        blockListenerMiners.forEach(listener -> listener.blockReceived(newBlock));
    }

    /**
     * Uses the remoteProxies to fetch all missing {@link Block}s from the other {@link org.educoins.core.p2p.peers.Peer}s.
     */
    public void update() {
        logger.info("Updating Blockchain...");
        this.remoteProxies.receiveBlocks(getLatestBlock().hash());
        logger.info("Updating Blockchain done.");
    }

    private void processNextBlock() {
        if (blockQueue.isEmpty()) return;

        Block block = blockQueue.poll();
        processBlock(block);
    }

    private void processBlock(Block block) {
        Sha256Hash hash = block.hash();
        logger.info("Processing next Block ({} left in queue) ({})...", blockQueue.size(), hash);

        Block latestBlock = getLatestBlock();
        if (block.equals(latestBlock) || store.contains(block)) {
            logger.info("Not a new Block.");
            return;
        }

        boolean isVerified = this.verification.verifyBlock(block);
        if (isVerified) {
            logger.info("Block successfully verified ({})", hash);
            store.put(block);

            notifyBlockReceived(block);

            List<Transaction> transactions = block.getTransactions();
            if (transactions != null) {
                logger.info("Found {} transactions", transactions.size());
                for (Transaction transaction : transactions) {
                    notifyTransactionReceived(transaction);
                }
            }
        }
        logger.info("Block processed ({}).", hash);
    }

    public @NotNull Block getLatestBlock() {
        Block latest = store.getLatest();
        if (latest != null)
            return latest;
        return new Block();
    }

    public @NotNull Block getBlock(Sha256Hash blockHash) throws BlockNotFoundException {
        return store.get(blockHash);
    }

    public @NotNull Collection<Block> getBlocks() throws BlockNotFoundException {
        List<Block> blocks = new ArrayList<>();
        IBlockIterator iterator = store.iterator();
        while (iterator.hasNext()) {
            blocks.add(iterator.next());
        }
        // Includes the genesis block.
        if (!blocks.isEmpty()) {
            Block genesisBlock = this.store.getGenesisBlock();
            blocks.add(genesisBlock);
        }
        Collections.reverse(blocks);

        return blocks;
    }

    public @NotNull Collection<Block> getBlocksFrom(Sha256Hash from) throws BlockNotFoundException {
        List<Block> blocks = new ArrayList<>();
        IBlockIterator iterator = this.store.iterator();

        while (iterator.hasNext()) {
            // TODO Does not return the genesis block.
            Block next = iterator.next();
            blocks.add(next);
            if (next.hash().equals(from)) {
                Collections.reverse(blocks);
                return blocks;
            }
        }

        // Includes the genesis block.
        if (!blocks.isEmpty()) {
            Block genesisBlock = this.store.getGenesisBlock();
            blocks.add(genesisBlock);
        }
        Collections.reverse(blocks);

        Set<Block> blocksFrom = blocks.stream().filter(block -> block.hash().equals(from)).collect(Collectors.toSet());
        if (blocksFrom.size() > 1)
            throw new IllegalStateException("More than one block with the same hash found!");

        if (blocksFrom.size() == 0 && from.equals(iterator.get().hash()))
            throw new BlockNotFoundException(from.getBytes());

        return blocks;
    }

    public @NotNull Collection<Block> getBlockHeaders() throws BlockNotFoundException {
        List<Block> headers = new ArrayList<>();
        IBlockIterator iterator = store.iterator();
        while (iterator.hasNext()) {
            headers.add(iterator.next().getHeader());
        }
        return headers;
    }

    public void addBlockListenerMiner(IBlockListenerMiner blockListenerMiner) {
        this.blockListenerMiners.add(blockListenerMiner);
    }

    public void addBlockListener(IBlockListener blockListener) {
        this.blockListeners.add(blockListener);
    }

    public void removeBlockListener(IBlockListener blockListener) {
        this.blockListeners.remove(blockListener);
    }

    public void notifyBlockReceived(Block newBlock) {
        for (IBlockListener blockListener : this.blockListeners) {
            blockListener.blockReceived(newBlock);
        }
    }

    public void notifyTransactionReceived(Transaction transaction) {
        for (int i = 0; i < this.transactionListeners.size(); i++) {
            ITransactionListener listener = this.transactionListeners.get(i);
            listener.transactionReceived(transaction);
            transactionReceived(transaction);
        }
    }

    public void sendTransaction(Transaction transaction) {
        logger.info("Transaction of type {} submitted.", transaction.whichTransaction());
        this.transactionTransmitters.transmitTransaction(transaction);
        triggerMiners(getLatestBlock());
    }

    public void transactionReceived(Transaction transaction) {
        logger.info("Received transaction.");

        switch (transaction.whichTransaction()) {
            case APPROVED:
                if (this.verification.verifyApprovedTransaction(transaction)) {
                    this.transactions.add(transaction);
                }
                break;
            case REGULAR:
                if (this.verification.verifyRegularTransaction(transaction)) {
                    this.transactions.add(transaction);
                }
                break;
            case REVOKE:
                if (this.verification.verifyRevokeTransaction(transaction)) {
                    this.transactions.add(transaction);
                }
                break;
        }

        sendTransaction(transaction);
    }

    public Block prepareNewBlock(Block currentBlock, String publicKey) {
        Block newBlock = new Block();
        // TODO [joeren]: which version?! Temporary take the version of the
        // previous block.
        newBlock.setVersion(currentBlock.getVersion());
        newBlock.setHashPrevBlock(currentBlock.hash());
        // TODO [joeren]: calculate hash merkle root! Temporary take the
        // hash merkle root of the previous block.
        newBlock.setHashMerkleRoot(currentBlock.getHashMerkleRoot());

        newBlock.addTransaction(coinbaseTransaction(currentBlock, publicKey));
        newBlock.addTransactions(this.transactions);
        this.transactions.clear();

        return retargedBits(newBlock, currentBlock);
    }

    private Transaction coinbaseTransaction(Block currentBlock, String publicKey) {
        // Input is empty because it is a coinbase transaction.
        int calculatedAmount = currentBlock.rewardCalculator();
        Transaction transaction = this.transactionFactory.generateCoinbasedTransaction(calculatedAmount, publicKey);
        return transaction;
    }

    /**
     * Bitcoin explanation: Mastering Bitcoin 195 Every 2,016 blocks, all nodes
     * retarget the proof-of-work difficulty. The equation for retargeting
     * difficulty measures the time it took to find the last 2,016 blocks and
     * compares that to the expected time of 20,160 minutes.
     * <p>
     * New Difficulty = Old Difficulty * (Actual Time of Last 2016 Blocks /
     * 20160 minutes)
     */
    private Block retargedBits(Block newBlock, Block previousBlock) {
        if (this.blockCounter == CHECK_AFTER_BLOCKS) {
            long currentTime = System.currentTimeMillis();
            long allBlocksSinceLastTime = previousBlock.getTime();

            newBlock.setBits(calcNewDifficulty(previousBlock.getBits(), currentTime, allBlocksSinceLastTime));
            newBlock.setTime(currentTime);
            this.blockCounter = RESET_BLOCKS_COUNT;
        } else {
            // The last time stamp since the last retargeting of the difficulty.
            newBlock.setTime(previousBlock.getTime());
            newBlock.setBits(previousBlock.getBits());
        }
        this.blockCounter++;
        return newBlock;
    }

    public Block getPreviousBlock(Block currentBlock) throws BlockNotFoundException {
        return this.store.get(currentBlock.getHashPrevBlock());
    }

    public Transaction getTransaction(Sha256Hash hash) {
        IBlockIterator it = this.store.iterator();
        while (it.hasNext()) {
            try {
                Block block = it.next();
                Transaction transaction = block.getTransaction(hash);
                if (transaction != null)
                    return transaction;
            } catch (BlockNotFoundException ignored) {
                logger.error("could not find block in Chain, very strange.");
            }
        }
        return null;
    }
}
