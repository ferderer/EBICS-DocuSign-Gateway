package de.ferderer.ebicsdocusign.gateway.domain.ebics.model;

import de.ferderer.ebicsdocusign.gateway.domain.ebics.model.Account.Amount;
import de.ferderer.ebicsdocusign.gateway.domain.ebics.model.Account.PostalAddress;
import jakarta.xml.bind.annotation.*;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@XmlAccessorType(XmlAccessType.FIELD)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EntryDetails {
    
    @XmlElement(name = "Btch", namespace = "urn:iso:std:iso:20022:tech:xsd:camt.053.001.08")
    private BatchOrLotDetails batch;
    
    @XmlElement(name = "TxDtls", namespace = "urn:iso:std:iso:20022:tech:xsd:camt.053.001.08")
    private List<TransactionDetails> transactionDetails;
    
    @XmlAccessorType(XmlAccessType.FIELD)
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BatchOrLotDetails {
        
        @XmlElement(name = "MsgId", namespace = "urn:iso:std:iso:20022:tech:xsd:camt.053.001.08")
        private String messageId;
        
        @XmlElement(name = "PmtInfId", namespace = "urn:iso:std:iso:20022:tech:xsd:camt.053.001.08")
        private String paymentInformationId;
        
        @XmlElement(name = "NbOfTxs", namespace = "urn:iso:std:iso:20022:tech:xsd:camt.053.001.08")
        private Integer numberOfTransactions;
        
        @XmlElement(name = "TtlAmt", namespace = "urn:iso:std:iso:20022:tech:xsd:camt.053.001.08")
        private Amount totalAmount;
        
        @XmlElement(name = "CdtDbtInd", namespace = "urn:iso:std:iso:20022:tech:xsd:camt.053.001.08")
        private String creditDebitIndicator;
    }
    
    @XmlAccessorType(XmlAccessType.FIELD)
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TransactionDetails {
        
        @XmlElement(name = "Refs", namespace = "urn:iso:std:iso:20022:tech:xsd:camt.053.001.08")
        private TransactionReferences references;
        
        @XmlElement(name = "Amt", namespace = "urn:iso:std:iso:20022:tech:xsd:camt.053.001.08")
        private Amount amount;
        
        @XmlElement(name = "CdtDbtInd", namespace = "urn:iso:std:iso:20022:tech:xsd:camt.053.001.08")
        private String creditDebitIndicator;
        
        @XmlElement(name = "AmtDtls", namespace = "urn:iso:std:iso:20022:tech:xsd:camt.053.001.08")
        private Entry.AmountDetails amountDetails;
        
        @XmlElement(name = "BkTxCd", namespace = "urn:iso:std:iso:20022:tech:xsd:camt.053.001.08")
        private Entry.BankTransactionCode bankTransactionCode;
        
        @XmlElement(name = "Chrgs", namespace = "urn:iso:std:iso:20022:tech:xsd:camt.053.001.08")
        private Entry.Charges charges;
        
        @XmlElement(name = "RltdPties", namespace = "urn:iso:std:iso:20022:tech:xsd:camt.053.001.08")
        private RelatedParties relatedParties;
        
        @XmlElement(name = "RltdAgts", namespace = "urn:iso:std:iso:20022:tech:xsd:camt.053.001.08")
        private RelatedAgents relatedAgents;
        
        @XmlElement(name = "RmtInf", namespace = "urn:iso:std:iso:20022:tech:xsd:camt.053.001.08")
        private RemittanceInformation remittanceInformation;
        
        @XmlElement(name = "RtrInf", namespace = "urn:iso:std:iso:20022:tech:xsd:camt.053.001.08")
        private ReturnInformation returnInformation;
    }
    
    @XmlAccessorType(XmlAccessType.FIELD)
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TransactionReferences {
        
        @XmlElement(name = "MsgId", namespace = "urn:iso:std:iso:20022:tech:xsd:camt.053.001.08")
        private String messageId;
        
        @XmlElement(name = "AcctSvcrRef", namespace = "urn:iso:std:iso:20022:tech:xsd:camt.053.001.08")
        private String accountServicerReference;
        
        @XmlElement(name = "PmtInfId", namespace = "urn:iso:std:iso:20022:tech:xsd:camt.053.001.08")
        private String paymentInformationId;
        
        @XmlElement(name = "InstrId", namespace = "urn:iso:std:iso:20022:tech:xsd:camt.053.001.08")
        private String instructionId;
        
        @XmlElement(name = "EndToEndId", namespace = "urn:iso:std:iso:20022:tech:xsd:camt.053.001.08")
        private String endToEndId;
        
        @XmlElement(name = "UETR", namespace = "urn:iso:std:iso:20022:tech:xsd:camt.053.001.08")
        private String uetr;
        
        @XmlElement(name = "TxId", namespace = "urn:iso:std:iso:20022:tech:xsd:camt.053.001.08")
        private String transactionId;
    }
    
    @XmlAccessorType(XmlAccessType.FIELD)
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RelatedParties {
        
        @XmlElement(name = "Dbtr", namespace = "urn:iso:std:iso:20022:tech:xsd:camt.053.001.08")
        private Party debtor;
        
        @XmlElement(name = "DbtrAcct", namespace = "urn:iso:std:iso:20022:tech:xsd:camt.053.001.08")
        private CashAccount debtorAccount;
        
        @XmlElement(name = "UltmtDbtr", namespace = "urn:iso:std:iso:20022:tech:xsd:camt.053.001.08")
        private Party ultimateDebtor;
        
        @XmlElement(name = "Cdtr", namespace = "urn:iso:std:iso:20022:tech:xsd:camt.053.001.08")
        private Party creditor;
        
        @XmlElement(name = "CdtrAcct", namespace = "urn:iso:std:iso:20022:tech:xsd:camt.053.001.08")
        private CashAccount creditorAccount;
        
        @XmlElement(name = "UltmtCdtr", namespace = "urn:iso:std:iso:20022:tech:xsd:camt.053.001.08")
        private Party ultimateCreditor;
    }
    
    @XmlAccessorType(XmlAccessType.FIELD)
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Party {
        
        @XmlElement(name = "Pty", namespace = "urn:iso:std:iso:20022:tech:xsd:camt.053.001.08")
        private PartyDetails party;
    }
    
    @XmlAccessorType(XmlAccessType.FIELD)
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PartyDetails {
        
        @XmlElement(name = "Nm", namespace = "urn:iso:std:iso:20022:tech:xsd:camt.053.001.08")
        private String name;
        
        @XmlElement(name = "PstlAdr", namespace = "urn:iso:std:iso:20022:tech:xsd:camt.053.001.08")
        private PostalAddress postalAddress;
    }
    
    @XmlAccessorType(XmlAccessType.FIELD)
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CashAccount {
        
        @XmlElement(name = "Id", namespace = "urn:iso:std:iso:20022:tech:xsd:camt.053.001.08")
        private AccountIdentification id;
    }
    
    @XmlAccessorType(XmlAccessType.FIELD)
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AccountIdentification {
        
        @XmlElement(name = "IBAN", namespace = "urn:iso:std:iso:20022:tech:xsd:camt.053.001.08")
        private String iban;
    }
    
    @XmlAccessorType(XmlAccessType.FIELD)
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RelatedAgents {
        
        @XmlElement(name = "DbtrAgt", namespace = "urn:iso:std:iso:20022:tech:xsd:camt.053.001.08")
        private BranchAndFinancialInstitution debtorAgent;
        
        @XmlElement(name = "CdtrAgt", namespace = "urn:iso:std:iso:20022:tech:xsd:camt.053.001.08")
        private BranchAndFinancialInstitution creditorAgent;
    }
    
    @XmlAccessorType(XmlAccessType.FIELD)
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BranchAndFinancialInstitution {
        
        @XmlElement(name = "FinInstnId", namespace = "urn:iso:std:iso:20022:tech:xsd:camt.053.001.08")
        private FinancialInstitutionIdentification financialInstitutionId;
    }
    
    @XmlAccessorType(XmlAccessType.FIELD)
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FinancialInstitutionIdentification {
        
        @XmlElement(name = "BICFI", namespace = "urn:iso:std:iso:20022:tech:xsd:camt.053.001.08")
        private String bic;
        
        @XmlElement(name = "ClrSysMmbId", namespace = "urn:iso:std:iso:20022:tech:xsd:camt.053.001.08")
        private ClearingSystemMemberId clearingSystemMemberId;
        
        @XmlElement(name = "Nm", namespace = "urn:iso:std:iso:20022:tech:xsd:camt.053.001.08")
        private String name;
        
        @XmlElement(name = "PstlAdr", namespace = "urn:iso:std:iso:20022:tech:xsd:camt.053.001.08")
        private PostalAddress postalAddress;
    }
    
    @XmlAccessorType(XmlAccessType.FIELD)
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ClearingSystemMemberId {
        
        @XmlElement(name = "ClrSysId", namespace = "urn:iso:std:iso:20022:tech:xsd:camt.053.001.08")
        private ClearingSystemIdentification clearingSystemId;
        
        @XmlElement(name = "MmbId", namespace = "urn:iso:std:iso:20022:tech:xsd:camt.053.001.08")
        private String memberId;
    }
    
    @XmlAccessorType(XmlAccessType.FIELD)
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ClearingSystemIdentification {
        
        @XmlElement(name = "Cd", namespace = "urn:iso:std:iso:20022:tech:xsd:camt.053.001.08")
        private String code;
    }
    
    @XmlAccessorType(XmlAccessType.FIELD)
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RemittanceInformation {
        
        @XmlElement(name = "Ustrd", namespace = "urn:iso:std:iso:20022:tech:xsd:camt.053.001.08")
        private List<String> unstructured;
        
        @XmlElement(name = "Strd", namespace = "urn:iso:std:iso:20022:tech:xsd:camt.053.001.08")
        private StructuredRemittanceInformation structured;
    }
    
    @XmlAccessorType(XmlAccessType.FIELD)
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StructuredRemittanceInformation {
        
        @XmlElement(name = "CdtrRefInf", namespace = "urn:iso:std:iso:20022:tech:xsd:camt.053.001.08")
        private CreditorReferenceInformation creditorReferenceInformation;
        
        @XmlElement(name = "AddtlRmtInf", namespace = "urn:iso:std:iso:20022:tech:xsd:camt.053.001.08")
        private String additionalRemittanceInformation;
    }
    
    @XmlAccessorType(XmlAccessType.FIELD)
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreditorReferenceInformation {
        
        @XmlElement(name = "Tp", namespace = "urn:iso:std:iso:20022:tech:xsd:camt.053.001.08")
        private CreditorReferenceType type;
        
        @XmlElement(name = "Ref", namespace = "urn:iso:std:iso:20022:tech:xsd:camt.053.001.08")
        private String reference;
    }
    
    @XmlAccessorType(XmlAccessType.FIELD)
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreditorReferenceType {
        
        @XmlElement(name = "CdOrPrtry", namespace = "urn:iso:std:iso:20022:tech:xsd:camt.053.001.08")
        private CodeOrProprietary codeOrProprietary;
    }
    
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "iso20022CodeOrProprietary")
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CodeOrProprietary {
        
        @XmlElement(name = "Prtry", namespace = "urn:iso:std:iso:20022:tech:xsd:camt.053.001.08")
        private String proprietary;
    }
    
    @XmlAccessorType(XmlAccessType.FIELD)
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ReturnInformation {
        
        @XmlElement(name = "OrgnlBkTxCd", namespace = "urn:iso:std:iso:20022:tech:xsd:camt.053.001.08")
        private Entry.BankTransactionCode originalBankTransactionCode;
        
        @XmlElement(name = "Orgtr", namespace = "urn:iso:std:iso:20022:tech:xsd:camt.053.001.08")
        private Party originator;
        
        @XmlElement(name = "Rsn", namespace = "urn:iso:std:iso:20022:tech:xsd:camt.053.001.08")
        private ReturnReason reason;
        
        @XmlElement(name = "AddtlInf", namespace = "urn:iso:std:iso:20022:tech:xsd:camt.053.001.08")
        private List<String> additionalInfo;
    }
    
    @XmlAccessorType(XmlAccessType.FIELD)
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ReturnReason {
        
        @XmlElement(name = "Cd", namespace = "urn:iso:std:iso:20022:tech:xsd:camt.053.001.08")
        private String code;
    }
}
