//----------------------------------------------------------------------------------------------
// Name: Gavin Papenthien
// Project: Attendance App - This is a full stack attendance tracking and management software.
// Purpose: Provide a mapping for clients to interact with AttendanceLogs.
//----------------------------------------------------------------------------------------------

package attendanceapp.api.attendancelog;

import attendanceapp.api.auth.AuthorityConstants;
import attendanceapp.api.exceptions.*;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Optional;

//---------------------------------------------------------------
// Provide mappings for clients to interact with AttendanceLogs.
//---------------------------------------------------------------
@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/attendancelogs")
@RequiredArgsConstructor
class AttendanceLogController {

    private final AttendanceLogService attendanceLogService;
    private final AttendanceLogRepository attendanceLogRepository;
    private final Logger logger = LoggerFactory.getLogger(AttendanceLogController.class);

    /**
     * Get an attendance log by id
     *
     * @param requestedId type int id of requested AttendanceLog
     * @return AttendanceLog or 404 NOT FOUND
     */
    @GetMapping("/{requestedId}")
    @PreAuthorize("(" + AuthorityConstants.ADMIN_AUTHORITY + ") OR " + AuthorityConstants.STUDENT_AUTHORITY)
    public ResponseEntity<AttendanceLog> findById(@PathVariable int requestedId) {
        logger.info("An AttendanceLog was requested");
        logger.trace(String.format("Entering findById with parameters (requestedId = %d)", requestedId));

        // Search for attendance log
        Optional<AttendanceLog> logOptional = attendanceLogRepository.findById(requestedId);

        // If attendance log found, return it, else return http not found
        if (logOptional.isPresent()) {
            logger.trace(String.format("Exiting findById with output AttendanceLog = %s", logOptional.get()));
            return ResponseEntity.ok().body(logOptional.get());
        }
        else {
            logger.warn("A client attempted to a request an AttendanceLog that does not exist");
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Get a page from all existing AttendanceLogs associated with a specified User and Section as a List
     * Accessible to Users with Student role, however they may only access AttendanceLogs with their ID
     * Default page = 0
     * Default size = 100
     *
     * @param pageable Pageable object containing page number, size and Sorting rule
     * @param studentId ID of associated User
     * @param sectionId ID of associated Section
     * @return List of AttendanceLogs within the Page with the associated User and Section
     */
    @GetMapping("/by-studentId-and-sectionId")
    @PreAuthorize("(" + AuthorityConstants.ADMIN_AUTHORITY + ")" + " OR (" + AuthorityConstants.PROFESSOR_AUTHORITY + ") OR " + AuthorityConstants.STUDENT_AUTHORITY)
    public ResponseEntity<List<AttendanceLog>> findAllByStudentAndSectionId(@PageableDefault(size = 100) Pageable pageable,
                                                                            @RequestParam int studentId,
                                                                            @RequestParam int sectionId) {
        try {
            Page<AttendanceLog> page = attendanceLogService.findAllByStudentAndSectionId(pageable, studentId, sectionId);
            logger.info("A list of AttendanceLogs was requested");
            return ResponseEntity.ok(page.getContent());
        }
        catch (InvalidUserException | InvalidSectionException e) {
            logger.warn("Invalid request: " + e.getMessage());
            return ResponseEntity.badRequest().build();
        }
        catch (InvalidAuthorization e) {
            logger.warn(e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    /**
     * Create a new AttendanceLog. Intended only to be invoked by classroom microcontrollers when a student's ID card is scanned.
     *
     * @param newLogRequest AttendanceLogDTO from request-body containing studentCardId and sectionId
     * @param ucb UriComponentsBuilder injected by Spring-Web to create URI for new object.
     * @return AttendanceLog or 403 FORBIDDEN or 400 BAD REQUEST
     */
    @PostMapping
    public ResponseEntity<AttendanceLog> createAttendanceLog(@RequestBody AttendanceLogDTO newLogRequest, UriComponentsBuilder ucb) {
        try {
            AttendanceLog savedLog = attendanceLogService.createAttendanceLog(newLogRequest);
            URI locationOfNewLog = ucb
                    .path("attendancelogs/{id}")
                    .buildAndExpand(savedLog.id)
                    .toUri();
            logger.info("A new AttendanceLog was created");
            logger.trace(String.format("Created AttendanceLog: %s", savedLog));
            return ResponseEntity.created(locationOfNewLog).body(savedLog);
        }
        catch (InvalidCredentialsException e) {
            logger.warn("Invalid request: " + e.getMessage());
            return ResponseEntity.badRequest().build();
        }
        catch (InvalidEnrollmentException e) {
            logger.warn("Invalid Enrollment: " + e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }
}