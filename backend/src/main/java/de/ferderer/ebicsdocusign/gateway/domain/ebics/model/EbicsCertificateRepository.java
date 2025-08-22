package de.ferderer.ebicsdocusign.gateway.domain.ebics.model;

import de.ferderer.ebicsdocusign.gateway.domain.ebics.model.EbicsCertificateEntity.CertificateType;
import de.ferderer.ebicsdocusign.gateway.domain.ebics.model.EbicsCertificateEntity.UsageType;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface EbicsCertificateRepository extends JpaRepository<EbicsCertificateEntity, Long> {

    /**
     * Find active certificates by connection ID
     */
    List<EbicsCertificateEntity> findByConnectionIdAndIsActiveTrue(Long connectionId);

    /**
     * Find certificate by connection, type and usage
     */
    Optional<EbicsCertificateEntity> findByConnectionIdAndCertificateTypeAndUsageTypeAndIsActiveTrue(
        Long connectionId, CertificateType certificateType, UsageType usageType);

    /**
     * Find all active client certificates for a connection
     */
    List<EbicsCertificateEntity> findByConnectionIdAndCertificateTypeAndIsActiveTrue(
        Long connectionId, CertificateType certificateType);

    /**
     * Find certificate by fingerprint
     */
    Optional<EbicsCertificateEntity> findByFingerprintAndIsActiveTrue(String fingerprint);

    /**
     * Find certificates expiring within specified days
     */
    @Query("SELECT c FROM EbicsCertificate c WHERE c.isActive = true AND c.notAfter <= :expiryDate")
    List<EbicsCertificateEntity> findCertificatesExpiringBefore(@Param("expiryDate") LocalDateTime expiryDate);

    /**
     * Find all active certificates of specific usage type
     */
    List<EbicsCertificateEntity> findByUsageTypeAndIsActiveTrue(UsageType usageType);

    /**
     * Find certificates by serial number
     */
    List<EbicsCertificateEntity> findBySerialNumberAndIsActiveTrue(String serialNumber);

    /**
     * Count active certificates for connection
     */
    long countByConnectionIdAndIsActiveTrue(Long connectionId);

    /**
     * Find expired certificates
     */
    @Query("SELECT c FROM EbicsCertificate c WHERE c.isActive = true AND c.notAfter < :now")
    List<EbicsCertificateEntity> findExpiredCertificates(@Param("now") LocalDateTime now);

    /**
     * Find certificates by subject name pattern
     */
    @Query("SELECT c FROM EbicsCertificate c WHERE c.isActive = true AND c.subjectName LIKE %:pattern%")
    List<EbicsCertificateEntity> findBySubjectNameContaining(@Param("pattern") String pattern);

    /**
     * Deactivate all certificates for a connection
     */
    @Query("UPDATE EbicsCertificate c SET c.isActive = false, c.updatedAt = :now WHERE c.connectionId = :connectionId")
    void deactivateAllForConnection(@Param("connectionId") Long connectionId, @Param("now") LocalDateTime now);
}
