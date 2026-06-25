//----------------------------------------------------------------------------------------------
// Name: Gavin Papenthien
// Project: Attendance App - This is a full stack attendance tracking and management software.
// Purpose: Service layer to handle SectionDTO validation and Section construction.
//----------------------------------------------------------------------------------------------

package attendanceapp.api.section;

import attendanceapp.api.course.CourseService;
import attendanceapp.api.enrollment.Enrollment;
import attendanceapp.api.enrollment.EnrollmentService;
import attendanceapp.api.exceptions.InvalidCourseException;
import attendanceapp.api.exceptions.InvalidRoleException;
import attendanceapp.api.exceptions.InvalidSectionException;
import attendanceapp.api.exceptions.InvalidUserException;
import attendanceapp.api.role.Role;
import attendanceapp.api.role.RoleRepository;
import attendanceapp.api.user.UserResponse;
import attendanceapp.api.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

//---------------------------------------------------------------
// Provide services for SectionDTO validation and Section construction.
//---------------------------------------------------------------
@Service
@RequiredArgsConstructor
public class SectionService {

    private final SectionRepository sectionRepository;
    private final CourseService courseService;
    private final UserService userService;
    private final RoleRepository roleRepository;
    private final EnrollmentService enrollmentService;

    /**
     * Find a Section by its ID
     *
     * @param id ID of requested Section
     * @return Section found
     * @throws InvalidSectionException No Section associated with provided ID
     */
    public Section findById(int id) throws InvalidSectionException {
        return sectionRepository.findById(id)
                .orElseThrow(() -> new InvalidSectionException("Requested Section does not exist"));
    }

    /**
     * Construct a page of Sections using Spring Data's Pagination feature
     *
     * @param pageable Pageable object containing page number, size and Sorting rule with default ids asc
     * @return Page containing found Sections
     */
    public Page<Section> findAll(Pageable pageable) {
        return sectionRepository.findAll(
                PageRequest.of(
                        pageable.getPageNumber(),
                        pageable.getPageSize(),
                        pageable.getSortOr(Sort.by(Sort.Direction.ASC, "id"))
                ));
    }

    /**
     * Construct a Page of Sections using Spring Data's Pagination feature
     * Sections in Page must have specified courseId
     *
     * @param courseId ID of associated Course
     * @param pageable Pageable object containing page number, size and Sorting rule
     * @return Page of sections with specified courseId
     * @throws InvalidCourseException No Course associated with provided courseId
     */
    public Page<Section> findAllByCourseId(int courseId, Pageable pageable) throws InvalidCourseException {
        validateCourseId(courseId);

        return sectionRepository.findAllByCourseId(
                courseId,
                PageRequest.of(
                        pageable.getPageNumber(),
                        pageable.getPageSize(),
                        pageable.getSortOr(Sort.by(Sort.Direction.ASC, "id"))
                ));
    }

    /**
     * Construct a Page of Sections using Spring Data's Pagination feature
     * Sections in Page must have a corresponding Enrollment with specified studentId
     *
     * @param studentId ID of associated User
     * @param pageable Pageable object containing page number, size and Sorting rule
     * @return Page of Sections with corresponding Enrollment with specified studentId
     * @throws InvalidUserException User does not exist or does not have the Student Role
     * @throws AccessDeniedException A User with the Student role sent the request for data that is not theirs
     */
    public Page<Section> findAllByStudentId(int studentId, Pageable pageable) throws InvalidUserException, AccessDeniedException {
        userService.validateStudent(studentId);
        List<Enrollment> enrollments = enrollmentService.findAllByStudentId(studentId);
        List<Section> sections = new ArrayList<>();

        for (Enrollment enrollment : enrollments) {
            sections.add(findById(enrollment.getSectionId()));
        }

        return new PageImpl<>(sections.subList(0, sections.size()), PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                pageable.getSortOr(Sort.by(Sort.Direction.ASC, "id"))
        ),
                sections.size());
    }

    /**
     * Create a new Section
     *
     * @param sectionRequest SectionDTO containing data for new Section
     * @return new Section
     */
    public Section createSection(SectionDTO sectionRequest) {
        validateCourseId(sectionRequest.getCourseId());
        validateProfessorId(sectionRequest.getProfessorId());

        Section newSection = new Section(sectionRequest.getRoomNum(),
                sectionRequest.getNumberOfStudent(),
                sectionRequest.getCourseId(),
                sectionRequest.getProfessorId());

        return sectionRepository.save(newSection);
    }

    /**
     * Validate the ID of the Course a new Section is being created under
     *
     * @param courseId ID of the Course
     * @throws InvalidCourseException If the Course does not exist
     */
    private void validateCourseId(int courseId) throws InvalidCourseException {
        courseService.findById(courseId);
    }

    /**
     * Validate that the User getting assigned to a new Section is an existing Professor
     *
     * @param professorId ID of the User to assign
     * @throws InvalidUserException User does not exist
     * @throws InvalidRoleException User does not have Professor Role
     */
    private void validateProfessorId(int professorId) throws InvalidUserException, InvalidRoleException {
        UserResponse user = userService.findById(professorId);
        // This should never throw an error
        Role userRole = roleRepository.findById(user.getRoleId())
                .orElseThrow(() -> new InvalidRoleException("User somehow has a role that does not exist"));

        if (!userRole.getName().equals("Professor")) {
            throw new InvalidRoleException("Cannot assign User without Role Professor to Section");
        }
    }
}
