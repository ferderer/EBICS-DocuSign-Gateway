package de.ferderer.ebicsdocusign.gateway.domain.ebics.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;
import lombok.Getter;
import lombok.Setter;

@Entity(name = "EbicsConnection")
@Table(name = "ebics_connections")
@Getter
@Setter
public class EbicsConnectionEntity {
    
    @Id
    private Long id;
    
    @Column(nullable = false)
    private String bankName;
    
    @Column(nullable = false, unique = true)
    private String hostId;
    
    @Column(nullable = false)
    private String partnerId;
    
    @Column(nullable = false)
    private String userId;
    
    @Column(nullable = false)
    private String bankUrl;
    
    @Enumerated(EnumType.STRING)
    private EbicsVersion version = EbicsVersion.H004;
    
    @Enumerated(EnumType.STRING)
    private ConnectionStatus status = ConnectionStatus.INACTIVE;
    
    private LocalDateTime lastConnected;
    private LocalDateTime createdAt = LocalDateTime.now();
    
    @Override
    public boolean equals(Object obj) {
        return obj instanceof EbicsConnectionEntity other && Objects.equals(id, other.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
