package de.ferderer.ebicsdocusign.gateway.domain.ebics;

import de.ferderer.ebicsdocusign.gateway.common.error.AppException;
import de.ferderer.ebicsdocusign.gateway.common.error.ErrorCode;
import de.ferderer.ebicsdocusign.gateway.domain.ebics.Iso20022ParserService.BankStatementRecord;
import de.ferderer.ebicsdocusign.gateway.domain.ebics.model.EbicsConnectionEntity;
import de.ferderer.ebicsdocusign.gateway.domain.ebics.xml.EbicsRequest;
import de.ferderer.ebicsdocusign.gateway.domain.ebics.xml.EbicsResponse;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;
import java.io.StringReader;
import java.io.StringWriter;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
@Slf4j
public class EbicsClientService {
    
    private final RestTemplate restTemplate;
    private final JAXBContext jaxbContext;
    private final Iso20022ParserService iso20022ParserService;
    
    /**
     * Test EBICS connection by sending HKD (Bank Keys Download) request
     */
    public boolean testConnection(EbicsConnectionEntity connection) {
        try {
            log.info("Testing EBICS connection to {} ({})", connection.getBankName(), connection.getHostId());
            
            EbicsRequest request = createHkdRequest(connection);
            EbicsResponse response = sendEbicsRequest(connection, request);
            
            if (response.isSuccess()) {
                log.info("EBICS connection test successful for {}", connection.getHostId());
                return true;
            }
            else {
                log.warn("EBICS connection test failed for {}: {} - {}", 
                    connection.getHostId(), response.getReturnCode(), response.getErrorMessage());
                return false;
            }
            
        }
        catch (Exception e) {
            log.error("EBICS connection test failed for {}: {}", connection.getHostId(), e.getMessage(), e);
            throw new AppException(ErrorCode.EBICS_CONNECTION_FAILED)
                .withData("hostId", connection.getHostId())
                .withData("error", e.getMessage());
        }
    }
    
    /**
     * Download bank statements using HTD (Download Transaction Data) request
     */
    public List<BankStatementRecord> downloadStatements(EbicsConnectionEntity connection, LocalDate fromDate, LocalDate toDate) {
        try {
            log.info("Downloading statements from {} for period {} to {}", 
                connection.getHostId(), fromDate, toDate);
            
            EbicsRequest request = createHtdRequest(connection, fromDate, toDate);
            EbicsResponse response = sendEbicsRequest(connection, request);
            
            if (!response.isSuccess()) {
                log.error("HTD request failed for {}: {} - {}",  connection.getHostId(), response.getReturnCode(), response.getErrorMessage());
                throw new AppException(ErrorCode.EBICS_TRANSACTION_FAILED)
                    .withData("hostId", connection.getHostId())
                    .withData("returnCode", response.getReturnCode())
                    .withData("error", response.getErrorMessage());
            }
            
            if (!response.hasOrderData()) {
                log.warn("No transaction data received from {}", connection.getHostId());
                return List.of();
            }
            
            byte[] orderData = response.getDecodedOrderData();
            log.debug("Received {} bytes of order data from {}", orderData.length, connection.getHostId());

            return parseIso20022Statements(orderData);
        }
        catch (AppException e) {
            throw e;
        }
        catch (Exception e) {
            log.error("Failed to download statements from {}: {}", connection.getHostId(), e.getMessage(), e);
            throw new AppException(ErrorCode.EBICS_TRANSACTION_FAILED)
                .withData("hostId", connection.getHostId())
                .withData("error", e.getMessage());
        }
    }
    
