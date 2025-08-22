package de.ferderer.ebicsdocusign.gateway.config;

import de.ferderer.ebicsdocusign.gateway.domain.ebics.model.BankStatement;
import de.ferderer.ebicsdocusign.gateway.domain.ebics.xml.EbicsRequest;
import de.ferderer.ebicsdocusign.gateway.domain.ebics.xml.EbicsResponse;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
@Slf4j
public class EbicsConfiguration {
    
    @Bean
    public JAXBContext jaxbContext() throws JAXBException {
        return JAXBContext.newInstance(
            EbicsRequest.class,
            EbicsResponse.class,
            BankStatement.class
        );
    }
    
    @Bean
    public RestTemplate restTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        
        // TODO: Configure timeouts, SSL settings, etc. for production
        // restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory());
        
        return restTemplate;
    }
}