package org.educoins.core.p2p.peers.server;

import org.educoins.core.BlockChain;
import org.educoins.core.transaction.Transaction;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

/**
 * The Controller representing the REST-API for the {@link org.educoins.core.transaction.Transaction} resource.
 * Created by dacki on 06.12.15.
 */
@RestController
@RequestMapping("/transactions/")
public class TransactionController {

    private Logger logger = LoggerFactory.getLogger(TransactionController.class);
    private BlockChain blockChain;

    @Autowired
    public TransactionController(BlockChain blockChain) {
        this.blockChain = blockChain;
    }


    /**
     * Adds a {@link org.educoins.core.transaction.Transaction} to the current {@link org.educoins.core.Block} of the
     * {@link BlockChain} for this {@link org.educoins.core.p2p.peers.Peer}.
     *
     * @param transaction Transaction to be added
     */
    @RequestMapping(method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    public void submitTransaction(@RequestBody @NotNull Transaction transaction) {
        logger.info("Received transaction of type: {}", transaction.whichTransaction().name());
        blockChain.transactionReceived(transaction);
    }
}
