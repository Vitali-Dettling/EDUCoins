package org.educoins.core.p2p.peers;

import org.educoins.core.p2p.peers.remote.HttpProxy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.HashSet;

@Component
@Scope("singleton")
public class HttpNodeCache extends HashSet<HttpProxy> {
}
