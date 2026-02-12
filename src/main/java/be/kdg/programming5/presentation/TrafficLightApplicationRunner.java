package be.kdg.programming5.presentation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;

// Commented out to disable console mode - web application mode is active
// This class is no longer a Spring component - web controllers are used instead
public class TrafficLightApplicationRunner implements CommandLineRunner {
    private static final Logger logger = LoggerFactory.getLogger(TrafficLightApplicationRunner.class);

    private final Presenter presenter;

    public TrafficLightApplicationRunner(Presenter presenter) {
        this.presenter = presenter;
        logger.debug("TrafficLightApplicationRunner initialized");
    }

    @Override
    public void run(String... args) {
        logger.debug("Console mode disabled - running as web application");
        // Console mode disabled for web application
        // System.out.println("Starting Traffic Light Management System...");
        // presenter.start();
    }
}
