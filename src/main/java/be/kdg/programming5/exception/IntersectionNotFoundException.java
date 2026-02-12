package be.kdg.programming5.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when an intersection with specified ID is not found in the system.
 * Returns HTTP 404 (Not Found) status to the client.
 */
@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "Intersection not found")
public class IntersectionNotFoundException extends RuntimeException {

    /**
     * Constructs a new IntersectionNotFoundException with the specified intersection ID.
     *
     * @param id the ID of the intersection that was not found
     */
    public IntersectionNotFoundException(int id) {
        super("Intersection with id " + id + " not found");
    }
}

