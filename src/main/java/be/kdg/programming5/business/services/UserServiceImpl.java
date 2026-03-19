package be.kdg.programming5.business.services;

import be.kdg.programming5.business.domain.ApplicationUser;
import be.kdg.programming5.config.security.CustomUserDetails;
import be.kdg.programming5.repository.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ApplicationUser> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ApplicationUser> getAuthenticatedUser() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return Optional.empty();
        }

        var principal = authentication.getPrincipal();
        if (principal instanceof CustomUserDetails customUserDetails) {
            return userRepository.findById(customUserDetails.getUserId());
        }

        if (principal instanceof org.springframework.security.core.userdetails.User springUser) {
            return userRepository.findByUsername(springUser.getUsername());
        }

        return Optional.empty();
    }
}

