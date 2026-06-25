//----------------------------------------------------------------------------------------------
// Name: Gavin Papenthien
// Project: Attendance App - This is a full stack attendance tracking and management software.
// Purpose: Command Pattern implementation to manage Course creation / deletion.
//----------------------------------------------------------------------------------------------

package attendanceapp.api.command;

import attendanceapp.api.course.Course;
import attendanceapp.api.course.CourseRepository;
import lombok.AllArgsConstructor;

//---------------------------------------------------------------
// Command Pattern implementation to manage Course creation / deletion
//---------------------------------------------------------------
@AllArgsConstructor
public class CreateCourseCommand implements Command {

    private Course course;
    private final CourseRepository courseRepository;

    /**
     * Create the Course
     */
    @Override
    public void execute() {
        Course newCourse = new Course(course.getName(), course.getSectionCount());

        course = courseRepository.save(newCourse);
    }

    /**
     * Delete the Course
     */
    @Override
    public void unExecute() {
        courseRepository.delete(course);
    }
}
