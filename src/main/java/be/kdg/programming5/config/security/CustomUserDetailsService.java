package be.kdg.programming5.config.security;

import be.kdg.programming5.business.domain.ApplicationUser;
import be.kdg.programming5.business.domain.UserRole;
import be.kdg.programming5.business.services.UserService;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

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
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        ApplicationUser user = userService.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "User not found: " + username));

        List<GrantedAuthority> authorities = List.of(toAuthority(user.getRole()));

        return new CustomUserDetails(
                user.getUsername(),
                user.getPasswordHash(),
                authorities,
                user.getId()
        );
    }

    private static GrantedAuthority toAuthority(UserRole role) {
        if (role == UserRole.ADMIN) {
            return new SimpleGrantedAuthority("ROLE_ADMIN");
        }
        return new SimpleGrantedAuthority("ROLE_USER");
    }
}
