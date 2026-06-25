//----------------------------------------------------------------------------------------------
// Name: Gavin Papenthien
// Project: Attendance App - This is a full stack attendance tracking and management software.
// Purpose: Service layer to handle command pattern multiple undo/redo operations.
//----------------------------------------------------------------------------------------------

package attendanceapp.api.command;

import attendanceapp.api.course.Course;
import attendanceapp.api.course.CourseDTO;
import attendanceapp.api.course.CourseRepository;
import attendanceapp.api.user.User;
import attendanceapp.api.user.UserDTO;
import attendanceapp.api.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


//---------------------------------------------------------------
// Provide services for command pattern multiple undo/redo operations
//---------------------------------------------------------------
@Service
@RequiredArgsConstructor
public class CommandService {
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    private final Invoker invoker;

    /**
     * Create a Course
     *
     * @param courseRequest CourseDTO containing data for new Course
     */
    public void doCreateCourse(CourseDTO courseRequest) {
        Course newCourse = new Course(courseRequest.getName(), 0);
        Command command = new CreateCourseCommand(newCourse, courseRepository);

        invoker.firstDo(command);
    }

    /**
     * Create a User
     *
     * @param userRequest UserDTO containing data for new User
     * @param encodedPassword Encoded version of the new User's password
     */
    public void doCreateUser(UserDTO userRequest, String encodedPassword) {
        User newUser = new User(
                null,
                userRequest.getFirstName(),
                userRequest.getLastName(),
                userRequest.getStudentCardId(),
                userRequest.getUsername(),
                encodedPassword,
                userRequest.getRoleId()
        );
        Command command = new CreateUserCommand(newUser, userRepository);

        invoker.firstDo(command);
    }

    /**
     * Ask the Invoker to undo the most recent Command
     *
     * @return true if the Command was undone, false if there was nothing to undo
     */
    public boolean undo() {
        return invoker.undo();
    }

    /**
     * Ask the Invoker to redo the most recently undone Command
     *
     * @return true if the Command was redone, false if there was nothing to redo
     */
    public boolean redo() {
        return invoker.redo();
    }
}
