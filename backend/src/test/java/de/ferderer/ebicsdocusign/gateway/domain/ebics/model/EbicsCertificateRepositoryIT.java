package de.ferderer.ebicsdocusign.gateway.domain.ebics.model;

import de.ferderer.ebicsdocusign.gateway.common.test.IntegrationTestBase;
import de.ferderer.ebicsdocusign.gateway.domain.ebics.model.EbicsCertificateEntity.CertificateType;
import de.ferderer.ebicsdocusign.gateway.domain.ebics.model.EbicsCertificateEntity.UsageType;
import jakarta.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
class EbicsCertificateRepositoryIT extends IntegrationTestBase {

    @Autowired
    private EbicsCertificateRepository certificateRepository;

    @Autowired
    private EntityManager entityManager;

    private EbicsCertificateEntity testClientCert;
    private EbicsCertificateEntity testBankCert;
    private static final Long TEST_CONNECTION_ID = 12345L;

    @BeforeEach
    void setUp() {
        // Create test client certificate
        testClientCert = createTestCertificate(
            TEST_CONNECTION_ID,
            CertificateType.CLIENT_CERTIFICATE,
            UsageType.AUTHENTICATION,
            "CN=EBICS_CLIENT_001,OU=Banking,O=Test Bank,C=DE",
            "fingerprint123",
            LocalDateTime.now().plusDays(365)
        );

        // Create test bank certificate
        testBankCert = createTestCertificate(
            TEST_CONNECTION_ID,
            CertificateType.BANK_CERTIFICATE,
            UsageType.ENCRYPTION,
            "CN=Test Bank,OU=Banking,O=Bank Corp,C=DE",
            "bankfingerprint456",
            LocalDateTime.now().plusDays(730)
        );

        certificateRepository.save(testClientCert);
        certificateRepository.save(testBankCert);
        
        entityManager.flush();
        entityManager.clear();
        
        log.info("Test certificates created: client={}, bank={}", testClientCert.getId(), testBankCert.getId());
    }

    @Test
    void shouldSaveAndLoadCertificate() {
        // Create new certificate
        EbicsCertificateEntity cert = createTestCertificate(
            999L,
            CertificateType.CLIENT_CERTIFICATE,
            UsageType.SIGNATURE,
            "CN=New Test Cert",
            "newfingerprint789",
            LocalDateTime.now().plusDays(180)
        );

        // Save certificate
        EbicsCertificateEntity saved = certificateRepository.save(cert);
        assertNotNull(saved.getId());
        assertNotNull(saved.getCreatedAt());
        assertNotNull(saved.getUpdatedAt());

        // Load certificate
        Optional<EbicsCertificateEntity> loaded = certificateRepository.findById(saved.getId());
        assertTrue(loaded.isPresent());
        
        EbicsCertificateEntity loadedCert = loaded.get();
        assertEquals(cert.getSubjectName(), loadedCert.getSubjectName());
        assertEquals(cert.getFingerprint(), loadedCert.getFingerprint());
        assertEquals(cert.getCertificateType(), loadedCert.getCertificateType());
        assertEquals(cert.getUsageType(), loadedCert.getUsageType());

        log.info("✓ Certificate saved and loaded successfully: {}", saved.getId());
    }

    @Test
    void shouldFindByConnectionIdAndActiveStatus() {
        // Find active certificates for connection
        List<EbicsCertificateEntity> activeCerts = certificateRepository
            .findByConnectionIdAndIsActiveTrue(TEST_CONNECTION_ID);

        assertEquals(2, activeCerts.size());
        assertTrue(activeCerts.stream().allMatch(EbicsCertificateEntity::isActive));
        assertTrue(activeCerts.stream().anyMatch(c -> c.getCertificateType() == CertificateType.CLIENT_CERTIFICATE));
        assertTrue(activeCerts.stream().anyMatch(c -> c.getCertificateType() == CertificateType.BANK_CERTIFICATE));

        log.info("✓ Found {} active certificates for connection {}", activeCerts.size(), TEST_CONNECTION_ID);
    }

