package de.ferderer.ebicsdocusign.gateway.domain.ebics.model;

import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EbicsConnectionRepository extends CrudRepository<EbicsConnectionEntity, Long> {

    @Query("from EbicsConnection order by createdAt desc")
    List<EbicsConnection> findAllConnections();

    long countByStatus(ConnectionStatus status);
}
