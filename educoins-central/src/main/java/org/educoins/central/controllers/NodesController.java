package org.educoins.central.controllers;

import org.educoins.central.domain.Node;
import org.educoins.central.domain.PeerType;
import org.educoins.central.repositories.NodesRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

/**
 * The Controller to present {@link Node}s to the requester.
 * Created by typus on 11/2/15.
 */
@RestController
@RequestMapping("/nodes/")
public class NodesController {

    private final Logger logger = LoggerFactory.getLogger(NodesController.class);
    @Autowired
    private NodesRepository nodesRepository;

    @RequestMapping
    public Collection<Node> getAll() {
        return nodesRepository.findAll();
    }

    @RequestMapping("miner")
    public Collection<Node> getMinerPeers() {
        return nodesRepository.findFirst10ByType(PeerType.MINER);
    }


    @RequestMapping("blockchain")
    public Collection<Node> getBlockChainPeers() {
        return nodesRepository.findFirst10ByType(PeerType.BLOCKCHAIN);
    }

    @RequestMapping("reference")
    public Collection<Node> getReferencePeers() {
        return nodesRepository.findFirst10ByType(PeerType.REFERENCE);
    }

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<Node> hello(@RequestBody Node node) {
        logger.info("Retrieved 'hello' from {}@{}", node.getPubkey(), node.getInetAddress());
        if (node != null)
            return new ResponseEntity<>(nodesRepository.save(node), HttpStatus.CREATED);

        return new ResponseEntity<>(new Node(), HttpStatus.BAD_REQUEST);
    }
}
