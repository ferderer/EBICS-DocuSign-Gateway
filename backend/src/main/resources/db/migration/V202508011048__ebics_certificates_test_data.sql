-- Insert test data for EBICS certificates table

-- Sample certificate data for testing (base64 encoded dummy certificates)
DECLARE @client_cert_data NVARCHAR(MAX) = 'LS0tLS1CRUdJTiBDRVJUSUZJQ0FURS0tLS0tCk1JSURYVENDQWtXZ0F3SUJBZ0lKQU1BdVkyWHU5U2pSTUEwR0NTcUdTSWIzRFFFQkN3VUFNRUl4Q3pBSkJnTlYKQkFZVEFrZEZNUkF3RGdZRFZRUUlEQWRDWVc1cmFXNW5NVEF3RGdZRFZRUUhEQWRHY21GdWEyWjFjblF4RVRBUApCZ05WQkFvTUNGUmxjM1FnUW1GdWF6QWVGdzB5TWpBeE1EQXhNREF3TURCYUZ3MHlOREl4TVRBeE1EQXdNREJhCk1FSXhDekFKQmdOVkJBWVRBa2RGTVJBd0RnWURWUVFJREFkQ1lXNXJhVzVuTVRBd0RnWURWUVFIREFkR2NtRnUKYTJaMWNuUXhFVEFQQmdOVkJBb01DRlJsYzNRZ1FtRnVhekNDQVNJd0RRWUpLb1pJaHZjTkFRRUJCUUFEZ2dFUApBRENDQVFvQ2dnRUJBSw==';

DECLARE @client_key_data NVARCHAR(MAX) = 'LS0tLS1CRUdJTiBQUklWQVRFIEtFWS0tLS0tCk1JSUV2Z0lCQURBTkJna3Foa2lHOXcwQkFRRUZBQVNDQktnd2dnU2tBZ0VBQW9JQkFRRFNZMmxwTjlzYVFwZVIKMzc2N01CZ3Q4SThrQ1ZJK0hnaVZBSVVNZm1WYUNPZURDTmRFUmZNZEJxUE54d0JYWjFFRkZOUEJlcnZCOHFkcgp5L1krS3I1blU1V0FqN1Y1bm9TMVZ3QUhEandnRWJTWWJhd1c4b05meHBSMStqZzZxcFgzTGNhOEhHNStaOURG';

DECLARE @bank_cert_data NVARCHAR(MAX) = 'LS0tLS1CRUdJTiBDRVJUSUZJQ0FURS0tLS0tCk1JSURYVENDQWtXZ0F3SUJBZ0lKQU1BdVkyWHU5U2pSTUEwR0NTcUdTSWIzRFFFQkN3VUFNRUl4Q3pBSkJnTlYKQkFZVEFrZEZNUkF3RGdZRFZRUUlEQWRDWVc1cmFXNW5NVEF3RGdZRFZRUUhEQWRHY21GdWEyWjFjblF4RVRBUApCZ05WQkFvTUNGUmxjM1FnUW1GdWF6QWVGdzB5TWpBeE1EQXhNREF3TURCYUZ3MHlOREl4TVRBeE1EQXdNREJhCk1FSXhDekFKQmdOVkJBWVRBa2RGTVJBd0RnWURWUVFJREFkQ1lXNXJhVzVuTVRBd0RnWURWUVFIREFkR2NtRnUKYTJaMWNuUXhFVEFQQmdOVkJBb01DRlJsYzNRZ1FtRnVhekNDQVNJd0RRWUpLb1pJaHZjTkFRRUJCUUFEZ2dFUApBRENDQVFvQ2dnRUJBSw==';