    @Test
    void shouldFindByConnectionTypeAndUsage() {
        // Find specific certificate by connection, type and usage
        Optional<EbicsCertificateEntity> authCert = certificateRepository
            .findByConnectionIdAndCertificateTypeAndUsageTypeAndIsActiveTrue(
                TEST_CONNECTION_ID, CertificateType.CLIENT_CERTIFICATE, UsageType.AUTHENTICATION);

        assertTrue(authCert.isPresent());
        assertEquals(testClientCert.getId(), authCert.get().getId());
        assertEquals(UsageType.AUTHENTICATION, authCert.get().getUsageType());

        log.info("✓ Found authentication certificate: {}", authCert.get().getSubjectName());
    }

    @Test
    void shouldFindByFingerprint() {
        // Find certificate by fingerprint
        Optional<EbicsCertificateEntity> cert = certificateRepository
            .findByFingerprintAndIsActiveTrue("fingerprint123");

        assertTrue(cert.isPresent());
        assertEquals(testClientCert.getId(), cert.get().getId());
        assertEquals("fingerprint123", cert.get().getFingerprint());

        log.info("✓ Found certificate by fingerprint: {}", cert.get().getSubjectName());
    }

    @Test
    void shouldFindExpiringCertificates() {
        // Create certificate expiring soon
        EbicsCertificateEntity expiringSoon = createTestCertificate(
            TEST_CONNECTION_ID,
            CertificateType.CLIENT_CERTIFICATE,
            UsageType.SIGNATURE,
            "CN=Expiring Soon",
            "expiring123",
            LocalDateTime.now().plusDays(5)
        );
        certificateRepository.save(expiringSoon);

        // Find certificates expiring within 30 days
        LocalDateTime thirtyDaysFromNow = LocalDateTime.now().plusDays(30);
        List<EbicsCertificateEntity> expiringCerts = certificateRepository
            .findCertificatesExpiringBefore(thirtyDaysFromNow);

        assertTrue(expiringCerts.size() >= 1);
        assertTrue(expiringCerts.stream()
            .anyMatch(c -> c.getSubjectName().contains("Expiring Soon")));

        log.info("✓ Found {} certificates expiring within 30 days", expiringCerts.size());
    }

    @Test
    void shouldFindByUsageType() {
        // Find all authentication certificates
        List<EbicsCertificateEntity> authCerts = certificateRepository
            .findByUsageTypeAndIsActiveTrue(UsageType.AUTHENTICATION);

        assertTrue(authCerts.size() >= 1);
        assertTrue(authCerts.stream()
            .allMatch(c -> c.getUsageType() == UsageType.AUTHENTICATION));

        log.info("✓ Found {} authentication certificates", authCerts.size());
    }

    @Test
    void shouldCountActiveCertificatesForConnection() {
        // Count certificates for connection
        long count = certificateRepository.countByConnectionIdAndIsActiveTrue(TEST_CONNECTION_ID);
        
        assertEquals(2, count);

        log.info("✓ Found {} active certificates for connection {}", count, TEST_CONNECTION_ID);
    }

    @Test
    void shouldFindExpiredCertificates() {
        // Create expired certificate
        EbicsCertificateEntity expired = createTestCertificate(
            TEST_CONNECTION_ID,
            CertificateType.CLIENT_CERTIFICATE,
            UsageType.ENCRYPTION,
            "CN=Expired Cert",
            "expired789",
            LocalDateTime.now().minusDays(1)
        );
        certificateRepository.save(expired);

        // Find expired certificates
        List<EbicsCertificateEntity> expiredCerts = certificateRepository
            .findExpiredCertificates(LocalDateTime.now());

        assertTrue(expiredCerts.size() >= 1);
        assertTrue(expiredCerts.stream()
            .anyMatch(c -> c.getSubjectName().contains("Expired Cert")));

        log.info("✓ Found {} expired certificates", expiredCerts.size());
    }

    @Test
    void shouldFindBySubjectNamePattern() {
        // Find certificates by subject name pattern
        List<EbicsCertificateEntity> ebicsCerts = certificateRepository
            .findBySubjectNameContaining("EBICS");

        assertTrue(ebicsCerts.size() >= 1);
        assertTrue(ebicsCerts.stream()
            .allMatch(c -> c.getSubjectName().contains("EBICS")));

        log.info("✓ Found {} certificates with 'EBICS' in subject", ebicsCerts.size());
    }

