package de.ferderer.ebicsdocusign.gateway.domain.ebics;

import de.ferderer.ebicsdocusign.gateway.common.error.AppException;
import de.ferderer.ebicsdocusign.gateway.common.test.IntegrationTestBase;
import de.ferderer.ebicsdocusign.gateway.domain.ebics.EbicsCertificateService.CertificateInfo;
import java.security.KeyPair;
import java.security.PublicKey;
import java.security.cert.X509Certificate;
import java.time.LocalDateTime;
import lombok.extern.slf4j.Slf4j;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
class EbicsCertificateServiceIT extends IntegrationTestBase {

    @Autowired
    private EbicsCertificateService certificateService;

    @Test
    void shouldGenerateRsaKeyPair() {
        // Generate 2048-bit RSA key pair
        KeyPair keyPair = certificateService.generateKeyPair(2048);

        assertNotNull(keyPair, "Key pair should not be null");
        assertNotNull(keyPair.getPrivate(), "Private key should not be null");
        assertNotNull(keyPair.getPublic(), "Public key should not be null");
        
        assertEquals("RSA", keyPair.getPrivate().getAlgorithm());
        assertEquals("RSA", keyPair.getPublic().getAlgorithm());
        
        // Verify key size (approximate check since exact size may vary)
        assertTrue(keyPair.getPrivate().getEncoded().length > 1000, "Private key should be substantial size");
        
        log.info("✓ Generated RSA key pair: private={} bytes, public={} bytes", 
            keyPair.getPrivate().getEncoded().length, 
            keyPair.getPublic().getEncoded().length);
    }

    @Test
    void shouldGenerateSelfSignedCertificate() {
        // Generate key pair first
        KeyPair keyPair = certificateService.generateKeyPair(2048);
        
        // Generate self-signed certificate
        String subject = "CN=EBICS Test Client,OU=IT,O=Test Company,C=DE";
        X509Certificate certificate = certificateService.generateSelfSignedCertificate(
            keyPair, subject, 365);

        assertNotNull(certificate, "Certificate should not be null");
        assertEquals("X.509", certificate.getType());
        assertEquals("RSA", certificate.getPublicKey().getAlgorithm());
        
        // Verify subject
        assertTrue(certificate.getSubjectX500Principal().getName().contains("EBICS Test Client"));
        
        // Verify validity period
        assertTrue(certificate.getNotBefore().before(certificate.getNotAfter()));
        
        // Should be valid now
        assertDoesNotThrow(() -> certificate.checkValidity());
        
        log.info("✓ Generated self-signed certificate: subject={}, valid until={}", 
            certificate.getSubjectX500Principal().getName(), certificate.getNotAfter());
    }

    @Test
    void shouldValidateValidCertificate() {
        // Generate test certificate
        KeyPair keyPair = certificateService.generateKeyPair(2048);
        X509Certificate certificate = certificateService.generateSelfSignedCertificate(
            keyPair, "CN=Valid Test Cert", 365);

        // Validate the certificate
        boolean isValid = certificateService.validateCertificate(certificate);
        
        assertTrue(isValid, "Valid certificate should pass validation");
        
        log.info("✓ Certificate validation passed for valid certificate");
    }

    @Test
    void shouldExtractPublicKeyFromCertificate() {
        // Generate test certificate
        KeyPair keyPair = certificateService.generateKeyPair(2048);
        X509Certificate certificate = certificateService.generateSelfSignedCertificate(
            keyPair, "CN=Public Key Test", 365);

        // Extract public key
        PublicKey extractedKey = certificateService.extractPublicKey(certificate);
        
        assertNotNull(extractedKey, "Extracted public key should not be null");
        assertEquals("RSA", extractedKey.getAlgorithm());
        
        // Should match the original public key
        assertEquals(keyPair.getPublic(), extractedKey);
        
        log.info("✓ Public key extracted successfully: algorithm={}", extractedKey.getAlgorithm());
    }

    @Test
    void shouldCalculateCertificateFingerprint() {
        // Generate test certificate
        KeyPair keyPair = certificateService.generateKeyPair(2048);
        X509Certificate certificate = certificateService.generateSelfSignedCertificate(
            keyPair, "CN=Fingerprint Test", 365);

        // Calculate fingerprint
        String fingerprint = certificateService.getCertificateFingerprint(certificate);
        
        assertNotNull(fingerprint, "Fingerprint should not be null");
        assertEquals(64, fingerprint.length(), "SHA-256 fingerprint should be 64 hex characters");
        assertTrue(fingerprint.matches("[0-9a-f]+"), "Fingerprint should contain only hex characters");
        
        // Should be consistent
        String fingerprint2 = certificateService.getCertificateFingerprint(certificate);
        assertEquals(fingerprint, fingerprint2, "Fingerprint should be consistent");
        
        log.info("✓ Certificate fingerprint calculated: {}", fingerprint);
    }

    @Test
    void shouldConvertCertificateToPem() {
        // Generate test certificate
        KeyPair keyPair = certificateService.generateKeyPair(2048);
        X509Certificate certificate = certificateService.generateSelfSignedCertificate(
            keyPair, "CN=PEM Test", 365);

        // Convert to PEM
        String pem = certificateService.certificateToPem(certificate);
        
        assertNotNull(pem, "PEM string should not be null");
        assertTrue(pem.startsWith("-----BEGIN CERTIFICATE-----"), "Should start with PEM header");
        assertTrue(pem.endsWith("-----END CERTIFICATE-----\n"), "Should end with PEM footer");
        assertTrue(pem.contains("\n"), "Should contain line breaks");
        
        log.info("✓ Certificate converted to PEM format: {} characters", pem.length());
    }

