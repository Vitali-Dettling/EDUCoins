package org.educoins.central.controllers;

import org.educoins.central.domain.Node;
import org.educoins.central.domain.PeerType;
import org.educoins.central.repositories.NodesRepository;
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
public class NodesController {

    @Autowired
    private NodesRepository nodesRepository;

    @RequestMapping("/nodes/")
    public Collection<Node> getAll() {
        return nodesRepository.findAll();
    }

    @RequestMapping("/nodes/miner")
    public Collection<Node> getMinerPeers() {
        return nodesRepository.findByType(PeerType.MINER);
    }


    @RequestMapping("/nodes/blockchain")
    public Collection<Node> getBlockChainPeers() {
        return nodesRepository.findByType(PeerType.BLOCKCHAIN);
    }

    @RequestMapping("/nodes/reference")
    public Collection<Node> getReferencePeers() {
        return nodesRepository.findByType(PeerType.REFERENCE);
    }


    @RequestMapping(value = "/peers/", method = RequestMethod.POST)
    public ResponseEntity<Node> hello(Node node) {
        if (node != null)
            return new ResponseEntity<>(nodesRepository.save(node), HttpStatus.CREATED);

        return new ResponseEntity<>(new Node(), HttpStatus.BAD_REQUEST);
    }
}
