package de.ferderer.ebicsdocusign.gateway.domain.ws;

import java.time.LocalDateTime;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class PaymentUpdatesBroadcaster {
    
    private final SimpMessagingTemplate messagingTemplate;
    
    public record PaymentUpdateMessage(
        String type,
        Long paymentId,
        String transactionId,
        String status,
        String amount,
        String currency,
        String debtorName,
        Long workflowId,
        String workflowStatus,
        LocalDateTime timestamp,
        Map<String, Object> details
    ) {}
    
    public void broadcastPaymentReceived(Long paymentId, String transactionId, String amount, 
                                       String currency, String debtorName) {
        var message = new PaymentUpdateMessage(
            "PAYMENT_RECEIVED",
            paymentId,
            transactionId,
            "RECEIVED",
            amount,
            currency,
            debtorName,
            null,
            null,
            LocalDateTime.now(),
            Map.of("source", "EBICS_DOWNLOAD")
        );
        
        broadcastToPaymentChannel(message);
        log.debug("Broadcasted payment received: {} - {} {}", transactionId, amount, currency);
    }
    
    public void broadcastPaymentProcessed(Long paymentId, String transactionId, Long workflowId, 
                                        String workflowStatus, String templateName) {
        var message = new PaymentUpdateMessage(
            "PAYMENT_PROCESSED",
            paymentId,
            transactionId,
            "CONTRACT_PENDING",
            null,
            null,
            null,
            workflowId,
            workflowStatus,
            LocalDateTime.now(),
            Map.of("templateName", templateName != null ? templateName : "No template")
        );
        
        broadcastToPaymentChannel(message);
        log.debug("Broadcasted payment processed: {} -> workflow {}", transactionId, workflowId);
    }
    
    public void broadcastWorkflowStatusChange(Long workflowId, Long paymentId, String transactionId,
                                            String oldStatus, String newStatus, String reason) {
        var message = new PaymentUpdateMessage(
            "WORKFLOW_STATUS_CHANGED",
            paymentId,
            transactionId,
            "CONTRACT_PENDING",
            null,
            null,
            null,
            workflowId,
            newStatus,
            LocalDateTime.now(),
            Map.of(
                "previousStatus", oldStatus,
                "reason", reason != null ? reason : "Status updated"
            )
        );
        
        broadcastToPaymentChannel(message);
        log.debug("Broadcasted workflow status change: {} {} -> {}", workflowId, oldStatus, newStatus);
    }
    
    public void broadcastContractCompleted(Long workflowId, Long paymentId, String transactionId,
                                         String clientName, String contractAmount) {
        var message = new PaymentUpdateMessage(
            "CONTRACT_COMPLETED",
            paymentId,
            transactionId,
            "CONTRACT_SIGNED",
            contractAmount,
            null,
            null,
            workflowId,
            "COMPLETED",
            LocalDateTime.now(),
            Map.of("clientName", clientName)
        );
        
        broadcastToPaymentChannel(message);
        log.info("Broadcasted contract completion: workflow {} for payment {}", workflowId, transactionId);
    }
    
    public void broadcastErrorOccurred(Long paymentId, String transactionId, Long workflowId,
                                     String errorType, String errorMessage) {
        var message = new PaymentUpdateMessage(
            "ERROR_OCCURRED",
            paymentId,
            transactionId,
            workflowId != null ? "CONTRACT_PENDING" : "ERROR",
            null,
            null,
            null,
            workflowId,
            "ERROR",
            LocalDateTime.now(),
            Map.of(
                "errorType", errorType,
                "errorMessage", errorMessage
            )
        );
        
        broadcastToPaymentChannel(message);
        log.warn("Broadcasted error: {} - {}", errorType, errorMessage);
    }
    
    private void broadcastToPaymentChannel(PaymentUpdateMessage message) {
        try {
            messagingTemplate.convertAndSend("/topic/payments", message);
            log.trace("Message sent to /topic/payments: {}", message.type());
        }
        catch (MessagingException e) {
            log.error("Failed to broadcast payment update: {}", e.getMessage());
        }
    }
    
    // Method to send system status updates
    public void broadcastSystemStatus(String status, String message, Map<String, Object> metrics) {
        var systemMessage = Map.of(
            "type", "SYSTEM_STATUS",
            "status", status,
            "message", message,
            "metrics", metrics,
            "timestamp", LocalDateTime.now()
        );
        
        try {
            messagingTemplate.convertAndSend("/topic/system", systemMessage);
            log.debug("Broadcasted system status: {}", status);
        }
        catch (MessagingException e) {
            log.error("Failed to broadcast system status: {}", e.getMessage());
        }
    }
}
