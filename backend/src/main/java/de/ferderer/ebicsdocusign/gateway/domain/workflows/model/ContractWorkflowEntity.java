package de.ferderer.ebicsdocusign.gateway.domain.workflows.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;
import lombok.Getter;
import lombok.Setter;

@Entity(name = "ContractWorkflow")
@Table(name = "contract_workflows")
@Getter
@Setter
public class ContractWorkflowEntity {
    public enum WorkflowStatus {
        CREATED, TEMPLATE_SELECTED, ENVELOPE_CREATED, SENT, SIGNED, COMPLETED, ERROR, CANCELLED
    }

    @Id
    private Long id;
    
    @Column(nullable = false)
    private Long paymentId;
    
    private Long templateId;
    private Long envelopeId;
    
    @Enumerated(EnumType.STRING)
    private WorkflowStatus status = WorkflowStatus.CREATED;
    
    @Column(nullable = false)
    private String contractAmount;
    
    @Column(nullable = false)
    private String contractCurrency;
    
    @Column(nullable = false)
    private String clientName;
    
    @Column(nullable = false)
    private String clientEmail;
    
    private String projectDescription;
    private String contractReference;
    
    @Column(columnDefinition = "TEXT")
    private String errorMessage;
    
    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime processedAt;
    private LocalDateTime sentAt;
    private LocalDateTime completedAt;
    
    @Override
    public boolean equals(Object obj) {
        return obj instanceof ContractWorkflowEntity other && Objects.equals(id, other.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
