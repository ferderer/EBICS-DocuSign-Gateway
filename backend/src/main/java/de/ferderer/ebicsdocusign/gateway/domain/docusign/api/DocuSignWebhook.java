package de.ferderer.ebicsdocusign.gateway.domain.docusign.api;

import de.ferderer.ebicsdocusign.gateway.domain.docusign.model.DocuSignEnvelopeEntity.EnvelopeStatus;
import de.ferderer.ebicsdocusign.gateway.domain.docusign.model.DocuSignEnvelopeRepository;
import de.ferderer.ebicsdocusign.gateway.domain.docusign.model.WebhookResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class DocuSignWebhook {
    
    private final DocuSignEnvelopeRepository repository;
    
    public record DocuSignWebhookRequest(
        @NotBlank String event,
        @NotBlank String envelopeId,
        String status,
        String signerEmail,
        LocalDateTime eventDateTime,
        List<RecipientStatus> recipients
    ) {}
    
    public record RecipientStatus(
        String email,
        String name,
        String status,
        LocalDateTime statusDateTime
    ) {}
    
    @PostMapping("/api/docusign/webhook")
    public WebhookResponse handleWebhook(@Valid @RequestBody DocuSignWebhookRequest request) {
        log.info("Received DocuSign webhook: event={}, envelopeId={}, status={}", 
                 request.event(), request.envelopeId(), request.status());
        
        try {
            var envelope = repository.findByEnvelopeId(request.envelopeId());
            
            if (envelope.isPresent()) {
                var env = envelope.get();
                
                // Update envelope status based on webhook event
                switch (request.event().toLowerCase()) {
                    case "envelope-sent" -> {
                        env.setStatus(EnvelopeStatus.SENT);
                        env.setSentAt(request.eventDateTime());
                    }
                    case "envelope-delivered" -> {
                        env.setStatus(EnvelopeStatus.DELIVERED);
                    }
                    case "envelope-signed" -> {
                        env.setStatus(EnvelopeStatus.SIGNED);
                    }
                    case "envelope-completed" -> {
                        env.setStatus(EnvelopeStatus.COMPLETED);
                        env.setCompletedAt(request.eventDateTime());
                    }
                    case "envelope-declined" -> {
                        env.setStatus(EnvelopeStatus.DECLINED);
                    }
                    case "envelope-voided" -> {
                        env.setStatus(EnvelopeStatus.VOIDED);
                    }
                    default -> log.warn("Unknown webhook event: {}", request.event());
                }
                
                repository.save(env);
                
                return new WebhookResponse(
                    "SUCCESS", 
                    "Envelope status updated",
                    request.envelopeId(),
                    env.getStatus().name(),
                    LocalDateTime.now()
                );
            } else {
                log.warn("Envelope not found for webhook: {}", request.envelopeId());
                return new WebhookResponse(
                    "NOT_FOUND",
                    "Envelope not found",
                    request.envelopeId(),
                    null,
                    LocalDateTime.now()
                );
            }
            
        } catch (Exception e) {
            log.error("Error processing webhook for envelope {}: {}", request.envelopeId(), e.getMessage());
            return new WebhookResponse(
                "ERROR",
                "Error processing webhook: " + e.getMessage(),
                request.envelopeId(),
                null,
                LocalDateTime.now()
            );
        }
    }
}
