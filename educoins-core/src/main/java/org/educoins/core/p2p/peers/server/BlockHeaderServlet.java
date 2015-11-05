package org.educoins.core.p2p.peers.server;

import com.google.gson.Gson;
import com.sun.istack.internal.NotNull;
import org.educoins.core.Block;
import org.educoins.core.store.IBlockIterator;
import org.educoins.core.store.IBlockStore;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * The {@link HttpServlet} serving {@link Block}-Header functionality.
 * Created by typus on 11/5/15.
 */
public class BlockHeaderServlet extends HttpServlet {
    private final String contentType = "application/json";

    private final Gson gson;
    private final IBlockStore blockStore;

    public BlockHeaderServlet(@NotNull IBlockStore blockStore) {
        this.blockStore = blockStore;
        gson = new Gson();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType(contentType);
        resp.setStatus(HttpServletResponse.SC_OK);

        List<Block> blocks = new ArrayList<>();
        IBlockIterator iterator = blockStore.iterator();
        while (iterator.hasNext()) {
            blocks.add(iterator.next().getHeader());
        }

        resp.getWriter().print(gson.toJson(blocks));
    }
}