    @Test
    void shouldGetCertificateInfo() {
        // Generate test certificate
        KeyPair keyPair = certificateService.generateKeyPair(2048);
        String subject = "CN=Info Test,OU=Testing,O=EBICS Gateway,C=DE";
        X509Certificate certificate = certificateService.generateSelfSignedCertificate(
            keyPair, subject, 180);

        // Get certificate info
        CertificateInfo info = certificateService.getCertificateInfo(certificate);
        
        assertNotNull(info, "Certificate info should not be null");
        assertTrue(info.subject().contains("Info Test"));
        assertTrue(info.issuer().contains("Info Test")); // Self-signed
        assertNotNull(info.serialNumber());
        assertNotNull(info.fingerprint());
        assertEquals("RSA", info.keyAlgorithm());
        
        // Verify dates
        assertTrue(info.notBefore().isBefore(info.notAfter()));
        assertTrue(info.notAfter().isAfter(LocalDateTime.now()));
        
        log.info("✓ Certificate info extracted: subject={}, expires={}", 
            info.subject(), info.notAfter());
    }

    @Test
    void shouldLoadPemCertificate() {
        // Generate and convert to PEM first
        KeyPair keyPair = certificateService.generateKeyPair(2048);
        X509Certificate originalCert = certificateService.generateSelfSignedCertificate(
            keyPair, "CN=PEM Load Test", 365);
        String pem = certificateService.certificateToPem(originalCert);

        // Load from PEM
        X509Certificate loadedCert = certificateService.loadCertificate(pem.getBytes());
        
        assertNotNull(loadedCert, "Loaded certificate should not be null");
        assertEquals(originalCert.getSubjectX500Principal(), loadedCert.getSubjectX500Principal());
        assertEquals(originalCert.getPublicKey(), loadedCert.getPublicKey());
        assertEquals(originalCert.getSerialNumber(), loadedCert.getSerialNumber());
        
        log.info("✓ PEM certificate loaded successfully");
    }

    @Test
    void shouldLoadDerCertificate() {
        // Generate certificate and get DER encoding
        KeyPair keyPair = certificateService.generateKeyPair(2048);
        X509Certificate originalCert = certificateService.generateSelfSignedCertificate(
            keyPair, "CN=DER Load Test", 365);
        
        byte[] derBytes;
        try {
            derBytes = originalCert.getEncoded();
        } catch (Exception e) {
            fail("Failed to get DER encoding: " + e.getMessage());
            return;
        }

        // Load from DER
        X509Certificate loadedCert = certificateService.loadCertificate(derBytes);
        
        assertNotNull(loadedCert, "Loaded certificate should not be null");
        assertEquals(originalCert.getSubjectX500Principal(), loadedCert.getSubjectX500Principal());
        assertEquals(originalCert.getPublicKey(), loadedCert.getPublicKey());
        assertEquals(originalCert.getSerialNumber(), loadedCert.getSerialNumber());
        
        log.info("✓ DER certificate loaded successfully");
    }

    @Test
    void shouldThrowExceptionForInvalidCertificateData() {
        byte[] invalidData = "This is not a certificate".getBytes();
        
        AppException exception = assertThrows(AppException.class, 
            () -> certificateService.loadCertificate(invalidData));
        
        assertNotNull(exception.getMessage());
        log.info("✓ Invalid certificate data handled correctly: {}", exception.getMessage());
    }

    @Test
    void shouldHandleDifferentKeySizes() {
        // Test different key sizes
        int[] keySizes = {2048, 3072, 4096};
        
        for (int keySize : keySizes) {
            KeyPair keyPair = certificateService.generateKeyPair(keySize);
            
            assertNotNull(keyPair);
            assertEquals("RSA", keyPair.getPrivate().getAlgorithm());
            
            // Generate certificate with this key pair
            X509Certificate cert = certificateService.generateSelfSignedCertificate(
                keyPair, "CN=Key Size Test " + keySize, 365);
            
            assertTrue(certificateService.validateCertificate(cert));
            
            log.info("✓ Key size {} bits validated successfully", keySize);
        }
    }

    @Test
    void shouldCreateEbicsCompatibleCertificate() {
        // Create certificate suitable for EBICS usage
        KeyPair keyPair = certificateService.generateKeyPair(2048);
        String ebicsSubject = "CN=EBICS_CLIENT_001,OU=Banking,O=Test Bank,L=Frankfurt,C=DE";
        
        X509Certificate cert = certificateService.generateSelfSignedCertificate(
            keyPair, ebicsSubject, 1095); // 3 years validity
        
        // Validate EBICS requirements
        assertNotNull(cert);
        assertTrue(cert.getSubjectX500Principal().getName().contains("EBICS_CLIENT_001"));
        assertEquals("RSA", cert.getPublicKey().getAlgorithm());
        
        // Should be valid for a reasonable period
        long validityDays = (cert.getNotAfter().getTime() - cert.getNotBefore().getTime()) / (1000 * 60 * 60 * 24);
        assertTrue(validityDays > 1000, "Certificate should be valid for at least 1000 days");
        
        // Should validate successfully
        assertTrue(certificateService.validateCertificate(cert));
        
        log.info("✓ EBICS-compatible certificate created: valid for {} days", validityDays);
    }
}
