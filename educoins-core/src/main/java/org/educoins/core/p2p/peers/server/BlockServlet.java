package org.educoins.core.p2p.peers.server;

import com.google.gson.Gson;
import org.educoins.core.Block;
import org.educoins.core.store.*;
import org.educoins.core.utils.Sha256Hash;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.*;
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
    protected void doGet(@NotNull HttpServletRequest req, @NotNull HttpServletResponse resp) throws ServletException, IOException {
        logger.info("Received request: {} {}", req.getMethod(), req.getRequestURI());
        resp.setContentType(BlockServer.contentType);
        resp.setStatus(HttpServletResponse.SC_OK);

        String requestPath = req.getRequestURI();

        String hashString;
        if (requestPath.contains("/"))
            hashString = requestPath.substring(requestPath.lastIndexOf("/") + 1, requestPath.length());
        else hashString = "";

        if (hashString.length() > 0) {
            try {
                Sha256Hash hash = Sha256Hash.wrap(hashString);
                resp.getWriter().print(gson.toJson(blockStore.get(hash)));
            } catch (IllegalArgumentException ex) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().print(ex.getMessage());
            } catch (BlockNotFoundException ex) {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                resp.getWriter().print(ex.getMessage());
            }
        } else {
            List<Block> blocks = getData();
            if (blocks.size() == 0) {
                resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
            } else {
                resp.getWriter().print(gson.toJson(blocks));
            }
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
