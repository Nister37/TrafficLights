package be.kdg.programming5.config.security;

import be.kdg.programming5.business.domain.UserRole;
import be.kdg.programming5.business.services.UserService;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.jspecify.annotations.NonNull;

import java.util.List;

/**
 * Spring Security service that loads user details from the database.
 * Uses UserService (n-layer) to retrieve the domain user,
 * then wraps it in CustomUserDetails for Spring Security.
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserService userService;

    public CustomUserDetailsService(UserService userService) {
        this.userService = userService;
    }

    @Override
    public UserDetails loadUserByUsername(@NonNull String username) throws UsernameNotFoundException {
        var userOptional = userService.findByUsername(username);
        return userOptional
                .map(user -> new CustomUserDetails(
                        user.getUsername(),
                        user.getPasswordHash(),
                        List.of(toAuthority(user.getRole())),
                        user.getId()))
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
    }

    private static GrantedAuthority toAuthority(UserRole role) {
        if (role == UserRole.ADMIN) {
            return new SimpleGrantedAuthority("ROLE_ADMIN");
        }
        return new SimpleGrantedAuthority("ROLE_USER");
    }
}
