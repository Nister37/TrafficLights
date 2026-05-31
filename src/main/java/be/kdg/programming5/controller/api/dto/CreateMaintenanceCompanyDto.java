package be.kdg.programming5.controller.api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;

import java.time.LocalDate;

/**
 * DTO for creating a maintenance company through the standalone client API.
 */
public record CreateMaintenanceCompanyDto(
        @NotBlank String name,
        @NotBlank String contactPhone,
        @NotBlank @Email String contactEmail,
        boolean active,
        @NotNull @PastOrPresent LocalDate since
) {
}
