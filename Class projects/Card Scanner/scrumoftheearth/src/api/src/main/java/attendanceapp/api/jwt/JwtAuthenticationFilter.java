//----------------------------------------------------------------------------------------------
// Name: Gavin Papenthien
// Project: Attendance App - This is a full stack attendance tracking and management software.
// Purpose: Configure JWT authentication protocols and standards.
//----------------------------------------------------------------------------------------------

package attendanceapp.api.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

//---------------------------------------------------------------
// Configure API JWT authentication protocols and standards.
//---------------------------------------------------------------
@Configuration
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String AUTH_HEADER = "Authorization";
    private static final String AUTH_PREFIX = "Bearer ";
    private static final int AUTH_PREFIX_LEN = 7;
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;


    /**
     * Dictates authentication process using JWTs
     * Verify JWT is present in proper format
     * Verify JWT is unmodified
     * Validate JWT contents and extract User associated with JWT
     * Create User authentication token and progress filter chain
     *
     * @param request API request - used to extract data
     * @param response API response - Will be set by controls, should not be modified by filters
     * @param filterChain Chain of Spring Security filters including JwtAuthenticationFilter
     * @throws ServletException Servlet Error
     * @throws IOException IO Error
     */
    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        final String authHeader = request.getHeader(AUTH_HEADER);
        final String jwt;
        final String username;

        // If JWT is not included skip to the next filter
        if (authHeader == null || !authHeader.startsWith(AUTH_PREFIX)) {
            filterChain.doFilter(request, response);
            return;
        }

        // Extract JWT from header and claims from JWT
        jwt = authHeader.substring(AUTH_PREFIX_LEN);
        username = jwtService.extractUsername(jwt);

        // Authenticate user if valid request and not already authenticated
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            // If JWT is valid create auth token for spring security
            if (jwtService.isTokenValid(jwt, userDetails)) {
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                    userDetails,
                    null,
                    userDetails.getAuthorities()
                );

                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        // Pass request forward for subsequent spring boot filters to process
        filterChain.doFilter(request, response);
    }
}
