//----------------------------------------------------------------------------------------------
// Name: Gavin Papenthien
// Project: Attendance App - This is a full stack attendance tracking and management software.
// Purpose: Configure API security protocols and standards.
//----------------------------------------------------------------------------------------------

package attendanceapp.api.config;

import attendanceapp.api.jwt.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

//---------------------------------------------------------------
// Configure API security protocols and standards.
//---------------------------------------------------------------
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final AuthenticationProvider authenticationProvider;

    /**
     * Configures API security.
     * Disables csrf, secures all endpoints aside a few whitelisted endpoints
     * Sets stateless policy and inserts jwtAuthFilter into the security filter chain
     *
     * @param http Spring Security injection - represents http protocol
     * @return constructed SecurityFilterChain
     * @throws Exception Spring Security exceptions
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable);

        // Set authorization rules for endpoint groups
        http.authorizeHttpRequests(request -> {
                        request.requestMatchers("/login").permitAll();
                        // This endpoint only creates an AttendanceLog if the client supplies a valid studentCardId
                        // Which is saved in the students ID card. Meaning the authentication & authorization is the
                        // ownership of the card that gets scanned client-side
                        request.requestMatchers(HttpMethod.POST, "/attendancelogs").permitAll();
                        request.requestMatchers("/h2-console").permitAll();
                        request.anyRequest().authenticated();
        });

        // JWT are used for authorization so server can be stateless
        http.sessionManagement((session) -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        // Insert jwtAuthFilter before username and password auth
        http.authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
