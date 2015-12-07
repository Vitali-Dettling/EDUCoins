package org.educoins.core.p2p.peers;

import org.educoins.core.*;
import org.educoins.core.p2p.discovery.CentralDiscovery;
import org.educoins.core.p2p.peers.remote.HttpProxy;
import org.educoins.core.p2p.peers.server.PeerServer;
import org.educoins.core.store.*;
import org.junit.*;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.net.URI;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

/**
 * Tests the {@link HttpProxyPeerGroup}
 * Created by typus on 12/3/15.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(PeerServer.class)
@WebAppConfiguration
@IntegrationTest("server.port:8082")
public class HttpProxyPeerGroupTest {

    @Autowired
    private IBlockStore blockStore;

    private IProxyPeerGroup clientPeerGroup = new HttpProxyPeerGroup();

    @Before
    public void setup() {
        clientPeerGroup.addProxy(new HttpProxy(URI.create(HttpProxy.PROTOCOL + "localhost:8082"), "myPub1"));
    }

    @Test
    public void testDiscover() throws Exception {
//        clientPeerGroup.discover();
    }

    @Test
    public void testTransmitTransaction() throws Exception {
    }

    @Test
    public void testReceiveBlocks() throws Exception {
        IBlockListener listener = Mockito.mock(IBlockListener.class);
        int count = getBlockCount();

        clientPeerGroup.addBlockListener(listener);

        clientPeerGroup.receiveBlocks();
        verify(listener, atLeast(count)).blockReceived(any(Block.class));
    }

    @Test
    @Ignore //Takes pretty long to test...
    public void testRediscovery() throws Exception {
        clientPeerGroup.clearProxies();
        clientPeerGroup.addProxy(new HttpProxy(URI.create(HttpProxy.PROTOCOL + "localhost:42"), "myPub1"));

        for (int i = 0; i < 4; ++i)
            clientPeerGroup.receiveBlocks();

        IProxyPeerGroup spy = spy(clientPeerGroup);
        spy.receiveBlocks();
        verify(spy, atLeastOnce()).discover(any(CentralDiscovery.class));
    }

    @Test
    public void testReceiveTransactions() throws Exception {
        ITransactionListener listener = Mockito.mock(ITransactionListener.class);
        int count = getTxnsCount();

        clientPeerGroup.addTransactionListener(listener);

        clientPeerGroup.receiveTransactions();
        verify(listener, atLeast(count)).transactionReceived(any(Transaction.class));
    }

    private int getBlockCount() throws BlockNotFoundException {
        int count = 0;
        IBlockIterator iterator = blockStore.iterator();
        while (iterator.hasNext()) {
            iterator.next();
            count++;
        }
        return count;
    }

    public int getTxnsCount() throws BlockNotFoundException {
        final int[] count = {0};
        IBlockIterator iterator = blockStore.iterator();
        while (iterator.hasNext()) {
            Block block = iterator.next();
            block.getTransactions().forEach(transaction -> count[0]++);
        }
        return count[0];
    }
}