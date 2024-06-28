package com.andresnss.sqsconcurrent.infrastructure.config;

import java.net.URI;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsClient;

@Configuration
public class SqsConfig {

    @Value("${cloud.aws.region.static}")
    private String region;
    @Value("${cloud.aws.sqs.endpoint}")
    private String endpoint;

    @Bean
    public SqsClient sqsClient() {
        return SqsClient.builder()
                .region(Region.of(region)) // Cambia la región según tu configuración
                .credentialsProvider(DefaultCredentialsProvider.create())
                .endpointOverride(URI.create(endpoint))
                .build();
    }
}

