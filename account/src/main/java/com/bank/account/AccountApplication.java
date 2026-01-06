package com.bank.account;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;

/**
 * Main application class for Account Service
 * Handles accounts and movements management
 */
@Slf4j
@SpringBootApplication
@EnableR2dbcRepositories
@OpenAPIDefinition(
		info = @Info(
				title = "Account Service API",
				version = "1.0.0",
				description = "Microservice for managing bank accounts and movements"),
		servers = {
				@Server(url = "http://localhost:8082", description = "Local server"),
				@Server(url = "http://account:8082", description = "Docker server")
		}
)
public class AccountApplication {

	public static void main(String[] args) {
		log.info("Starting Account Service...");
		SpringApplication.run(AccountApplication.class, args);
		log.info("Account Service started successfully!");
	}

}