-- Insert sample certificates
INSERT INTO ebics_certificates (
    id, connection_id, certificate_type, usage_type, subject_name, issuer_name, 
    serial_number, fingerprint, not_before, not_after, key_algorithm, key_size,
    certificate_data, private_key_data, is_active, created_at
) VALUES
    -- Client Authentication Certificate (A005)
    (1001, 100, 'CLIENT_CERTIFICATE', 'AUTHENTICATION', 
     'CN=EBICS_CLIENT_001,OU=Banking,O=Test Company,L=Frankfurt,C=DE',
     'CN=EBICS_CLIENT_001,OU=Banking,O=Test Company,L=Frankfurt,C=DE',
     '1234567890ABCDEF', 'a1b2c3d4e5f6789012345678901234567890abcdef123456789012345678901234',
     '2024-01-01 00:00:00', '2025-12-31 23:59:59',
     'RSA', 2048,
     CAST('' AS XML).value('xs:base64Binary(sql:variable("@client_cert_data"))', 'VARBINARY(MAX)'),
     CAST('' AS XML).value('xs:base64Binary(sql:variable("@client_key_data"))', 'VARBINARY(MAX)'),
     1, GETDATE()),

    -- Client Encryption Certificate (E002)  
    (1002, 100, 'CLIENT_CERTIFICATE', 'ENCRYPTION',
     'CN=EBICS_CLIENT_001,OU=Banking,O=Test Company,L=Frankfurt,C=DE',
     'CN=EBICS_CLIENT_001,OU=Banking,O=Test Company,L=Frankfurt,C=DE', 
     '2345678901BCDEFG', 'b2c3d4e5f6789012345678901234567890abcdef123456789012345678901235',
     '2024-01-01 00:00:00', '2025-12-31 23:59:59',
     'RSA', 2048,
     CAST('' AS XML).value('xs:base64Binary(sql:variable("@client_cert_data"))', 'VARBINARY(MAX)'),
     CAST('' AS XML).value('xs:base64Binary(sql:variable("@client_key_data"))', 'VARBINARY(MAX)'),
     1, GETDATE()),

    -- Client Signature Certificate (X002)
    (1003, 100, 'CLIENT_CERTIFICATE', 'SIGNATURE',
     'CN=EBICS_CLIENT_001,OU=Banking,O=Test Company,L=Frankfurt,C=DE',
     'CN=EBICS_CLIENT_001,OU=Banking,O=Test Company,L=Frankfurt,C=DE',
     '3456789012CDEFGH', 'c3d4e5f6789012345678901234567890abcdef123456789012345678901236',
     '2024-01-01 00:00:00', '2025-12-31 23:59:59',
     'RSA', 2048,
     CAST('' AS XML).value('xs:base64Binary(sql:variable("@client_cert_data"))', 'VARBINARY(MAX)'),
     CAST('' AS XML).value('xs:base64Binary(sql:variable("@client_key_data"))', 'VARBINARY(MAX)'),
     1, GETDATE()),

    -- Bank Authentication Certificate
    (1004, 100, 'BANK_CERTIFICATE', 'AUTHENTICATION',
     'CN=DEUTDEFF,OU=EBICS,O=Deutsche Bank AG,L=Frankfurt,C=DE',
     'CN=Deutsche Bank Root CA,O=Deutsche Bank AG,C=DE',
     '4567890123DEFGHI', 'd4e5f6789012345678901234567890abcdef123456789012345678901237',
     '2023-06-01 00:00:00', '2026-05-31 23:59:59',
     'RSA', 4096,
     CAST('' AS XML).value('xs:base64Binary(sql:variable("@bank_cert_data"))', 'VARBINARY(MAX)'),
     NULL, -- Bank certificates don't include private keys
     1, GETDATE()),

    -- Bank Encryption Certificate  
    (1005, 100, 'BANK_CERTIFICATE', 'ENCRYPTION',
     'CN=DEUTDEFF,OU=EBICS,O=Deutsche Bank AG,L=Frankfurt,C=DE',
     'CN=Deutsche Bank Root CA,O=Deutsche Bank AG,C=DE',
     '5678901234EFGHIJ', 'e5f6789012345678901234567890abcdef123456789012345678901238',
     '2023-06-01 00:00:00', '2026-05-31 23:59:59',
     'RSA', 4096,
     CAST('' AS XML).value('xs:base64Binary(sql:variable("@bank_cert_data"))', 'VARBINARY(MAX)'),
     NULL,
     1, GETDATE()),

    -- Credit Suisse Test Environment  
    (1006, 200, 'BANK_CERTIFICATE', 'AUTHENTICATION',
     'CN=CRESCHZZ80A,OU=Test Environment,O=Credit Suisse AG,L=Zurich,C=CH',
     'CN=Credit Suisse Test Root CA,O=Credit Suisse AG,C=CH',
     '6789012345FGHIJK', 'f6789012345678901234567890abcdef123456789012345678901239',
     '2024-03-01 00:00:00', '2027-02-28 23:59:59',
     'RSA', 3072,
     CAST('' AS XML).value('xs:base64Binary(sql:variable("@bank_cert_data"))', 'VARBINARY(MAX)'),
     NULL,
     1, GETDATE()),

    -- Expired Certificate (for testing expiry scenarios)
    (1007, 100, 'CLIENT_CERTIFICATE', 'SIGNATURE',
     'CN=EXPIRED_CLIENT,OU=Banking,O=Test Company,C=DE',
     'CN=EXPIRED_CLIENT,OU=Banking,O=Test Company,C=DE',
     '7890123456GHIJKL', 'g789012345678901234567890abcdef123456789012345678901240',
     '2022-01-01 00:00:00', '2023-12-31 23:59:59',
     'RSA', 2048,
     CAST('' AS XML).value('xs:base64Binary(sql:variable("@client_cert_data"))', 'VARBINARY(MAX)'),
     CAST('' AS XML).value('xs:base64Binary(sql:variable("@client_key_data"))', 'VARBINARY(MAX)'),
     0, GETDATE()),

    -- CA Root Certificate
    (1008, NULL, 'CA_CERTIFICATE', 'ROOT_CA',
     'CN=German Banking Root CA,OU=PKI,O=German Banking Association,C=DE',
     'CN=German Banking Root CA,OU=PKI,O=German Banking Association,C=DE',
     '8901234567HIJKLM', 'h89012345678901234567890abcdef123456789012345678901241',
     '2020-01-01 00:00:00', '2030-12-31 23:59:59',
     'RSA', 4096,
     CAST('' AS XML).value('xs:base64Binary(sql:variable("@bank_cert_data"))', 'VARBINARY(MAX)'),
     NULL,
     1, GETDATE());
