package be.kdg.programming5.config;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * Top-level application configuration.
 * Enables Spring's async task execution and cache abstraction.
 */
@Configuration
@EnableAsync
@EnableCaching
public class ApplicationConfig {
}

