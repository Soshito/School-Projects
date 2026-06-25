//----------------------------------------------------------------------------------------------
// Name: Gavin Papenthien
// Project: Attendance App - This is a full stack attendance tracking and management software.
// Purpose: Configure authentication protocols and standards.
//----------------------------------------------------------------------------------------------

package attendanceapp.api.config;

import attendanceapp.api.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.security.SecureRandom;

//---------------------------------------------------------------
// Configure API security protocols and standards.
//---------------------------------------------------------------
@Configuration
@RequiredArgsConstructor
public class AuthConfig {
    private final UserRepository userRepository;
    private static final int passwordEncodingStrength = 10;

    /**
     * Fetches authentication DAO
     * Asserts usage of UserDetails for authentication
     * Sets password encoder
     *
     * @return AuthenticationProvider
     */
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService());
        authProvider.setPasswordEncoder(passwordEncoder());

        return authProvider;
    }

    /**
     * Constructs password encoder to be used for User passwords
     * Sets custom password "strength" level and adds random salt to end of hash
     *
     * @return password encoder
     */
    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(passwordEncodingStrength, new SecureRandom());
    }

    /**
     * Single use UserDetails service to fetch users by username
     *
     * @return User if found
     */
    @Bean
    public UserDetailsService userDetailsService() {
        return username -> userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    /**
     * Constructs control to manage authentication
     * Uses Spring Security default username & password authentication
     * JWT authentication is inserted before this in the filter chain to bypass this upon a valid provided JWT
     *
     * @param config Authentication configuration to be used
     * @return Authentication Manager
     * @throws Exception Auth exceptions - invalid credentials, etc
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
