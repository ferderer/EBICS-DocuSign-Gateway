package de.ferderer.ebicsdocusign.gateway.domain.workflows.model;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ContractWorkflowRepository 
    extends CrudRepository<ContractWorkflowEntity, Long>, PagingAndSortingRepository<ContractWorkflowEntity, Long> {
    
    @Query("SELECT new de.ferderer.ebicsdocusign.gateway.domain.workflows.model.ContractWorkflowInfo(" +
           "w.id, w.paymentId, p.transactionId, CAST(w.status AS string), w.contractAmount, w.contractCurrency, " +
           "w.clientName, w.clientEmail, w.projectDescription, t.templateName, e.envelopeId, " +
           "w.createdAt, w.completedAt) " +
           "FROM ContractWorkflow w " +
           "JOIN Payment p ON w.paymentId = p.id " +
           "LEFT JOIN DocuSignTemplate t ON w.templateId = t.id " +
           "LEFT JOIN DocuSignEnvelope e ON w.envelopeId = e.id " +
           "ORDER BY w.createdAt DESC LIMIT :limit")
    List<ContractWorkflowInfo> findAllContracts(@Param("limit") int limit);
    
    @Query("SELECT new de.ferderer.ebicsdocusign.gateway.domain.workflows.model.ContractWorkflowInfo(" +
           "w.id, w.paymentId, p.transactionId, CAST(w.status AS string), w.contractAmount, w.contractCurrency, " +
           "w.clientName, w.clientEmail, w.projectDescription, t.templateName, e.envelopeId, " +
           "w.createdAt, w.completedAt) " +
           "FROM ContractWorkflow w " +
           "JOIN Payment p ON w.paymentId = p.id " +
           "LEFT JOIN DocuSignTemplate t ON w.templateId = t.id " +
           "LEFT JOIN DocuSignEnvelope e ON w.envelopeId = e.id " +
           "WHERE CAST(w.status AS string) = :status " +
           "ORDER BY w.createdAt DESC LIMIT :limit")
    List<ContractWorkflowInfo> findContractsByStatus(@Param("status") String status, @Param("limit") int limit);

    boolean existsByPaymentId(Long paymentId);

    @Query("SELECT new de.ferderer.ebicsdocusign.gateway.domain.workflows.model.ContractDetails(" +
           "w.id, w.paymentId, p.transactionId, CAST(w.status AS string), w.contractAmount, w.contractCurrency, " +
           "w.clientName, w.clientEmail, w.projectDescription, w.contractReference, " +
           "w.templateId, t.templateName, w.envelopeId, e.envelopeId, e.signerEmail, " +
           "c.bankName, c.hostId, w.errorMessage, " +
           "w.createdAt, w.processedAt, w.sentAt, w.completedAt) " +
           "FROM ContractWorkflow w " +
           "JOIN Payment p ON w.paymentId = p.id " +
           "JOIN EbicsConnection c ON p.connectionId = c.id " +
           "LEFT JOIN DocuSignTemplate t ON w.templateId = t.id " +
           "LEFT JOIN DocuSignEnvelope e ON w.envelopeId = e.id " +
           "WHERE w.id = :contractId")
    Optional<ContractDetails> findContractDetails(@Param("contractId") Long contractId);
}
