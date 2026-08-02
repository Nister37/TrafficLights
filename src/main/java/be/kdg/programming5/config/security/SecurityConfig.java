package be.kdg.programming5.config.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Spring Security configuration.
 * - Public: home, login, static resources, traffic light and intersection detail pages
 *   (anonymous users can view basic info, authenticated users see full details)
 * - Authenticated: all remaining pages and REST endpoints
 * - CSRF enabled for browser-session flows; only public standalone client creation is exempt
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
            .cors(Customizer.withDefaults())
            .csrf(csrf -> csrf.ignoringRequestMatchers(
                    "/api/public/maintenance-companies"))
            .authorizeHttpRequests(auths -> auths
                // Public pages — accessible without login
                .requestMatchers(HttpMethod.GET,
                        "/", "/login",
                        "/trafficLight/**", "/intersection/**",
                        "/js/**", "/css/**", "/fonts/**", "/images/**", "/webjars/**").permitAll()
                // Public API — read-only endpoints used by public pages and the standalone client repo
                .requestMatchers(HttpMethod.GET,  "/api/traffic-lights/search").permitAll()
                .requestMatchers(HttpMethod.GET,  "/api/intersections/*/traffic-lights").permitAll()
                .requestMatchers(HttpMethod.GET,  "/api/public/**").permitAll()
                // Public API — createMaintenanceCompany endpoint used only by the standalone client repo
                .requestMatchers(HttpMethod.POST, "/api/public/maintenance-companies").permitAll()
                .requestMatchers("/error").permitAll()
                // Everything else (MVC pages + REST API) requires authentication
                .anyRequest().authenticated()
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
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/api/traffic-lights/search")
                        .allowedOrigins("http://localhost:9000")
                        .allowedMethods(HttpMethod.GET.name());
                registry.addMapping("/api/public/maintenance-companies")
                        .allowedOrigins("http://localhost:9000")
                        .allowedMethods(HttpMethod.POST.name());
            }
        };
    }
}
