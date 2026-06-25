//----------------------------------------------------------------------------------------------
// Name: Gavin Papenthien
// Project: Attendance App - This is a full stack attendance tracking and management software.
// Purpose: Test proper functionality and outputs of endpoints from SectionController
//----------------------------------------------------------------------------------------------

package attendanceapp.api.section;

import attendanceapp.api.user.User;
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
// A testing class to test for proper functionality and outputs of endpoints from SectionController
//----------------------------------------------------------------------------------------------
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class SectionControllerTest {

    @Autowired
    TestRestTemplate restTemplate;

    /**
     * Ensure that a Section is created when all data and configuration is valid
     */
    @Test
    void shouldCreateANewSection() {
        HttpHeaders adminHeader = getAdminHeaders(restTemplate);

        String roomNum = "1";
        int numberOfStudents = 10;
        int courseId = 1; // Provided from data.sql
        int professorId = 4;

        SectionDTO newSection = new SectionDTO(roomNum, numberOfStudents, courseId, professorId);
        HttpEntity<SectionDTO> request = new HttpEntity<>(newSection, adminHeader);
        ResponseEntity<Section> createResponse = restTemplate.postForEntity("/sections", request, Section.class);

        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        // Verify location header points to created object
        URI locationOfNewCourse = createResponse.getHeaders().getLocation();
        HttpEntity<Void> getRequest = new HttpEntity<>(adminHeader);
        ResponseEntity<String> getResponse = restTemplate.exchange(locationOfNewCourse, HttpMethod.GET, getRequest, String.class);
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    /**
     * Ensure that a Section is not created if the Course ID is invalid
     */
    @Test
    void shouldNotCreateANewSectionIfCourseIdInvalid() {
        HttpHeaders adminHeader = getAdminHeaders(restTemplate);

        String roomNum = "1";
        int numberOfStudents = 10;
        int courseId = 99999; // Not provided in test data - should cause BAD REQUEST
        int professorId = 4;

        SectionDTO newSection = new SectionDTO(roomNum, numberOfStudents, courseId, professorId);
        HttpEntity<SectionDTO> request = new HttpEntity<>(newSection, adminHeader);
        ResponseEntity<Section> createResponse = restTemplate.postForEntity("/sections", request, Section.class);

        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    /**
     * Ensure that a Section is not created if the professorId is not associated with an existing User
     */
    @Test
    void shouldNotCreateANewSectionIfProfessorIdInvalid() {
        HttpHeaders adminHeader = getAdminHeaders(restTemplate);

        String roomNum = "1";
        int numberOfStudents = 10;
        int courseId = 99999; // Not provided in test data - should cause BAD REQUEST
        int professorId = 999999999;

        SectionDTO newSection = new SectionDTO(roomNum, numberOfStudents, courseId, professorId);
        HttpEntity<SectionDTO> request = new HttpEntity<>(newSection, adminHeader);
        ResponseEntity<Section> createResponse = restTemplate.postForEntity("/sections", request, Section.class);

        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    /**
     * Ensure that a Section is not created if the provided professorId is associated with a User that does not have the Professor Role
     */
    @Test
    void shouldNotCreateANewSectionIfProfessorIdIsNotAssociatedWithProfessor() {
        HttpHeaders adminHeader = getAdminHeaders(restTemplate);

        String roomNum = "1";
        int numberOfStudents = 10;
        int courseId = 1;
        int professorId = 1; // This is a student

        SectionDTO newSection = new SectionDTO(roomNum, numberOfStudents, courseId, professorId);
        HttpEntity<SectionDTO> request = new HttpEntity<>(newSection, adminHeader);
        ResponseEntity<Section> createResponse = restTemplate.postForEntity("/sections", request, Section.class);

        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    /**
     * Ensure that a Section is not created if the request is coming from a non-admin User
     */
    @Test
    void shouldNotCreateANewSectionIfNotRequestByAdmin() {
        HttpHeaders studentHeaders = getStudentHeaders(restTemplate);

        String roomNum = "1";
        int numberOfStudents = 10;
        int courseId = 1; // Provided from data.sql
        int professorId = 4;

        SectionDTO newSection = new SectionDTO(roomNum, numberOfStudents, courseId, professorId);
        HttpEntity<SectionDTO> request = new HttpEntity<>(newSection, studentHeaders);
        ResponseEntity<Section> createResponse = restTemplate.postForEntity("/sections", request, Section.class);

        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    /**
     * Ensure that a Section is not created if the request is coming from an unauthenticated User
     */
    @Test
    void shouldNotCreateANewSectionIfNotAuthenticated() {
        String roomNum = "1";
        int numberOfStudents = 10;
        int courseId = 1; // Provided from data.sql
        int professorId = 4;

        SectionDTO newSection = new SectionDTO(roomNum, numberOfStudents, courseId, professorId);
        HttpEntity<SectionDTO> request = new HttpEntity<>(newSection);
        ResponseEntity<Section> createResponse = restTemplate.postForEntity("/sections", request, Section.class);

        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    /**
     * Ensure that a List of Sections following the request parameters is returned
     */
    @Test
    void shouldReturnAListOfSections() {
        HttpHeaders headers = getAdminHeaders(restTemplate);
        int page = 0;
        int size = 1;

        UriComponentsBuilder ucb = UriComponentsBuilder.fromUriString("/sections")
                .queryParam("page", page)
                .queryParam("size", size);

        HttpEntity<Void> getRequest = new HttpEntity<>(headers);
        ResponseEntity<List<Section>> getResponse = restTemplate.exchange(
                ucb.toUriString(),
                HttpMethod.GET,
                getRequest,
                new ParameterizedTypeReference<List<Section>>() {
                });

        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(getResponse.getBody().size()).isEqualTo(size);
    }

    /**
     * Ensure that a List of Sections is not returned if not requested by an Admin
     */
    @Test
    void shouldNotReturnAListOfSectionsIfNotRequestedByAdmin() {
        HttpHeaders headers = getStudentHeaders(restTemplate);
        int page = 0;
        int size = 1;

        UriComponentsBuilder ucb = UriComponentsBuilder.fromUriString("/sections")
                .queryParam("page", page)
                .queryParam("size", size);

        HttpEntity<Void> getRequest = new HttpEntity<>(headers);
        ResponseEntity<List<Section>> getResponse = restTemplate.exchange(
                ucb.toUriString(),
                HttpMethod.GET,
                getRequest,
                new ParameterizedTypeReference<List<Section>>() {
                });

        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    /**
     * Ensure that a List of Section is not returned if the request is unauthenticated
     */
    @Test
    void shouldNotReturnAListOfSectionIfNotAuthenticated() {
        HttpHeaders headers = new HttpHeaders(); // empty
        int page = 0;
        int size = 1;

        UriComponentsBuilder ucb = UriComponentsBuilder.fromUriString("/section")
                .queryParam("page", page)
                .queryParam("size", size);

        HttpEntity<Void> getRequest = new HttpEntity<>(headers);
        ResponseEntity<List<Section>> getResponse = restTemplate.exchange(
                ucb.toUriString(),
                HttpMethod.GET,
                getRequest,
                new ParameterizedTypeReference<List<Section>>() {
                });

        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    /**
     * Ensure that a List of Sections following the request parameters is returned
     */
    @Test
    void shouldReturnAListOfSectionsByStudentId() {
        HttpHeaders headers = getAdminHeaders(restTemplate);
        int page = 0;
        int size = 1;
        int studentId = 1;

        UriComponentsBuilder ucb = UriComponentsBuilder.fromUriString("/sections/by-studentId")
                .queryParam("studentId", studentId)
                .queryParam("page", page)
                .queryParam("size", size);

        HttpEntity<Void> getRequest = new HttpEntity<>(headers);
        ResponseEntity<List<Section>> getResponse = restTemplate.exchange(
                ucb.toUriString(),
                HttpMethod.GET,
                getRequest,
                new ParameterizedTypeReference<List<Section>>() {
                });

        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(getResponse.getBody().size()).isEqualTo(size);
    }

    /**
     * Ensure that a List of Sections is not returned if the requested studentId  is not associated with any existing Users
     */
    @Test
    void shouldNotReturnAListOfSectionsByCourseIdIfStudentIdInvalid() {
        HttpHeaders headers = getAdminHeaders(restTemplate);
        int page = 0;
        int size = 2;
        int studentId = 999999999;

        UriComponentsBuilder ucb = UriComponentsBuilder.fromUriString("/sections/by-studentId")
                .queryParam("studentId", studentId)
                .queryParam("page", page)
                .queryParam("size", size);

        HttpEntity<Void> getRequest = new HttpEntity<>(headers);
        ResponseEntity<List<Section>> getResponse = restTemplate.exchange(
                ucb.toUriString(),
                HttpMethod.GET,
                getRequest,
                new ParameterizedTypeReference<List<Section>>() {
                });

        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void shouldNotReturnAListOfSectionsByStudentIdIfRequestedByStudentForDataThatIsNotTheirs() {
        HttpHeaders headers = getStudentHeaders(restTemplate);
        int page = 0;
        int size = 2;
        int studentId = 2;

        UriComponentsBuilder ucb = UriComponentsBuilder.fromUriString("/sections/by-studentId")
                .queryParam("studentId", studentId)
                .queryParam("page", page)
                .queryParam("size", size);

        HttpEntity<Void> getRequest = new HttpEntity<>(headers);
        ResponseEntity<List<Section>> getResponse = restTemplate.exchange(
                ucb.toUriString(),
                HttpMethod.GET,
                getRequest,
                new ParameterizedTypeReference<List<Section>>() {
                });

        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    /**
     * Ensure that a list of Sections is not returned if not request is not authenticated
     */
    @Test
    void shouldNotReturnAListOfSectionsByStudentIdIfNotAuthenticated() {
        HttpHeaders headers = new HttpHeaders(); // empty
        int page = 0;
        int size = 2;
        int studentId = 1;

        UriComponentsBuilder ucb = UriComponentsBuilder.fromUriString("/sections/by-studentId")
                .queryParam("studentId", studentId)
                .queryParam("page", page)
                .queryParam("size", size);

        HttpEntity<Void> getRequest = new HttpEntity<>(headers);
        ResponseEntity<List<Section>> getResponse = restTemplate.exchange(
                ucb.toUriString(),
                HttpMethod.GET,
                getRequest,
                new ParameterizedTypeReference<List<Section>>() {
                });

        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    /**
     * Ensure that a List of Sections following the request parameters is returned
     */
    @Test
    void shouldReturnAListOfSectionsByCourseId() {
        HttpHeaders headers = getAdminHeaders(restTemplate);
        int page = 0;
        int size = 1;
        int courseId = 1;

        UriComponentsBuilder ucb = UriComponentsBuilder.fromUriString("/sections/by-courseId")
                .queryParam("courseId", courseId)
                .queryParam("page", page)
                .queryParam("size", size);

        HttpEntity<Void> getRequest = new HttpEntity<>(headers);
        ResponseEntity<List<Section>> getResponse = restTemplate.exchange(
                ucb.toUriString(),
                HttpMethod.GET,
                getRequest,
                new ParameterizedTypeReference<List<Section>>() {
                });

        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(getResponse.getBody().size()).isEqualTo(size);
    }

    /**
     * Ensure that a List of Sections is not returned if the requested courseId is not associated with any existing Courses
     */
    @Test
    void shouldNotReturnAListOfSectionsByCourseIdIfCourseIdInvalid() {
        HttpHeaders headers = getAdminHeaders(restTemplate);
        int page = 0;
        int size = 2;
        int courseId = 999999999;

        UriComponentsBuilder ucb = UriComponentsBuilder.fromUriString("/sections/by-courseId")
                .queryParam("courseId", courseId)
                .queryParam("page", page)
                .queryParam("size", size);

        HttpEntity<Void> getRequest = new HttpEntity<>(headers);
        ResponseEntity<List<Section>> getResponse = restTemplate.exchange(
                ucb.toUriString(),
                HttpMethod.GET,
                getRequest,
                new ParameterizedTypeReference<List<Section>>() {
                });

        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    /**
     * Ensure that a list of Sections is not returned if not requested by an Admin
     */
    @Test
    void shouldNotReturnAListOfSectionsByCourseIdIfNotRequestedByAdmin() {
        HttpHeaders headers = getStudentHeaders(restTemplate);
        int page = 0;
        int size = 2;
        int courseId = 1;

        UriComponentsBuilder ucb = UriComponentsBuilder.fromUriString("/sections/by-courseId")
                .queryParam("courseId", courseId)
                .queryParam("page", page)
                .queryParam("size", size);

        HttpEntity<Void> getRequest = new HttpEntity<>(headers);
        ResponseEntity<List<Section>> getResponse = restTemplate.exchange(
                ucb.toUriString(),
                HttpMethod.GET,
                getRequest,
                new ParameterizedTypeReference<List<Section>>() {
                });

        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    /**
     * Ensure that a list of Sections is not returned if not request is not authenticated
     */
    @Test
    void shouldNotReturnAListOfSectionsByCourseIdIfNotAuthenticated() {
        HttpHeaders headers = new HttpHeaders(); // empty
        int page = 0;
        int size = 2;
        int courseId = 1;

        UriComponentsBuilder ucb = UriComponentsBuilder.fromUriString("/sections/by-courseId")
                .queryParam("courseId", courseId)
                .queryParam("page", page)
                .queryParam("size", size);

        HttpEntity<Void> getRequest = new HttpEntity<>(headers);
        ResponseEntity<List<Section>> getResponse = restTemplate.exchange(
                ucb.toUriString(),
                HttpMethod.GET,
                getRequest,
                new ParameterizedTypeReference<List<Section>>() {
                });

        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    /**
     * Ensure that a Section is not returned if the ID does not correspond to any existing Sections
     */
    @Test
    void shouldNotReturnASectionIfIdInvalid() {
        HttpHeaders headers = getAdminHeaders(restTemplate);
        int id = 999999999;

        HttpEntity<Void> getRequest = new HttpEntity<>(headers);
        ResponseEntity<String> getResponse = restTemplate.exchange("/sections/" + id, HttpMethod.GET, getRequest, String.class);
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }
}
