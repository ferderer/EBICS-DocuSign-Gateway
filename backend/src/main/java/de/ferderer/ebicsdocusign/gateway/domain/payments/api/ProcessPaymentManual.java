package de.ferderer.ebicsdocusign.gateway.domain.payments.api;

import de.ferderer.ebicsdocusign.gateway.common.error.AppException;
import de.ferderer.ebicsdocusign.gateway.common.error.ErrorCode;
import de.ferderer.ebicsdocusign.gateway.domain.docusign.model.DocuSignTemplateRepository;
import de.ferderer.ebicsdocusign.gateway.domain.payments.model.PaymentEntity.PaymentStatus;
import de.ferderer.ebicsdocusign.gateway.domain.payments.model.PaymentProcessed;
import de.ferderer.ebicsdocusign.gateway.domain.payments.model.PaymentRepository;
import de.ferderer.ebicsdocusign.gateway.domain.workflows.model.ContractWorkflowEntity;
import de.ferderer.ebicsdocusign.gateway.domain.workflows.model.ContractWorkflowEntity.WorkflowStatus;
import de.ferderer.ebicsdocusign.gateway.domain.workflows.model.ContractWorkflowRepository;
import de.ferderer.ebicsdocusign.gateway.domain.ws.PaymentUpdatesBroadcaster;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import java.math.BigDecimal;
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
public class ProcessPaymentManual {
    
    private final PaymentRepository paymentRepository;
    private final ContractWorkflowRepository workflowRepository;
    private final DocuSignTemplateRepository templateRepository;
    private final PaymentUpdatesBroadcaster broadcaster;
    
    public record ProcessPaymentRequest(
        Long templateId,
        @Email String clientEmail,
        String clientName,
        String projectDescription,
        String contractReference,
        boolean skipAmountValidation
    ) {}
    
    @PostMapping("/api/payments/{paymentId}/process")
    public PaymentProcessed processPayment(
            @PathVariable Long paymentId,
            @Valid @RequestBody ProcessPaymentRequest request) {
        
        var payment = paymentRepository.findById(paymentId)
            .orElseThrow(() -> new AppException(ErrorCode.DATA_NOT_FOUND)
                .withData("paymentId", paymentId));
        
        // Check if payment is already processed
        if (payment.getStatus() != PaymentStatus.RECEIVED) {
            throw new AppException(ErrorCode.BUSINESS_RULE_VIOLATION)
                .withData("paymentId", paymentId)
                .withData("currentStatus", payment.getStatus().name())
                .withData("reason", "Payment is not in RECEIVED status");
        }
        
        // Check if workflow already exists
        if (workflowRepository.existsByPaymentId(paymentId)) {
            throw new AppException(ErrorCode.BUSINESS_RULE_VIOLATION)
                .withData("paymentId", paymentId)
                .withData("reason", "Contract workflow already exists for this payment");
        }
        
        try {
            // Validate template if provided
            var template = validateAndGetTemplate(request.templateId());
            
            // Validate payment amount against business rules
            if (!request.skipAmountValidation()) {
                validatePaymentAmount(payment.getAmount(), payment.getCurrency());
            }
            
            // Create contract workflow
            var workflow = createContractWorkflow(payment, request, template);
            
            // Update payment status
            payment.setStatus(PaymentStatus.CONTRACT_PENDING);
            paymentRepository.save(payment);
            
            // Start automated workflow processing
            processWorkflowSteps(workflow);
            
            log.info("Manually triggered contract workflow {} for payment {} ({})", 
                     workflow.getId(), paymentId, payment.getAmount() + " " + payment.getCurrency());

            broadcaster.broadcastPaymentProcessed(
                payment.getId(),
                payment.getTransactionId(), 
                workflow.getId(),
                workflow.getStatus().name(),
                template != null ? template.getTemplateName() : null
            );            

            return new PaymentProcessed(
                payment.getId(),
                payment.getTransactionId(),
                payment.getAmount(),
                payment.getCurrency(),
                payment.getDebtorName(),
                workflow.getId(),
                workflow.getStatus().name(),
                template != null ? template.getTemplateName() : null,
                workflow.getClientEmail(),
                LocalDateTime.now()
            );
            
        }
        catch (AppException e) {
            throw e;
        }
        catch (Exception e) {
            // Set payment to error status if processing fails
            payment.setStatus(PaymentStatus.ERROR);
            paymentRepository.save(payment);
            
            throw new AppException(ErrorCode.BUSINESS_OPERATION_NOT_ALLOWED)
                .withData("paymentId", paymentId)
                .withData("error", e.getMessage());
        }
    }
    
