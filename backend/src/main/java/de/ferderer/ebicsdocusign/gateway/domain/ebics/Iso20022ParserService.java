package de.ferderer.ebicsdocusign.gateway.domain.ebics;

import de.ferderer.ebicsdocusign.gateway.common.error.AppException;
import de.ferderer.ebicsdocusign.gateway.common.error.ErrorCode;
import de.ferderer.ebicsdocusign.gateway.domain.ebics.model.*;
import de.ferderer.ebicsdocusign.gateway.domain.ebics.model.Account.Amount;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;
import java.io.ByteArrayInputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class Iso20022ParserService {
    
    /**
     * Parse ISO 20022 CAMT.053 bank statement XML data
     */
    public List<BankStatementRecord> parseStatements(byte[] xmlData) {
        try {
            log.info("Parsing {} bytes of ISO 20022 CAMT.053 data", xmlData.length);
            
            // Create JAXB context for ISO 20022 classes
            JAXBContext jaxbContext = JAXBContext.newInstance(BankStatement.class);
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            
            // Parse XML to object
            BankStatement bankStatement = (BankStatement) unmarshaller.unmarshal(
                new ByteArrayInputStream(xmlData)
            );
            
            // Convert to our internal format
            return convertToStatementRecords(bankStatement);
            
        }
        catch (JAXBException e) {
            log.error("Failed to parse ISO 20022 XML: {}", e.getMessage(), e);
            throw new AppException(ErrorCode.EBICS_PROCESSING_ERROR)
                .withData("error", "Failed to parse CAMT.053 XML")
                .withData("details", e.getMessage());
        }
    }
    
    /**
     * Convert BankStatement to our internal BankStatementRecord format
     */
    private List<BankStatementRecord> convertToStatementRecords(BankStatement statement) {
        List<BankStatementRecord> records = new ArrayList<>();
        
        if (statement.getBankToCustomerStatement() == null || 
            statement.getBankToCustomerStatement().getStatement() == null) {
            log.warn("No statement data found in CAMT.053");
            return records;
        }
        
        BankStatement.Statement stmt = statement.getBankToCustomerStatement().getStatement();
        
        // Extract account information
        String accountNumber = extractAccountNumber(stmt.getAccount());
        String accountOwner = extractAccountOwner(stmt.getAccount());
        String currency = stmt.getAccount() != null ? stmt.getAccount().getCurrency() : "CHF";
        
        log.debug("Processing statement for account {} owned by {}", accountNumber, accountOwner);
        
        // Process each entry (transaction)
        if (stmt.getEntries() != null) {
            for (Entry entry : stmt.getEntries()) {
                try {
                    BankStatementRecord r = convertEntry(entry, accountNumber, accountOwner, currency);
                    if (r != null) {
                        records.add(r);
                    }
                } catch (Exception e) {
                    log.warn("Failed to convert entry {}: {}", entry.getAccountServicerReference(), e.getMessage());
                }
            }
        }
        
        log.info("Converted {} entries to statement records", records.size());
        return records;
    }
    
    /**
     * Convert single Entry to BankStatementRecord
     */
    private BankStatementRecord convertEntry(Entry entry, String accountNumber, String accountOwner, String currency) {
        if (entry.getAmount() == null) {
            log.warn("Entry without amount: {}", entry.getAccountServicerReference());
            return null;
        }
        
        // Extract transaction details
        EntryDetails.TransactionDetails txDetails = extractTransactionDetails(entry);
        
        return new BankStatementRecord(
            entry.getAccountServicerReference(), // transactionId
            entry.getValueDate() != null ? entry.getValueDate().getDate() : null, // valueDate
            entry.getBookingDate() != null ? entry.getBookingDate().getDate() : null, // bookingDate
            formatAmount(entry.getAmount(), entry.getCreditDebitIndicator()), // amount
            entry.getAmount().getCurrency() != null ? entry.getAmount().getCurrency() : currency, // currency
            extractDebtorName(txDetails), // debtorName
            extractDebtorAccount(txDetails), // debtorAccount
            extractCreditorName(txDetails), // creditorName  
            extractCreditorAccount(txDetails), // creditorAccount
            extractRemittanceInfo(txDetails), // remittanceInfo
            extractEndToEndId(txDetails) // endToEndId
        );
    }
    
    /**
     * Extract first transaction details from entry
     */
    private EntryDetails.TransactionDetails extractTransactionDetails(Entry entry) {
        if (entry.getEntryDetails() != null && 
            entry.getEntryDetails().getTransactionDetails() != null &&
            !entry.getEntryDetails().getTransactionDetails().isEmpty()) {
            return entry.getEntryDetails().getTransactionDetails().get(0);
        }
        return null;
    }
    
    /**
     * Extract account number from account info
     */
    private String extractAccountNumber(Account account) {
        if (account == null || account.getId() == null) return null;
        
        // Try IBAN first
        if (account.getId().getIban() != null) {
            return account.getId().getIban();
        }
        
        // Try other account ID
        if (account.getId().getOther() != null) {
            return account.getId().getOther().getId();
        }
        
        return null;
    }
    
    /**
     * Extract account owner name
     */
    private String extractAccountOwner(Account account) {
        if (account != null && account.getOwner() != null) {
            return account.getOwner().getName();
        }
        return null;
    }
    
    /**
     * Format amount with sign based on credit/debit indicator
     */
    private String formatAmount(Amount amount, String creditDebitIndicator) {
        if (amount == null || amount.getValue() == null) return "0.00";
        
        BigDecimal value = amount.getValue();
        if ("DBIT".equals(creditDebitIndicator)) {
            value = value.negate();
        }
        
        return value.toString();
    }
    
    /**
     * Extract debtor name from transaction details
     */
    private String extractDebtorName(EntryDetails.TransactionDetails txDetails) {
        if (txDetails != null && txDetails.getRelatedParties() != null) {
            EntryDetails.RelatedParties parties = txDetails.getRelatedParties();
            
            // Try debtor first
            if (parties.getDebtor() != null && parties.getDebtor().getParty() != null) {
                return parties.getDebtor().getParty().getName();
            }
            
            // Try ultimate debtor
            if (parties.getUltimateDebtor() != null && parties.getUltimateDebtor().getParty() != null) {
                return parties.getUltimateDebtor().getParty().getName();
            }
        }
        return null;
    }
    
    /**
     * Extract debtor account from transaction details
     */
    private String extractDebtorAccount(EntryDetails.TransactionDetails txDetails) {
        if (txDetails != null && txDetails.getRelatedParties() != null && 
            txDetails.getRelatedParties().getDebtorAccount() != null &&
            txDetails.getRelatedParties().getDebtorAccount().getId() != null) {
            return txDetails.getRelatedParties().getDebtorAccount().getId().getIban();
        }
        return null;
    }
    
    /**
     * Extract creditor name from transaction details
     */
    private String extractCreditorName(EntryDetails.TransactionDetails txDetails) {
        if (txDetails != null && txDetails.getRelatedParties() != null) {
            EntryDetails.RelatedParties parties = txDetails.getRelatedParties();
            
            // Try creditor first
            if (parties.getCreditor() != null && parties.getCreditor().getParty() != null) {
                return parties.getCreditor().getParty().getName();
            }
            
            // Try ultimate creditor
            if (parties.getUltimateCreditor() != null && parties.getUltimateCreditor().getParty() != null) {
                return parties.getUltimateCreditor().getParty().getName();
            }
        }
        return null;
    }
    
    /**
     * Extract creditor account from transaction details
     */
    private String extractCreditorAccount(EntryDetails.TransactionDetails txDetails) {
        if (txDetails != null && txDetails.getRelatedParties() != null && 
            txDetails.getRelatedParties().getCreditorAccount() != null &&
            txDetails.getRelatedParties().getCreditorAccount().getId() != null) {
            return txDetails.getRelatedParties().getCreditorAccount().getId().getIban();
        }
        return null;
    }
    
    /**
     * Extract remittance information from transaction details
     */
    private String extractRemittanceInfo(EntryDetails.TransactionDetails txDetails) {
        if (txDetails == null || txDetails.getRemittanceInformation() == null) {
            return null;
        }
        
        EntryDetails.RemittanceInformation rmtInf = txDetails.getRemittanceInformation();
        
        // Try unstructured first
        if (rmtInf.getUnstructured() != null && !rmtInf.getUnstructured().isEmpty()) {
            return String.join(" ", rmtInf.getUnstructured());
        }
        
        // Try structured
        if (rmtInf.getStructured() != null) {
            StringBuilder sb = new StringBuilder();
            
            if (rmtInf.getStructured().getCreditorReferenceInformation() != null &&
                rmtInf.getStructured().getCreditorReferenceInformation().getReference() != null) {
                sb.append("Ref: ").append(rmtInf.getStructured().getCreditorReferenceInformation().getReference());
            }
            
            if (rmtInf.getStructured().getAdditionalRemittanceInformation() != null) {
                if (sb.length() > 0) sb.append(" ");
                sb.append(rmtInf.getStructured().getAdditionalRemittanceInformation());
            }
            
            return sb.length() > 0 ? sb.toString() : null;
        }
        
        return null;
    }
    
    /**
     * Extract end-to-end ID from transaction details
     */
    private String extractEndToEndId(EntryDetails.TransactionDetails txDetails) {
        if (txDetails != null && txDetails.getReferences() != null) {
            return txDetails.getReferences().getEndToEndId();
        }
        return null;
    }
    
    /**
     * Record representing a parsed bank statement transaction
     */
    public record BankStatementRecord(
        String transactionId,
        LocalDate valueDate,
        LocalDate bookingDate,
        String amount,
        String currency,
        String debtorName,
        String debtorAccount,
        String creditorName,
        String creditorAccount,
        String remittanceInfo,
        String endToEndId
    ) {}
}
