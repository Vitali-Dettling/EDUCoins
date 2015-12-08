package org.educoins.central.repositories;

import org.educoins.central.domain.Node;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Collection;

/**
 * The Repository handling CRUD-Requests to the database concerning {@link Node}s.
 * Created by typus on 11/2/15.
 */
@Repository
public interface NodesRepository extends CrudRepository<Node, Long> {
    Collection<Node> findByTimestampBefore(LocalDateTime dateTime);

    Node save(Node node);

    Collection<Node> findAll();
}
