package de.ferderer.ebicsdocusign.gateway.domain.docusign.model;

import java.util.Optional;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface DocuSignEnvelopeRepository extends CrudRepository<DocuSignEnvelopeEntity, Long> {
    
    Optional<DocuSignEnvelopeEntity> findByEnvelopeId(String envelopeId);

    @Query("SELECT new de.ferderer.ebicsdocusign.gateway.domain.docusign.model.EnvelopeStatusInfo(" +
           "e.id, e.envelopeId, t.templateName, e.signerEmail, e.signerName, " +
           "CAST(e.status AS string), e.paymentId, e.createdAt, e.sentAt, e.completedAt) " +
           "FROM DocuSignEnvelope e JOIN DocuSignTemplate t ON e.templateId = t.id " +
           "WHERE e.envelopeId = :envelopeId")
    Optional<EnvelopeStatusInfo> findEnvelopeStatus(@Param("envelopeId") String envelopeId);
}
