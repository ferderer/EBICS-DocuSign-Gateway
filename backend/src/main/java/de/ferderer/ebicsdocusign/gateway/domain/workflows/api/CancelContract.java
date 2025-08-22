package de.ferderer.ebicsdocusign.gateway.domain.workflows.api;

import de.ferderer.ebicsdocusign.gateway.common.error.AppException;
import de.ferderer.ebicsdocusign.gateway.common.error.ErrorCode;
import de.ferderer.ebicsdocusign.gateway.domain.docusign.model.DocuSignEnvelopeEntity.EnvelopeStatus;
import de.ferderer.ebicsdocusign.gateway.domain.docusign.model.DocuSignEnvelopeRepository;
import de.ferderer.ebicsdocusign.gateway.domain.workflows.model.ContractCancelled;
import de.ferderer.ebicsdocusign.gateway.domain.workflows.model.ContractWorkflowEntity.WorkflowStatus;
import de.ferderer.ebicsdocusign.gateway.domain.workflows.model.ContractWorkflowRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class CancelContract {
    
    private final ContractWorkflowRepository workflowRepository;
    private final DocuSignEnvelopeRepository envelopeRepository;
    
    public record CancelContractRequest(
        @NotBlank String reason
    ) {}
    
    @DeleteMapping("/api/workflows/contracts/{contractId}/cancel")
    public ContractCancelled cancelContract(
            @PathVariable Long contractId,
            @Valid @RequestBody CancelContractRequest request) {
        
        var workflow = workflowRepository.findById(contractId)
            .orElseThrow(() -> new AppException(ErrorCode.DATA_NOT_FOUND)
                .withData("contractId", contractId));
        
        // Check if contract can be cancelled
        if (workflow.getStatus() == WorkflowStatus.COMPLETED) {
            throw new AppException(ErrorCode.BUSINESS_RULE_VIOLATION)
                .withData("contractId", contractId)
                .withData("currentStatus", workflow.getStatus().name())
                .withData("reason", "Cannot cancel completed contract");
        }
        
        if (workflow.getStatus() == WorkflowStatus.CANCELLED) {
            throw new AppException(ErrorCode.BUSINESS_RULE_VIOLATION)
                .withData("contractId", contractId)
                .withData("currentStatus", workflow.getStatus().name())
                .withData("reason", "Contract is already cancelled");
        }
        
        try {
            WorkflowStatus previousStatus = workflow.getStatus();
            LocalDateTime cancelledAt = LocalDateTime.now();
            
            // Cancel DocuSign envelope if it exists and is active
            String docuSignResult = null;
            if (workflow.getEnvelopeId() != null) {
                docuSignResult = cancelDocuSignEnvelope(workflow.getEnvelopeId());
            }
            
            // Update workflow status
            workflow.setStatus(WorkflowStatus.CANCELLED);
            workflow.setCompletedAt(cancelledAt);
            workflow.setErrorMessage("Cancelled: " + request.reason());
            
            workflowRepository.save(workflow);
            
            log.info("Cancelled contract workflow {} (was {}) - Reason: {}", 
                     contractId, previousStatus, request.reason());
            
            return new ContractCancelled(
                workflow.getId(),
                workflow.getPaymentId(),
                previousStatus.name(),
                request.reason(),
                docuSignResult,
                cancelledAt
            );
            
        }
        catch (AppException e) {
            throw e;
        }
        catch (Exception e) {
            throw new AppException(ErrorCode.BUSINESS_OPERATION_NOT_ALLOWED)
                .withData("contractId", contractId)
                .withData("error", e.getMessage());
        }
    }
    
    private String cancelDocuSignEnvelope(Long envelopeId) {
        var envelope = envelopeRepository.findById(envelopeId);
        
        if (envelope.isPresent()) {
            var env = envelope.get();
            
            // Only cancel if envelope is not already completed or signed
            if (env.getStatus() == EnvelopeStatus.SENT || env.getStatus() == EnvelopeStatus.DELIVERED) {
                try {
                    // TODO: Implement actual DocuSign envelope voiding
                    voidDocuSignEnvelope(env.getEnvelopeId());
                    
                    env.setStatus(EnvelopeStatus.VOIDED);
                    envelopeRepository.save(env);
                    
                    return "DocuSign envelope voided successfully";
                } catch (Exception e) {
                    log.warn("Failed to void DocuSign envelope {}: {}", env.getEnvelopeId(), e.getMessage());
                    return "Failed to void DocuSign envelope: " + e.getMessage();
                }
            } else {
                return "DocuSign envelope status (" + env.getStatus() + ") does not allow voiding";
            }
        }
        
        return "No DocuSign envelope found to cancel";
    }
    
    private void voidDocuSignEnvelope(String envelopeId) {
        // Placeholder for actual DocuSign envelope voiding
        // Will be implemented with real DocuSign API on Day 3
        log.info("Would void DocuSign envelope: {}", envelopeId);
    }
}
