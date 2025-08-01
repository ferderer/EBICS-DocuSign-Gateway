package de.ferderer.ebicsdocusign.gateway.domain.ws;

import de.ferderer.ebicsdocusign.gateway.common.test.WebIntegrationTestBase;
import de.ferderer.ebicsdocusign.gateway.domain.payments.api.ProcessPaymentManual.ProcessPaymentRequest;
import de.ferderer.ebicsdocusign.gateway.domain.ws.PaymentUpdatesBroadcaster.PaymentUpdateMessage;
import static io.restassured.RestAssured.given;
import io.restassured.http.ContentType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.RestTemplateXhrTransport;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

class PaymentUpdatesBroadcasterIT extends WebIntegrationTestBase {

    private WebSocketStompClient stompClient;
    private BlockingQueue<PaymentUpdateMessage> receivedMessages;

    @BeforeEach
    public void before() throws Exception {
        receivedMessages = new LinkedBlockingDeque<>();
        SockJsClient sockJsClient = new SockJsClient(List.of(
            new WebSocketTransport(new StandardWebSocketClient()),
            new RestTemplateXhrTransport()));

        this.stompClient = new WebSocketStompClient(sockJsClient);
        this.stompClient.setMessageConverter(new MappingJackson2MessageConverter());

        String url = "http://localhost:" + port + "/ws/payments";
        
        stompClient
            .connectAsync(url, new StompSessionHandlerAdapter() {})
            .get(5, TimeUnit.SECONDS)
            .subscribe("/topic/payments", new StompFrameHandler() {
                @Override
                public Type getPayloadType(StompHeaders headers) {
                    return PaymentUpdatesBroadcaster.PaymentUpdateMessage.class;
                }

                @Override
                public void handleFrame(StompHeaders headers, Object payload) {
                    receivedMessages.offer((PaymentUpdatesBroadcaster.PaymentUpdateMessage) payload);
                }
            });
        
        // Give subscription time to register
        Thread.sleep(500);
    }

    @Test
    @Disabled
    void shouldBroadcastPaymentProcessedMessage() throws Exception {
        var request = new ProcessPaymentRequest(
            3001L,
            "websocket@test.com",
            "WebSocket Test Company",
            "Testing WebSocket broadcast",
            "WS-TEST-001",
            false
        );

        given()
            .contentType(ContentType.JSON)
            .body(request)
        .when()
            .post("/api/payments/9003/process")
        .then()
            .log().all()
            .statusCode(200)
            .body("paymentId", is(9003));

        // Wait for WebSocket message
        PaymentUpdatesBroadcaster.PaymentUpdateMessage message = 
            receivedMessages.poll(3, TimeUnit.SECONDS);
        
        assertNotNull(message, "Should receive WebSocket message");
        assertEquals("PAYMENT_PROCESSED", message.type());
        assertEquals(9003L, message.paymentId());
        assertEquals("TEST-TXN-20240726-003", message.transactionId());
        assertEquals("CONTRACT_PENDING", message.status());
        assertNotNull(message.workflowId());
        assertEquals("ENVELOPE_CREATED", message.workflowStatus());
        assertNotNull(message.timestamp());
        assertTrue(message.details().containsKey("templateName"));
    }
    
    @Test
    @Disabled
    void shouldBroadcastMultipleMessageTypes() throws Exception {
        // Process multiple payments to generate different message types
        var request1 = new ProcessPaymentRequest(
            3002L, "test1@example.com", "Test Company 1", "Test 1", "TEST-1", false
        );
        var request2 = new ProcessPaymentRequest(
            null, "test2@example.com", "Test Company 2", "Test 2", "TEST-2", false
        );

        given()
            .contentType(ContentType.JSON)
            .body(request1)
        .when()
            .post("/api/payments/9004/process")
        .then()
            .log().all();

        given()
            .contentType(ContentType.JSON)
            .body(request2)
        .when()
            .post("/api/payments/9005/process")
        .then()
            .log().all();


        // Should receive 2 messages
        PaymentUpdatesBroadcaster.PaymentUpdateMessage message1 = 
            receivedMessages.poll(3, TimeUnit.SECONDS);
        PaymentUpdatesBroadcaster.PaymentUpdateMessage message2 = 
            receivedMessages.poll(3, TimeUnit.SECONDS);
        
        assertNotNull(message1);
        assertNotNull(message2);
        
        assertEquals("PAYMENT_PROCESSED", message1.type());
        assertEquals("PAYMENT_PROCESSED", message2.type());
        
        // Different payments should have different IDs
        assertNotEquals(message1.paymentId(), message2.paymentId());
    }
    
    @Test
    @Disabled
    void shouldHandleWebSocketConnectionFailureGracefully() throws Exception {
        stompClient.stop();

        given()
            .contentType(ContentType.JSON)
            .body(new ProcessPaymentRequest(3001L, "test@example.com", "Test Company", "Test", "TEST", false))
        .when()
            .post("/api/payments/9006/process")
        .then()
            .statusCode(200)
            .body("paymentId", is(9006));
    }
}