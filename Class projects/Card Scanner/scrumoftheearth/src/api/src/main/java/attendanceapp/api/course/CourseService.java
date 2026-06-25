//----------------------------------------------------------------------------------------------
// Name: Gavin Papenthien
// Project: Attendance App - This is a full stack attendance tracking and management software.
// Purpose: Service layer to handle CourseDTO validation and Course construction.
//----------------------------------------------------------------------------------------------

package attendanceapp.api.course;

import attendanceapp.api.command.Command;
import attendanceapp.api.command.CommandService;
import attendanceapp.api.command.CreateCourseCommand;
import attendanceapp.api.command.Invoker;
import attendanceapp.api.exceptions.InvalidCourseException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

//---------------------------------------------------------------
// Provide services for CourseDTO validation and Course construction.
//---------------------------------------------------------------
@Service
@RequiredArgsConstructor
public class CourseService {

    private final CourseRepository courseRepository;
    private final CommandService commandService;

    /**
     * Find a Course by its ID
     *
     * @param id ID of the Course to be obtained
     * @return Course if found
     * @throws InvalidCourseException If ID is not associated with any Courses
     */
    public Course findById(int id) throws InvalidCourseException {
         return courseRepository.findById(id)
                .orElseThrow(() -> new InvalidCourseException("Requested Course does not exist"));
    }

    /**
     * Construct a page of Users using Spring Data's Pagination feature
     *
     * @param pageable Pageable object containing page number, size and Sorting rule with default ids asc
     * @return Page containing found Users
     */
    public Page<Course> findAll(Pageable pageable) {
        return courseRepository.findAll(
                PageRequest.of(
                        pageable.getPageNumber(),
                        pageable.getPageSize(),
                        pageable.getSortOr(Sort.by(Sort.Direction.ASC, "id"))
                ));
    }

    /**
     * Create a new Course
     * Very minimal logic, Courses are only composed of a name right now
     * This has to jump through a bit of a weird hoop because there is a very specific implementation
     * of the command pattern we have to follow for a homework assignment
     *
     * @param courseRequest CourseDTO containing name of new Course
     * @return created Course
     */
    public Course createCourse(CourseDTO courseRequest) {
        commandService.doCreateCourse(courseRequest);

        return courseRepository.findByName(courseRequest.getName());
    }
}
