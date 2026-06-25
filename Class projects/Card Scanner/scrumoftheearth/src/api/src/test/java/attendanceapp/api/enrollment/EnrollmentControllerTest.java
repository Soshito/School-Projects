//----------------------------------------------------------------------------------------------
// Name: Gavin Papenthien
// Project: Attendance App - This is a full stack attendance tracking and management software.
// Purpose: Test proper functionality and outputs of endpoints from EnrollmentController
//----------------------------------------------------------------------------------------------

package attendanceapp.api.enrollment;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.net.URI;

import static attendanceapp.api.utils.HeadersGenerator.getAdminHeaders;
import static attendanceapp.api.utils.HeadersGenerator.getStudentHeaders;
import static org.assertj.core.api.Assertions.assertThat;

//----------------------------------------------------------------------------------------------
// A testing class to test for proper functionality and outputs of endpoints from EnrollmentController
//----------------------------------------------------------------------------------------------
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class EnrollmentControllerTest {

    @Autowired
    TestRestTemplate restTemplate;

    /**
     * Ensure that an Enrollment is created when all data and configuration is valid
     */
    @Test
    void shouldCreateANewEnrollment() {
        HttpHeaders adminHeader = getAdminHeaders(restTemplate);

        int sectionId = 1;
        int studentId = 2;

        EnrollmentDTO newEnrollment = new EnrollmentDTO(sectionId, studentId);
        HttpEntity<EnrollmentDTO> request = new HttpEntity<>(newEnrollment, adminHeader);
        ResponseEntity<Enrollment> createResponse = restTemplate.postForEntity("/enrollments", request, Enrollment.class);

        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        // Verify location header points to created object
        URI locationOfNewEnrollment = createResponse.getHeaders().getLocation();
        HttpEntity<Void> getRequest = new HttpEntity<>(adminHeader);
        ResponseEntity<String> getResponse = restTemplate.exchange(locationOfNewEnrollment, HttpMethod.GET, getRequest, String.class);
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    /**
     * Ensure that an Enrollment is not created if the provided studentId is not associated with any existing Users
     */
    @Test
    void shouldNotCreateANewEnrollmentIfStudentDoesNotExist() {
        HttpHeaders adminHeader = getAdminHeaders(restTemplate);

        int sectionId = 1;
        int studentId = 99999;

        EnrollmentDTO newEnrollment = new EnrollmentDTO(sectionId, studentId);
        HttpEntity<EnrollmentDTO> request = new HttpEntity<>(newEnrollment, adminHeader);
        ResponseEntity<Enrollment> createResponse = restTemplate.postForEntity("/enrollments", request, Enrollment.class);

        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    /**
     * Ensure that an Enrollment is not created if the provided sectionId is not associated with any existing Sections
     */
    @Test
    void shouldNotCreateANewEnrollmentIfSectionDoesNotExist() {
        HttpHeaders adminHeader = getAdminHeaders(restTemplate);

        int sectionId = 9999999;
        int studentId = 2;

        EnrollmentDTO newEnrollment = new EnrollmentDTO(sectionId, studentId);
        HttpEntity<EnrollmentDTO> request = new HttpEntity<>(newEnrollment, adminHeader);
        ResponseEntity<Enrollment> createResponse = restTemplate.postForEntity("/enrollments", request, Enrollment.class);

        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    /**
     * Ensure that an Enrollment is not created if the provided studentId is associated with a non-student User
     */
    @Test
    void shouldNotCreateANewEnrollmentIfUserIsNotStudent() {
        HttpHeaders adminHeader = getAdminHeaders(restTemplate);

        int sectionId = 1;
        int studentId = 3; // 3 is an admin

        EnrollmentDTO newEnrollment = new EnrollmentDTO(sectionId, studentId);
        HttpEntity<EnrollmentDTO> request = new HttpEntity<>(newEnrollment, adminHeader);
        ResponseEntity<Enrollment> createResponse = restTemplate.postForEntity("/enrollments", request, Enrollment.class);

        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    /**
     * Ensure that an Enrollment is not created if the request is coming from a non-admin User
     */
    @Test
    void shouldNotCreateANewEnrollmentIfNotRequestByAdmin() {
        HttpHeaders studentHeader = getStudentHeaders(restTemplate);

        int sectionId = 1;
        int studentId = 2;

        EnrollmentDTO newEnrollment = new EnrollmentDTO(sectionId, studentId);
        HttpEntity<EnrollmentDTO> request = new HttpEntity<>(newEnrollment, studentHeader);
        ResponseEntity<Enrollment> createResponse = restTemplate.postForEntity("/enrollments", request, Enrollment.class);

        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    /**
     * Ensure that an Enrollment is not created if the request is coming from an unauthenticated User
     */
    @Test
    void shouldNotCreateANewEnrollmentIfNotAuthenticated() {
        int sectionId = 1;
        int studentId = 2;

        EnrollmentDTO newEnrollment = new EnrollmentDTO(sectionId, studentId);
        HttpEntity<EnrollmentDTO> request = new HttpEntity<>(newEnrollment);
        ResponseEntity<Enrollment> createResponse = restTemplate.postForEntity("/enrollments", request, Enrollment.class);

        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    /**
     * Ensure that an Enrollment is not returned if the ID does not correspond to any existing Enrollments
     */
    @Test
    void shouldNotReturnAnEnrollmentIfIdInvalid() {
        HttpHeaders headers = getAdminHeaders(restTemplate);
        int id = 999999999;

        HttpEntity<Void> getRequest = new HttpEntity<>(headers);
        ResponseEntity<String> getResponse = restTemplate.exchange("/enrollments/" + id, HttpMethod.GET, getRequest, String.class);
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    /**
     * Ensure that an Enrollment is deleted
     */
    @Test
    @DirtiesContext
    void shouldDeleteAnEnrollment() {
        HttpHeaders headers = getAdminHeaders(restTemplate);
        int id = 2; // Need to specify an Enrollment that can be deleted without FK violations

        HttpEntity<Void> deleteRequest = new HttpEntity<>(headers);
        ResponseEntity<Void> deleteResponse = restTemplate.exchange(
                "/enrollments/" + id,
                HttpMethod.DELETE,
                deleteRequest,
                new ParameterizedTypeReference<Void>() {
                });

        assertThat(deleteResponse.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }

    /**
     * Ensure that an Enrollment is not deleted if the ID is not associated with an existing Enrollment
     */
    @Test
    void shouldNotDeleteAnEnrollmentIfIdInvalid() {
        HttpHeaders headers = getAdminHeaders(restTemplate);
        int id = 999999999;

        HttpEntity<Void> deleteRequest = new HttpEntity<>(headers);
        ResponseEntity<Void> deleteResponse = restTemplate.exchange(
                "/enrollments/" + id,
                HttpMethod.DELETE,
                deleteRequest,
                new ParameterizedTypeReference<Void>() {
                });

        assertThat(deleteResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    /**
     * Ensure that an Enrollment is not deleted if the request is coming from a non-admin User
     */
    @Test
    void shouldNotDeleteAnEnrollmentIfNotRequestedByAdmin() {
        HttpHeaders headers = getStudentHeaders(restTemplate);
        int id = 2;

        HttpEntity<Void> deleteRequest = new HttpEntity<>(headers);
        ResponseEntity<Void> deleteResponse = restTemplate.exchange(
                "/enrollments/" + id,
                HttpMethod.DELETE,
                deleteRequest,
                new ParameterizedTypeReference<Void>() {
                });

        assertThat(deleteResponse.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    /**
     * Ensure that an Enrollment is not deleted if the request is unauthenticated
     */
    @Test
    void shouldNotDeleteAnEnrollmentIfUnauthenticated() {
        HttpHeaders headers = new HttpHeaders();
        int id = 999999999;

        HttpEntity<Void> deleteRequest = new HttpEntity<>(headers);
        ResponseEntity<Void> deleteResponse = restTemplate.exchange(
                "/enrollments/" + id,
                HttpMethod.DELETE,
                deleteRequest,
                new ParameterizedTypeReference<Void>() {
                });

        assertThat(deleteResponse.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }
}
