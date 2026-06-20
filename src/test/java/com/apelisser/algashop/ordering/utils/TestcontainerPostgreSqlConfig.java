package com.apelisser.algashop.ordering.utils;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.PostgreSQLContainer;

@TestConfiguration
public class TestcontainerPostgreSqlConfig {

    private static final PostgreSQLContainer postgreSQLContainer =
        new PostgreSQLContainer("postgres:17-alpine").withDatabaseName("ordering_test");

    @Bean
    @ServiceConnection
    public PostgreSQLContainer postgreSQLContainer() {
        return postgreSQLContainer;
    }

}
