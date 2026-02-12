package be.kdg.programming5.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
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
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * Handles all database-related exceptions globally.
     * Logs the error and returns a custom database error page.
     *
     * @param req the HTTP request where the exception occurred
     * @param ex the database exception that was thrown
     * @return ModelAndView directing to the database error page (500.html)
     */
    @ExceptionHandler({DataAccessException.class, SQLException.class})
    public ModelAndView handleDatabaseException(HttpServletRequest req, Exception ex) {
        logger.error("Database error occurred at {}: {}", req.getRequestURL(), ex.getMessage(), ex);

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
     * Returns 404 page for normal requests, empty response for favicon.
     */
    @ExceptionHandler(NoResourceFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ModelAndView handleNoResourceFoundException(HttpServletRequest req) {
        String path = req.getRequestURI();

        // Silently ignore favicon requests
        if (path.contains("favicon")) {
            return null; // Empty response
        }

        logger.debug("Resource not found: {}", path);

        ModelAndView mav = new ModelAndView();
        mav.addObject("path", path);
        mav.setViewName("error/404");
        return mav;
    }

    /**
     * Handles custom not found exceptions (TrafficLightNotFoundException, IntersectionNotFoundException).
     * Returns user-friendly 404 page.
     */
    @ExceptionHandler({TrafficLightNotFoundException.class, IntersectionNotFoundException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ModelAndView handleNotFoundException(HttpServletRequest req, RuntimeException ex) {
        logger.warn("Not found: {} at {}", ex.getMessage(), req.getRequestURL());

        ModelAndView mav = new ModelAndView();
        mav.addObject("message", ex.getMessage());
        mav.addObject("path", req.getRequestURL().toString());
        mav.setViewName("error/404");
        return mav;
    }

    /**
     * Fallback handler for all unexpected exceptions.
     * If the exception has @ResponseStatus annotation, rethrows it to let Spring handle it.
     * Otherwise, logs the error and returns a generic error page.
     *
     * @param req the HTTP request where the exception occurred
     * @param ex the exception that was thrown
     * @return ModelAndView directing to the generic error page
     * @throws Exception if the exception has @ResponseStatus annotation (rethrown)
     */
    @ExceptionHandler(Exception.class)
    public ModelAndView handleGenericException(HttpServletRequest req, Exception ex) throws Exception {

        // If exception has @ResponseStatus, rethrow and let Spring handle it
        if (AnnotationUtils.findAnnotation(ex.getClass(), ResponseStatus.class) != null) {
            throw ex;
        }

        logger.error("Unexpected error occurred at {}: {}", req.getRequestURL(), ex.getMessage(), ex);

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

