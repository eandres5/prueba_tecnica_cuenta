package com.bank.customer;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;

/**
 * Main application class for Customer Service
 * Handles customer and person management
 */
@Slf4j
@SpringBootApplication
@EnableR2dbcRepositories
@OpenAPIDefinition(
        info = @Info(
                title = "Customer Service API",
                version = "1.0.0",
                description = "Microservice for managing bank customers and persons"),
        servers = {
                @Server(url = "http://localhost:8081", description = "Local server"),
                @Server(url = "http://customer:8081", description = "Docker server")
        }
)
public class CustomerApplication {

    /**
     * This method is constructor of application.
     *
     * @param args array String to application
     */
    public static void main(final String[] args) {
        log.info("Starting Customer Service...");
        SpringApplication.run(CustomerApplication.class, args);
        log.info("Customer Service started successfully!");
    }
}
