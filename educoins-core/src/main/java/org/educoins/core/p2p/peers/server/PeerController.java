package org.educoins.core.p2p.peers.server;

import org.educoins.core.p2p.peers.HttpNodeCache;
import org.educoins.core.p2p.peers.Peer;
import org.educoins.core.p2p.peers.remote.HttpProxy;
import org.educoins.core.p2p.peers.remote.RemoteProxy;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * Manages {@link org.educoins.core.p2p.discovery.DiscoveryStrategy} calls for {@link Peer}s.
 * Created by typus on 12/1/15.
 */
@RestController
@RequestMapping("/peers/")
public class PeerController {
    private final HttpNodeCache httpPeerCache;
    private final Logger logger = LoggerFactory.getLogger(BlockController.class);

    @Autowired
    public PeerController(@NotNull HttpNodeCache httpPeerCache) {
        this.httpPeerCache = httpPeerCache;
    }

    /**
     * Manages ingoing {@link RemoteProxy#hello()} requests.
     *
     * @param peer the helloing Peer.
     * @return all peers known so far renouced the newly added one.
     */
    @RequestMapping(value = "http", method = RequestMethod.POST)
    @ResponseStatus(code = HttpStatus.CREATED)
    public Collection<HttpProxy> addHttpPeer(@RequestBody @NotNull HttpProxy peer) {
        logger.info("Added peer " + peer);

        Set<HttpProxy> proxies = new HashSet<>();
        proxies.addAll(httpPeerCache);

        httpPeerCache.add(peer);

        return proxies;
    }
}
