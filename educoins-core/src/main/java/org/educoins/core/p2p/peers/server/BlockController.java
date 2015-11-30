package org.educoins.core.p2p.peers.server;

import org.educoins.core.Block;
import org.educoins.core.store.*;
import org.educoins.core.utils.Sha256Hash;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * The Controller representing the REST-API for the {@link Block} resource.
 * Created by typus on 11/30/15.
 */
@RestController
@RequestMapping("/blocks/")
public class BlockController {

    private IBlockStore blockStore;

    @Autowired
    public BlockController(IBlockStore blockStore) {
        this.blockStore = blockStore;
    }

    /**
     * Returns all {@link Block}s stored in this {@link org.educoins.core.p2p.peers.Peer}.
     *
     * @return The List of all Blocks.
     * @throws BlockNotFoundException if a Block could not be found.
     */
    @RequestMapping(value = "", method = RequestMethod.GET)
    public Collection<Block> getBlocks() throws BlockNotFoundException {
        try {
            return aggregateBlocks();
        } catch (BlockNotFoundException e) {
            throw new BlockNotFoundException(e.getMessage());
        }
    }

    /**
     * Returns a specific {@link Block} specified by its  {@link Block#hash()}.
     *
     * @param hash the {@link Sha256Hash} identifying this {@link Block}.
     * @return The Block stored in this {@link org.educoins.core.p2p.peers.Peer}.
     * @throws BlockNotFoundException if the Block could not be found.
     */
    @RequestMapping(value = "{hash}", method = RequestMethod.GET)
    public Block getBlock(@PathVariable(value = "hash") String hash) throws
            BlockNotFoundException {
        try {
            return blockStore.get(Sha256Hash.wrap(hash));
        } catch (BlockNotFoundException e) {
            throw new BlockNotFoundException(e.getMessage());
        }
    }

    /**
     * Returns a list of all {@link Block#getHeader()}s stored in this {@link org.educoins.core.p2p.peers.Peer}.
     *
     * @return The list of all {@link Block#getHeader()}s.
     * @throws BlockNotFoundException if a Block could not be found.
     */
    @RequestMapping(value = "headers", method = RequestMethod.GET)
    public Collection<Block> getBlockHeaders() throws BlockNotFoundException {
        try {
            return aggregateBlockHeaders();
        } catch (BlockNotFoundException e) {
            throw new BlockNotFoundException(e.getMessage());
        }
    }

    @NotNull
    private Collection<Block> aggregateBlockHeaders() throws BlockNotFoundException {
        List<Block> headers = new ArrayList<>();
        IBlockIterator iterator = blockStore.iterator();
        while (iterator.hasNext()) {
            headers.add(iterator.next().getHeader());
        }
        return headers;
    }

    private @NotNull List<Block> aggregateBlocks() throws BlockNotFoundException {
        List<Block> blocks = new ArrayList<>();
        IBlockIterator iterator = blockStore.iterator();
        while (iterator.hasNext()) {
            blocks.add(iterator.next());
        }
        return blocks;
    }

}