    /**
     * Send EBICS request to bank server
     */
    private EbicsResponse sendEbicsRequest(EbicsConnectionEntity connection, EbicsRequest request) {
        try {
            // Marshal request to XML
            String requestXml = marshalRequest(request);
            log.debug("Sending EBICS request to {}: {}", connection.getBankUrl(), requestXml);
            
            // Prepare HTTP request
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_XML);
            headers.add("User-Agent", "EBICS DocuSign Gateway/1.0");
            headers.add("Accept", "application/xml, text/xml");
            
            HttpEntity<String> httpEntity = new HttpEntity<>(requestXml, headers);
            
            // Send request to bank
            ResponseEntity<String> httpResponse = restTemplate.exchange(
                connection.getBankUrl(),
                HttpMethod.POST,
                httpEntity,
                String.class
            );
            
            if (httpResponse.getStatusCode() != HttpStatus.OK) {
                log.error("HTTP error from EBICS server {}: {}", 
                    connection.getBankUrl(), httpResponse.getStatusCode());
                throw new AppException(ErrorCode.EBICS_CONNECTION_FAILED)
                    .withData("httpStatus", httpResponse.getStatusCode().toString());
            }
            
            String responseXml = httpResponse.getBody();
            log.debug("Received EBICS response from {}: {}", connection.getBankUrl(), responseXml);
            
            // Parse response
            return unmarshalResponse(responseXml);
            
        }
        catch (AppException e) {
            throw e;
        }
        catch (JAXBException | RestClientException e) {
            log.error("Failed to send EBICS request to {}: {}", connection.getBankUrl(), e.getMessage(), e);
            throw new AppException(ErrorCode.EBICS_CONNECTION_FAILED)
                .withData("bankUrl", connection.getBankUrl())
                .withData("error", e.getMessage());
        }
    }
    
    /**
     * Create HKD (Bank Keys Download) request for connection testing
     */
    private EbicsRequest createHkdRequest(EbicsConnectionEntity connection) {
        log.debug("Creating HKD request for {}", connection.getHostId());
        
        var product = new EbicsRequest.Product();
        product.setLanguage("de");
        product.setProductName("EBICS DocuSign Gateway");
        
        var orderDetails = new EbicsRequest.OrderDetails();
        orderDetails.setOrderType("HKD");
        orderDetails.setOrderAttribute("DZHNN");
        
        var staticHeader = new EbicsRequest.StaticHeader();
        staticHeader.setHostId(connection.getHostId());
        staticHeader.setPartnerId(connection.getPartnerId());
        staticHeader.setUserId(connection.getUserId());
        staticHeader.setProduct(product);
        staticHeader.setOrderDetails(orderDetails);
        staticHeader.setSecurityMedium("0000");
        
        var header = new EbicsRequest.Header();
        header.setAuthenticate(true);
        header.setStaticHeader(staticHeader);
        header.setMutableHeader(new EbicsRequest.MutableHeader());
        
        var request = new EbicsRequest();
        request.setVersion("H004");
        request.setRevision("1");
        request.setHeader(header);
        request.setBody(new EbicsRequest.Body());
        
        return request;
    }
    
    /**
     * Create HTD (Download Transaction Data) request for downloading statements
     */
    private EbicsRequest createHtdRequest(EbicsConnectionEntity connection, LocalDate fromDate, LocalDate toDate) {
        log.debug("Creating HTD request for {} from {} to {}", connection.getHostId(), fromDate, toDate);
        
        var dateRange = new EbicsRequest.DateRange();
        dateRange.setStart(fromDate.format(DateTimeFormatter.ISO_LOCAL_DATE));
        dateRange.setEnd(toDate.format(DateTimeFormatter.ISO_LOCAL_DATE));
        
        var standardParams = new EbicsRequest.StandardOrderParams();
        standardParams.setDateRange(dateRange);
        
        var orderDetails = new EbicsRequest.OrderDetails();
        orderDetails.setOrderType("HTD");
        orderDetails.setOrderAttribute("DZHNN");
        orderDetails.setStandardOrderParams(standardParams);
        
        var product = new EbicsRequest.Product();
        product.setLanguage("de");
        product.setProductName("EBICS DocuSign Gateway");
        
        var staticHeader = new EbicsRequest.StaticHeader();
        staticHeader.setHostId(connection.getHostId());
        staticHeader.setPartnerId(connection.getPartnerId());
        staticHeader.setUserId(connection.getUserId());
        staticHeader.setProduct(product);
        staticHeader.setOrderDetails(orderDetails);
        staticHeader.setSecurityMedium("0000");
        
        var header = new EbicsRequest.Header();
        header.setAuthenticate(true);
        header.setStaticHeader(staticHeader);
        header.setMutableHeader(new EbicsRequest.MutableHeader());
        
        var request = new EbicsRequest();
        request.setVersion("H004");
        request.setRevision("1");
        request.setHeader(header);
        request.setBody(new EbicsRequest.Body());
        
        return request;
    }
    
    /**
     * Marshal EBICS request to XML string
     */
    private String marshalRequest(EbicsRequest request) throws JAXBException {
        Marshaller marshaller = jaxbContext.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        
        StringWriter writer = new StringWriter();
        marshaller.marshal(request, writer);
        
        String xml = writer.toString();
        log.debug("Marshalled EBICS request: {}", xml);
        return xml;
    }
    
    /**
     * Unmarshal EBICS response from XML string
     */
    private EbicsResponse unmarshalResponse(String responseXml) throws JAXBException {
        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
        EbicsResponse response = (EbicsResponse) unmarshaller.unmarshal(new StringReader(responseXml));
        
        log.debug("Unmarshalled EBICS response - success: {}, returnCode: {}", 
            response.isSuccess(), response.getReturnCode());
        
        return response;
    }
    
    /**
     * Parse ISO 20022 statements from order data
     */
    private List<BankStatementRecord> parseIso20022Statements(byte[] orderData) {
        return iso20022ParserService.parseStatements(orderData);
    }
}
