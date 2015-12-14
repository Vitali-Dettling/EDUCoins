package org.educoins.central.controllers;

import org.educoins.central.domain.Node;
import org.educoins.central.repositories.NodesRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.net.URI;
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

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<URI> hello(@RequestBody Node node, HttpServletRequest request) {
        URI inetAddr = URI.create(request.getRemoteAddr());
        logger.info("Retrieved 'hello' from {}@{}", node.getPubkey(), inetAddr);

        //TODO: get protocol aware
//        node.setInetAddress(URI.create("http://" + inetAddr.toString() + ':' + request.getRemotePort()));
        node.setInetAddress(URI.create("http://" + inetAddr.toString() + ':' + node.getPort()));
        nodesRepository.save(node);

        return new ResponseEntity<>(inetAddr, HttpStatus.OK);
    }
}
