package org.educoins.core.p2p.peers.server;

import com.google.gson.Gson;
import com.sun.istack.internal.NotNull;
import org.educoins.core.Block;
import org.educoins.core.store.BlockNotFoundException;
import org.educoins.core.store.IBlockIterator;
import org.educoins.core.store.IBlockStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * The {@link HttpServlet} serving {@link Block} functionality.
 * Created by typus on 11/5/15.
 */
public class BlockServlet extends HttpServlet {
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());
    protected final Gson gson;
    protected final IBlockStore blockStore;

    public BlockServlet(@NotNull IBlockStore blockStore) {
        this.blockStore = blockStore;
        gson = new Gson();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        logger.info("Received request: {} {}", req.getMethod(), req.getRequestURI());
        resp.setContentType(BlockServer.contentType);
        resp.setStatus(HttpServletResponse.SC_OK);

        List<Block> blocks = getData();
        if (blocks.size() == 0) {
            resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
        } else {
            resp.getWriter().print(gson.toJson(blocks));
        }

        logger.info("Request answered successful.");
    }

    @NotNull
    protected List<Block> getData() throws BlockNotFoundException {
        List<Block> blocks = new ArrayList<>();
        IBlockIterator iterator = blockStore.iterator();
        while (iterator.hasNext()) {
            blocks.add(iterator.next());
        }
        return blocks;
    }
}
