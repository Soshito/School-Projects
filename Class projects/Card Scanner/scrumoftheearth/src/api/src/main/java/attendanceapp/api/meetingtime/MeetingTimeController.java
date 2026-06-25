//----------------------------------------------------------------------------------------------
// Name: Gavin Papenthien
// Project: Attendance App - This is a full stack attendance tracking and management software.
// Purpose: Provide a mapping for clients to interact with MeetingTimes.
//----------------------------------------------------------------------------------------------

package attendanceapp.api.meetingtime;

import attendanceapp.api.auth.AuthorityConstants;
import attendanceapp.api.exceptions.InvalidCourseException;
import attendanceapp.api.exceptions.InvalidDayOfWeekException;
import attendanceapp.api.exceptions.InvalidMeetingTimeException;
import attendanceapp.api.exceptions.InvalidSectionException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.text.ParseException;

//---------------------------------------------------------------
// Provide mappings for clients to interact with Sections.
//---------------------------------------------------------------
@RestController
@RequestMapping("/meetingtimes")
@CrossOrigin("*")
@RequiredArgsConstructor
public class MeetingTimeController {

    private final Logger logger = LoggerFactory.getLogger(MeetingTimeController.class);;
    private final MeetingTimeService meetingTimeService;

    /**
     * Get a MeetingTime by id
     * Accessible only to administrators
     *
     * @param id ID of requested MeetingTime
     * @return MeetingTime
     */
    @GetMapping("/{id}")
    @PreAuthorize(AuthorityConstants.ADMIN_AUTHORITY)
    public ResponseEntity<MeetingTime> findById(@PathVariable int id) {
        try {
            logger.info("A MeetingTime was requested");
            logger.trace(String.format("Entering findById with parameters (id = %d)", id));

            // Search for attendance log
            MeetingTime requestedMeetingTime = meetingTimeService.findById(id);
            return ResponseEntity.ok().body(requestedMeetingTime);
        }
        catch (InvalidMeetingTimeException e) {
            logger.warn(e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Create a new MeetingTime
     * Only accessible to administrators
     *
     * @param meetingTimeRequest MeetingTimeDTO containing data for new MeetingTime
     * @param ucb UriComponentsBuilder - injected by Spring-Web to create URI for new MeetingTime
     */
    @PostMapping
    @PreAuthorize(AuthorityConstants.ADMIN_AUTHORITY)
    public ResponseEntity<MeetingTime> createMeetingTime(@RequestBody MeetingTimeDTO meetingTimeRequest, UriComponentsBuilder ucb) {
        try {
            MeetingTime savedMeetingTime = meetingTimeService.createMeetingTime(meetingTimeRequest);

            URI locationOfNewMeetingTime = ucb
                    .path("/meetingtimes/{id}")
                    .buildAndExpand(savedMeetingTime.getId())
                    .toUri();

            logger.info("A new MeetingTime was created");
            logger.trace(String.format("Created MeetingTime: %s", savedMeetingTime));
            return ResponseEntity.created(locationOfNewMeetingTime).body(savedMeetingTime);
        }
        catch (InvalidSectionException | InvalidDayOfWeekException | ParseException e) {
            logger.warn("Invalid request: " + e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }
}
