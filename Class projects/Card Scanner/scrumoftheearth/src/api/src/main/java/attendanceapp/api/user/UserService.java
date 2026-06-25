//----------------------------------------------------------------------------------------------
// Name: Gavin Papenthien
// Project: Attendance App - This is a full stack attendance tracking and management software.
// Purpose: Service layer to handle UserDTO validation and User construction.
//----------------------------------------------------------------------------------------------

package attendanceapp.api.user;

import attendanceapp.api.command.CommandService;
import attendanceapp.api.exceptions.InvalidAuthorization;
import attendanceapp.api.exceptions.InvalidRoleException;
import attendanceapp.api.exceptions.InvalidUserException;
import attendanceapp.api.exceptions.MissingStudentCardIdException;
import attendanceapp.api.role.Role;
import attendanceapp.api.role.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

//---------------------------------------------------------------
// Provide services for UserDTO validation and User construction.
//---------------------------------------------------------------
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final CommandService commandService;

    /**
     * Find a User by their ID
     *
     * @param id ID of the requested User
     * @return User found
     * @throws InvalidUserException No User associated with the provided ID
     */
    public UserResponse findById(int id) throws InvalidUserException {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new InvalidUserException("Requested User does not exist"));

        return convertUserToUserResponse(user);
    }

    public UserResponse findByUsername(String username) throws InvalidUserException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new InvalidUserException("Requested User does not exist"));

        return convertUserToUserResponse(user);
    }

    /**
     * Construct a page of Users using Spring Data's Pagination feature
     *
     * @param pageable Pageable object containing page number, size and Sorting rule with default ids asc
     * @return Page containing found Users
     */
    public Page<UserResponse> findAll(Pageable pageable) {
        Page<User> userPage = userRepository.findAll(
                PageRequest.of(
                        pageable.getPageNumber(),
                        pageable.getPageSize(),
                        pageable.getSortOr(Sort.by(Sort.Direction.ASC, "id"))
                ));

        return convertUserPageToUserResponsePage(userPage);
    }

    /**
     * Construct a page of Users using Spring Data's Pagination feature
     * Users must have Role associated with specified roleId
     *
     * @param roleId ID associated with desired Role
     * @param pageable Pageable object containing page number, size and Sorting rule with default ids asc
     * @return Page containing found Users
     */
    public Page<UserResponse> findAllByRoleId(int roleId, Pageable pageable) {
        getRole(roleId);

        Page<User> userPage = userRepository.findAllByRoleId(
                roleId,
                PageRequest.of(
                        pageable.getPageNumber(),
                        pageable.getPageSize(),
                        pageable.getSortOr(Sort.by(Sort.Direction.ASC, "id"))
                ));

        return convertUserPageToUserResponsePage(userPage);
    }

    /**
     * Map User objects within a Page to a Page of UserResponse objects
     * Hides confidential User data from clients
     *
     * @param userPage Page of User
     * @return Page of UserResponse
     */
    private Page<UserResponse> convertUserPageToUserResponsePage(Page<User> userPage) {
        return userPage.map(user ->
                new UserResponse.UserResponseBuilder()
                        .id(user.getId())
                        .firstName(user.getFirstName())
                        .lastName(user.getLastName())
                        .studentCardId(user.getStudentCardId())
                        .username(user.getUsername())
                        .roleId(user.getRoleId())
                        .build()
        );
    }

    /**
     * Create new UserResponse from a User object
     * Hides confidential User data from clients
     *
     * @param user User
     * @return UserResponse
     */
    private UserResponse convertUserToUserResponse(User user) {
        return new UserResponse.UserResponseBuilder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .studentCardId(user.getStudentCardId())
                .username(user.getUsername())
                .roleId(user.getRoleId())
                .build();
    }

    /**
     * Validate and create a User
     * Unconventional implementation because particular assignment homework calls for specific Command-Pattern undo/redo implementation
     *
     * @param userRequest UserDTO containing data related to the new User
     * @return User if it was created
     * @throws InvalidRoleException roleId not associated with any Role
     * @throws MissingStudentCardIdException Requested to create student but didn't provide a studentCardId
     */
    public UserResponse createUser(UserDTO userRequest) throws InvalidRoleException, MissingStudentCardIdException {
        Role role = getRole(userRequest.getRoleId());
        String encodedPassword = passwordEncoder.encode(userRequest.getPassword());

        verifyStudentCardId(userRequest.getStudentCardId(), role); // Make sure if request is for a Student that a valid studentCardId has been provided
        commandService.doCreateUser(userRequest, encodedPassword);

        // Need command pattern to actually save the user, but the ID isn't assigned until saved to the UserRepo
        // And the User cannot be extracted back out of the command
        // So need to grab the user somehow in order to return data to the client
        return convertUserToUserResponse(userRepository.findByUsername(userRequest.getUsername()).orElseThrow(() -> new InvalidUserException("Error")));
    }

    /**
     * Find a Role by its ID
     *
     * @param roleId id of the requested role
     * @return Role if it exists
     * @throws InvalidRoleException No existing Role associated with roleId
     */
    public Role getRole(int roleId) throws InvalidRoleException {
        Optional<Role> roleOptional = roleRepository.findById(roleId);

        if (roleOptional.isEmpty()) {
            throw new InvalidRoleException("No Role associated with roleId");
        }

        return roleOptional.get();
    }

    /**
     * Verify necessary data was provided to create a User with the Student role
     *
     * @param studentCardId String id associated with the students id card
     * @param role role for the new user
     * @throws MissingStudentCardIdException Requested to create a student but didn't provide a studentCardId
     */
    public void verifyStudentCardId(String studentCardId, Role role) throws MissingStudentCardIdException {
        if (studentCardId.isEmpty() && role.getName().equals("Student")) {
            throw new MissingStudentCardIdException("User request for User with Role: 'Student' must contain studentCardId");
        }
    }

    /**
     * Validate that a User associated with the provided ID exists
     * Validate that the User has the Student role
     * Validate that if a Student is requesting the data, they are only requesting their own data
     *
     * @param id ID of the desired User
     * @throws InvalidUserException User does not exist, is not a student, or is not requesting their own data
     * @throws AccessDeniedException A User with the Student role sent the request for data that is not theirs
     */
    public void validateStudent(int id) throws InvalidUserException, InvalidAuthorization {
        UserResponse user = findById(id);
        Role role = roleRepository.findById(user.getRoleId())
                // Should be impossible
                .orElseThrow(() -> new InvalidRoleException("User exists but has invalid Role?"));

        if (!role.getName().equals("Student")) {
            throw new InvalidUserException("Requested User is not a Student");
        }

        // If the request is coming from a Student, make sure they're requesting data only related to them
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        UserResponse requester = findByUsername(username);
        int requesterId = requester.getId();
        Role requesterRole = roleRepository.findById(requester.getRoleId())
                .orElseThrow(() -> new InvalidRoleException("User exists but has invalid Role?"));

        if (requesterRole.getName().equals("Student") && requesterId != user.getId()) {
            throw new InvalidAuthorization(String.format("Student with ID %d attempted to access data belonging to User with ID %d", requesterId, id));
        }
    }

    /**
     * Hard delete a User from the database
     * Check if the User exists first
     *
     * @param id User ID
     * @throws InvalidUserException User does not exist
     */
    public void hardDelete(int id) throws InvalidUserException {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new InvalidUserException("User does not exist"));

        userRepository.delete(user);
    }
}
