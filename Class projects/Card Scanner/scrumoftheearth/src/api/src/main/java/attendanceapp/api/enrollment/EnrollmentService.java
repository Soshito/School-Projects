package attendanceapp.api.enrollment;

import attendanceapp.api.exceptions.InvalidEnrollmentException;
import attendanceapp.api.exceptions.InvalidRoleException;
import attendanceapp.api.exceptions.InvalidSectionException;
import attendanceapp.api.exceptions.InvalidUserException;
import attendanceapp.api.role.Role;
import attendanceapp.api.role.RoleRepository;
import attendanceapp.api.section.SectionService;
import attendanceapp.api.user.UserResponse;
import attendanceapp.api.user.UserService;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;
    private final UserService userService;
    private final RoleRepository roleRepository;
    private final SectionService sectionService;

    // Need custom constructor since Section Service create a dependency cycle
    // @Lazy can't be used if Lombok generate constructor
    // @Lazy necessary because it stops SectionService from being fully constructed until it is needed
    public EnrollmentService(EnrollmentRepository enrollmentRepository,
                             UserService userService,
                             RoleRepository roleRepository,
                             @Lazy SectionService sectionService) {
        this.enrollmentRepository = enrollmentRepository;
        this.userService = userService;
        this.roleRepository = roleRepository;
        this.sectionService = sectionService;
    }

    /**
     * Find an Enrollment by its ID
     *
     * @param id ID of requested Enrollment
     * @return Enrollment found
     * @throws InvalidEnrollmentException No Enrollment associated with provided ID
     */
    public Enrollment findById(int id) throws InvalidEnrollmentException {
        return enrollmentRepository.findById(id)
                .orElseThrow(() -> new InvalidEnrollmentException("Requested Enrollment does not exist"));
    }

    /**
     * Find all of a Student's enrollments
     *
     * @param id ID of the desired student
     * @return List of Enrollment
     */
    public List<Enrollment> findAllByStudentId(int id) {
        return enrollmentRepository.findAllByStudentId(id);
    }

    /**
     * Validate and create a new Enrollment
     *
     * @param enrollmentRequest EnrollmentDTO containing data related to the Enrollment
     * @return Enrollment if created
     * @throws InvalidSectionException sectionId not associated with any existing Sections
     * @throws InvalidUserException studentId not associated with any existing Users or User is not a Student
     */
    public Enrollment createEnrollment(EnrollmentDTO enrollmentRequest) throws InvalidSectionException, InvalidUserException {
        validateSection(enrollmentRequest.getSectionId());
        validateUser(enrollmentRequest.getStudentId());

        Enrollment newEnrollment = new Enrollment(enrollmentRequest.getSectionId(), enrollmentRequest.getStudentId());

        return enrollmentRepository.save(newEnrollment);
    }

    /**
     * Verify that a requested Section exists
     *
     * @param sectionId ID of requested section
     * @throws InvalidSectionException Provided ID not associated with any existing Sections
     */
    private void validateSection(int sectionId) throws InvalidSectionException {
        sectionService.findById(sectionId);
    }

    /**
     * Verify that a requested User exists and has the Student role.
     *
     * @param id id of User
     * @throws InvalidUserException Provided ID not associated with any existing Users or User is not a Student
     */
    private void validateUser(int id) throws InvalidUserException {
        UserResponse user = userService.findById(id);
        Role role = roleRepository.findById(user.getRoleId())
                .orElseThrow(() -> new InvalidRoleException("This will never happen"));

        if (!role.getName().equals("Student")) {
            throw new InvalidUserException("User is not a Student");
        }
    }

    /**
     * Hard delete an Enrollment from the database
     * Check if Enrollment exists first
     *
     * @param id ID of Enrollment to delete
     * @throws InvalidEnrollmentException Enrollment does not exist
     */
    public void hardDelete(int id) throws InvalidEnrollmentException {
        Enrollment enrollment = enrollmentRepository.findById(id)
                .orElseThrow(() -> new InvalidEnrollmentException("Enrollment does not exist"));

        enrollmentRepository.delete(enrollment);
    }
}
