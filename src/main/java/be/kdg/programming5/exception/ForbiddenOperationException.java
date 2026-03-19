package be.kdg.programming5.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Thrown when an authenticated user attempts an operation they are not allowed to do.
 *
 * This is used for Week 5 ownership-based authorization (owner-or-admin).
 */
@ResponseStatus(value = HttpStatus.FORBIDDEN)
public class ForbiddenOperationException extends RuntimeException {
    public ForbiddenOperationException(String message) {
        super(message);
    }
}

