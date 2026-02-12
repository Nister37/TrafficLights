package be.kdg.programming5.presentation.converter;

import be.kdg.programming5.enums.TrafficLightStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

/**
 * Custom converter that converts String to TrafficLightStatus enum.
 * Supports case-insensitive matching and partial matching (first 3 characters).
 * Examples:
 * - "act" or "ACTIVE" or "active" -> ACTIVE
 * - "mai" or "MAINTENANCE" -> MAINTENANCE
 * - "bro" or "BROKEN" -> BROKEN
 * - "pla" or "PLANNED" -> PLANNED
 */
@Component
public class StringToTrafficLightStatusConverter implements Converter<String, TrafficLightStatus> {
    private static final Logger logger = LoggerFactory.getLogger(StringToTrafficLightStatusConverter.class);

    @Override
    public TrafficLightStatus convert(String source) {
        if (source == null || source.trim().isEmpty()) {
            logger.warn("Attempted to convert null or empty string to TrafficLightStatus");
            throw new IllegalArgumentException("Status cannot be null or empty");
        }

        String normalized = source.trim().toUpperCase();
        logger.debug("Converting '{}' to TrafficLightStatus", source);

        // Try exact match first
        try {
            TrafficLightStatus status = TrafficLightStatus.valueOf(normalized);
            logger.debug("Exact match found: {}", status);
            return status;
        } catch (IllegalArgumentException e) {
            // If exact match fails, try partial match (first 3 characters)
            if (normalized.length() >= 3) {
                String prefix = normalized.substring(0, 3);
                logger.debug("Trying partial match with prefix: {}", prefix);

                for (TrafficLightStatus status : TrafficLightStatus.values()) {
                    if (status.name().startsWith(prefix)) {
                        logger.debug("Partial match found: {}", status);
                        return status;
                    }
                }
            }
        }

        logger.error("Could not convert '{}' to TrafficLightStatus", source);
        throw new IllegalArgumentException("Invalid traffic light status: " + source);
    }
}

