//----------------------------------------------------------------------------------------------
// Name: Gavin Papenthien
// Project: Attendance App - This is a full stack attendance tracking and management software.
// Purpose: Test proper functionality and outputs of endpoints from Command Pattern implementation
//----------------------------------------------------------------------------------------------

package attendanceapp.api.command;

import attendanceapp.api.course.Course;
import attendanceapp.api.course.CourseDTO;
import attendanceapp.api.user.UserDTO;
import attendanceapp.api.user.UserResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.net.URI;

import static attendanceapp.api.utils.HeadersGenerator.getAdminHeaders;
import static org.assertj.core.api.Assertions.assertThat;

//----------------------------------------------------------------------------------------------
// A testing class to test for proper functionality and outputs of endpoints from Command Pattern implementation
//----------------------------------------------------------------------------------------------
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class CommandControllerTest {

    @Autowired
    TestRestTemplate restTemplate;

    /**
     * Ensure that Courses and Student CRUD operations can be undone / redone
     */
    @Test
    @DirtiesContext
    void shouldUndoAndRedoUserAndCourseCreation() {
        HttpHeaders headers = getAdminHeaders(restTemplate);

        // Create User - borrowed from UserControllerTest
        // ========================================================================================================================
        String firstName = "John";
        String lastName = "Trachte";
        String studentCardId = "GHI789";
        String username = "trachteJohn";
        String password = "anotherPwd";
        int roleId = 3;

        UserDTO newUser = new UserDTO(firstName, lastName, studentCardId, username, password, roleId);
        HttpEntity<UserDTO> request = new HttpEntity<>(newUser, headers);

        ResponseEntity<UserResponse> createResponse = restTemplate.postForEntity("/users", request, UserResponse.class);

        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        URI locationOfNewUser = createResponse.getHeaders().getLocation();
        ResponseEntity<String> getResponse = restTemplate.exchange(locationOfNewUser, HttpMethod.GET, new HttpEntity<>(headers), String.class);
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        // ======================================================================================================================

        // Create Course
        // ======================================================================================================================
        String newCourseName = "CS3330";

        CourseDTO newCourse = new CourseDTO(newCourseName, null);
        HttpEntity<CourseDTO> courseReq = new HttpEntity<>(newCourse, headers);
        ResponseEntity<Course> courseResponse = restTemplate.postForEntity("/courses", courseReq, Course.class);

        assertThat(courseResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        // Verify location header points to created object
        URI locationOfNewCourse = courseResponse.getHeaders().getLocation();
        ResponseEntity<String> courseGetResponse = restTemplate.exchange(locationOfNewCourse, HttpMethod.GET, new HttpEntity<>(headers), String.class);
        assertThat(courseGetResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        // ======================================================================================================================

        // Undo Both
        // ======================================================================================================================
        ResponseEntity<Void> undoCourse = restTemplate.postForEntity("/commands/undo", new HttpEntity<>(headers), Void.class);
        ResponseEntity<Void> undoUser = restTemplate.postForEntity("/commands/undo", new HttpEntity<>(headers), Void.class);

        assertThat(undoCourse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(undoUser.getStatusCode()).isEqualTo(HttpStatus.OK);
        // ======================================================================================================================

        // Redo Both
        // ======================================================================================================================
        ResponseEntity<Void> redoUser = restTemplate.postForEntity("/commands/redo", new HttpEntity<>(headers), Void.class);
        ResponseEntity<Void> redoCourse = restTemplate.postForEntity("/commands/redo", new HttpEntity<>(headers), Void.class);

        assertThat(redoUser.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(redoCourse.getStatusCode()).isEqualTo(HttpStatus.OK);
        // ======================================================================================================================
    }

    /**
     * Ensure that no CRUD operations are executed if there is nothing to undo
     */
    @Test
    void shouldNotUndoIfNothingToUndo() {
        HttpHeaders headers = getAdminHeaders(restTemplate);

        ResponseEntity<Void> undo = restTemplate.postForEntity("/commands/undo", new HttpEntity<>(headers), Void.class);

        assertThat(undo.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    /**
     * Ensure that no CRUD operations are executed if there is nothing to redo
     */
    @Test
    void shouldNotRedoIfNothingToRedo() {
        HttpHeaders headers = getAdminHeaders(restTemplate);

        ResponseEntity<Void> undo = restTemplate.postForEntity("/commands/redo", new HttpEntity<>(headers), Void.class);

        assertThat(undo.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }
}
