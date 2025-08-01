package de.ferderer.ebicsdocusign.gateway.domain.docusign.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;
import lombok.Getter;
import lombok.Setter;

@Entity(name = "DocuSignTemplate")
@Table(name = "docusign_templates")
@Getter
@Setter
public class DocuSignTemplateEntity {
    public enum TemplateStatus { ACTIVE, INACTIVE, DRAFT }

    @Id
    private Long id;
    
    @Column(nullable = false, unique = true)
    private String templateId;
    
    @Column(nullable = false)
    private String templateName;
    
    private String description;
    
    @Enumerated(EnumType.STRING)
    private TemplateStatus status = TemplateStatus.ACTIVE;
    
    @Column(nullable = false)
    private String documentName;
    
    private Integer pageCount;
    
    @Column(columnDefinition = "TEXT")
    private String fieldMappings;
    
    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime lastUsed;
    
    @Override
    public boolean equals(Object obj) {
        return obj instanceof DocuSignTemplateEntity other && Objects.equals(id, other.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
