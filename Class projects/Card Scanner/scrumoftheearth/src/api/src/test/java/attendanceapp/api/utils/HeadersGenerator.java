//----------------------------------------------------------------------------------------------
// Name: Gavin Papenthien
// Project: Attendance App - This is a full stack attendance tracking and management software.
// Purpose: Provide a utility for common testing processes.
//----------------------------------------------------------------------------------------------

package attendanceapp.api.utils;

import attendanceapp.api.auth.AuthDTO;
import attendanceapp.api.auth.AuthResponse;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;

//---------------------------------------------------------------
// Provide a utility for common testing processes.
//---------------------------------------------------------------
public class HeadersGenerator {

    /**
     * Get headers with an authenticated administrator account
     *
     * @param restTemplate TestRestTemplate to send login request to
     * @return HttpHeaders with authenticated administrator JWT
     */
    public static HttpHeaders getAdminHeaders(TestRestTemplate restTemplate) {
        AuthDTO adminLogin = new AuthDTO("admin", "password");
        ResponseEntity<AuthResponse> authResponse = restTemplate.postForEntity("/login", adminLogin, AuthResponse.class);
        assertThat(authResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        String jwt = authResponse.getBody().getToken();
        assertThat(jwt).isNotNull();
        assertThat(jwt).isNotBlank();
        assertThat(jwt).isNotEmpty();

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + jwt);

        return headers;
    }

    /**
     * Get headers with an authenticated student account
     *
     * @param restTemplate TestRestTemplate to send login request to
     * @return HttpHeaders with authenticated student JWT
     */
    public static HttpHeaders getStudentHeaders(TestRestTemplate restTemplate) {
        AuthDTO adminLogin = new AuthDTO("papengav", "password");
        ResponseEntity<AuthResponse> authResponse = restTemplate.postForEntity("/login", adminLogin, AuthResponse.class);
        assertThat(authResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        String jwt = authResponse.getBody().getToken();
        assertThat(jwt).isNotNull();
        assertThat(jwt).isNotBlank();
        assertThat(jwt).isNotEmpty();

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + jwt);

        return headers;
    }
}
