package de.ferderer.ebicsdocusign.gateway.domain.workflows.api;

import de.ferderer.ebicsdocusign.gateway.common.error.AppException;
import de.ferderer.ebicsdocusign.gateway.common.error.ErrorCode;
import de.ferderer.ebicsdocusign.gateway.domain.workflows.model.ContractStatusUpdated;
import de.ferderer.ebicsdocusign.gateway.domain.workflows.model.ContractWorkflowEntity.WorkflowStatus;
import de.ferderer.ebicsdocusign.gateway.domain.workflows.model.ContractWorkflowRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UpdateContractStatus {
    
    private final ContractWorkflowRepository repository;
    
    public record UpdateContractStatusRequest(
        @NotBlank String status,
        String reason,
        String errorMessage
    ) {}
    
    @PutMapping("/api/workflows/contracts/{contractId}/status")
    public ContractStatusUpdated updateContractStatus(
            @PathVariable Long contractId,
            @Valid @RequestBody UpdateContractStatusRequest request) {
        
        var workflow = repository.findById(contractId)
            .orElseThrow(() -> new AppException(ErrorCode.DATA_NOT_FOUND)
                .withData("contractId", contractId));
        
        try {
            WorkflowStatus newStatus = WorkflowStatus.valueOf(request.status().toUpperCase());
            WorkflowStatus previousStatus = workflow.getStatus();
            
            // Validate status transition
            validateStatusTransition(previousStatus, newStatus, contractId);
            
            // Update workflow status and timestamps
            workflow.setStatus(newStatus);
            LocalDateTime now = LocalDateTime.now();
            
            switch (newStatus) {
                case TEMPLATE_SELECTED -> workflow.setProcessedAt(now);
                case ENVELOPE_CREATED -> workflow.setProcessedAt(now);
                case SENT -> {
                    workflow.setProcessedAt(now);
                    workflow.setSentAt(now);
                }
                case SIGNED -> workflow.setSentAt(workflow.getSentAt() != null ? workflow.getSentAt() : now);
                case COMPLETED -> {
                    workflow.setSentAt(workflow.getSentAt() != null ? workflow.getSentAt() : now);
                    workflow.setCompletedAt(now);
                }
                case ERROR -> workflow.setErrorMessage(request.errorMessage());
                case CANCELLED -> workflow.setCompletedAt(now);
            }
            
            if (request.errorMessage() != null && newStatus == WorkflowStatus.ERROR) {
                workflow.setErrorMessage(request.errorMessage());
            }
            
            repository.save(workflow);
            
            return new ContractStatusUpdated(
                workflow.getId(),
                workflow.getPaymentId(),
                previousStatus.name(),
                newStatus.name(),
                request.reason(),
                now
            );
            
        }
        catch (IllegalArgumentException e) {
            throw new AppException(ErrorCode.VALIDATION_INVALID_INPUT)
                .withData("contractId", contractId)
                .withData("status", request.status())
                .withData("error", "Invalid status value");
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
    
    private void validateStatusTransition(WorkflowStatus current, WorkflowStatus target, Long contractId) {
        // Define allowed transitions
        boolean validTransition = switch (current) {
            case CREATED -> target == WorkflowStatus.TEMPLATE_SELECTED || target == WorkflowStatus.ERROR || target == WorkflowStatus.CANCELLED;
            case TEMPLATE_SELECTED -> target == WorkflowStatus.ENVELOPE_CREATED || target == WorkflowStatus.ERROR || target == WorkflowStatus.CANCELLED;
            case ENVELOPE_CREATED -> target == WorkflowStatus.SENT || target == WorkflowStatus.ERROR || target == WorkflowStatus.CANCELLED;
            case SENT -> target == WorkflowStatus.SIGNED || target == WorkflowStatus.ERROR || target == WorkflowStatus.CANCELLED;
            case SIGNED -> target == WorkflowStatus.COMPLETED || target == WorkflowStatus.ERROR;
            case COMPLETED, CANCELLED -> false; // Terminal states
            case ERROR -> target == WorkflowStatus.CREATED || target == WorkflowStatus.CANCELLED; // Allow retry from error
        };
        
        if (!validTransition) {
            throw new AppException(ErrorCode.BUSINESS_RULE_VIOLATION)
                .withData("contractId", contractId)
                .withData("currentStatus", current.name())
                .withData("targetStatus", target.name())
                .withData("reason", "Invalid status transition");
        }
    }
}