    @Test
    void shouldHandleInactiveCertificates() {
        // Mark certificate as inactive
        testClientCert.setActive(false);
        certificateRepository.save(testClientCert);

        // Should not find inactive certificate
        List<EbicsCertificateEntity> activeCerts = certificateRepository
            .findByConnectionIdAndIsActiveTrue(TEST_CONNECTION_ID);

        assertEquals(1, activeCerts.size()); // Only bank cert should remain
        assertTrue(activeCerts.stream()
            .noneMatch(c -> c.getId().equals(testClientCert.getId())));

        log.info("✓ Inactive certificates properly filtered out");
    }

    @Test
    @Disabled
    void shouldUpdateTimestampsOnModification() {
        // Get original timestamps
        LocalDateTime originalCreated = testClientCert.getCreatedAt();
        LocalDateTime originalUpdated = testClientCert.getUpdatedAt();

        // Wait a bit and update
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        testClientCert.setSubjectName("CN=Updated Subject");
        certificateRepository.save(testClientCert);

        // Verify timestamps
        Optional<EbicsCertificateEntity> updated = certificateRepository.findById(testClientCert.getId());
        assertTrue(updated.isPresent());

        assertEquals(originalCreated, updated.get().getCreatedAt());
        assertTrue(updated.get().getUpdatedAt().isAfter(originalUpdated));

        log.info("✓ Timestamps updated correctly on modification");
    }

    @Test
    void shouldHandleLobData() {
        // Create certificate with large binary data
        byte[] largeCertData = new byte[8192];
        byte[] largeKeyData = new byte[4096];
        
        // Fill with test data
        for (int i = 0; i < largeCertData.length; i++) {
            largeCertData[i] = (byte) (i % 256);
        }
        for (int i = 0; i < largeKeyData.length; i++) {
            largeKeyData[i] = (byte) ((i * 2) % 256);
        }

        EbicsCertificateEntity cert = createTestCertificate(
            TEST_CONNECTION_ID,
            CertificateType.CLIENT_CERTIFICATE,
            UsageType.SIGNATURE,
            "CN=Large Data Test",
            "largedata123",
            LocalDateTime.now().plusDays(365)
        );
        cert.setCertificateData(largeCertData);
        cert.setPrivateKeyData(largeKeyData);

        // Save and reload
        EbicsCertificateEntity saved = certificateRepository.save(cert);
        entityManager.flush();
        entityManager.clear();

        Optional<EbicsCertificateEntity> loaded = certificateRepository.findById(saved.getId());
        assertTrue(loaded.isPresent());

        assertArrayEquals(largeCertData, loaded.get().getCertificateData());
        assertArrayEquals(largeKeyData, loaded.get().getPrivateKeyData());

        log.info("✓ Large binary data (cert: {} bytes, key: {} bytes) handled correctly",
            largeCertData.length, largeKeyData.length);
    }

    private EbicsCertificateEntity createTestCertificate(
            Long connectionId,
            CertificateType certType,
            UsageType usageType,
            String subjectName,
            String fingerprint,
            LocalDateTime notAfter) {
        
        EbicsCertificateEntity cert = new EbicsCertificateEntity();
        cert.setId(System.currentTimeMillis() + (long) (Math.random() * 1000)); // Simple ID generation for tests
        cert.setConnectionId(connectionId);
        cert.setCertificateType(certType);
        cert.setUsageType(usageType);
        cert.setSubjectName(subjectName);
        cert.setIssuerName(subjectName); // Self-signed for tests
        cert.setSerialNumber("123456789" + System.currentTimeMillis());
        cert.setFingerprint(fingerprint);
        cert.setNotBefore(LocalDateTime.now().minusDays(1));
        cert.setNotAfter(notAfter);
        cert.setKeyAlgorithm("RSA");
        cert.setKeySize(2048);
        cert.setCertificateData("dummy certificate data".getBytes());
        cert.setPrivateKeyData("dummy private key data".getBytes());
        cert.setActive(true);
        
        return cert;
    }
}
