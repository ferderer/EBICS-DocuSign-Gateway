package de.ferderer.ebicsdocusign.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain tempSecurityFilterChain(HttpSecurity http) throws Exception {
        return http
            .securityMatcher("/**")
            .cors(cors -> cors.disable())
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(registry -> registry.anyRequest().permitAll())
            .build();
    }
}
