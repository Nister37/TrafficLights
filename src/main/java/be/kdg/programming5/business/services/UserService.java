package be.kdg.programming5.business.services;

import be.kdg.programming5.business.domain.ApplicationUser;

import java.util.Optional;

public interface UserService {
    Optional<ApplicationUser> findByUsername(String username);

    /**
     * Convenience helper for MVC/controllers/services: resolve the currently authenticated user
     * as a domain {@link ApplicationUser}.
     *
     * Returns empty when no user is authenticated.
     */
    Optional<ApplicationUser> getAuthenticatedUser();
}

