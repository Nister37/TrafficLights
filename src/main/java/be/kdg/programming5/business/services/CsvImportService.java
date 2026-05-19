package be.kdg.programming5.business.services;

import be.kdg.programming5.business.domain.Intersection;
import be.kdg.programming5.business.domain.TrafficLight;
import be.kdg.programming5.enums.Direction;
import be.kdg.programming5.enums.TrafficLightStatus;
import be.kdg.programming5.enums.TrafficLightType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Handles async bulk import of traffic lights from a CSV file.
 *
 * CSV format (header line required):
 *   status,installationDate,direction,type,rightArrow,intersectionId
 *
 * Rows with parse errors are skipped and logged; the import continues.
 */
@Service
public class CsvImportService {
    private static final Logger logger = LoggerFactory.getLogger(CsvImportService.class);

    private final TrafficLightService trafficLightService;
    private final IntersectionService intersectionService;

    public CsvImportService(TrafficLightService trafficLightService,
                            IntersectionService intersectionService) {
        this.trafficLightService = trafficLightService;
        this.intersectionService = intersectionService;
    }

    /**
     * Parses the CSV stream and persists each row as a new traffic light.
     * Runs on a separate thread — the HTTP response is returned before this method finishes.
     *
     * @param csvStream raw bytes of the uploaded CSV file
     */
    @Async
    public void importTrafficLightsAsync(InputStream csvStream) {
        logger.info("CSV import started on thread: {}", Thread.currentThread().getName());

        AtomicInteger imported = new AtomicInteger(0);
        AtomicInteger skipped = new AtomicInteger(0);

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(csvStream))) {
            // Skip the header line
            String header = reader.readLine();
            if (header == null) {
                logger.warn("CSV file is empty — nothing imported");
                return;
            }

            String line;
            int lineNumber = 1;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                String trimmed = line.trim();
                if (trimmed.isEmpty()) continue;

                try {
                    String[] parts = trimmed.split(",", -1);
                    if (parts.length != 6) {
                        throw new IllegalArgumentException("Expected 6 columns, got " + parts.length);
                    }

                    TrafficLightStatus status = TrafficLightStatus.valueOf(parts[0].trim().toUpperCase());
                    LocalDate installationDate = LocalDate.parse(parts[1].trim());
                    Direction direction = Direction.valueOf(parts[2].trim().toUpperCase());
                    TrafficLightType type = TrafficLightType.valueOf(parts[3].trim().toUpperCase());
                    boolean rightArrow = Boolean.parseBoolean(parts[4].trim());
                    int intersectionId = Integer.parseInt(parts[5].trim());

                    TrafficLight trafficLight = new TrafficLight(status, installationDate, direction, type, rightArrow);
                    Intersection intersection = intersectionService.getIntersectionById(intersectionId);
                    if (intersection != null) {
                        trafficLight.setIntersection(intersection);
                    }

                    trafficLightService.addTrafficLight(trafficLight);
                    imported.incrementAndGet();

                } catch (Exception e) {
                    skipped.incrementAndGet();
                    logger.warn("Skipping CSV line {}: {} — reason: {}", lineNumber, trimmed, e.getMessage());
                }
            }
        } catch (IOException e) {
            logger.error("Failed to read CSV stream", e);
        }

        logger.info("CSV import complete — imported: {}, skipped: {}", imported.get(), skipped.get());
    }
}

