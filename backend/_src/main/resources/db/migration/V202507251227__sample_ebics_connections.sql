-- Sample data for ebics_connections table
INSERT INTO ebics_connections (id, bank_name, host_id, partner_id, user_id, bank_url, version, status, last_connected, created_at) VALUES
    (1001, 'Deutsche Bank AG', 'DEUTDEFF', 'PARTNER001', 'USER001', 'https://ebics.deutsche-bank.de/ebics', 'H004', 'ACTIVE', '2024-01-15 10:30:00', '2024-01-10 09:15:00'),
    (1002, 'Commerzbank AG', 'COBADEFF', 'PARTNER002', 'USER002', 'https://ebics.commerzbank.de/ebics', 'H004', 'ACTIVE', '2024-01-14 16:45:00', '2024-01-08 14:20:00'),
    (1003, 'UniCredit Bank AG', 'HYVEDEMM', 'PARTNER003', 'USER003', 'https://ebics.unicredit.de/ebics', 'H003', 'INACTIVE', NULL, '2024-01-05 11:30:00'),
    (1004, 'DZ Bank AG', 'GENODED1DZ', 'PARTNER004', 'USER004', 'https://ebics.dzbank.de/ebics', 'H004', 'ACTIVE', '2024-01-13 08:20:00', '2024-01-03 16:45:00'),
    (1005, 'Landesbank Baden-Württemberg', 'SOLADEST', 'PARTNER005', 'USER005', 'https://ebics.lbbw.de/ebics', 'H004', 'ERROR', '2024-01-12 14:10:00', '2023-12-28 10:00:00'),
    (1006, 'Sparkasse Köln Bonn', 'COLSDE33', 'PARTNER006', 'USER006', 'https://ebics.sparkasse-koelnbonn.de/ebics', 'H004', 'PENDING_SETUP', NULL, '2024-01-14 12:30:00'),
    (1007, 'ING-DiBa AG', 'INGDDEFF', 'PARTNER007', 'USER007', 'https://ebics.ing.de/ebics', 'H005', 'ACTIVE', '2024-01-15 07:15:00', '2024-01-02 13:45:00'),
    (1008, 'Santander Consumer Bank AG', 'SCFBDE33', 'PARTNER008', 'USER008', 'https://ebics.santander.de/ebics', 'H004', 'INACTIVE', '2024-01-10 19:30:00', '2023-12-20 15:20:00');
