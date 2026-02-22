package be.kdg.programming5.exception;

import be.kdg.programming5.controller.api.dto.ErrorDto;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Global exception handler for the application.
 * Handles database exceptions and provides fallback error handling for unexpected exceptions.
 * Uses @ControllerAdvice to apply exception handling across all controllers.
 * Differentiates between MVC and API requests to return appropriate responses.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * Checks if the request is an API request (starts with /api/).
     */
    private boolean isApiRequest(HttpServletRequest request) {
        return request.getRequestURI().startsWith("/api");
    }

    /**
     * Handles all database-related exceptions globally.
     * Returns JSON for API requests, HTML error page for MVC requests.
     */
    @ExceptionHandler({DataAccessException.class, SQLException.class})
    public Object handleDatabaseException(HttpServletRequest req, Exception ex) {
        logger.error("Database error occurred at {}: {}", req.getRequestURL(), ex.getMessage(), ex);

        if (isApiRequest(req)) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorDto("A database error occurred. Please try again later."));
        }

        ModelAndView mav = new ModelAndView();
        mav.addObject("timestamp", LocalDateTime.now().format(FORMATTER));
        mav.addObject("status", 500);
        mav.addObject("error", "Database Error");
        mav.addObject("message", "A database error occurred. Please try again later.");
        mav.addObject("path", req.getRequestURL().toString());
        mav.setViewName("error/500");

        return mav;
    }

    /**
     * Handles missing static resources.
     * Returns JSON for API requests, 404 page for MVC requests.
     */
    @ExceptionHandler(NoResourceFoundException.class)
    public Object handleNoResourceFoundException(HttpServletRequest req) {
        String path = req.getRequestURI();

        // Silently ignore favicon requests
        if (path.contains("favicon")) {
            return null;
        }

        logger.debug("Resource not found: {}", path);

        if (isApiRequest(req)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorDto("Resource not found: " + path));
        }

        ModelAndView mav = new ModelAndView();
        mav.addObject("path", path);
        mav.setViewName("error/404");
        return mav;
    }

    /**
     * Handles custom not found exceptions (TrafficLightNotFoundException, IntersectionNotFoundException).
     * Returns JSON for API requests, 404 page for MVC requests.
     */
    @ExceptionHandler({TrafficLightNotFoundException.class, IntersectionNotFoundException.class})
    public Object handleNotFoundException(HttpServletRequest req, RuntimeException ex) {
        logger.warn("Not found: {} at {}", ex.getMessage(), req.getRequestURL());

        if (isApiRequest(req)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorDto(ex.getMessage()));
        }

        ModelAndView mav = new ModelAndView();
        mav.addObject("message", ex.getMessage());
        mav.addObject("path", req.getRequestURL().toString());
        mav.setViewName("error/404");
        return mav;
    }

    /**
     * Fallback handler for all unexpected exceptions.
     * Returns JSON for API requests, error page for MVC requests.
     */
    @ExceptionHandler(Exception.class)
    public Object handleGenericException(HttpServletRequest req, Exception ex) throws Exception {

        // If exception has @ResponseStatus, rethrow and let Spring handle it
        if (AnnotationUtils.findAnnotation(ex.getClass(), ResponseStatus.class) != null) {
            throw ex;
        }

        logger.error("Unexpected error occurred at {}: {}", req.getRequestURL(), ex.getMessage(), ex);

        if (isApiRequest(req)) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorDto(ex.getMessage() != null ? ex.getMessage() : "An unexpected error occurred"));
        }

        ModelAndView mav = new ModelAndView();
        mav.addObject("timestamp", LocalDateTime.now().format(FORMATTER));
        mav.addObject("status", 500);
        mav.addObject("error", "Internal Server Error");
        mav.addObject("message", ex.getMessage() != null ? ex.getMessage() : "An unexpected error occurred");
        mav.addObject("path", req.getRequestURL().toString());
        mav.setViewName("error");

        return mav;
    }
}

