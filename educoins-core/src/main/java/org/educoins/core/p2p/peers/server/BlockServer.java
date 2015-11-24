package org.educoins.core.p2p.peers.server;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.educoins.core.Block;
import org.educoins.core.store.IBlockStore;
import org.jetbrains.annotations.NotNull;

/**
 * The HttpServer serving {@link Block}s.
 * Manages all ingoing request by usage of {@link BlockServlet} and {@link BlockHeaderServlet}.
 * Created by typus on 11/5/15.
 */
public class BlockServer {
    public static final String contentType = "application/json";
    public static final String BLOCKS_RESOURCE_PATH = "/blocks/";
    public static final String BLOCK_HEADERS_RESOURCE_PATH = "/blocks/headers/";
    private final Server server;

    public BlockServer(@NotNull IBlockStore blockStore, int port) {
        this.server = new Server(port);

        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");
        server.setHandler(context);

        context.addServlet(new ServletHolder(new BlockServlet(blockStore)), BLOCKS_RESOURCE_PATH + '*');
        context.addServlet(new ServletHolder(new BlockHeaderServlet(blockStore)), BLOCK_HEADERS_RESOURCE_PATH + '*');
    }

    /**
     * Starts the {@link BlockServer}.
     *
     * @throws Exception if anything went wrong.
     */
    public void start() throws Exception {
        this.server.start();
    }

    /**
     * Joins the {@link Server}'s internal {@link Thread}. This synchronizes the calling {@link Thread} with the Server.
     *
     * @throws InterruptedException if the {@link Server}'s Thread got interrupted.
     */
    public void join() throws InterruptedException {
        this.server.join();
    }

    /**
     * Stops the running instance.
     *
     * @throws Exception if anything went wrong.
     */
    public void stop() throws Exception {
        this.server.stop();
    }
}
