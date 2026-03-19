package be.kdg.programming5.config.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Spring Security configuration.
 * - Public: home, login, static resources, traffic light and intersection detail pages
 *   (anonymous users can view basic info, authenticated users see full details)
 * - Authenticated: all other pages and REST write operations
 * - CSRF disabled for REST API usage
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        // @formatter:off
        http
            .authorizeHttpRequests(auths -> auths
                // Public pages — accessible without login
                .requestMatchers(HttpMethod.GET,
                        "/", "/login",
                        "/trafficLight/**", "/intersection/**",
                        "/js/**", "/css/**", "/images/**", "/webjars/**")
                    .permitAll()
                // Admin-only page (Week 5 roles demonstration)
                .requestMatchers(HttpMethod.GET, "/admin")
                    .hasRole("ADMIN")
                // Everything else (MVC pages + REST API) requires authentication
                .anyRequest()
                    .authenticated()
            )
            .formLogin(formLogin -> formLogin
                .loginPage("/login")
                .defaultSuccessUrl("/", true)
                .permitAll()
            )
            .logout(logout -> logout
                .logoutSuccessUrl("/login?logout")
                .permitAll()
            )
            .exceptionHandling(ex -> ex
                // REST API: return 403 instead of redirect to login
                .authenticationEntryPoint((request, response, exception) -> {
                    if (request.getRequestURI().startsWith("/api")) {
                        response.setStatus(HttpStatus.UNAUTHORIZED.value());
                    } else {
                        response.sendRedirect(request.getContextPath() + "/login");
                    }
                })
            );
        // @formatter:on
        return http.build();
    }
}
