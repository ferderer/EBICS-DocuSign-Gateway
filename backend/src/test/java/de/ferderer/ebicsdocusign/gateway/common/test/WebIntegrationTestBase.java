package de.ferderer.ebicsdocusign.gateway.common.test;

import de.ferderer.ebicsdocusign.gateway.App;
import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureTestEntityManager;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestEntityManager
@ContextConfiguration(classes = App.class)
@Transactional
public class WebIntegrationTestBase {
    @LocalServerPort
    protected Integer port;

    @BeforeEach
    public void setUp() {
        RestAssured.port = port;
    }
}