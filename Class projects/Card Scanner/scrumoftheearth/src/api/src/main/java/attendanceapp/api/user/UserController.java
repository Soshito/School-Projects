//----------------------------------------------------------------------------------------------
// Name: Gavin Papenthien
// Project: Attendance App - This is a full stack attendance tracking and management software.
// Purpose: Provide a mapping for clients to interact with Users.
//----------------------------------------------------------------------------------------------

package attendanceapp.api.user;

import attendanceapp.api.auth.AuthorityConstants;
import attendanceapp.api.exceptions.InvalidRoleException;
import attendanceapp.api.exceptions.InvalidUserException;
import attendanceapp.api.exceptions.MissingStudentCardIdException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;


//---------------------------------------------------------------
// Provide mappings for clients to interact with AttendanceLogs.
//---------------------------------------------------------------
@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/users")
@RequiredArgsConstructor
class UserController {

    private final UserService userService;
    private final Logger logger = LoggerFactory.getLogger(UserController.class);

    /**
     * Get a User by id
     *
     * @param requestedId int id of requested User
     * @return User or 404 NOT FOUND
     */
    @GetMapping("/{requestedId}")
    @PreAuthorize("(" + AuthorityConstants.ADMIN_AUTHORITY + ") OR " + AuthorityConstants.PROFESSOR_AUTHORITY)
    public ResponseEntity<UserResponse> findById(@PathVariable int requestedId) {
        try {
            logger.info("A User was requested");
            logger.trace(String.format("Entering findById with parameters (requestedId = %d)", requestedId));

            UserResponse user = userService.findById(requestedId);
            return ResponseEntity.ok(user);
        }
        catch (InvalidUserException e) {
            logger.warn(e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Get a Page from all existing Users represented as List
     * Default page = 0
     * Default size = 100
     *
     * @param pageable Pageable object containing page number, size and Sorting rule
     * @return List of Users within the Page
     */
    @GetMapping
    @PreAuthorize(AuthorityConstants.ADMIN_AUTHORITY)
    public ResponseEntity<List<UserResponse>> findAll(@PageableDefault(size = 100) Pageable pageable) {
        Page<UserResponse> page = userService.findAll(pageable);
        logger.info("A list of Users was requested");
        return ResponseEntity.ok(page.getContent());
    }

    /**
     * Get a Page from all existing Users of a specified Role as List
     * Default page = 0
     * Default size = 100
     *
     * @param pageable Pageable object containing page number, size and Sorting rule
     * @param roleId ID of desired Role
     * @return List of Users within the Page with the specified roleId
     */
    @GetMapping("/by-roleId")
    @PreAuthorize("(" + AuthorityConstants.ADMIN_AUTHORITY + ")" + " OR " + AuthorityConstants.PROFESSOR_AUTHORITY)
    public ResponseEntity<List<UserResponse>> findAllByRoleId(@PageableDefault(size = 100) Pageable pageable, @RequestParam int roleId) {
        try {
            Page<UserResponse> page = userService.findAllByRoleId(roleId, pageable);
            logger.info("A list of Users was requested");
            return ResponseEntity.ok(page.getContent());
        }
        catch (InvalidRoleException e) {
            logger.warn("Invalid Role: " + e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Create a new User. Only accessible by existing Administrator Users
     *
     * @param newUserRequest UserDTO from request-body containing data for new User
     * @param ucb UriComponentsBuilder injected by Spring-Web to create URI for new object.
     * @return User or 400 BAD REQUEST OR 401 UNAUTHORIZED OR 403 FORBIDDEN
     */
    @PostMapping
    @PreAuthorize(AuthorityConstants.ADMIN_AUTHORITY)
    public ResponseEntity<UserResponse> createUser(@RequestBody UserDTO newUserRequest, UriComponentsBuilder ucb) {
        try {
            UserResponse savedUser = userService.createUser(newUserRequest);
            URI locationOfNewUser = ucb
                    .path("/users/{id}")
                    .buildAndExpand(savedUser.getId())
                    .toUri();

            logger.info("A new User was created");
            logger.trace(String.format("Created User: %s", savedUser));
            return ResponseEntity.created(locationOfNewUser).body(savedUser);
        }
        catch (InvalidRoleException e) {
            logger.warn("Invalid Role: " + e.getMessage());
            return ResponseEntity.badRequest().build();
        }
        catch (MissingStudentCardIdException e) {
            logger.warn("Invalid request: " + e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Delete a User
     *
     * @param id ID of User to delete
     * @return 204 NO-CONTENT or appropriate error code
     */
    @DeleteMapping("/{id}")
    @PreAuthorize(AuthorityConstants.ADMIN_AUTHORITY)
    public ResponseEntity<Void> deleteById(@PathVariable int id) {
        try {
            userService.hardDelete(id);
            return ResponseEntity.noContent().build();
        }
        catch (InvalidUserException e) {
            logger.warn("Invalid request" + e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }
}
