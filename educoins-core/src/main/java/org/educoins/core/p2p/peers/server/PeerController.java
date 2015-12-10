package org.educoins.core.p2p.peers.server;

import org.educoins.core.p2p.peers.IProxyPeerGroup;
import org.educoins.core.p2p.peers.Peer;
import org.educoins.core.p2p.peers.remote.HttpProxy;
import org.educoins.core.p2p.peers.remote.RemoteProxy;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * Manages {@link org.educoins.core.p2p.discovery.DiscoveryStrategy} calls for {@link Peer}s.
 * Created by typus on 12/1/15.
 */
@RestController
@RequestMapping("/peers")
public class PeerController {
    private final IProxyPeerGroup httpPeerCache;
    private final Logger logger = LoggerFactory.getLogger(PeerController.class);

    @Autowired
    public PeerController(@NotNull IProxyPeerGroup httpPeerCache) {
        this.httpPeerCache = httpPeerCache;
    }

    /**
     * Manages ingoing {@link RemoteProxy#hello()} requests.
     *
     * @param peer the helloing Peer.
     * @return all peers known so far renounced the newly added one.
     */
    @RequestMapping(path = "/http", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    public Collection<RemoteProxy> addHttpPeer(@RequestBody @NotNull HttpProxy peer, HttpServletRequest request) {
        logger.info("Retrieved peer {} from {}", peer, request.getRemoteAddr());

        Set<RemoteProxy> proxies = new HashSet<>();
        proxies.addAll(httpPeerCache.getAllProxies());

        httpPeerCache.addProxy(peer);

        return proxies;
    }
}
