CREATE TABLE ebics_certificates (
    id BIGINT PRIMARY KEY,
    connection_id BIGINT,
    certificate_type VARCHAR(50) NOT NULL CHECK (certificate_type IN ('CLIENT_CERTIFICATE', 'BANK_CERTIFICATE', 'CA_CERTIFICATE')),
    usage_type VARCHAR(50) NOT NULL CHECK (usage_type IN ('AUTHENTICATION', 'ENCRYPTION', 'SIGNATURE', 'ROOT_CA', 'INTERMEDIATE_CA')),
    subject_name VARCHAR(500) NOT NULL,
    issuer_name VARCHAR(500) NOT NULL,
    serial_number VARCHAR(100) NOT NULL,
    fingerprint VARCHAR(100) NOT NULL,
    not_before DATETIME2 NOT NULL,
    not_after DATETIME2 NOT NULL,
    key_algorithm VARCHAR(50) NOT NULL,
    key_size INTEGER,
    certificate_data VARBINARY(MAX) NOT NULL,
    private_key_data VARBINARY(MAX),
    is_active BIT NOT NULL DEFAULT 1,
    created_at DATETIME2 NOT NULL DEFAULT GETDATE(),
    updated_at DATETIME2
);

-- Create indexes for common query patterns
CREATE INDEX IX_ebics_certificates_connection_active ON ebics_certificates(connection_id, is_active);
CREATE INDEX IX_ebics_certificates_type_usage_active ON ebics_certificates(certificate_type, usage_type, is_active);
CREATE UNIQUE INDEX IX_ebics_certificates_fingerprint_active ON ebics_certificates(fingerprint, is_active) WHERE is_active = 1;
CREATE INDEX IX_ebics_certificates_expiry ON ebics_certificates(not_after, is_active);
CREATE INDEX IX_ebics_certificates_serial ON ebics_certificates(serial_number, is_active);
CREATE INDEX IX_ebics_certificates_subject ON ebics_certificates(subject_name);

-- Add extended properties for documentation
EXEC sp_addextendedproperty 
    @name = N'MS_Description', 
    @value = N'Storage for EBICS client and bank certificates with metadata and private keys',
    @level0type = N'SCHEMA', @level0name = N'dbo',
    @level1type = N'TABLE', @level1name = N'ebics_certificates';

EXEC sp_addextendedproperty 
    @name = N'MS_Description', 
    @value = N'Type of certificate: CLIENT_CERTIFICATE, BANK_CERTIFICATE, CA_CERTIFICATE',
    @level0type = N'SCHEMA', @level0name = N'dbo',
    @level1type = N'TABLE', @level1name = N'ebics_certificates',
    @level2type = N'COLUMN', @level2name = N'certificate_type';

EXEC sp_addextendedproperty 
    @name = N'MS_Description', 
    @value = N'Certificate usage: AUTHENTICATION (A005), ENCRYPTION (E002), SIGNATURE (X002), ROOT_CA, INTERMEDIATE_CA',
    @level0type = N'SCHEMA', @level0name = N'dbo',
    @level1type = N'TABLE', @level1name = N'ebics_certificates',
    @level2type = N'COLUMN', @level2name = N'usage_type';

EXEC sp_addextendedproperty 
    @name = N'MS_Description', 
    @value = N'SHA-256 fingerprint of the certificate in lowercase hex',
    @level0type = N'SCHEMA', @level0name = N'dbo',
    @level1type = N'TABLE', @level1name = N'ebics_certificates',
    @level2type = N'COLUMN', @level2name = N'fingerprint';

EXEC sp_addextendedproperty 
    @name = N'MS_Description', 
    @value = N'Certificate in DER format',
    @level0type = N'SCHEMA', @level0name = N'dbo',
    @level1type = N'TABLE', @level1name = N'ebics_certificates',
    @level2type = N'COLUMN', @level2name = N'certificate_data';

EXEC sp_addextendedproperty 
    @name = N'MS_Description', 
    @value = N'Private key in PKCS#8 format (only for client certificates)',
    @level0type = N'SCHEMA', @level0name = N'dbo',
    @level1type = N'TABLE', @level1name = N'ebics_certificates',
    @level2type = N'COLUMN', @level2name = N'private_key_data';

EXEC sp_addextendedproperty 
    @name = N'MS_Description', 
    @value = N'Soft delete flag - 0 means certificate is deactivated',
    @level0type = N'SCHEMA', @level0name = N'dbo',
    @level1type = N'TABLE', @level1name = N'ebics_certificates',
    @level2type = N'COLUMN', @level2name = N'is_active';
