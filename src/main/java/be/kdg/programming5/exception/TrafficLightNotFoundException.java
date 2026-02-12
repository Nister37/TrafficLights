package be.kdg.programming5.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when a traffic light with specified ID is not found in the system.
 * Returns HTTP 404 (Not Found) status to the client.
 */
@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "Traffic light not found")
public class TrafficLightNotFoundException extends RuntimeException {

    /**
     * Constructs a new TrafficLightNotFoundException with the specified traffic light ID.
     *
     * @param id the ID of the traffic light that was not found
     */
    public TrafficLightNotFoundException(int id) {
        super("Traffic light with id " + id + " not found");
    }
}

