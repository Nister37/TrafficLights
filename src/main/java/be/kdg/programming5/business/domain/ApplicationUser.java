package be.kdg.programming5.business.domain;

import jakarta.persistence.*;

/**
 * Domain entity representing an application user.
 * Table is named 'application_user' because 'user' is a reserved SQL keyword.
 */
@Entity
@Table(name = "application_user")
public class ApplicationUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role = UserRole.USER;

    protected ApplicationUser() {
    }

    public ApplicationUser(String username, String passwordHash) {
        this.username = username;
        this.passwordHash = passwordHash;
    }

    public ApplicationUser(String username, String passwordHash, UserRole role) {
        this.username = username;
        this.passwordHash = passwordHash;
        this.role = role;
    }

    public int getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public UserRole getRole() {
        return role;
    }
}
