package de.ferderer.ebicsdocusign.gateway.domain.ebics.xml;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;
import java.io.StringReader;
import java.util.Base64;
import lombok.extern.slf4j.Slf4j;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@Slf4j
class EbicsResponseTest {

    private JAXBContext jaxbContext;
    private Unmarshaller unmarshaller;

    @BeforeEach
    void setUp() throws JAXBException {
        jaxbContext = JAXBContext.newInstance(EbicsResponse.class);
        unmarshaller = jaxbContext.createUnmarshaller();
    }

    @Test
    void shouldUnmarshalSuccessfulEbicsResponse() throws JAXBException {
        String xmlInput = """
            <?xml version="1.0" encoding="UTF-8"?>
            <ebicsResponse xmlns="urn:org:ebics:H004" Version="H004" Revision="1">
              <header authenticate="true">
                <static>
                  <TransactionPhase>Initialisation</TransactionPhase>
                  <TransactionID>1234567890ABCDEF</TransactionID>
                  <NumSegments>1</NumSegments>
                </static>
                <mutable>
                  <TransactionPhase>Initialisation</TransactionPhase>
                  <SegmentNumber lastSegment="true">1</SegmentNumber>
                  <ReturnCode>000000</ReturnCode>
                  <ReportText>[EBICS_OK] OK</ReportText>
                </mutable>
              </header>
              <body>
                <ReturnCode>000000</ReturnCode>
                <ReportText>[EBICS_OK] OK</ReportText>
              </body>
            </ebicsResponse>
            """;

        EbicsResponse response = (EbicsResponse) unmarshaller.unmarshal(new StringReader(xmlInput));

        log.debug("Parsed EBICS response: {}", response);

        assertNotNull(response);
        assertEquals("H004", response.getVersion());
        assertEquals("1", response.getRevision());
        
        assertNotNull(response.getHeader());
        assertTrue(response.getHeader().isAuthenticate());
        
        assertNotNull(response.getHeader().getStaticHeader());
        assertEquals("1234567890ABCDEF", response.getHeader().getStaticHeader().getTransactionId());
        assertEquals(Integer.valueOf(1), response.getHeader().getStaticHeader().getNumSegments());
        
        assertNotNull(response.getHeader().getMutableHeader());
        assertEquals("000000", response.getHeader().getMutableHeader().getReturnCode());
        assertEquals("[EBICS_OK] OK", response.getHeader().getMutableHeader().getReportText());
        
        assertNotNull(response.getBody());
        assertEquals("000000", response.getBody().getReturnCode());
        
        assertTrue(response.isSuccess());
        assertEquals("000000", response.getReturnCode());
    }

    @Test
    void shouldUnmarshalErrorEbicsResponse() throws JAXBException {
        String xmlInput = """
            <?xml version="1.0" encoding="UTF-8"?>
            <ebicsResponse xmlns="urn:org:ebics:H004" Version="H004" Revision="1">
              <header authenticate="true">
                <mutable>
                  <TransactionPhase>Initialisation</TransactionPhase>
                  <SegmentNumber lastSegment="true">1</SegmentNumber>
                  <ReturnCode>091002</ReturnCode>
                  <ReportText>[EBICS_INVALID_USER_OR_USER_STATE] Invalid user or user state</ReportText>
                </mutable>
              </header>
              <body>
                <ReturnCode>091002</ReturnCode>
                <ReportText>[EBICS_INVALID_USER_OR_USER_STATE] Invalid user or user state</ReportText>
              </body>
            </ebicsResponse>
            """;

        EbicsResponse response = (EbicsResponse) unmarshaller.unmarshal(new StringReader(xmlInput));

        log.debug("Parsed EBICS error response: {}", response);

        assertNotNull(response);
        assertFalse(response.isSuccess());
        assertEquals("091002", response.getReturnCode());
        assertTrue(response.getErrorMessage().contains("Invalid user or user state"));
    }

