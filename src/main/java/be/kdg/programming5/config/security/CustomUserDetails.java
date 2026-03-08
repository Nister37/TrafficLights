package be.kdg.programming5.config.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

/**
 * Spring Security user details extended with application-specific user ID.
 * Extends Spring's User class (which implements UserDetails).
 * This is a security framework user, NOT the domain ApplicationUser.
 */
public class CustomUserDetails extends User {

    private final int userId;

    public CustomUserDetails(String username, String password,
                             Collection<? extends GrantedAuthority> authorities,
                             int userId) {
        super(username, password, authorities);
        this.userId = userId;
    }

    public int getUserId() {
        return userId;
    }
}

