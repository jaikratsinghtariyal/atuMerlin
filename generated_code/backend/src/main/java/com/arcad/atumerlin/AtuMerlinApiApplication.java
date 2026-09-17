package com.arcad.atumerlin;

import com.arcad.atumerlin.config.AppProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

/**
 * Entry point for the ATU Merlin REST API.
 *
 * <p>This service is a modern Java/Spring Boot re-implementation of the ATU Merlin
 * IBM i (AS/400) application. The original RPGLE/DDS data model (articles,
 * customers, providers, families, countries, VAT rates and customer orders) is
 * exposed here as configurable REST resources.</p>
 */
@SpringBootApplication
@EnableConfigurationProperties(AppProperties.class)
public class AtuMerlinApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(AtuMerlinApiApplication.class, args);
    }
}
