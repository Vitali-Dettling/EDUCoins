package org.educoins.central.repositories;

import org.educoins.central.domain.Node;
import org.educoins.central.domain.PeerType;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;

/**
 * The Repository handling CRUD-Requests to the database concerning {@link Node}s.
 * Created by typus on 11/2/15.
 */
@Repository
public interface NodesRepository extends CrudRepository<Node, Long> {
    Collection<Node> findFirst10ByType(PeerType type);

    Node save(Node node);

    Collection<Node> findAll();
}
