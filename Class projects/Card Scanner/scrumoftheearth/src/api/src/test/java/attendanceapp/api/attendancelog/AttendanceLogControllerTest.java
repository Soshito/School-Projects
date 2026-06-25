//----------------------------------------------------------------------------------------------
// Name: Gavin Papenthien
// Project: Attendance App - This is a full stack attendance tracking and management software.
// Purpose: Test proper functionality and outputs of endpoints from AttendanceLogController
//----------------------------------------------------------------------------------------------

package attendanceapp.api.attendancelog;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

import static attendanceapp.api.utils.HeadersGenerator.getAdminHeaders;
import static attendanceapp.api.utils.HeadersGenerator.getStudentHeaders;
import static org.assertj.core.api.Assertions.assertThat;

//----------------------------------------------------------------------------------------------
// A testing class to test for proper functionality and outputs of endpoints from AttendanceLogController
//----------------------------------------------------------------------------------------------
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class AttendanceLogControllerTest {
    @Autowired
    TestRestTemplate restTemplate;

    /**
     * Ensure that an AttendanceLog is created when all data and configuration is valid.
     */
    @Test
    void shouldCreateANewAttendanceLog() {
        HttpHeaders headers = getAdminHeaders(restTemplate); // Need to be an admin to obtain the created log via GET

        // Get AttendanceLogDTO body info. This will all be supplied by the client sending in the request normally
        String studentCardId = "ABC123";
        String roomNum = "1";

        // Create request - Takes in a DTO and returns the actual object with server-calculated values
        AttendanceLogDTO newLog = new AttendanceLogDTO(studentCardId, roomNum);
        ResponseEntity<AttendanceLog> createResponse = restTemplate.postForEntity("/attendancelogs", newLog, AttendanceLog.class);      // By setting type to AttendanceLog, it essentially asserts that we were returned the expected object.

        // Verify HTTP status code
        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        // Verify location header points to created object
        URI locationOfNewLog = createResponse.getHeaders().getLocation();
        HttpEntity<Void> request = new HttpEntity<>(headers);
        ResponseEntity<String> getResponse = restTemplate.exchange(locationOfNewLog, HttpMethod.GET, request, String.class);
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    /**
     * Ensure that an AttendanceLog is not created if the studentCardId isn't associated with a user.
     */
    @Test
    void shouldNotCreateAttendanceLogWhenStudentCardIdDoesNotExist() {
        String studentCardId = "This Doesn't Exist";
        String roomNum = "1";

        AttendanceLogDTO newLog = new AttendanceLogDTO(studentCardId, roomNum);
        ResponseEntity<AttendanceLog> createResponse = restTemplate.postForEntity("/attendancelogs", newLog, AttendanceLog.class);

        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    /**
     * Ensure that an AttendanceLog is not created if the sectionId / section does not exist.
     */
    @Test
    void shouldNotCreateAttendanceLogWhenRoomNumDoesNotExist() {
        String studentCardId = "ABC123";
        String roomNum = "-1";

        AttendanceLogDTO newLog = new AttendanceLogDTO(studentCardId, roomNum);
        ResponseEntity<AttendanceLog> createResponse = restTemplate.postForEntity("/attendancelogs", newLog, AttendanceLog.class);

        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    /**
     * Ensure that an AttendanceLog is not created if the student associated with studentCardId is not enrolled in the existing section associated with sectionId.
     */
    @Test
    void shouldNotCreateAttendanceLogWhenStudentIsNotEnrolledInSection() {
        String studentCardId = "DEF456";
        String roomNum = "1";

        AttendanceLogDTO newLog = new AttendanceLogDTO(studentCardId, roomNum);
        ResponseEntity<AttendanceLog> createResponse = restTemplate.postForEntity("/attendancelogs", newLog, AttendanceLog.class);

        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    /**
     * Ensure that an AttendanceLog is not created if the student associated with studentCardId has no Enrollments
     */
    @Test
    void shouldNotCreateAttendanceLogWhenStudentHasNoEnrollments() {
        String studentCardId = "no";
        String roomNum = "1";

        AttendanceLogDTO newLog = new AttendanceLogDTO(studentCardId, roomNum);
        ResponseEntity<AttendanceLog> createResponse = restTemplate.postForEntity("/attendancelogs", newLog, AttendanceLog.class);

        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    /**
     * Ensure that an AttendanceLog is not created when all configuration is valid, but the Section is not currently meeting
     */
    @Test
    void shouldNotCreateAttendanceLogWhenValidEnrolledSectionNotCurrentlyMeeting() {
        String studentCardId = "DEF456";
        String roomNum = "10";

        AttendanceLogDTO newLog = new AttendanceLogDTO(studentCardId, roomNum);
        ResponseEntity<AttendanceLog> createResponse = restTemplate.postForEntity("/attendancelogs", newLog, AttendanceLog.class);

        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    /**
     * Ensure that a List of AttendanceLogs following the request parameters is returned
     */
    @Test
    void shouldReturnAListOfAttendanceLogsByStudentAndSectionId() {
        HttpHeaders headers = getAdminHeaders(restTemplate);
        int page = 0;
        int size = 1;
        int studentId = 1;
        int sectionId = 1;

        UriComponentsBuilder ucb = UriComponentsBuilder.fromUriString("/attendancelogs/by-studentId-and-sectionId")
                .queryParam("studentId", studentId)
                .queryParam("sectionId", sectionId)
                .queryParam("page", page)
                .queryParam("size", size);

        HttpEntity<Void> getRequest = new HttpEntity<>(headers);
        ResponseEntity<List<AttendanceLog>> getResponse = restTemplate.exchange(
                ucb.toUriString(),
                HttpMethod.GET,
                getRequest,
                new ParameterizedTypeReference<List<AttendanceLog>>() {
                });

        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(getResponse.getBody().size()).isEqualTo(size);
    }

    /**
     * Ensure that a List of AttendanceLog is not returned if the requested studentId is not associated with any existing Users
     */
    @Test
    void shouldNotReturnAListOfAttendanceLogsByStudentAndSectionIdIfStudentIdInvalid() {
        HttpHeaders headers = getAdminHeaders(restTemplate);
        int page = 0;
        int size = 2;
        int studentId = 999999999;
        int sectionId = 1;

        UriComponentsBuilder ucb = UriComponentsBuilder.fromUriString("/attendancelogs/by-studentId-and-sectionId")
                .queryParam("studentId", studentId)
                .queryParam("sectionId", sectionId)
                .queryParam("page", page)
                .queryParam("size", size);

        HttpEntity<Void> getRequest = new HttpEntity<>(headers);
        ResponseEntity<List<AttendanceLog>> getResponse = restTemplate.exchange(
                ucb.toUriString(),
                HttpMethod.GET,
                getRequest,
                new ParameterizedTypeReference<List<AttendanceLog>>() {
                });

        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    /**
     * Ensure that a List of AttendanceLog is not returned if the requested studentId is associated with a non-Student User
     */
    @Test
    void shouldNotReturnAListOfAttendanceLogsByStudentAndSectionIdIfStudentIdIsNotAssociatedWithStudent() {
        HttpHeaders headers = getAdminHeaders(restTemplate);
        int page = 0;
        int size = 2;
        int studentId = 3; // admin
        int sectionId = 1;

        UriComponentsBuilder ucb = UriComponentsBuilder.fromUriString("/attendancelogs/by-studentId-and-sectionId")
                .queryParam("studentId", studentId)
                .queryParam("sectionId", sectionId)
                .queryParam("page", page)
                .queryParam("size", size);

        HttpEntity<Void> getRequest = new HttpEntity<>(headers);
        ResponseEntity<List<AttendanceLog>> getResponse = restTemplate.exchange(
                ucb.toUriString(),
                HttpMethod.GET,
                getRequest,
                new ParameterizedTypeReference<List<AttendanceLog>>() {
                });

        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    /**
     * Ensure that a List of AttendanceLog is not returned if the requested sectionId is not associated with any existing Sections
     */
    @Test
    void shouldNotReturnAListOfSectionsByStudentAndSectionIdIfSectionIdInvalid() {
        HttpHeaders headers = getAdminHeaders(restTemplate);
        int page = 0;
        int size = 2;
        int studentId = 2;
        int sectionId = 999999999;

        UriComponentsBuilder ucb = UriComponentsBuilder.fromUriString("/attendancelogs/by-studentId-and-sectionId")
                .queryParam("studentId", studentId)
                .queryParam("sectionId", sectionId)
                .queryParam("page", page)
                .queryParam("size", size);

        HttpEntity<Void> getRequest = new HttpEntity<>(headers);
        ResponseEntity<List<AttendanceLog>> getResponse = restTemplate.exchange(
                ucb.toUriString(),
                HttpMethod.GET,
                getRequest,
                new ParameterizedTypeReference<List<AttendanceLog>>() {
                });

        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void shouldNotReturnAListOfAttendanceLogsByStudentAndSectionIdIfRequestedByStudentForDataThatIsNotTheirs() {
        HttpHeaders headers = getStudentHeaders(restTemplate); // returns first student entry (id = 1)
        int page = 0;
        int size = 2;
        int studentId = 2; // Not student 1, should throw error
        int sectionId = 1;

        UriComponentsBuilder ucb = UriComponentsBuilder.fromUriString("/attendancelogs/by-studentId-and-sectionId")
                .queryParam("studentId", studentId)
                .queryParam("sectionId", sectionId)
                .queryParam("page", page)
                .queryParam("size", size);

        HttpEntity<Void> getRequest = new HttpEntity<>(headers);
        ResponseEntity<List<AttendanceLog>> getResponse = restTemplate.exchange(
                ucb.toUriString(),
                HttpMethod.GET,
                getRequest,
                new ParameterizedTypeReference<List<AttendanceLog>>() {
                });

        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    /**
     * Ensure that a list of AttendanceLogs is not returned if not request is not authenticated
     */
    @Test
    void shouldNotReturnAListOfAttendanceLogsByStudentAndSectionIdIfNotAuthenticated() {
        HttpHeaders headers = new HttpHeaders(); // empty
        int page = 0;
        int size = 2;
        int studentId = 2;
        int sectionId = 1;

        UriComponentsBuilder ucb = UriComponentsBuilder.fromUriString("/attendancelogs/by-studentId-and-sectionId")
                .queryParam("studentId", studentId)
                .queryParam("sectionId", sectionId)
                .queryParam("page", page)
                .queryParam("size", size);

        HttpEntity<Void> getRequest = new HttpEntity<>(headers);
        ResponseEntity<List<AttendanceLog>> getResponse = restTemplate.exchange(
                ucb.toUriString(),
                HttpMethod.GET,
                getRequest,
                new ParameterizedTypeReference<List<AttendanceLog>>() {
                });

        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    /**
     * Ensure that an AttendanceLog is not returned if the ID does not correspond to any existing AttendanceLogs
     */
    @Test
    void shouldNotReturnAnAttendanceLogIfIdInvalid() {
        HttpHeaders headers = getAdminHeaders(restTemplate);
        int id = 999999999;

        HttpEntity<Void> getRequest = new HttpEntity<>(headers);
        ResponseEntity<String> getResponse = restTemplate.exchange("/attendancelogs/" + id, HttpMethod.GET, getRequest, String.class);
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }
}