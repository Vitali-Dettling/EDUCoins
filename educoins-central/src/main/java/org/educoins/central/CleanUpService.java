package org.educoins.central;

import org.educoins.central.domain.Node;
import org.educoins.central.repositories.NodesRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.Period;
import java.util.Collection;

/**
 * Cleans the {@link org.educoins.central.repositories.NodesRepository} from time to time.
 * Created by typus on 12/8/15.
 */
@Service
public class CleanUpService {
    public static final int CLEAN_UP_INTERVAL = 24 * 60 * 60 * 1000;
    public static final Period MAX_AGE = Period.ofDays(10);
    private final Logger logger = LoggerFactory.getLogger(CleanUpService.class);
    @Autowired
    private NodesRepository repository;

    /**
     * Is (automatically) called each {@link CleanUpService#CLEAN_UP_INTERVAL}ms.
     * Will remove all Nodes which are older then {@link CleanUpService#MAX_AGE}.
     */
    @Scheduled(fixedRate = CleanUpService.CLEAN_UP_INTERVAL)
    public void cleanUp() {
        Collection<Node> toDelete = repository.findByTimestampBefore(LocalDateTime.now().minus(MAX_AGE));
        toDelete.forEach(node -> repository.delete(node));
        logger.info("Deleted {} nodes", toDelete.size());
    }
}