    @Test
    void shouldUnmarshalResponseWithOrderData() throws JAXBException {
        String sampleOrderData = Base64.getEncoder().encodeToString("Sample bank statement data".getBytes());
        
        String xmlInput = String.format("""
            <?xml version="1.0" encoding="UTF-8"?>
            <ebicsResponse xmlns="urn:org:ebics:H004" Version="H004" Revision="1">
              <header authenticate="true">
                <static>
                  <TransactionPhase>Transfer</TransactionPhase>
                  <TransactionID>ABCDEF1234567890</TransactionID>
                </static>
                <mutable>
                  <TransactionPhase>Transfer</TransactionPhase>
                  <SegmentNumber lastSegment="true">1</SegmentNumber>
                  <ReturnCode>000000</ReturnCode>
                  <ReportText>[EBICS_OK] OK</ReportText>
                </mutable>
              </header>
              <body>
                <ReturnCode>000000</ReturnCode>
                <ReportText>[EBICS_OK] OK</ReportText>
                <DataTransfer>
                  <DataEncryptionInfo authenticate="true">
                    <EncryptionPubKeyDigest Version="X002" Algorithm="http://www.w3.org/2001/04/xmlenc#sha256">
                      abcdef1234567890
                    </EncryptionPubKeyDigest>
                    <TransactionKey>encrypted_transaction_key</TransactionKey>
                  </DataEncryptionInfo>
                  <OrderData>%s</OrderData>
                </DataTransfer>
              </body>
            </ebicsResponse>
            """, sampleOrderData);

        EbicsResponse response = (EbicsResponse) unmarshaller.unmarshal(new StringReader(xmlInput));

        log.debug("Parsed EBICS response with order data: {}", response);

        assertNotNull(response);
        assertTrue(response.isSuccess());
        assertTrue(response.hasOrderData());
        
        byte[] decodedData = response.getDecodedOrderData();
        assertNotNull(decodedData);
        assertEquals("Sample bank statement data", new String(decodedData));
        
        log.debug("Decoded order data: {}", new String(decodedData));
    }

    @Test
    void shouldHandleResponseWithoutOrderData() throws JAXBException {
        String xmlInput = """
            <?xml version="1.0" encoding="UTF-8"?>
            <ebicsResponse xmlns="urn:org:ebics:H004" Version="H004" Revision="1">
              <header authenticate="true">
                <mutable>
                  <ReturnCode>000000</ReturnCode>
                  <ReportText>[EBICS_OK] OK</ReportText>
                </mutable>
              </header>
              <body>
                <ReturnCode>000000</ReturnCode>
                <ReportText>[EBICS_OK] OK</ReportText>
              </body>
            </ebicsResponse>
            """;

        EbicsResponse response = (EbicsResponse) unmarshaller.unmarshal(new StringReader(xmlInput));

        log.debug("Parsed EBICS response without order data: {}", response);

        assertNotNull(response);
        assertTrue(response.isSuccess());
        assertFalse(response.hasOrderData());
        assertNull(response.getDecodedOrderData());
    }

    @Test
    void shouldIdentifyCommonEbicsErrorCodes() throws JAXBException {
        String[] errorCodes = {"091002", "091003", "091004", "091005", "061001", "061002"};
        
        for (String errorCode : errorCodes) {
            String xmlInput = String.format("""
                <?xml version="1.0" encoding="UTF-8"?>
                <ebicsResponse xmlns="urn:org:ebics:H004" Version="H004">
                  <body>
                    <ReturnCode>%s</ReturnCode>
                    <ReportText>[EBICS_ERROR] Error occurred</ReportText>
                  </body>
                </ebicsResponse>
                """, errorCode);

            EbicsResponse response = (EbicsResponse) unmarshaller.unmarshal(new StringReader(xmlInput));
            
            log.debug("Testing error code {}: success={}", errorCode, response.isSuccess());
            
            assertFalse(response.isSuccess(), "Error code " + errorCode + " should not be success");
            assertEquals(errorCode, response.getReturnCode());
        }
    }
}
