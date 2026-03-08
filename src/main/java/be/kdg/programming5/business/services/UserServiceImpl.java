package be.kdg.programming5.business.services;

import be.kdg.programming5.business.domain.ApplicationUser;
import be.kdg.programming5.repository.UserRepository;
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
}

