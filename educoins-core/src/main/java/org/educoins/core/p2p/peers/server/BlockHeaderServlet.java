package org.educoins.core.p2p.peers.server;

import org.educoins.core.Block;
import org.educoins.core.store.*;
import org.jetbrains.annotations.NotNull;

import javax.servlet.http.HttpServlet;
import java.util.ArrayList;
import java.util.List;

/**
 * The {@link HttpServlet} serving {@link Block}-Header functionality.
 * Created by typus on 11/5/15.
 */
public class BlockHeaderServlet extends BlockServlet {
    public BlockHeaderServlet(@NotNull IBlockStore blockStore) {
        super(blockStore);
    }

    @Override
    @NotNull
    protected List<Block> getData() throws BlockNotFoundException {
        List<Block> blocks = new ArrayList<>();
        IBlockIterator iterator = blockStore.iterator();
        while (iterator.hasNext()) {
            blocks.add(iterator.next().getHeader());
        }
        return blocks;
    }
}