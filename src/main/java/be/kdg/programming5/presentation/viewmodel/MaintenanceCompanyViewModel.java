package be.kdg.programming5.presentation.viewmodel;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;

import java.time.LocalDate;

/**
 * ViewModel for the maintenance company form.
 */
public class MaintenanceCompanyViewModel {

    @NotBlank(message = "{maintenanceCompany.name.required}")
    private String name;

    @NotBlank(message = "{maintenanceCompany.contactPhone.required}")
    private String contactPhone;

    @NotBlank(message = "{maintenanceCompany.contactEmail.required}")
    @Email(message = "{maintenanceCompany.contactEmail.valid}")
    private String contactEmail;

    private boolean active;

    @NotNull(message = "{maintenanceCompany.since.required}")
    @PastOrPresent(message = "{maintenanceCompany.since.pastOrPresent}")
    private LocalDate since;

    public MaintenanceCompanyViewModel() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContactPhone() {
        return contactPhone;
    }

    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }

    public String getContactEmail() {
        return contactEmail;
    }

    public void setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public LocalDate getSince() {
        return since;
    }

    public void setSince(LocalDate since) {
        this.since = since;
    }
}
