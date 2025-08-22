package de.ferderer.ebicsdocusign.gateway.domain.ebics.xml;

import jakarta.xml.bind.*;
import java.io.StringReader;
import java.io.StringWriter;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

@Slf4j
class EbicsRequestTest {

    private JAXBContext jaxbContext;
    private Marshaller marshaller;
    private Unmarshaller unmarshaller;

    @BeforeEach
    void setup() throws JAXBException {
        jaxbContext = JAXBContext.newInstance(EbicsRequest.class);
        marshaller = jaxbContext.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        unmarshaller = jaxbContext.createUnmarshaller();
    }

    @Test
    void shouldMarshalSimpleEbicsRequest() throws JAXBException {
        // Create a simple EBICS request
        var product = new EbicsRequest.Product();
        product.setLanguage("de");
        product.setInstituteId("12345678");
        product.setProductName("EBICS DocuSign Gateway");
        
        var orderDetails = new EbicsRequest.OrderDetails();
        orderDetails.setOrderType("HKD");
        orderDetails.setOrderAttribute("DZHNN");
        
        var staticHeader = new EbicsRequest.StaticHeader();
        staticHeader.setHostId("DEUTDEFF");
        staticHeader.setPartnerId("PARTNER001");
        staticHeader.setUserId("USER001");
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

        // Marshal to XML
        StringWriter writer = new StringWriter();
        marshaller.marshal(request, writer);
        String xml = writer.toString();

        log.debug("Generated XML: {}", xml);

        // Basic assertions
        assertNotNull(xml);
        assertTrue(xml.contains("DEUTDEFF"));
        assertTrue(xml.contains("PARTNER001"));
        assertTrue(xml.contains("USER001"));
        assertTrue(xml.contains("H004"));
        assertTrue(xml.contains("urn:org:ebics:H004"));
    }

    @Test
    void shouldUnmarshalEbicsRequest() throws JAXBException {
        String xmlInput = """
            <?xml version="1.0" encoding="UTF-8"?>
            <ebicsRequest xmlns="urn:org:ebics:H004" Version="H004" Revision="1">
              <header authenticate="true">
                <static>
                  <HostID>TESTBANK</HostID>
                  <PartnerID>TESTPARTNER</PartnerID>
                  <UserID>TESTUSER</UserID>
                  <Product Language="de">Test Product</Product>
                  <OrderDetails>
                    <OrderType>HKD</OrderType>
                    <OrderAttribute>DZHNN</OrderAttribute>
                  </OrderDetails>
                  <SecurityMedium>0000</SecurityMedium>
                </static>
                <mutable>
                  <TransactionPhase>Initialisation</TransactionPhase>
                  <SegmentNumber lastSegment="true">1</SegmentNumber>
                </mutable>
              </header>
              <body/>
            </ebicsRequest>
            """;

        // Unmarshal from XML
        EbicsRequest request = (EbicsRequest) unmarshaller.unmarshal(new StringReader(xmlInput));

        // Assertions
        assertNotNull(request);
        assertEquals("H004", request.getVersion());
        assertEquals("1", request.getRevision());
        
        assertNotNull(request.getHeader());
        assertTrue(request.getHeader().isAuthenticate());
        
        assertNotNull(request.getHeader().getStaticHeader());
        assertEquals("TESTBANK", request.getHeader().getStaticHeader().getHostId());
        assertEquals("TESTPARTNER", request.getHeader().getStaticHeader().getPartnerId());
        assertEquals("TESTUSER", request.getHeader().getStaticHeader().getUserId());
        
        assertNotNull(request.getHeader().getStaticHeader().getOrderDetails());
        assertEquals("HKD", request.getHeader().getStaticHeader().getOrderDetails().getOrderType());
        
        assertNotNull(request.getHeader().getMutableHeader());
        assertEquals("Initialisation", request.getHeader().getMutableHeader().getTransactionPhase());
    }

    @Test
    void shouldMarshalAndUnmarshalRoundTrip() throws JAXBException {
        // Create original request
        var original = createSampleRequest();

        // Marshal to XML
        StringWriter writer = new StringWriter();
        marshaller.marshal(original, writer);
        String xml = writer.toString();

        // Unmarshal back to object
        EbicsRequest roundTrip = (EbicsRequest) unmarshaller.unmarshal(new StringReader(xml));

        // Verify they're equivalent
        assertEquals(original.getVersion(), roundTrip.getVersion());
        assertEquals(original.getRevision(), roundTrip.getRevision());
        assertEquals(original.getHeader().getStaticHeader().getHostId(), roundTrip.getHeader().getStaticHeader().getHostId());
        assertEquals(original.getHeader().getStaticHeader().getPartnerId(), roundTrip.getHeader().getStaticHeader().getPartnerId());
        assertEquals(original.getHeader().getStaticHeader().getUserId(), roundTrip.getHeader().getStaticHeader().getUserId());
    }

    @Test
    void shouldHandleComplexOrderDetailsWithDateRange() throws JAXBException {
        var dateRange = new EbicsRequest.DateRange("2024-07-01", "2024-07-31");
        var standardParams = new EbicsRequest.StandardOrderParams(dateRange);
        var orderDetails = new EbicsRequest.OrderDetails("HTD", "DZHNN", standardParams);
        
        var staticHeader = new EbicsRequest.StaticHeader(
            "DEUTDEFF", "PARTNER001", "USER001",
            new EbicsRequest.Product(), orderDetails, null, "0000"
        );
        
        var request = new EbicsRequest("H004", "1", 
            new EbicsRequest.Header(true, staticHeader, new EbicsRequest.MutableHeader()),
            new EbicsRequest.Body()
        );

        // Marshal and check for date range
        StringWriter writer = new StringWriter();
        marshaller.marshal(request, writer);
        String xml = writer.toString();

        assertTrue(xml.contains("2024-07-01"));
        assertTrue(xml.contains("2024-07-31"));
        assertTrue(xml.contains("HTD"));
    }

    private EbicsRequest createSampleRequest() {
        return new EbicsRequest(
            "H004",
            "1",
            new EbicsRequest.Header(
                true,
                new EbicsRequest.StaticHeader(
                    "DEUTDEFF",
                    "PARTNER001",
                    "USER001", 
                    new EbicsRequest.Product(),
                    new EbicsRequest.OrderDetails("HKD", "DZHNN", null),
                    null,
                    "0000"
                ),
                new EbicsRequest.MutableHeader()
            ),
            new EbicsRequest.Body()
        );
    }
}
