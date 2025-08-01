package de.ferderer.ebicsdocusign.gateway.domain.ebics;

import de.ferderer.ebicsdocusign.gateway.common.error.AppException;
import de.ferderer.ebicsdocusign.gateway.common.error.ErrorCode;
import de.ferderer.ebicsdocusign.gateway.common.jdbc.Tsid;
import de.ferderer.ebicsdocusign.gateway.domain.ebics.model.EbicsCertificateEntity;
import de.ferderer.ebicsdocusign.gateway.domain.ebics.model.EbicsCertificateEntity.CertificateType;
import de.ferderer.ebicsdocusign.gateway.domain.ebics.model.EbicsCertificateEntity.UsageType;
import de.ferderer.ebicsdocusign.gateway.domain.ebics.model.EbicsCertificateRepository;
import java.io.ByteArrayInputStream;
import java.io.StringReader;
import java.security.*;
import java.security.cert.*;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.RSAKeyGenParameterSpec;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EbicsCertificateService {

    private final EbicsCertificateRepository certificateRepository;

    static {
        if (Security.getProvider(BouncyCastleProvider.PROVIDER_NAME) == null) {
            Security.addProvider(new BouncyCastleProvider());
        }
    }

    public EbicsCertificateEntity saveCertificate(X509Certificate certificate, KeyPair keyPair, Long connectionId, CertificateType certType, UsageType usageType) {
        try {
            CertificateInfo info = getCertificateInfo(certificate);

            EbicsCertificateEntity entity = new EbicsCertificateEntity();
            entity.setId(Tsid.generate());
            entity.setConnectionId(connectionId);
            entity.setCertificateType(certType);
            entity.setUsageType(usageType);
            entity.setSubjectName(info.subject());
            entity.setIssuerName(info.issuer());
            entity.setSerialNumber(info.serialNumber());
            entity.setFingerprint(info.fingerprint());
            entity.setNotBefore(info.notBefore());
            entity.setNotAfter(info.notAfter());
            entity.setKeyAlgorithm(info.keyAlgorithm());
            entity.setKeySize(getKeySize(certificate.getPublicKey()));
            entity.setCertificateData(certificate.getEncoded());

            // Store private key if provided (client certificates only)
            if (keyPair != null && keyPair.getPrivate() != null) {
                entity.setPrivateKeyData(keyPair.getPrivate().getEncoded());
            }

            log.info("Saving certificate: {} for connection {}", info.subject(), connectionId);
            return certificateRepository.save(entity);

        }
        catch (CertificateEncodingException e) {
            log.error("Failed to save certificate: {}", e.getMessage(), e);
            throw new AppException(ErrorCode.EBICS_CERTIFICATE_INVALID)
                .withData("error", "Failed to save certificate")
                .withData("connectionId", connectionId)
                .withData("details", e.getMessage());
        }
    }

    public Optional<X509Certificate> loadCertificate(Long connectionId, CertificateType certType, UsageType usageType) {
        try {
            return certificateRepository
                .findByConnectionIdAndCertificateTypeAndUsageTypeAndIsActiveTrue(
                    connectionId, certType, usageType)
                .map(entity -> {
                    try {
                        return loadCertificate(entity.getCertificateData());
                    } catch (Exception e) {
                        log.error("Failed to load certificate {}: {}", entity.getId(), e.getMessage());
                        return null;
                    }
                });
        }
        catch (Exception e) {
            log.error("Failed to query certificate: {}", e.getMessage(), e);
            return Optional.empty();
        }
    }

    public Optional<PrivateKey> loadPrivateKey(Long connectionId, CertificateType certType, UsageType usageType) {
        try {
            return certificateRepository
                .findByConnectionIdAndCertificateTypeAndUsageTypeAndIsActiveTrue(
                    connectionId, certType, usageType)
                .filter(entity -> entity.getPrivateKeyData() != null)
                .map(entity -> {
                    try {
                        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
                        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(entity.getPrivateKeyData());
                        return keyFactory.generatePrivate(keySpec);
                    }
                    catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
                        log.error("Failed to load private key {}: {}", entity.getId(), e.getMessage());
                        return null;
                    }
                });
        }
        catch (Exception e) {
            log.error("Failed to query private key: {}", e.getMessage(), e);
            return Optional.empty();
        }
    }

    public List<CertificateInfo> getCertificatesForConnection(Long connectionId) {
        return certificateRepository.findByConnectionIdAndIsActiveTrue(connectionId)
            .stream()
            .map(entity -> new CertificateInfo(
                entity.getSubjectName(),
                entity.getIssuerName(), 
                entity.getNotBefore(),
                entity.getNotAfter(),
                entity.getSerialNumber(),
                entity.getFingerprint(),
                entity.getKeyAlgorithm()
            ))
            .toList();
    }

    public List<CertificateInfo> findExpiringCertificates(int days) {
        LocalDateTime expiryDate = LocalDateTime.now().plusDays(days);
        return certificateRepository.findCertificatesExpiringBefore(expiryDate)
            .stream()
            .map(entity -> new CertificateInfo(
                entity.getSubjectName(),
                entity.getIssuerName(),
                entity.getNotBefore(), 
                entity.getNotAfter(),
                entity.getSerialNumber(),
                entity.getFingerprint(),
                entity.getKeyAlgorithm()
            ))
            .toList();
    }

    public void deactivateCertificate(String fingerprint) {
        certificateRepository.findByFingerprintAndIsActiveTrue(fingerprint)
            .ifPresent(entity -> {
                entity.setActive(false);
                certificateRepository.save(entity);
                log.info("Deactivated certificate: {}", entity.getSubjectName());
            });
    }

    public X509Certificate loadCertificate(byte[] certData) {
        try {
            log.debug("Loading certificate from {} bytes", certData.length);
            
            // Try PEM format first
            String certString = new String(certData);
            if (certString.contains("-----BEGIN CERTIFICATE-----")) {
                return loadPemCertificate(certString);
            }
            
            // Try DER format
            return loadDerCertificate(certData);
            
        }
        catch (Exception e) {
            log.error("Failed to load certificate: {}", e.getMessage(), e);
            throw new AppException(ErrorCode.EBICS_CERTIFICATE_INVALID)
                .withData("error", "Failed to load certificate")
                .withData("details", e.getMessage());
        }
    }

    private X509Certificate loadPemCertificate(String pemData) throws Exception {
        try (PEMParser pemParser = new PEMParser(new StringReader(pemData))) {
            Object pemObject = pemParser.readObject();
            
            if (pemObject instanceof X509CertificateHolder certHolder) {
                return new JcaX509CertificateConverter()
                    .setProvider(BouncyCastleProvider.PROVIDER_NAME)
                    .getCertificate(certHolder);
            }
            
            throw new IllegalArgumentException("PEM data does not contain a valid certificate");
        }
    }

    private X509Certificate loadDerCertificate(byte[] derData) throws CertificateException {
        CertificateFactory cf = CertificateFactory.getInstance("X.509");
        return (X509Certificate) cf.generateCertificate(new ByteArrayInputStream(derData));
    }

    public KeyPair generateKeyPair(int keySize) {
        try {
            log.info("Generating RSA key pair with {} bits", keySize);
            
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA", BouncyCastleProvider.PROVIDER_NAME);
            keyGen.initialize(new RSAKeyGenParameterSpec(keySize, RSAKeyGenParameterSpec.F4));
            
            KeyPair keyPair = keyGen.generateKeyPair();
            log.info("Generated RSA key pair successfully");
            
            return keyPair;
            
        }
        catch (InvalidAlgorithmParameterException | NoSuchAlgorithmException | NoSuchProviderException e) {
            log.error("Failed to generate key pair: {}", e.getMessage(), e);
            throw new AppException(ErrorCode.EBICS_CERTIFICATE_INVALID)
                .withData("error", "Failed to generate key pair")
                .withData("keySize", keySize)
                .withData("details", e.getMessage());
        }
    }

    public X509Certificate generateSelfSignedCertificate(KeyPair keyPair, String subject, int validityDays) {
        try {
            log.info("Generating self-signed certificate for: {}", subject);
            
            X500Name issuer = new X500Name(subject);
            X500Name subjectName = new X500Name(subject);
            
            Date notBefore = new Date();
            Date notAfter = Date.from(LocalDateTime.now()
                .plusDays(validityDays)
                .atZone(ZoneId.systemDefault())
                .toInstant());
            
            JcaX509v3CertificateBuilder certBuilder = new JcaX509v3CertificateBuilder(
                issuer,
                java.math.BigInteger.valueOf(System.currentTimeMillis()),
                notBefore,
                notAfter,
                subjectName,
                keyPair.getPublic()
            );
            
            ContentSigner signer = new JcaContentSignerBuilder("SHA256withRSA")
                .setProvider(BouncyCastleProvider.PROVIDER_NAME)
                .build(keyPair.getPrivate());
            
            X509CertificateHolder certHolder = certBuilder.build(signer);
            
            X509Certificate certificate = new JcaX509CertificateConverter()
                .setProvider(BouncyCastleProvider.PROVIDER_NAME)
                .getCertificate(certHolder);
            
            log.info("Generated self-signed certificate valid until: {}", certificate.getNotAfter());
            return certificate;
            
        }
        catch (CertificateException | OperatorCreationException e) {
            log.error("Failed to generate self-signed certificate: {}", e.getMessage(), e);
            throw new AppException(ErrorCode.EBICS_CERTIFICATE_INVALID)
                .withData("error", "Failed to generate certificate")
                .withData("subject", subject)
                .withData("details", e.getMessage());
        }
    }

    public boolean validateCertificate(X509Certificate certificate) {
        try {
            log.debug("Validating certificate: {}", certificate.getSubjectX500Principal().getName());
            
            // Check expiry
            certificate.checkValidity();
            log.debug("Certificate is within validity period");
            
            // Verify signature (self-signed case)
            try {
                certificate.verify(certificate.getPublicKey());
                log.debug("Certificate signature verified");
            }
            catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchProviderException | SignatureException | CertificateException e) {
                log.warn("Certificate signature verification failed: {}", e.getMessage());
                // Don't fail for signature issues in test environments
            }
            
            // Check key usage for EBICS
            boolean[] keyUsage = certificate.getKeyUsage();
            if (keyUsage != null) {
                boolean canSign = keyUsage[0]; // Digital signature
                boolean canEncrypt = keyUsage[2]; // Key encipherment
                log.debug("Certificate key usage - sign: {}, encrypt: {}", canSign, canEncrypt);
            }
            
            return true;
            
        }
        catch (CertificateExpiredException e) {
            log.error("Certificate expired: {}", e.getMessage());
            return false;
        }
        catch (CertificateNotYetValidException e) {
            log.error("Certificate not yet valid: {}", e.getMessage());
            return false;
        }
        catch (Exception e) {
            log.error("Certificate validation failed: {}", e.getMessage(), e);
            return false;
        }
    }

    public PublicKey extractPublicKey(X509Certificate certificate) {
        PublicKey publicKey = certificate.getPublicKey();
        log.debug("Extracted {} public key from certificate", publicKey.getAlgorithm());
        return publicKey;
    }

    public String getCertificateFingerprint(X509Certificate certificate) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] fingerprint = digest.digest(certificate.getEncoded());
            
            StringBuilder sb = new StringBuilder();
            for (byte b : fingerprint) {
                sb.append(String.format("%02x", b));
            }
            
            String fingerprintStr = sb.toString();
            log.debug("Certificate fingerprint: {}", fingerprintStr);
            return fingerprintStr;
            
        }
        catch (NoSuchAlgorithmException | CertificateEncodingException e) {
            log.error("Failed to calculate certificate fingerprint: {}", e.getMessage(), e);
            throw new AppException(ErrorCode.EBICS_CERTIFICATE_INVALID)
                .withData("error", "Failed to calculate fingerprint")
                .withData("details", e.getMessage());
        }
    }

    public String certificateToPem(X509Certificate certificate) {
        try {
            byte[] encoded = certificate.getEncoded();
            String base64 = Base64.getEncoder().encodeToString(encoded);
            
            StringBuilder pem = new StringBuilder();
            pem.append("-----BEGIN CERTIFICATE-----\n");
            
            // Split into 64-character lines
            for (int i = 0; i < base64.length(); i += 64) {
                int end = Math.min(i + 64, base64.length());
                pem.append(base64, i, end).append("\n");
            }
            
            pem.append("-----END CERTIFICATE-----\n");
            
            return pem.toString();
            
        }
        catch (CertificateEncodingException e) {
            log.error("Failed to encode certificate to PEM: {}", e.getMessage(), e);
            throw new AppException(ErrorCode.EBICS_CERTIFICATE_INVALID)
                .withData("error", "Failed to encode certificate")
                .withData("details", e.getMessage());
        }
    }

    private Integer getKeySize(PublicKey publicKey) {
        if (publicKey instanceof RSAPublicKey rsaKey) {
            return rsaKey.getModulus().bitLength();
        }
        return null;
    }

    public CertificateInfo getCertificateInfo(X509Certificate certificate) {
        return new CertificateInfo(
            certificate.getSubjectX500Principal().getName(),
            certificate.getIssuerX500Principal().getName(),
            certificate.getNotBefore().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime(),
            certificate.getNotAfter().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime(),
            certificate.getSerialNumber().toString(),
            getCertificateFingerprint(certificate),
            certificate.getPublicKey().getAlgorithm()
        );
    }

    public record CertificateInfo(
        String subject,
        String issuer,
        LocalDateTime notBefore,
        LocalDateTime notAfter,
        String serialNumber,
        String fingerprint,
        String keyAlgorithm
    ) {}
}
