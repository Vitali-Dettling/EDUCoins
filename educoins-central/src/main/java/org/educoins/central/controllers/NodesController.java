package org.educoins.central.controllers;

import org.educoins.central.domain.Node;
import org.educoins.central.repositories.NodesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;

/**
 * The Controller to present {@link Node}s to the requester.
 * Created by typus on 11/2/15.
 */
@RestController
public class NodesController {

    @Autowired
    private NodesRepository nodesRepository;

    @RequestMapping("/nodes/full")
    public Collection<Node> getFullNodes() {
        return nodesRepository.findAll();
    }


    @RequestMapping("/nodes/read-only")
    public Collection<Node> getPearNodes() {
        return nodesRepository.findAll();
    }


//    @RequestMapping(value = "/peers/", method = RequestMethod.POST)
//    public ResponseEntity<Node> addNode(Node node) {
//        if (node != null)
//            return new ResponseEntity<Node>(nodesRepository.save(node), HttpStatus.CREATED);
//        return new ResponseEntity<Node>(new Node(), HttpStatus.BAD_REQUEST);
//    }
}
