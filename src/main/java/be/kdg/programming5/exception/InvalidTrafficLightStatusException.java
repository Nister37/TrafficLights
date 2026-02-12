package be.kdg.programming5.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when attempting an invalid traffic light status transition.
 * Business rules:
 * - PLANNED can only transition to ACTIVE
 * - BROKEN cannot transition directly to ACTIVE (must go through MAINTENANCE first)
 * Returns HTTP 400 (Bad Request) status to the client.
 */
@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "Invalid status transition")
public class InvalidTrafficLightStatusException extends RuntimeException {

    /**
     * Constructs a new InvalidTrafficLightStatusException with the attempted transition details.
     *
     * @param from the current status
     * @param to the attempted new status
     */
    public InvalidTrafficLightStatusException(String from, String to) {
        super("Cannot change status from " + from + " to " + to);
    }
}

