//----------------------------------------------------------------------------------------------
// Name: Gavin Papenthien
// Project: Attendance App - This is a full stack attendance tracking and management software.
// Purpose: Provide a mapping for clients to interact with Enrollments.
//----------------------------------------------------------------------------------------------

package attendanceapp.api.enrollment;

import attendanceapp.api.auth.AuthorityConstants;
import attendanceapp.api.exceptions.InvalidEnrollmentException;
import attendanceapp.api.exceptions.InvalidSectionException;
import attendanceapp.api.exceptions.InvalidUserException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;


//---------------------------------------------------------------
// Provide mappings for clients to interact with Enrollments.
//---------------------------------------------------------------
@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/enrollments")
@RequiredArgsConstructor
public class EnrollmentController {

    private final Logger logger = LoggerFactory.getLogger(EnrollmentController.class);
    private final EnrollmentService enrollmentService;

    @GetMapping("/{id}")
    @PreAuthorize(AuthorityConstants.ADMIN_AUTHORITY)
    public ResponseEntity<Enrollment> findById(@PathVariable int id) {
        try {
            logger.info("A Enrollment was requested");
            logger.trace(String.format("Entering findById with parameters: (id = %d)", id));

            Enrollment enrollment = enrollmentService.findById(id);
            return ResponseEntity.ok(enrollment);
        }
        catch (InvalidEnrollmentException e) {
            logger.warn(e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Create a new Enrollment. Only accessible by existing Administrator Users
     *
     * @param enrollmentRequest EnrollmentDTO from request-body containing data for new Enrollment
     * @param ucb UriComponentsBuilder injected by Spring-Web to create a URI for new Object
     * @return Enrollment or appropriate Http error code
     */
    @PostMapping
    @PreAuthorize(AuthorityConstants.ADMIN_AUTHORITY)
    public ResponseEntity<Enrollment> createEnrollment(@RequestBody EnrollmentDTO enrollmentRequest, UriComponentsBuilder ucb) {
        try {
            Enrollment savedEnrollment = enrollmentService.createEnrollment(enrollmentRequest);
            URI locationOfNewEnrollment = ucb
                    .path("/enrollments/{id}")
                    .buildAndExpand(savedEnrollment.getId())
                    .toUri();

            logger.info("A new Enrollment was created");
            logger.trace(String.format("Created Enrollment: %s", savedEnrollment));

            return ResponseEntity.created(locationOfNewEnrollment).body(savedEnrollment);
        }
        catch (InvalidSectionException e) {
            logger.warn("Invalid Section: " + e.getMessage());
            return ResponseEntity.badRequest().build();
        }
        catch (InvalidUserException e) {
            logger.warn("Invalid User: " + e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Delete an Enrollment
     *
     * @param id ID of Enrollment to delete
     * @return 204 NO-CONTENT or appropriate error code
     */
    @DeleteMapping("/{id}")
    @PreAuthorize(AuthorityConstants.ADMIN_AUTHORITY)
    public ResponseEntity<Void> deleteById(@PathVariable int id) {
        try {
            enrollmentService.hardDelete(id);
            return ResponseEntity.noContent().build();
        }
        catch (InvalidEnrollmentException e) {
            logger.warn("Invalid request: " + e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }
}
