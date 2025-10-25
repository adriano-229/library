package com.adriano.library.config;

import org.springframework.boot.autoconfigure.flyway.FlywayMigrationStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration to recreate the database from scratch on each run. This will delete all data each time.
 * WARNING: Only use in development.
 */
@Configuration
public class FlywayConfig {

    @Bean
    public FlywayMigrationStrategy cleanMigrateStrategy() {
        return flyway -> {
            System.out.println("🗑️ Cleaning database...");
            // cleans the database (removes all tables)
            flyway.clean();
            // runs the migrations from scratch
            flyway.migrate();
        };
    }
}
