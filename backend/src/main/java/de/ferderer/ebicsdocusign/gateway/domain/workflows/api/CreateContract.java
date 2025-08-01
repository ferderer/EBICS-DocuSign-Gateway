package de.ferderer.ebicsdocusign.gateway.domain.workflows.api;

import de.ferderer.ebicsdocusign.gateway.common.error.AppException;
import de.ferderer.ebicsdocusign.gateway.common.error.ErrorCode;
import de.ferderer.ebicsdocusign.gateway.domain.payments.model.PaymentRepository;
import de.ferderer.ebicsdocusign.gateway.domain.workflows.model.ContractCreated;
import de.ferderer.ebicsdocusign.gateway.domain.workflows.model.ContractWorkflowEntity;
import de.ferderer.ebicsdocusign.gateway.domain.workflows.model.ContractWorkflowEntity.WorkflowStatus;
import de.ferderer.ebicsdocusign.gateway.domain.workflows.model.ContractWorkflowRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class CreateContract {
    
    private final ContractWorkflowRepository workflowRepository;
    private final PaymentRepository paymentRepository;
    
    public record CreateContractRequest(
        @NotNull Long paymentId,
        @NotBlank @Email String clientEmail,
        @NotBlank String clientName,
        String projectDescription,
        String contractReference,
        Long templateId
    ) {}
    
    @PostMapping("/api/workflows/contracts/create")
    public ContractCreated createContract(@Valid @RequestBody CreateContractRequest request) {
        // Verify payment exists
        var payment = paymentRepository.findById(request.paymentId())
            .orElseThrow(() -> new AppException(ErrorCode.DATA_NOT_FOUND)
                .withData("paymentId", request.paymentId()));
        
        // Check if workflow already exists for this payment
        if (workflowRepository.existsByPaymentId(request.paymentId())) {
            throw new AppException(ErrorCode.BUSINESS_RULE_VIOLATION)
                .withData("paymentId", request.paymentId())
                .withData("reason", "Contract workflow already exists for this payment");
        }
        
        try {
            // Create new contract workflow
            var workflow = new ContractWorkflowEntity();
            workflow.setId(System.currentTimeMillis()); // Temporary ID generation
            workflow.setPaymentId(request.paymentId());
            workflow.setTemplateId(request.templateId());
            workflow.setStatus(WorkflowStatus.CREATED);
            workflow.setContractAmount(payment.getAmount());
            workflow.setContractCurrency(payment.getCurrency());
            workflow.setClientName(request.clientName());
            workflow.setClientEmail(request.clientEmail());
            workflow.setProjectDescription(request.projectDescription());
            workflow.setContractReference(request.contractReference());
            workflow.setCreatedAt(LocalDateTime.now());
            
            workflowRepository.save(workflow);
            
            // TODO: Trigger automated workflow processing
            processWorkflowAutomatically(workflow);
            
            return new ContractCreated(
                workflow.getId(),
                workflow.getPaymentId(),
                payment.getTransactionId(),
                workflow.getStatus().name(),
                workflow.getContractAmount(),
                workflow.getContractCurrency(),
                workflow.getClientName(),
                workflow.getClientEmail(),
                workflow.getCreatedAt()
            );
            
        }
        catch (AppException e) {
            throw e;
        }
        catch (Exception e) {
            throw new AppException(ErrorCode.BUSINESS_OPERATION_NOT_ALLOWED)
                .withData("paymentId", request.paymentId())
                .withData("error", e.getMessage());
        }
    }
    
    private void processWorkflowAutomatically(ContractWorkflowEntity workflow) {
        // Placeholder for automated workflow processing
        // Will be implemented in ProcessPaymentManual endpoint
        if (workflow.getTemplateId() != null) {
            workflow.setStatus(WorkflowStatus.TEMPLATE_SELECTED);
            workflow.setProcessedAt(LocalDateTime.now());
            workflowRepository.save(workflow);
        }
    }
}
