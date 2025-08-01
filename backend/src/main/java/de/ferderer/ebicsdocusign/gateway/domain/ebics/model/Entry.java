package de.ferderer.ebicsdocusign.gateway.domain.ebics.model;

import de.ferderer.ebicsdocusign.gateway.domain.ebics.model.Account.Amount;
import jakarta.xml.bind.annotation.*;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@XmlAccessorType(XmlAccessType.FIELD)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Entry {
    
    @XmlElement(name = "NtryRef", namespace = "urn:iso:std:iso:20022:tech:xsd:camt.053.001.08")
    private String entryReference;
    
    @XmlElement(name = "Amt", namespace = "urn:iso:std:iso:20022:tech:xsd:camt.053.001.08")
    private Amount amount;
    
    @XmlElement(name = "CdtDbtInd", namespace = "urn:iso:std:iso:20022:tech:xsd:camt.053.001.08")
    private String creditDebitIndicator;
    
    @XmlElement(name = "RvslInd", namespace = "urn:iso:std:iso:20022:tech:xsd:camt.053.001.08")
    private Boolean reversalIndicator;
    
    @XmlElement(name = "Sts", namespace = "urn:iso:std:iso:20022:tech:xsd:camt.053.001.08")
    private EntryStatus status;
    
    @XmlElement(name = "BookgDt", namespace = "urn:iso:std:iso:20022:tech:xsd:camt.053.001.08")
    private DateAndDateTimeChoice bookingDate;
    
    @XmlElement(name = "ValDt", namespace = "urn:iso:std:iso:20022:tech:xsd:camt.053.001.08")
    private DateAndDateTimeChoice valueDate;
    
    @XmlElement(name = "AcctSvcrRef", namespace = "urn:iso:std:iso:20022:tech:xsd:camt.053.001.08")
    private String accountServicerReference;
    
    @XmlElement(name = "BkTxCd", namespace = "urn:iso:std:iso:20022:tech:xsd:camt.053.001.08")
    private BankTransactionCode bankTransactionCode;
    
    @XmlElement(name = "AmtDtls", namespace = "urn:iso:std:iso:20022:tech:xsd:camt.053.001.08")
    private AmountDetails amountDetails;
    
    @XmlElement(name = "Chrgs", namespace = "urn:iso:std:iso:20022:tech:xsd:camt.053.001.08")
    private Charges charges;
    
    @XmlElement(name = "NtryDtls", namespace = "urn:iso:std:iso:20022:tech:xsd:camt.053.001.08")
    private EntryDetails entryDetails;
    
    @XmlElement(name = "AddtlNtryInf", namespace = "urn:iso:std:iso:20022:tech:xsd:camt.053.001.08")
    private String additionalEntryInfo;
    
    @XmlAccessorType(XmlAccessType.FIELD)
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EntryStatus {
        
        @XmlElement(name = "Cd", namespace = "urn:iso:std:iso:20022:tech:xsd:camt.053.001.08")
        private String code;
    }
    
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "iso20022EntryDateAndDateTimeChoice")
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DateAndDateTimeChoice {
        
        @XmlElement(name = "Dt", namespace = "urn:iso:std:iso:20022:tech:xsd:camt.053.001.08")
        @XmlJavaTypeAdapter(LocalDateAdapter.class)
        private LocalDate date;
    }
    
    @XmlAccessorType(XmlAccessType.FIELD)
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BankTransactionCode {
        
        @XmlElement(name = "Domn", namespace = "urn:iso:std:iso:20022:tech:xsd:camt.053.001.08")
        private Domain domain;
        
        @XmlElement(name = "Prtry", namespace = "urn:iso:std:iso:20022:tech:xsd:camt.053.001.08")
        private Proprietary proprietary;
    }
    
    @XmlAccessorType(XmlAccessType.FIELD)
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Domain {
        
        @XmlElement(name = "Cd", namespace = "urn:iso:std:iso:20022:tech:xsd:camt.053.001.08")
        private String code;
        
        @XmlElement(name = "Fmly", namespace = "urn:iso:std:iso:20022:tech:xsd:camt.053.001.08")
        private Family family;
    }
    
    @XmlAccessorType(XmlAccessType.FIELD)
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Family {
        
        @XmlElement(name = "Cd", namespace = "urn:iso:std:iso:20022:tech:xsd:camt.053.001.08")
        private String code;
        
        @XmlElement(name = "SubFmlyCd", namespace = "urn:iso:std:iso:20022:tech:xsd:camt.053.001.08")
        private String subFamilyCode;
    }
    
    @XmlAccessorType(XmlAccessType.FIELD)
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Proprietary {
        
        @XmlElement(name = "Cd", namespace = "urn:iso:std:iso:20022:tech:xsd:camt.053.001.08")
        private String code;
    }
    
    @XmlAccessorType(XmlAccessType.FIELD)
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AmountDetails {
        
        @XmlElement(name = "InstdAmt", namespace = "urn:iso:std:iso:20022:tech:xsd:camt.053.001.08")
        private Amount instructedAmount;
        
        @XmlElement(name = "TxAmt", namespace = "urn:iso:std:iso:20022:tech:xsd:camt.053.001.08")
        private Amount transactionAmount;
        
        @XmlElement(name = "CntrValAmt", namespace = "urn:iso:std:iso:20022:tech:xsd:camt.053.001.08")
        private CounterValueAmount counterValueAmount;
    }
    
    @XmlAccessorType(XmlAccessType.FIELD)
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CounterValueAmount {
        
        @XmlElement(name = "Amt", namespace = "urn:iso:std:iso:20022:tech:xsd:camt.053.001.08")
        private Amount amount;
        
        @XmlElement(name = "CcyXchg", namespace = "urn:iso:std:iso:20022:tech:xsd:camt.053.001.08")
        private CurrencyExchange currencyExchange;
    }
    
    @XmlAccessorType(XmlAccessType.FIELD)
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CurrencyExchange {
        
        @XmlElement(name = "SrcCcy", namespace = "urn:iso:std:iso:20022:tech:xsd:camt.053.001.08")
        private String sourceCurrency;
        
        @XmlElement(name = "TrgtCcy", namespace = "urn:iso:std:iso:20022:tech:xsd:camt.053.001.08")
        private String targetCurrency;
        
        @XmlElement(name = "XchgRate", namespace = "urn:iso:std:iso:20022:tech:xsd:camt.053.001.08")
        private BigDecimal exchangeRate;
        
        @XmlElement(name = "QtnDt", namespace = "urn:iso:std:iso:20022:tech:xsd:camt.053.001.08")
        private String quotationDate;
    }
    
    @XmlAccessorType(XmlAccessType.FIELD)
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Charges {
        
        @XmlElement(name = "TtlChrgsAndTaxAmt", namespace = "urn:iso:std:iso:20022:tech:xsd:camt.053.001.08")
        private Amount totalChargesAndTaxAmount;
        
        @XmlElement(name = "Rcrd", namespace = "urn:iso:std:iso:20022:tech:xsd:camt.053.001.08")
        private List<ChargeRecord> records;
    }
    
    @XmlAccessorType(XmlAccessType.FIELD)
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ChargeRecord {
        
        @XmlElement(name = "Amt", namespace = "urn:iso:std:iso:20022:tech:xsd:camt.053.001.08")
        private Amount amount;
        
        @XmlElement(name = "CdtDbtInd", namespace = "urn:iso:std:iso:20022:tech:xsd:camt.053.001.08")
        private String creditDebitIndicator;
        
        @XmlElement(name = "ChrgInclInd", namespace = "urn:iso:std:iso:20022:tech:xsd:camt.053.001.08")
        private Boolean chargeIncludedIndicator;
        
        @XmlElement(name = "Tp", namespace = "urn:iso:std:iso:20022:tech:xsd:camt.053.001.08")
        private ChargeType type;
        
        @XmlElement(name = "Rate", namespace = "urn:iso:std:iso:20022:tech:xsd:camt.053.001.08")
        private BigDecimal rate;
        
        @XmlElement(name = "Br", namespace = "urn:iso:std:iso:20022:tech:xsd:camt.053.001.08")
        private String bearer;
    }
    
    @XmlAccessorType(XmlAccessType.FIELD)
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ChargeType {
        
        @XmlElement(name = "Prtry", namespace = "urn:iso:std:iso:20022:tech:xsd:camt.053.001.08")
        private Proprietary proprietary;
    }
}
