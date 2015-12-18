package org.educoins.core;

import org.educoins.core.p2p.peers.Peer;
import org.educoins.core.store.IBlockStore;
import org.educoins.core.utils.Threading;
import org.junit.*;

import static org.mockito.Mockito.*;

/**
 * Created by dacki on 01.12.15.
 */
public class MinerTest {

    private static Miner miner = mock(Miner.class);
    private static IBlockReceiver blockReceiver = mock(IBlockReceiver.class);
    private static ITransactionReceiver transactionReceiver = mock(ITransactionReceiver.class);
    private static ITransactionTransmitter transactionTransmitter = mock(ITransactionTransmitter.class);
    private static IBlockStore blockStore = mock(IBlockStore.class);

    @BeforeClass
    public static void init() {
        BlockChain blockchain = new BlockChain(blockReceiver, transactionReceiver,transactionTransmitter, blockStore);
    }

    @AfterClass
    public static void shutdown(){}

    @Before
    public void setUp() throws Exception {

    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void testAddPoWListener() throws Exception {

    }

    @Test
    public void testRemovePoWListener() throws Exception {

    }

    @Test
    public void testNotifyFoundPoW() throws Exception {

    }

    /**
     * This test should trigger a PoW run and notify PoW Listener when found.
     */
    @Test
    public void testBlockReceived() throws Exception {
        IPoWListener powListener = mock(IPoWListener.class);
        boolean found = false;
        Block block = new Block();
        miner.addPoWListener(powListener);
        miner.blockListener(block);
        verify(powListener, timeout(300)).foundPoW(any(Block.class));
    }
}