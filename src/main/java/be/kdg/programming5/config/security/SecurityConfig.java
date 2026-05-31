package be.kdg.programming5.config.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

/**
 * Spring Security configuration.
 * - Public: home, login, static resources, traffic light and intersection detail pages
 *   (anonymous users can view basic info, authenticated users see full details)
 * - Authenticated: all other pages and REST write operations
 * - CSRF disabled for REST API usage
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        // @formatter:off
        http
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .csrf(csrf -> csrf
                // Disable CSRF only for the public API path — the client repo can't carry CSRF tokens
                .ignoringRequestMatchers(
                    PathPatternRequestMatcher.withDefaults().matcher(HttpMethod.POST, "/api/public/**")
                )
            )
            .authorizeHttpRequests(auths -> auths
                // Public pages — accessible without login
                .requestMatchers(HttpMethod.GET,
                        "/", "/login",
                        "/trafficLight/**", "/intersection/**",
                        "/js/**", "/css/**", "/fonts/**", "/images/**", "/webjars/**")
                    .permitAll()
                // Public API — read-only endpoints used by public pages and the standalone client repo
                .requestMatchers(HttpMethod.GET,  "/api/traffic-lights/search").permitAll()
                .requestMatchers(HttpMethod.GET,  "/api/intersections/*/traffic-lights").permitAll()
                .requestMatchers(HttpMethod.GET,  "/api/public/**").permitAll()
                // Public API — create endpoint used only by the standalone client repo
                .requestMatchers(HttpMethod.POST, "/api/public/traffic-lights").permitAll()
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
                // REST API: return 401 instead of redirect to login
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

    /**
     * Allow the standalone client repo (webpack-dev-server on port 9000) to call our REST API
     * even though it is served from a different origin.
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("http://localhost:9000"));
        config.setAllowedMethods(List.of("GET", "POST", "PATCH", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(false);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        // Register on /** so that even redirect responses (e.g. 302 → /login) carry the header
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
