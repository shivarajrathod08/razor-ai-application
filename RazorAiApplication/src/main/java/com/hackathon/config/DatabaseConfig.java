package com.hackathon.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DriverManager;

@Configuration
public class DatabaseConfig {
    private static final Logger log = LoggerFactory.getLogger(DatabaseConfig.class);

    @Value("${spring.datasource.url:jdbc:mysql://localhost:3306/razorai_db?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC}")
    private String mysqlUrl;

    @Value("${spring.datasource.username:root}")
    private String mysqlUsername;

    @Value("${spring.datasource.password:}")
    private String mysqlPassword;

    @Value("${spring.datasource.driver-class-name:com.mysql.cj.jdbc.Driver}")
    private String mysqlDriver;

    @Bean
    @Primary
    public DataSource dataSource() {
        log.info("Checking MySQL availability at {}...", mysqlUrl);
        boolean mysqlAvailable = false;
        try {
            Class.forName(mysqlDriver);
            // Quick check with 3-second timeout
            DriverManager.setLoginTimeout(3);
            try (Connection conn = DriverManager.getConnection(mysqlUrl, mysqlUsername, mysqlPassword)) {
                if (conn != null && !conn.isClosed()) {
                    mysqlAvailable = true;
                    log.info("MySQL connection established successfully.");
                }
            }
        } catch (Exception e) {
            log.warn("MySQL connection check failed ({}). Switching to resilient in-memory H2 fallback for instant evaluation.", e.getMessage());
        }

        if (mysqlAvailable) {
            return DataSourceBuilder.create()
                    .driverClassName(mysqlDriver)
                    .url(mysqlUrl)
                    .username(mysqlUsername)
                    .password(mysqlPassword)
                    .build();
        } else {
            log.info("Configuring H2 Database fallback: jdbc:h2:mem:razorai_db;DB_CLOSE_DELAY=-1;MODE=MySQL");
            return DataSourceBuilder.create()
                    .driverClassName("org.h2.Driver")
                    .url("jdbc:h2:mem:razorai_db;DB_CLOSE_DELAY=-1;MODE=MySQL;DATABASE_TO_LOWER=TRUE")
                    .username("sa")
                    .password("")
                    .build();
        }
    }
}