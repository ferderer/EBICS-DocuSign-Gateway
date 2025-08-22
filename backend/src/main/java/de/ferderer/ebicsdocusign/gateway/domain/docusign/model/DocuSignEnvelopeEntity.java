package de.ferderer.ebicsdocusign.gateway.domain.docusign.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;
import lombok.Getter;
import lombok.Setter;

@Entity(name = "DocuSignEnvelope")
@Table(name = "docusign_envelopes")
@Getter
@Setter
public class DocuSignEnvelopeEntity {
    public enum EnvelopeStatus { CREATED, SENT, DELIVERED, SIGNED, COMPLETED, DECLINED, VOIDED }

    @Id
    private Long id;
    
    @Column(nullable = false, unique = true)
    private String envelopeId;
    
    @Column(nullable = false)
    private Long templateId;
    
    @Column(nullable = false)
    private String signerEmail;
    
    @Column(nullable = false)
    private String signerName;
    
    @Enumerated(EnumType.STRING)
    private EnvelopeStatus status = EnvelopeStatus.CREATED;
    
    private Long paymentId;
    
    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime sentAt;
    private LocalDateTime completedAt;
    
    @Override
    public boolean equals(Object obj) {
        return obj instanceof DocuSignEnvelopeEntity other && Objects.equals(id, other.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
