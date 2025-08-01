package de.ferderer.ebicsdocusign.gateway.domain.ebics.model;

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
public class Account {
    
    @XmlElement(name = "Id", namespace = "urn:iso:std:iso:20022:tech:xsd:camt.053.001.08")
    private AccountId id;
    
    @XmlElement(name = "Ccy", namespace = "urn:iso:std:iso:20022:tech:xsd:camt.053.001.08")
    private String currency;
    
    @XmlElement(name = "Ownr", namespace = "urn:iso:std:iso:20022:tech:xsd:camt.053.001.08")
    private AccountOwner owner;
    
    @XmlElement(name = "Svcr", namespace = "urn:iso:std:iso:20022:tech:xsd:camt.053.001.08")
    private AccountServicer servicer;
    
    @XmlAccessorType(XmlAccessType.FIELD)
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AccountId {
        
        @XmlElement(name = "Othr", namespace = "urn:iso:std:iso:20022:tech:xsd:camt.053.001.08")
        private OtherAccountId other;
        
        @XmlElement(name = "IBAN", namespace = "urn:iso:std:iso:20022:tech:xsd:camt.053.001.08")
        private String iban;
    }
    
    @XmlAccessorType(XmlAccessType.FIELD)
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OtherAccountId {
        
        @XmlElement(name = "Id", namespace = "urn:iso:std:iso:20022:tech:xsd:camt.053.001.08")
        private String id;
    }
    
    @XmlAccessorType(XmlAccessType.FIELD)
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AccountOwner {
        
        @XmlElement(name = "Nm", namespace = "urn:iso:std:iso:20022:tech:xsd:camt.053.001.08")
        private String name;
        
        @XmlElement(name = "PstlAdr", namespace = "urn:iso:std:iso:20022:tech:xsd:camt.053.001.08")
        private PostalAddress postalAddress;
    }
    
    @XmlAccessorType(XmlAccessType.FIELD)
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AccountServicer {
        
        @XmlElement(name = "FinInstnId", namespace = "urn:iso:std:iso:20022:tech:xsd:camt.053.001.08")
        private FinancialInstitutionId financialInstitutionId;
    }
    
    @XmlAccessorType(XmlAccessType.FIELD)
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FinancialInstitutionId {
        
        @XmlElement(name = "BICFI", namespace = "urn:iso:std:iso:20022:tech:xsd:camt.053.001.08")
        private String bic;
        
        @XmlElement(name = "Nm", namespace = "urn:iso:std:iso:20022:tech:xsd:camt.053.001.08")
        private String name;
        
        @XmlElement(name = "Othr", namespace = "urn:iso:std:iso:20022:tech:xsd:camt.053.001.08")
        private OtherFinancialInstitution other;
    }
    
    @XmlAccessorType(XmlAccessType.FIELD)
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OtherFinancialInstitution {
        
        @XmlElement(name = "Id", namespace = "urn:iso:std:iso:20022:tech:xsd:camt.053.001.08")
        private String id;
        
        @XmlElement(name = "Issr", namespace = "urn:iso:std:iso:20022:tech:xsd:camt.053.001.08")
        private String issuer;
    }

    @XmlAccessorType(XmlAccessType.FIELD)
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PostalAddress {

        @XmlElement(name = "AdrLine", namespace = "urn:iso:std:iso:20022:tech:xsd:camt.053.001.08")
        private List<String> addressLines;

        @XmlElement(name = "Ctry", namespace = "urn:iso:std:iso:20022:tech:xsd:camt.053.001.08")
        private String country;

        @XmlElement(name = "PstCd", namespace = "urn:iso:std:iso:20022:tech:xsd:camt.053.001.08")
        private String postalCode;

        @XmlElement(name = "TwnNm", namespace = "urn:iso:std:iso:20022:tech:xsd:camt.053.001.08")
        private String townName;
    }

