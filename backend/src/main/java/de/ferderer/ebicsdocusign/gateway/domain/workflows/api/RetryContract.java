package de.ferderer.ebicsdocusign.gateway.domain.workflows.api;

import de.ferderer.ebicsdocusign.gateway.common.error.AppException;
import de.ferderer.ebicsdocusign.gateway.common.error.ErrorCode;
import de.ferderer.ebicsdocusign.gateway.domain.workflows.model.ContractRetried;
import de.ferderer.ebicsdocusign.gateway.domain.workflows.model.ContractWorkflowEntity.WorkflowStatus;
import de.ferderer.ebicsdocusign.gateway.domain.workflows.model.ContractWorkflowRepository;
import jakarta.validation.Valid;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class RetryContract {
    
    private final ContractWorkflowRepository repository;
    
    public record RetryContractRequest(
        String reason,
        Long newTemplateId
    ) {}
    
    @PostMapping("/api/workflows/contracts/{contractId}/retry")
    public ContractRetried retryContract(
            @PathVariable Long contractId,
            @Valid @RequestBody RetryContractRequest request) {
        
        var workflow = repository.findById(contractId)
            .orElseThrow(() -> new AppException(ErrorCode.DATA_NOT_FOUND)
                .withData("contractId", contractId));
        
        // Only allow retry for contracts in ERROR status
        if (workflow.getStatus() != WorkflowStatus.ERROR) {
            throw new AppException(ErrorCode.BUSINESS_RULE_VIOLATION)
                .withData("contractId", contractId)
                .withData("currentStatus", workflow.getStatus().name())
                .withData("reason", "Can only retry contracts in ERROR status");
        }
        
        try {
            WorkflowStatus previousStatus = workflow.getStatus();
            String previousError = workflow.getErrorMessage();
            
            // Reset workflow to appropriate starting point
            WorkflowStatus retryFromStatus = determineRetryStartingPoint(workflow);
            
            workflow.setStatus(retryFromStatus);
            workflow.setErrorMessage(null);
            
            // Update template if provided
            if (request.newTemplateId() != null) {
                workflow.setTemplateId(request.newTemplateId());
                if (retryFromStatus == WorkflowStatus.CREATED) {
                    workflow.setStatus(WorkflowStatus.TEMPLATE_SELECTED);
                    workflow.setProcessedAt(LocalDateTime.now());
                }
            }
            
            // Clear timestamps that will be regenerated during retry
            resetTimestampsForRetry(workflow, retryFromStatus);
            
            repository.save(workflow);
            
            log.info("Retrying contract workflow {} from status {} (was ERROR)", 
                     contractId, retryFromStatus);
            
            // TODO: Trigger automated workflow processing from retry point
            processWorkflowFromRetryPoint(workflow, retryFromStatus);
            
            return new ContractRetried(
                workflow.getId(),
                workflow.getPaymentId(),
                previousStatus.name(),
                workflow.getStatus().name(),
                previousError,
                request.reason(),
                LocalDateTime.now()
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
    
    private WorkflowStatus determineRetryStartingPoint(
            de.ferderer.ebicsdocusign.gateway.domain.workflows.model.ContractWorkflowEntity workflow) {
        
        // Determine where to restart based on what was completed
        if (workflow.getCompletedAt() != null) {
            return WorkflowStatus.SIGNED; // Retry from signed state
        }
        if (workflow.getSentAt() != null) {
            return WorkflowStatus.ENVELOPE_CREATED; // Retry sending envelope
        }
        if (workflow.getProcessedAt() != null && workflow.getTemplateId() != null) {
            return WorkflowStatus.TEMPLATE_SELECTED; // Retry envelope creation
        }
        if (workflow.getTemplateId() != null) {
            return WorkflowStatus.TEMPLATE_SELECTED; // Retry from template selection
        }
        
        return WorkflowStatus.CREATED; // Start from beginning
    }
    
    private void resetTimestampsForRetry(
            de.ferderer.ebicsdocusign.gateway.domain.workflows.model.ContractWorkflowEntity workflow,
            WorkflowStatus retryFromStatus) {
        
        // Reset timestamps for steps that will be re-executed
        switch (retryFromStatus) {
            case CREATED -> {
                workflow.setProcessedAt(null);
                workflow.setSentAt(null);
                workflow.setCompletedAt(null);
            }
            case TEMPLATE_SELECTED -> {
                workflow.setSentAt(null);
                workflow.setCompletedAt(null);
            }
            case ENVELOPE_CREATED -> {
                workflow.setCompletedAt(null);
            }
            // SENT and SIGNED don't reset any timestamps
        }
    }
    
    private void processWorkflowFromRetryPoint(
            de.ferderer.ebicsdocusign.gateway.domain.workflows.model.ContractWorkflowEntity workflow,
            WorkflowStatus retryFromStatus) {
        
        // Placeholder for automated workflow processing from retry point
        // Will integrate with ProcessPaymentManual logic
        log.info("Would trigger workflow processing from status: {}", retryFromStatus);
    }
}
