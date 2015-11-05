package org.educoins.core.p2p.peers.server;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.educoins.core.Block;
import org.educoins.core.store.IBlockStore;

/**
 * The HttpServer serving {@link Block}s.
 * Created by typus on 11/5/15.
 */
public class BlockServer {
    private final Server server;

    public BlockServer(IBlockStore blockStore, int port) {
        this.server = new Server(port);

        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");
        server.setHandler(context);

        context.addServlet(new ServletHolder(new BlockHeaderServlet(blockStore)), "/blocks/headers/*");
        context.addServlet(new ServletHolder(new BlockServlet(blockStore)), "/blocks/*");
    }

    public void start() throws Exception {
        this.server.start();
    }

    public void join() throws InterruptedException {
        this.server.join();
    }
}
