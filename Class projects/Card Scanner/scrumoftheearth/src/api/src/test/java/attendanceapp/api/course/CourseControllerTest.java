//----------------------------------------------------------------------------------------------
// Name: Gavin Papenthien
// Project: Attendance App - This is a full stack attendance tracking and management software.
// Purpose: Test proper functionality and outputs of endpoints from CourseController
//----------------------------------------------------------------------------------------------

package attendanceapp.api.course;

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
// A testing class to test for proper functionality and outputs of endpoints from CourseController
//----------------------------------------------------------------------------------------------
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class CourseControllerTest {

    @Autowired
    TestRestTemplate restTemplate;

    /**
     * Ensure that a Course is created when all data and configuration is valid
     */
    @Test
    void shouldCreateANewCourse() {
        HttpHeaders adminHeader = getAdminHeaders(restTemplate);

        String newCourseName = "CS3330";

        CourseDTO newCourse = new CourseDTO(newCourseName, null);
        HttpEntity<CourseDTO> request = new HttpEntity<>(newCourse, adminHeader);
        ResponseEntity<Course> createResponse = restTemplate.postForEntity("/courses", request, Course.class);

        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        // Verify location header points to created object
        URI locationOfNewCourse = createResponse.getHeaders().getLocation();
        HttpEntity<Void> getRequest = new HttpEntity<>(adminHeader);
        ResponseEntity<String> getResponse = restTemplate.exchange(locationOfNewCourse, HttpMethod.GET, getRequest, String.class);
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    /**
     * Ensure that a Course is not created if the request is coming from a non-admin User
     */
    @Test
    void shouldNotCreateANewCourseIfNotRequestByAdmin() {
        HttpHeaders header = getStudentHeaders(restTemplate);

        String newCourseName = "CS3330";

        CourseDTO newCourse = new CourseDTO(newCourseName, null);
        HttpEntity<CourseDTO> request = new HttpEntity<>(newCourse, header);
        ResponseEntity<Course> createResponse = restTemplate.postForEntity("/courses", request, Course.class);

        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    /**
     * Ensure that a Course is not created if the request is coming from an unauthenticated User
     */
    @Test
    void shouldNotCreateANewCourseIfNotAuthenticated() {
        String newCourseName = "CS3330";

        CourseDTO newCourse = new CourseDTO(newCourseName, null);
        HttpEntity<CourseDTO> request = new HttpEntity<>(newCourse);
        ResponseEntity<Course> createResponse = restTemplate.postForEntity("/courses", request, Course.class);

        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    /**
     * Ensure that a List of Course following the request parameters is returned
     */
    @Test
    void shouldReturnAListOfCourses() {
        HttpHeaders headers = getAdminHeaders(restTemplate);
        int page = 0;
        int size = 1;

        UriComponentsBuilder ucb = UriComponentsBuilder.fromUriString("/courses")
                .queryParam("page", page)
                .queryParam("size", size);

        HttpEntity<Void> getRequest = new HttpEntity<>(headers);
        ResponseEntity<List<Course>> getResponse = restTemplate.exchange(
                ucb.toUriString(),
                HttpMethod.GET,
                getRequest,
                new ParameterizedTypeReference<List<Course>>() {
                });

        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(getResponse.getBody().size()).isEqualTo(size);
    }

    /**
     * Ensure that a List of Courses is not returned if not requested by an Admin
     */
    @Test
    void shouldNotReturnAListOfCoursesIfNotRequestedByAdmin() {
        HttpHeaders headers = getStudentHeaders(restTemplate);
        int page = 0;
        int size = 2;

        UriComponentsBuilder ucb = UriComponentsBuilder.fromUriString("/courses")
                .queryParam("page", page)
                .queryParam("size", size);

        HttpEntity<Void> getRequest = new HttpEntity<>(headers);
        ResponseEntity<List<Course>> getResponse = restTemplate.exchange(
                ucb.toUriString(),
                HttpMethod.GET,
                getRequest,
                new ParameterizedTypeReference<List<Course>>() {
                });

        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    /**
     * Ensure that a List of Courses is not returned if the request is unauthenticated
     */
    @Test
    void shouldNotReturnAListOfCoursesIfNotAuthenticated() {
        HttpHeaders headers = new HttpHeaders(); // empty
        int page = 0;
        int size = 2;

        UriComponentsBuilder ucb = UriComponentsBuilder.fromUriString("/courses")
                .queryParam("page", page)
                .queryParam("size", size);

        HttpEntity<Void> getRequest = new HttpEntity<>(headers);
        ResponseEntity<List<Course>> getResponse = restTemplate.exchange(
                ucb.toUriString(),
                HttpMethod.GET,
                getRequest,
                new ParameterizedTypeReference<List<Course>>() {
                });

        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    /**
     * Ensure that a Course is not returned if the ID does not correspond to any existing Courses
     */
    @Test
    void shouldNotReturnACourseIfIdInvalid() {
        HttpHeaders headers = getAdminHeaders(restTemplate);
        int id = 999999999;

        HttpEntity<Void> getRequest = new HttpEntity<>(headers);
        ResponseEntity<String> getResponse = restTemplate.exchange("/courses/" + id, HttpMethod.GET, getRequest, String.class);
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }
}
