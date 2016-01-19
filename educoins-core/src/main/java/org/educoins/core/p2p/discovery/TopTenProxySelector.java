package org.educoins.core.p2p.discovery;

import org.educoins.core.p2p.peers.remote.RemoteProxy;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * A concrete implementation of the
 * {@link IProxySelectorStrategy}. It returns only the top ten highest rated {@link RemoteProxy}s.
 * Created by typus on 1/19/16.
 */
@Component("topTenProxySelector")
public class TopTenProxySelector implements IProxySelectorStrategy {

    @Override
    public Collection<RemoteProxy> getProxies(Collection<RemoteProxy> allKnownProxies) {
        List<RemoteProxy> proxies = new ArrayList<>(allKnownProxies);

        Collections.sort(proxies, (o1, o2) -> {
            double val = o1.getRating() - o2.getRating();
            if (val > 0)
                return 1;
            else if (val < 0)
                return -1;
            return 0;
        });

        return proxies.subList(0, Math.min(proxies.size(), 10));
    }
}
