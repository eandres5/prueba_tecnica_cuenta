package com.bank.account.infrastructure.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.config.CorsRegistry;
import org.springframework.web.reactive.config.EnableWebFlux;
import org.springframework.web.reactive.config.WebFluxConfigurer;

/**
 * Web configuration for CORS and WebFlux
 */
@Configuration
@EnableWebFlux
public class WebConfig implements WebFluxConfigurer {

    /**
     * Configure CORS mappings for the application.
     *
     * @param registry cors resgistry
     */
    @Override
    public void addCorsMappings(final CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOriginPatterns("*") // ✅ CORRECTO
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);
    }
}
