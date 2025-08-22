package de.ferderer.ebicsdocusign.gateway.domain.ebics.model;

import de.ferderer.ebicsdocusign.gateway.domain.ebics.model.Account.Balance;
import de.ferderer.ebicsdocusign.gateway.domain.ebics.model.Account.TransactionsSummary;
import jakarta.xml.bind.annotation.*;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@XmlRootElement(name = "Document", namespace = "urn:iso:std:iso:20022:tech:xsd:camt.053.001.08")
@XmlAccessorType(XmlAccessType.FIELD)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BankStatement {
    
    @XmlElement(name = "BkToCstmrStmt", namespace = "urn:iso:std:iso:20022:tech:xsd:camt.053.001.08")
    private BankToCustomerStatement bankToCustomerStatement;
    
    @XmlAccessorType(XmlAccessType.FIELD)
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BankToCustomerStatement {
        
        @XmlElement(name = "GrpHdr", namespace = "urn:iso:std:iso:20022:tech:xsd:camt.053.001.08")
        private GroupHeader groupHeader;
        
        @XmlElement(name = "Stmt", namespace = "urn:iso:std:iso:20022:tech:xsd:camt.053.001.08")
        private Statement statement;
    }
    
    @XmlAccessorType(XmlAccessType.FIELD)
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GroupHeader {
        
        @XmlElement(name = "MsgId", namespace = "urn:iso:std:iso:20022:tech:xsd:camt.053.001.08")
        private String messageId;
        
        @XmlElement(name = "CreDtTm", namespace = "urn:iso:std:iso:20022:tech:xsd:camt.053.001.08")
        @XmlJavaTypeAdapter(LocalDateTimeAdapter.class)
        private LocalDateTime creationDateTime;
        
        @XmlElement(name = "MsgPgntn", namespace = "urn:iso:std:iso:20022:tech:xsd:camt.053.001.08")
        private MessagePagination messagePagination;
        
        @XmlElement(name = "AddtlInf", namespace = "urn:iso:std:iso:20022:tech:xsd:camt.053.001.08")
        private String additionalInfo;
    }
    
    @XmlAccessorType(XmlAccessType.FIELD)
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MessagePagination {
        
        @XmlElement(name = "PgNb", namespace = "urn:iso:std:iso:20022:tech:xsd:camt.053.001.08")
        private Integer pageNumber;
        
        @XmlElement(name = "LastPgInd", namespace = "urn:iso:std:iso:20022:tech:xsd:camt.053.001.08")
        private Boolean lastPageIndicator;
    }
    
    @XmlAccessorType(XmlAccessType.FIELD)
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Statement {
        
        @XmlElement(name = "Id", namespace = "urn:iso:std:iso:20022:tech:xsd:camt.053.001.08")
        private String id;
        
        @XmlElement(name = "ElctrncSeqNb", namespace = "urn:iso:std:iso:20022:tech:xsd:camt.053.001.08")
        private Integer electronicSequenceNumber;
        
        @XmlElement(name = "CreDtTm", namespace = "urn:iso:std:iso:20022:tech:xsd:camt.053.001.08")
        @XmlJavaTypeAdapter(LocalDateTimeAdapter.class)
        private LocalDateTime creationDateTime;
        
        @XmlElement(name = "FrToDt", namespace = "urn:iso:std:iso:20022:tech:xsd:camt.053.001.08")
        private FromToDate fromToDate;
        
        @XmlElement(name = "Acct", namespace = "urn:iso:std:iso:20022:tech:xsd:camt.053.001.08")
        private Account account;
        
        @XmlElement(name = "Bal", namespace = "urn:iso:std:iso:20022:tech:xsd:camt.053.001.08")
        private List<Balance> balances;
        
        @XmlElement(name = "TxsSummry", namespace = "urn:iso:std:iso:20022:tech:xsd:camt.053.001.08")
        private TransactionsSummary transactionsSummary;
        
        @XmlElement(name = "Ntry", namespace = "urn:iso:std:iso:20022:tech:xsd:camt.053.001.08")
        private List<Entry> entries;
    }
    
    @XmlAccessorType(XmlAccessType.FIELD)
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FromToDate {
        
        @XmlElement(name = "FrDtTm", namespace = "urn:iso:std:iso:20022:tech:xsd:camt.053.001.08")
        @XmlJavaTypeAdapter(LocalDateTimeAdapter.class)
        private LocalDateTime fromDateTime;
        
        @XmlElement(name = "ToDtTm", namespace = "urn:iso:std:iso:20022:tech:xsd:camt.053.001.08")
        @XmlJavaTypeAdapter(LocalDateTimeAdapter.class)
        private LocalDateTime toDateTime;
    }
}
