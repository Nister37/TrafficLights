package be.kdg.programming5.controller.api;

import be.kdg.programming5.business.domain.MaintenanceCompany;
import be.kdg.programming5.business.services.MaintenanceCompanyService;
import be.kdg.programming5.controller.api.dto.CreateMaintenanceCompanyDto;
import be.kdg.programming5.controller.api.dto.MaintenanceCompanyDto;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/public/maintenance-companies")
public class PublicMaintenanceCompaniesController {
    private final MaintenanceCompanyService service;

    public PublicMaintenanceCompaniesController(MaintenanceCompanyService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<MaintenanceCompanyDto> createMaintenanceCompany(
            @RequestBody @Valid CreateMaintenanceCompanyDto dto) {
        MaintenanceCompany company = service.createMaintenanceCompany(
                dto.name(),
                dto.contactPhone(),
                dto.contactEmail(),
                dto.active(),
                dto.since()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(
                new MaintenanceCompanyDto(
                        company.getId(),
                        company.getName(),
                        company.getContactPhone(),
                        company.getContactEmail(),
                        company.isActive(),
                        company.getSince()
                )
        );
    }
}