    private de.ferderer.ebicsdocusign.gateway.domain.docusign.model.DocuSignTemplateEntity validateAndGetTemplate(Long templateId) {
        if (templateId == null) {
            return null;
        }
        
        return templateRepository.findById(templateId)
            .orElseThrow(() -> new AppException(ErrorCode.DATA_NOT_FOUND)
                .withData("templateId", templateId));
    }
    
    private void validatePaymentAmount(String amount, String currency) {
        try {
            BigDecimal paymentAmount = new BigDecimal(amount);
            
            // Business rule: Minimum contract amount
            if (paymentAmount.compareTo(new BigDecimal("1000.00")) < 0) {
                throw new AppException(ErrorCode.BUSINESS_RULE_VIOLATION)
                    .withData("amount", amount)
                    .withData("currency", currency)
                    .withData("reason", "Payment amount below minimum contract threshold (€1000)");
            }
            
            // Business rule: Maximum automatic processing amount
            if (paymentAmount.compareTo(new BigDecimal("50000.00")) > 0) {
                throw new AppException(ErrorCode.BUSINESS_RULE_VIOLATION)
                    .withData("amount", amount)
                    .withData("currency", currency)
                    .withData("reason", "Payment amount exceeds automatic processing limit (€50000)");
            }
            
        } catch (NumberFormatException e) {
            throw new AppException(ErrorCode.VALIDATION_INVALID_INPUT)
                .withData("amount", amount)
                .withData("error", "Invalid amount format");
        }
    }
    
    private ContractWorkflowEntity createContractWorkflow(
            de.ferderer.ebicsdocusign.gateway.domain.payments.model.PaymentEntity payment,
            ProcessPaymentRequest request,
            de.ferderer.ebicsdocusign.gateway.domain.docusign.model.DocuSignTemplateEntity template) {
        
        var workflow = new ContractWorkflowEntity();
        workflow.setId(System.currentTimeMillis()); // Temporary ID generation
        workflow.setPaymentId(payment.getId());
        workflow.setTemplateId(template != null ? template.getId() : null);
        workflow.setStatus(template != null ? WorkflowStatus.TEMPLATE_SELECTED : WorkflowStatus.CREATED);
        workflow.setContractAmount(payment.getAmount());
        workflow.setContractCurrency(payment.getCurrency());
        
        // Use provided client details or extract from payment
        workflow.setClientName(request.clientName() != null ? request.clientName() : payment.getDebtorName());
        workflow.setClientEmail(request.clientEmail());
        workflow.setProjectDescription(request.projectDescription() != null ? 
                                     request.projectDescription() : payment.getRemittanceInfo());
        workflow.setContractReference(request.contractReference());
        
        workflow.setCreatedAt(LocalDateTime.now());
        if (template != null) {
            workflow.setProcessedAt(LocalDateTime.now());
        }
        
        return workflowRepository.save(workflow);
    }
    
    private void processWorkflowSteps(ContractWorkflowEntity workflow) {
        // TODO: Implement automated workflow processing
        // This will integrate with DocuSign API calls when implemented on Day 3
        
        if (workflow.getStatus() == WorkflowStatus.TEMPLATE_SELECTED) {
            // Would trigger envelope creation
            log.info("Would create DocuSign envelope for workflow {}", workflow.getId());
        }
        
        // For now, just advance one step to demonstrate progression
        if (workflow.getTemplateId() != null && workflow.getStatus() == WorkflowStatus.TEMPLATE_SELECTED) {
            workflow.setStatus(WorkflowStatus.ENVELOPE_CREATED);
            workflowRepository.save(workflow);
            log.info("Advanced workflow {} to ENVELOPE_CREATED status", workflow.getId());
        }
    }
}