    @XmlAccessorType(XmlAccessType.FIELD)
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Balance {

        @XmlElement(name = "Tp", namespace = "urn:iso:std:iso:20022:tech:xsd:camt.053.001.08")
        private BalanceType type;

        @XmlElement(name = "Amt", namespace = "urn:iso:std:iso:20022:tech:xsd:camt.053.001.08")
        private Amount amount;

        @XmlElement(name = "CdtDbtInd", namespace = "urn:iso:std:iso:20022:tech:xsd:camt.053.001.08")
        private String creditDebitIndicator;

        @XmlElement(name = "Dt", namespace = "urn:iso:std:iso:20022:tech:xsd:camt.053.001.08")
        private DateAndDateTimeChoice date;

        @XmlAccessorType(XmlAccessType.FIELD)
        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        public static class BalanceType {

            @XmlElement(name = "CdOrPrtry", namespace = "urn:iso:std:iso:20022:tech:xsd:camt.053.001.08")
            private CodeOrProprietary codeOrProprietary;
        }

        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlType(name = "iso20022AccountCodeOrProprietary")
        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        public static class CodeOrProprietary {

            @XmlElement(name = "Cd", namespace = "urn:iso:std:iso:20022:tech:xsd:camt.053.001.08")
            private String code;
        }

        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlType(name = "iso20022AccountDateAndDateTimeChoice")
        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        public static class DateAndDateTimeChoice {

            @XmlElement(name = "Dt", namespace = "urn:iso:std:iso:20022:tech:xsd:camt.053.001.08")
            @XmlJavaTypeAdapter(LocalDateAdapter.class)
            private LocalDate date;
        }
    }

    @XmlAccessorType(XmlAccessType.FIELD)
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Amount {

        @XmlAttribute(name = "Ccy")
        private String currency;

        @XmlValue
        private BigDecimal value;
    }

    @XmlAccessorType(XmlAccessType.FIELD)
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TransactionsSummary {

        @XmlElement(name = "TtlNtries", namespace = "urn:iso:std:iso:20022:tech:xsd:camt.053.001.08")
        private TotalEntries totalEntries;

        @XmlElement(name = "TtlCdtNtries", namespace = "urn:iso:std:iso:20022:tech:xsd:camt.053.001.08")
        private TotalCreditEntries totalCreditEntries;

        @XmlElement(name = "TtlDbtNtries", namespace = "urn:iso:std:iso:20022:tech:xsd:camt.053.001.08")
        private TotalDebitEntries totalDebitEntries;

        @XmlAccessorType(XmlAccessType.FIELD)
        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        public static class TotalEntries {

            @XmlElement(name = "NbOfNtries", namespace = "urn:iso:std:iso:20022:tech:xsd:camt.053.001.08")
            private Integer numberOfEntries;

            @XmlElement(name = "Sum", namespace = "urn:iso:std:iso:20022:tech:xsd:camt.053.001.08")
            private BigDecimal sum;

            @XmlElement(name = "TtlNetNtry", namespace = "urn:iso:std:iso:20022:tech:xsd:camt.053.001.08")
            private TotalNetEntry totalNetEntry;
        }

        @XmlAccessorType(XmlAccessType.FIELD)
        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        public static class TotalNetEntry {

            @XmlElement(name = "Amt", namespace = "urn:iso:std:iso:20022:tech:xsd:camt.053.001.08")
            private BigDecimal amount;

            @XmlElement(name = "CdtDbtInd", namespace = "urn:iso:std:iso:20022:tech:xsd:camt.053.001.08")
            private String creditDebitIndicator;
        }

        @XmlAccessorType(XmlAccessType.FIELD)
        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        public static class TotalCreditEntries {

            @XmlElement(name = "NbOfNtries", namespace = "urn:iso:std:iso:20022:tech:xsd:camt.053.001.08")
            private Integer numberOfEntries;

            @XmlElement(name = "Sum", namespace = "urn:iso:std:iso:20022:tech:xsd:camt.053.001.08")
            private BigDecimal sum;
        }

        @XmlAccessorType(XmlAccessType.FIELD)
        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        public static class TotalDebitEntries {

            @XmlElement(name = "NbOfNtries", namespace = "urn:iso:std:iso:20022:tech:xsd:camt.053.001.08")
            private Integer numberOfEntries;

            @XmlElement(name = "Sum", namespace = "urn:iso:std:iso:20022:tech:xsd:camt.053.001.08")
            private BigDecimal sum;
        }
    }
}
