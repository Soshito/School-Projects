//----------------------------------------------------------------------------------------------
// Name: Gavin Papenthien
// Project: Attendance App - This is a full stack attendance tracking and management software.
// Purpose: Provide an interface that allows Enrollments to be queried to and from the database.
//----------------------------------------------------------------------------------------------

package attendanceapp.api.enrollment;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

//----------------------------------------------------------------------------------------------
// An interface that allows Enrollments to be queried to and from the database.
//----------------------------------------------------------------------------------------------
public interface EnrollmentRepository extends CrudRepository<Enrollment, Integer> {
    /**
     * Custom query to find all of a Student's Enrollments by studentId
     *
     * @param studentId int id of student in requested enrollments
     * @return List of found Enrollments
     */
    List<Enrollment> findAllByStudentId(int studentId);

    /**
     * Custom query to find all Enrollments for a Section
     *
     * @param sectionId ID of the section
     * @return List of found Enrollments
     */
    List<Enrollment> findAllBySectionId(int sectionId);
}
