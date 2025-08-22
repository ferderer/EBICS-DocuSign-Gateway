package de.ferderer.ebicsdocusign.gateway.config;

import de.ferderer.ebicsdocusign.gateway.domain.Endpoints;
import org.springframework.boot.web.server.ErrorPage;
import org.springframework.boot.web.server.ErrorPageRegistrar;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ErrorPageConfig {

    @Bean
    public ErrorPageRegistrar errorPageRegistrar() {
        return registry -> registry.addErrorPages(
            new ErrorPage(Endpoints.URL_ERROR)
        );
    }
}
