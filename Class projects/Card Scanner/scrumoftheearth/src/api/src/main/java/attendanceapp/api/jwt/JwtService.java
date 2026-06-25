//----------------------------------------------------------------------------------------------
// Name: Gavin Papenthien
// Project: Attendance App - This is a full stack attendance tracking and management software.
// Purpose: Service layer for JWT business logic
//----------------------------------------------------------------------------------------------

package attendanceapp.api.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.function.Function;


//---------------------------------------------------------------
// Provide business logic services for JWTs
//---------------------------------------------------------------
@Service
public class JwtService {

    private static final String SECRET_KEY = Base64.getEncoder().encodeToString(Keys.secretKeyFor(SignatureAlgorithm.HS256).getEncoded());

    /**
     * Extract the username (subject) from a JWT
     *
     * @param token JWT
     * @return String username
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Extract a JWT claim by passing the claim name through claimsResolver
     *
     * @param token JWT
     * @param claimsResolver Provided class for extracting claims
     * @return Claim contents
     * @param <T> Claim generic
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Generate a JWT from UserDetails
     * Sets claims and signs JWT
     *
     * @param userDetails UserDetails
     * @return JWT
     */
    public String generateToken(UserDetails userDetails) {
        Date currentTime = new Date(System.currentTimeMillis());
        int oneDayMillis = 1000 * 60 * 60 * 24;
        Date expTime = new Date(System.currentTimeMillis() + oneDayMillis);

        return Jwts.builder()
                .setSubject(userDetails.getUsername())
                .setIssuedAt(currentTime)
                .setExpiration(expTime)
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Determine if an existing JWT is valid
     * Check JWT expiration
     * Check that user requesting "owns" the JWT
     *
     * @param token JWT
     * @param userDetails Details of requesting user
     * @return boolean
     */
    public boolean isTokenValid(String token, UserDetails userDetails) {
        String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    /**
     * Check if a JWT is expired
     *
     * @param token JWT
     * @return boolean
     */
    private boolean isTokenExpired(String token) {
        Date expiration = extractClaim(token, Claims::getExpiration);
        return expiration.before(new Date());
    }

    /**
     * Extract all claims from a JWT
     *
     * @param token JWT
     * @return Claims
     */
    private Claims extractClaims(String token) {
        return Jwts
            .parserBuilder()
            .setSigningKey(getSignInKey())
            .build()
            .parseClaimsJws(token)
            .getBody();
    }

    /**
     * Get key to sign JWT with
     *
     * @return Key
     */
    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
