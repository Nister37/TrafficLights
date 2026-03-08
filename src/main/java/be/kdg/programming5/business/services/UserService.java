package be.kdg.programming5.business.services;

import be.kdg.programming5.business.domain.ApplicationUser;

import java.util.Optional;

public interface UserService {
    Optional<ApplicationUser> findByUsername(String username);
}

