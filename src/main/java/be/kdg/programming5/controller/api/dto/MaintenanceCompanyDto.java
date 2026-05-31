package be.kdg.programming5.controller.api.dto;

import java.time.LocalDate;

/**
 * DTO for maintenance company API responses.
 */
public record MaintenanceCompanyDto(
        Integer id,
        String name,
        String contactPhone,
        String contactEmail,
        boolean active,
        LocalDate since
) {
}
