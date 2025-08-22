package de.ferderer.ebicsdocusign.gateway.domain.ebics;

import de.ferderer.ebicsdocusign.gateway.domain.ebics.Iso20022ParserService.BankStatementRecord;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;

@Slf4j
class Iso20022ParserServiceTest {

    private Iso20022ParserService parserService;

    @BeforeEach
    public void setup() {
        parserService = new Iso20022ParserService();
    }

    @Test
    void shouldParseRealCamt053XmlFile() throws IOException {
        // Load the CAMT.053 XML file from classpath
        ClassPathResource resource = new ClassPathResource("camt.053.xml");
        byte[] xmlData = Files.readAllBytes(Paths.get(resource.getURI()));
        
        log.info("Loaded CAMT.053 XML file: {} bytes", xmlData.length);

        // Parse the XML using our service
        List<BankStatementRecord> records = parserService.parseStatements(xmlData);

        // Verify parsing results
        assertNotNull(records, "Parsed records should not be null");
        log.info("Parsed {} bank statement records", records.size());
        
        // Expected 17 entries from the XML file
        assertEquals(17, records.size(), "Should parse all 17 entries from the XML");

        // Test specific transactions from the XML
        validateFirstDebitTransaction(records);
        validateLargeIncomingPayment(records);
        validateCurrencyExchangeTransaction(records);
        validateStructuredRemittanceInfo(records);
        
        // Log all records for manual verification
        records.forEach(record -> log.debug("Parsed record: {}", record));
    }

    private void validateFirstDebitTransaction(List<BankStatementRecord> records) {
        // First entry: CHF 2.36 DBIT (EUR 2.00 converted)
        BankStatementRecord firstDebit = records.stream()
            .filter(r -> "DNQR-180322-CS-43783/1".equals(r.transactionId()))
            .findFirst()
            .orElseThrow(() -> new AssertionError("First debit transaction not found"));

        assertEquals("DNQR-180322-CS-43783/1", firstDebit.transactionId());
        assertEquals("-2.36", firstDebit.amount(), "Should be negative for DBIT");
        assertEquals("CHF", firstDebit.currency());
        assertEquals(LocalDate.of(2022, 12, 22), firstDebit.valueDate());
        assertEquals(LocalDate.of(2022, 12, 22), firstDebit.bookingDate());
        
        log.info("✓ First debit transaction validated: {}", firstDebit);
    }

    private void validateLargeIncomingPayment(List<BankStatementRecord> records) {
        // Large credit: CHF 6000 from Barbara Muster
        BankStatementRecord largeCredit = records.stream()
            .filter(r -> "6000".equals(r.amount()))
            .findFirst()
            .orElseThrow(() -> new AssertionError("Large credit transaction not found"));

        assertEquals("6000", largeCredit.amount());
        assertEquals("CHF", largeCredit.currency());
        assertEquals("BARBARA MUSTER", largeCredit.debtorName());
        assertTrue(largeCredit.remittanceInfo().contains("RECHNUNG 67890"));
        assertEquals(LocalDate.of(2022, 12, 22), largeCredit.bookingDate());
        assertEquals(LocalDate.of(2022, 12, 23), largeCredit.valueDate()); // Different value date
        
        log.info("✓ Large incoming payment validated: {}", largeCredit);
    }

    private void validateCurrencyExchangeTransaction(List<BankStatementRecord> records) {
        // EUR to CHF conversion: EUR 3.00 → CHF 3.47
        BankStatementRecord eurTransaction = records.stream()  
            .filter(r -> "3.47".equals(r.amount()))
            .findFirst()
            .orElseThrow(() -> new AssertionError("EUR conversion transaction not found"));

        assertEquals("3.47", eurTransaction.amount());
        assertEquals("CHF", eurTransaction.currency());
        assertEquals("KOWALSKI JAN", eurTransaction.debtorName());
        assertTrue(eurTransaction.remittanceInfo().contains("Invoice 45678"));
        
        log.info("✓ Currency exchange transaction validated: {}", eurTransaction);
    }

    private void validateStructuredRemittanceInfo(List<BankStatementRecord> records) {
        // QR reference payment: Batch total CHF 997.25 from Max Muster with structured reference
        BankStatementRecord qrPayment = records.stream()
            .filter(r -> "Max Muster".equals(r.debtorName()))
            .findFirst()
            .orElseThrow(() -> new AssertionError("QR payment with Max Muster not found"));

        assertEquals("997.25", qrPayment.amount());
        assertEquals("Max Muster", qrPayment.debtorName());
        assertTrue(qrPayment.remittanceInfo().contains("000000000000000000000000034"));
        assertTrue(qrPayment.remittanceInfo().contains("FREE TEXT"));

        log.info("✓ Structured remittance info validated: {}", qrPayment);
    }

    @Test
    void shouldHandleEmptyXmlGracefully() {
        String emptyXml = """
            <?xml version="1.0" encoding="UTF-8"?>
            <Document xmlns="urn:iso:std:iso:20022:tech:xsd:camt.053.001.08">
                <BkToCstmrStmt>
                    <GrpHdr>
                        <MsgId>EMPTY_MSG</MsgId>
                    </GrpHdr>
                    <Stmt>
                        <Id>empty-statement</Id>
                        <Acct>
                            <Id><Othr><Id>TEST</Id></Othr></Id>
                            <Ccy>CHF</Ccy>
                        </Acct>
                    </Stmt>
                </BkToCstmrStmt>
            </Document>
            """;

        List<BankStatementRecord> records = parserService.parseStatements(emptyXml.getBytes());
        
        assertNotNull(records);
        assertTrue(records.isEmpty(), "Empty statement should result in empty records list");
        
        log.info("✓ Empty XML handling validated");
    }

    @Test
    void shouldExtractCorrectAccountInfo() throws IOException {
        ClassPathResource resource = new ClassPathResource("camt.053.xml");
        byte[] xmlData = Files.readAllBytes(Paths.get(resource.getURI()));

        List<BankStatementRecord> records = parserService.parseStatements(xmlData);
        
        // Verify that records contain expected account-related info
        assertFalse(records.isEmpty());
        
        // Check for transactions with IBAN references
        long ibanTransactions = records.stream()
            .filter(r -> r.creditorAccount() != null || r.debtorAccount() != null)
            .count();
        
        assertTrue(ibanTransactions > 0, "Should have transactions with account references");
        
        log.info("✓ Account information extraction validated: {} transactions with account info", ibanTransactions);
    }
}
