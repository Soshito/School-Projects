//----------------------------------------------------------------------------------------------
// Name: Gavin Papenthien
// Project: Attendance App - This is a full stack attendance tracking and management software.
// Purpose: Provide a mapping for global multi-command undo/redo processes.
// Only implemented because required for homework assignment
//----------------------------------------------------------------------------------------------

package attendanceapp.api.command;

import attendanceapp.api.auth.AuthorityConstants;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

//---------------------------------------------------------------
// Provide mappings for global multi-command undo/redo processes.
//---------------------------------------------------------------
@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/commands")
@RequiredArgsConstructor
public class CommandController {
    private final CommandService commandService;
    private final Logger logger = LoggerFactory.getLogger(CommandController.class);

    /**
     * Undo the most recent CRUD process
     * Currently only supports User and Enrollment creation / deletion
     *
     * @return 200 OK if successful
     */
    @PostMapping("/undo")
    @PreAuthorize(AuthorityConstants.ADMIN_AUTHORITY)
    public ResponseEntity<Void> undo() {
        if (commandService.undo()) {
            logger.info("A resource management process was undone");
            return ResponseEntity.ok().build();
        }

        logger.info("Unsuccessful undo of resource management process");
        return ResponseEntity.badRequest().build();
    }

    /**
     * Redo the most recently undone CRUD process
     * Currently only supports User and Enrollment creation / deletion
     *
     * @return 200 OK if successful
     */
    @PostMapping("/redo")
    @PreAuthorize(AuthorityConstants.ADMIN_AUTHORITY)
    public ResponseEntity<Void> redo() {
        if (commandService.redo()) {
            logger.info("A resource management process was redone");
            return ResponseEntity.ok().build();
        }

        logger.info("Unsuccessful redo of resource management process");
        return ResponseEntity.badRequest().build();
    }
}
