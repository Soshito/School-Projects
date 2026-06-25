//----------------------------------------------------------------------------------------------
// Name: Gavin Papenthien
// Project: Attendance App - This is a full stack attendance tracking and management software.
// Purpose: Test proper functionality and outputs of endpoints from AuthController
//----------------------------------------------------------------------------------------------

package attendanceapp.api.auth;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

//----------------------------------------------------------------------------------------------
// A testing class to test for proper functionality and outputs of endpoints from AuthController
//----------------------------------------------------------------------------------------------
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class AuthControllerTest {

    @Autowired
    TestRestTemplate restTemplate;

    /**
     * Ensure that a User is logged in when proper credentials are supplied
     */
    @Test
    void shouldLoginAUser() {
        String username = "papengav";
        String password = "password";

        AuthDTO newAuth = new AuthDTO(username, password);
        ResponseEntity<String> createResponse = restTemplate.postForEntity("/login", newAuth, String.class);

        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(createResponse.getBody()).contains("Authentication successful");
    }

    /**
     * Ensure that a User is not logged in when the supplied username is invalid
     */
    @Test
    void shouldNotLoginWithInvalidUsername() {
        String username = "I_dont_exit";
        String password = "password";

        AuthDTO newAuth = new AuthDTO(username, password);
        ResponseEntity<String> createResponse = restTemplate.postForEntity("/login", newAuth, String.class);

        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    /**
     * Ensure that a User is not logged in when the supplied password is invalid
     */
    @Test
    void shouldNotLoginWithInvalidPassword() {
        String username = "papengav";
        String password = "I_dont_exist";

        AuthDTO newAuth = new AuthDTO(username, password);
        ResponseEntity<String> createResponse = restTemplate.postForEntity("/login", newAuth, String.class);

        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }
}
