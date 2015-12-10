package org.educoins.core.p2p.peers.server;

import org.educoins.core.Block;
import org.educoins.core.BlockChain;
import org.educoins.core.p2p.peers.server.exceptions.BlockChainVerificationException;
import org.educoins.core.store.BlockNotFoundException;
import org.educoins.core.utils.Sha256Hash;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

/**
 * The Controller representing the REST-API for the {@link Block} resource.
 * Created by typus on 11/30/15.
 */
@RestController
@RequestMapping("/blocks")
public class BlockController {

    private Logger logger = LoggerFactory.getLogger(BlockController.class);
    private BlockChain blockChain;

    @Autowired
    public BlockController(BlockChain blockChain) {
        this.blockChain = blockChain;
    }

    /**
     * Returns all {@link Block}s stored in this {@link org.educoins.core.p2p.peers.Peer}.
     *
     * @return The List of all Blocks.
     * @throws BlockNotFoundException if a Block could not be found.
     */
    @RequestMapping(value = "", method = RequestMethod.GET)
    public Collection<Block> getBlocks() throws BlockNotFoundException {
        logger.info("Offering blocks");
        return blockChain.getBlocks();
    }

    /**
     * Returns all {@link Block}s stored in this {@link org.educoins.core.p2p.peers.Peer}.
     *
     * @return The List of all Blocks.
     * @throws BlockNotFoundException if a Block could not be found.
     */
    @RequestMapping(value = "/from/{hash}", method = RequestMethod.GET)
    public Collection<Block> getBlocksFrom(@PathVariable(value = "hash") String hash) throws BlockNotFoundException {
        logger.info("Offering blocks from hash: {}", hash);
        return blockChain.getBlocksFrom(Sha256Hash.wrap(hash));
    }

    /**
     * Adds the {@link Block} to the {@link BlockChain} of the {@link org.educoins.core.p2p.peers.Peer}.
     *
     * @param block the Block to addProxy.
     * @throws BlockChainVerificationException if the there was a conflict while validation.
     */
    @RequestMapping(value = "", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    public void createBlock(@RequestBody @NotNull Block block) throws BlockChainVerificationException {
        logger.info("Received Block from foreign node via push");
        blockChain.blockReceived(block);
    }

    /**
     * Returns a specific {@link Block} specified by its  {@link Block#hash()}.
     *
     * @param hash the {@link Sha256Hash} identifying this {@link Block}.
     * @return The Block stored in this {@link org.educoins.core.p2p.peers.Peer}.
     * @throws BlockNotFoundException if the Block could not be found.
     */
    @RequestMapping(value = "/{hash}", method = RequestMethod.GET)
    public Block getBlock(@PathVariable(value = "hash") String hash) throws
            BlockNotFoundException {
        logger.info("Offering block {}", hash);
        return blockChain.getBlock(Sha256Hash.wrap(hash));
    }

    /**
     * Returns a list of all {@link Block#getHeader()}s stored in this {@link org.educoins.core.p2p.peers.Peer}.
     *
     * @return The list of all {@link Block#getHeader()}s.
     * @throws BlockNotFoundException if a Block could not be found.
     */
    @RequestMapping(value = "/headers", method = RequestMethod.GET)
    public Collection<Block> getBlockHeaders() throws BlockNotFoundException {
        logger.info("Offering block headers.");
        return blockChain.getBlockHeaders();
    }

}
