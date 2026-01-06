package com.bank.account.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

/**
 * Security configuration for password encryption
 */
@Configuration
public class SecurityConfig {


    /**
     * Security configuration for WebFlux
     */
    @Bean
    public SecurityWebFilterChain securityWebFilterChain(final ServerHttpSecurity http) {

        return http

                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                // CORS
                .cors(cors -> {})
                // Authorization rules
                .authorizeExchange(exchange -> exchange
                        .pathMatchers(
                                "/api/v1/accounts/**",
                                "/api/v1/movements/**",
                                "/api/v1/reports/**",
                                "/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html"
                        ).permitAll()
                        .anyExchange().authenticated()
                )
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
                .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
                .build();
    }
}
