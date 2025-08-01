package de.ferderer.ebicsdocusign.gateway.domain.ebics.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;
import lombok.Getter;
import lombok.Setter;

@Entity(name = "EbicsCertificate")
@Table(name = "ebics_certificates")
@Getter
@Setter
public class EbicsCertificateEntity {

    @Id
    private Long id;

    @Column(name = "connection_id")
    private Long connectionId;

    @Enumerated(EnumType.STRING)
    @Column(name = "certificate_type", nullable = false)
    private CertificateType certificateType;

    @Enumerated(EnumType.STRING)
    @Column(name = "usage_type", nullable = false)
    private UsageType usageType;

    @Column(name = "subject_name", nullable = false, length = 500)
    private String subjectName;

    @Column(name = "issuer_name", nullable = false, length = 500)
    private String issuerName;

    @Column(name = "serial_number", nullable = false, length = 100)
    private String serialNumber;

    @Column(name = "fingerprint", nullable = false, length = 64)
    private String fingerprint;

    @Column(name = "not_before", nullable = false)
    private LocalDateTime notBefore;

    @Column(name = "not_after", nullable = false)
    private LocalDateTime notAfter;

    @Column(name = "key_algorithm", nullable = false, length = 50)
    private String keyAlgorithm;

    @Column(name = "key_size")
    private Integer keySize;

    @Lob
    @Column(name = "certificate_data", nullable = false)
    private byte[] certificateData;

    @Lob
    @Column(name = "private_key_data")
    private byte[] privateKeyData;

    @Column(name = "is_active")
    private boolean isActive = true;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    @PrePersist
    void onPrePersist() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    void onPreUpdate() {
        updatedAt = LocalDateTime.now();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof EbicsCertificateEntity other && Objects.equals(id, other.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public enum CertificateType {
        CLIENT_CERTIFICATE,
        BANK_CERTIFICATE,
        CA_CERTIFICATE
    }

    public enum UsageType {
        AUTHENTICATION,  // A005 - EBICS authentication
        ENCRYPTION,      // E002 - Data encryption
        SIGNATURE,       // X002 - Digital signatures
        ROOT_CA,         // Root certificate authority
        INTERMEDIATE_CA  // Intermediate certificate authority
    }
}
