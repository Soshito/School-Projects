//----------------------------------------------------------------------------------------------
// Name: Gavin Papenthien
// Project: Attendance App - This is a full stack attendance tracking and management software.
// Purpose: Command Pattern implementation to manage User creation / deletion.
//----------------------------------------------------------------------------------------------

package attendanceapp.api.command;

import attendanceapp.api.user.User;
import attendanceapp.api.user.UserRepository;
import lombok.AllArgsConstructor;


//---------------------------------------------------------------
// Command Pattern implementation to manage User creation / deletion
//---------------------------------------------------------------
@AllArgsConstructor
public class CreateUserCommand implements Command {

    private User user;
    private final UserRepository userRepository;

    /**
     * Create the User
     */
    @Override
    public void execute() {
        // Need to construct a new User to exclude ID
        User newUser = new User(
                null,
                user.getFirstName(),
                user.getLastName(),
                user.getStudentCardId(),
                user.getUsername(),
                user.getPassword(),
                user.getRoleId());

        user = userRepository.save(newUser);
    }

    /**
     * Delete the User
     */
    @Override
    public void unExecute() {
        userRepository.delete(user);
    }
}
