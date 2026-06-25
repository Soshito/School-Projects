//----------------------------------------------------------------------------------------------
// Name: Gavin Papenthien
// Project: Attendance App - This is a full stack attendance tracking and management software.
// Purpose: Provide an interface that allows Courses to be queried to and from the database.
//----------------------------------------------------------------------------------------------

package attendanceapp.api.course;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

//----------------------------------------------------------------------------------------------
// An interface that allows Courses to be queried to and from the database.
//----------------------------------------------------------------------------------------------
public interface CourseRepository extends CrudRepository<Course, Integer>, PagingAndSortingRepository<Course, Integer> {

    /**
     * Custom query to search for a Course by its name
     *
     * @param name Name of the Course
     * @return Course if found
     */
    Course findByName(String name);
}
