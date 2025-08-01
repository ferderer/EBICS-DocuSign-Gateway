package de.ferderer.ebicsdocusign.gateway.domain.ebics.xml;

import jakarta.xml.bind.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@XmlRootElement(name = "ebicsRequest", namespace = "urn:org:ebics:H004")
@XmlAccessorType(XmlAccessType.FIELD)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EbicsRequest {
    
    @XmlAttribute(name = "Version")
    private String version = "H004";
    
    @XmlAttribute(name = "Revision")
    private String revision = "1";
    
    @XmlElement(name = "header", namespace = "urn:org:ebics:H004")
    private Header header;
    
    @XmlElement(name = "body", namespace = "urn:org:ebics:H004")
    private Body body;
    
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "ebicsRequestHeader")
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Header {
        
        @XmlAttribute(name = "authenticate")
        private boolean authenticate = true;
        
        @XmlElement(name = "static", namespace = "urn:org:ebics:H004")
        private StaticHeader staticHeader;
        
        @XmlElement(name = "mutable", namespace = "urn:org:ebics:H004")
        private MutableHeader mutableHeader;
    }
    
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "ebicsRequestStaticHeader")
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StaticHeader {
        
        @XmlElement(name = "HostID", namespace = "urn:org:ebics:H004")
        private String hostId;
        
        @XmlElement(name = "PartnerID", namespace = "urn:org:ebics:H004")
        private String partnerId;
        
        @XmlElement(name = "UserID", namespace = "urn:org:ebics:H004")
        private String userId;
        
        @XmlElement(name = "Product", namespace = "urn:org:ebics:H004")
        private Product product;
        
        @XmlElement(name = "OrderDetails", namespace = "urn:org:ebics:H004")
        private OrderDetails orderDetails;
        
        @XmlElement(name = "BankPubKeyDigests", namespace = "urn:org:ebics:H004")
        private BankPubKeyDigests bankPubKeyDigests;
        
        @XmlElement(name = "SecurityMedium", namespace = "urn:org:ebics:H004")
        private String securityMedium = "0000";
    }
    
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "ebicsRequestMutableHeader")
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MutableHeader {
        
        @XmlElement(name = "TransactionPhase", namespace = "urn:org:ebics:H004")
        private String transactionPhase = "Initialisation";
        
        @XmlElement(name = "SegmentNumber", namespace = "urn:org:ebics:H004")
        private SegmentNumber segmentNumber = new SegmentNumber();
    }
    
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "ebicsRequestProduct")
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Product {
        
        @XmlAttribute(name = "Language")
        private String language = "de";
        
        @XmlAttribute(name = "InstituteID")
        private String instituteId;
        
        @XmlValue
        private String productName = "EBICS DocuSign Gateway";
    }
    
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "ebicsRequestOrderDetails")
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrderDetails {
        
        @XmlElement(name = "OrderType", namespace = "urn:org:ebics:H004")
        private String orderType;
        
        @XmlElement(name = "OrderAttribute", namespace = "urn:org:ebics:H004")
        private String orderAttribute = "DZHNN";
        
        @XmlElement(name = "StandardOrderParams", namespace = "urn:org:ebics:H004")
        private StandardOrderParams standardOrderParams;
    }
    
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "ebicsRequestStandardOrderParams")
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StandardOrderParams {
        
        @XmlElement(name = "DateRange", namespace = "urn:org:ebics:H004")
        private DateRange dateRange;
    }
    
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "ebicsRequestDateRange")
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DateRange {
        
        @XmlElement(name = "Start", namespace = "urn:org:ebics:H004")
        private String start;
        
        @XmlElement(name = "End", namespace = "urn:org:ebics:H004")
        private String end;
    }
    
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "ebicsRequestBankPubKeyDigests")
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BankPubKeyDigests {
        
        @XmlElement(name = "Authentication", namespace = "urn:org:ebics:H004")
        private PubKeyDigest authentication;
        
        @XmlElement(name = "Encryption", namespace = "urn:org:ebics:H004")
        private PubKeyDigest encryption;
    }
    
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "ebicsRequestPubKeyDigest")
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PubKeyDigest {
        
        @XmlAttribute(name = "Version")
        private String version = "X002";
        
        @XmlAttribute(name = "Algorithm")
        private String algorithm = "http://www.w3.org/2001/04/xmlenc#sha256";
        
        @XmlValue
        private String digest;
    }
    
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "ebicsRequestSegmentNumber")
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SegmentNumber {
        
        @XmlAttribute(name = "lastSegment")
        private boolean lastSegment = true;
        
        @XmlValue
        private int value = 1;
    }
    
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "ebicsRequestBody")
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Body {
        
        @XmlElement(name = "DataTransfer", namespace = "urn:org:ebics:H004")
        private DataTransfer dataTransfer;
    }
    
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "ebicsRequestDataTransfer")
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DataTransfer {
        
        @XmlElement(name = "OrderData", namespace = "urn:org:ebics:H004")
        private String orderData;
    }
}
