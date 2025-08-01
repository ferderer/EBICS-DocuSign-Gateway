package de.ferderer.ebicsdocusign.gateway.domain.ebics.xml;

import jakarta.xml.bind.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@XmlRootElement(name = "ebicsResponse", namespace = "urn:org:ebics:H004")
@XmlAccessorType(XmlAccessType.FIELD)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Slf4j
public class EbicsResponse {
    
    @XmlAttribute(name = "Version")
    private String version;
    
    @XmlAttribute(name = "Revision") 
    private String revision;
    
    @XmlElement(name = "header", namespace = "urn:org:ebics:H004")
    private Header header;
    
    @XmlElement(name = "body", namespace = "urn:org:ebics:H004")
    private Body body;
    
    @XmlAccessorType(XmlAccessType.FIELD)
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Header {
        
        @XmlAttribute(name = "authenticate")
        private boolean authenticate;
        
        @XmlElement(name = "static", namespace = "urn:org:ebics:H004")
        private StaticHeader staticHeader;
        
        @XmlElement(name = "mutable", namespace = "urn:org:ebics:H004")
        private MutableHeader mutableHeader;
    }
    
    @XmlAccessorType(XmlAccessType.FIELD)
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StaticHeader {
        
        @XmlElement(name = "TransactionPhase", namespace = "urn:org:ebics:H004")
        private String transactionPhase;
        
        @XmlElement(name = "TransactionID", namespace = "urn:org:ebics:H004")
        private String transactionId;
        
        @XmlElement(name = "NumSegments", namespace = "urn:org:ebics:H004")
        private Integer numSegments;
    }
    
    @XmlAccessorType(XmlAccessType.FIELD)
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MutableHeader {
        
        @XmlElement(name = "TransactionPhase", namespace = "urn:org:ebics:H004")
        private String transactionPhase;
        
        @XmlElement(name = "SegmentNumber", namespace = "urn:org:ebics:H004")
        private SegmentNumber segmentNumber;
        
        @XmlElement(name = "ReturnCode", namespace = "urn:org:ebics:H004")
        private String returnCode;
        
        @XmlElement(name = "ReportText", namespace = "urn:org:ebics:H004")
        private String reportText;
    }
    
    @XmlAccessorType(XmlAccessType.FIELD)
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SegmentNumber {
        
        @XmlAttribute(name = "lastSegment")
        private boolean lastSegment;
        
        @XmlValue
        private int value;
    }
    
    @XmlAccessorType(XmlAccessType.FIELD)
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Body {
        
        @XmlElement(name = "ReturnCode", namespace = "urn:org:ebics:H004")
        private String returnCode;
        
        @XmlElement(name = "ReportText", namespace = "urn:org:ebics:H004")
        private String reportText;
        
        @XmlElement(name = "DataTransfer", namespace = "urn:org:ebics:H004")
        private DataTransfer dataTransfer;
    }
    
    @XmlAccessorType(XmlAccessType.FIELD)
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DataTransfer {
        
        @XmlElement(name = "DataEncryptionInfo", namespace = "urn:org:ebics:H004")
        private DataEncryptionInfo dataEncryptionInfo;
        
        @XmlElement(name = "OrderData", namespace = "urn:org:ebics:H004")
        private String orderData;
    }
    
    @XmlAccessorType(XmlAccessType.FIELD)
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DataEncryptionInfo {
        
        @XmlAttribute(name = "authenticate")
        private boolean authenticate;
        
        @XmlElement(name = "EncryptionPubKeyDigest", namespace = "urn:org:ebics:H004")
        private PubKeyDigest encryptionPubKeyDigest;
        
        @XmlElement(name = "TransactionKey", namespace = "urn:org:ebics:H004")
        private String transactionKey;
    }
    
    @XmlAccessorType(XmlAccessType.FIELD)
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PubKeyDigest {
        
        @XmlAttribute(name = "Version")
        private String version;
        
        @XmlAttribute(name = "Algorithm")
        private String algorithm;
        
        @XmlValue
        private String digest;
    }
    
    /**
     * Check if the EBICS response indicates success
     */
    public boolean isSuccess() {
        String bodyReturnCode = body != null ? body.getReturnCode() : null;
        String headerReturnCode = header != null && header.getMutableHeader() != null ? 
            header.getMutableHeader().getReturnCode() : null;
            
        log.debug("Checking EBICS response success - body: {}, header: {}", bodyReturnCode, headerReturnCode);
        
        return "000000".equals(bodyReturnCode) || "000000".equals(headerReturnCode);
    }
    
    /**
     * Get error message from response
     */
    public String getErrorMessage() {
        if (body != null && body.getReportText() != null) {
            return body.getReportText();
        }
        if (header != null && header.getMutableHeader() != null && 
            header.getMutableHeader().getReportText() != null) {
            return header.getMutableHeader().getReportText();
        }
        return "Unknown EBICS error";
    }
    
    /**
     * Get return code (error code) from response
     */
    public String getReturnCode() {
        if (body != null && body.getReturnCode() != null) {
            return body.getReturnCode();
        }
        if (header != null && header.getMutableHeader() != null) {
            return header.getMutableHeader().getReturnCode();
        }
        return null;
    }
    
    /**
     * Check if response contains order data
     */
    public boolean hasOrderData() {
        boolean hasData = body != null && body.getDataTransfer() != null && 
            body.getDataTransfer().getOrderData() != null;
        log.debug("EBICS response has order data: {}", hasData);
        return hasData;
    }
    
    /**
     * Get decoded order data (Base64 decoded)
     */
    public byte[] getDecodedOrderData() {
        if (!hasOrderData()) {
            log.debug("No order data available in EBICS response");
            return null;
        }
        
        try {
            String orderData = body.getDataTransfer().getOrderData();
            byte[] decoded = java.util.Base64.getDecoder().decode(orderData);
            log.debug("Decoded EBICS order data: {} bytes", decoded.length);
            return decoded;
        } catch (Exception e) {
            log.error("Failed to decode EBICS order data: {}", e.getMessage());
            return null;
        }
    }
}
