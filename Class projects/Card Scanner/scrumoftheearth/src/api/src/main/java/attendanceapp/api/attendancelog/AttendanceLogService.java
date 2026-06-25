//----------------------------------------------------------------------------------------------
// Name: Gavin Papenthien
// Project: Attendance App - This is a full stack attendance tracking and management software.
// Purpose: Service layer to handle AttendanceLogDTO validation and AttendanceLog construction.
//----------------------------------------------------------------------------------------------

package attendanceapp.api.attendancelog;

import attendanceapp.api.enrollment.Enrollment;
import attendanceapp.api.enrollment.EnrollmentRepository;
import attendanceapp.api.exceptions.InvalidCredentialsException;
import attendanceapp.api.exceptions.InvalidEnrollmentException;
import attendanceapp.api.exceptions.InvalidSectionException;
import attendanceapp.api.exceptions.InvalidUserException;
import attendanceapp.api.meetingtime.MeetingTime;
import attendanceapp.api.meetingtime.MeetingTimeRepository;
import attendanceapp.api.role.RoleRepository;
import attendanceapp.api.section.Section;
import attendanceapp.api.section.SectionRepository;
import attendanceapp.api.section.SectionService;
import attendanceapp.api.user.User;
import attendanceapp.api.user.UserRepository;
import attendanceapp.api.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalTime;
import java.util.Calendar;
import java.util.List;
import java.util.Optional;

//---------------------------------------------------------------
// Provide services for AttendanceLogDTO validation and AttendanceLog construction.
//---------------------------------------------------------------
@Service
@RequiredArgsConstructor
public class AttendanceLogService {

    private final UserRepository userRepository;
    private final UserService userService;
    private final EnrollmentRepository enrollmentRepository;
    private final AttendanceLogRepository attendanceLogRepository;
    private final MeetingTimeRepository meetingTimeRepository;
    private final SectionRepository sectionRepository;
    private final SectionService sectionService;

    /**
     * Construct a page of AttendanceLogs using Spring Data's Pagination feature
     * AttendanceLogs must be associated with specified studentId (User ID) and sectionId
     *
     * @param pageable Pageable object containing page number, size and Sorting rule with default ids asc
     * @param studentId ID associated with the desired student/user
     * @param sectionId ID associated with the desired section
     * @return Page containing found AttendanceLogs
     * @throws InvalidUserException studentId not associated with any existing User or User does not have Student Role
     * @throws InvalidSectionException sectionId not associated with any existing Section
     */
    public Page<AttendanceLog> findAllByStudentAndSectionId(Pageable pageable, int studentId, int sectionId)
    throws InvalidUserException, InvalidSectionException {
        userService.validateStudent(studentId);
        validateSection(sectionId);

        return attendanceLogRepository.findAllByStudentIdAndSectionId(
                studentId,
                sectionId,
                PageRequest.of(
                        pageable.getPageNumber(),
                        pageable.getPageSize(),
                        pageable.getSortOr(Sort.by(Sort.Direction.ASC, "id"))
                ));
    }

    /**
     * Validate that a Section associated with the provided ID exists
     *
     * @param id ID of the desired Section
     * @throws InvalidSectionException Section does not exist
     */
    private void validateSection(int id) throws InvalidSectionException {
        sectionService.findById(id);
    }

    /**
     * Validate and create an AttendanceLog
     *
     * @param logRequest AttendanceLogDTO containing studentId and roomNum for request
     * @return AttendanceLog if it was created
     * @throws InvalidCredentialsException No User associated with studentCardId
     * @throws InvalidEnrollmentException No Enrollment for User at given roomNum and Timestamp
     */
    public AttendanceLog createAttendanceLog(AttendanceLogDTO logRequest) throws InvalidCredentialsException, InvalidEnrollmentException {
        Timestamp timestamp = Timestamp.from(Instant.now());
        String roomNum = logRequest.getRoomNum();
        User student = getUserByStudentCardId(logRequest.getStudentCardId());

        // Get list of student Enrollments
        List<Enrollment> enrollments = getStudentEnrollments(student);

        // See if any Enrollments have a MeetingTime at the current date & time
        MeetingTime meetingTime = findMatchingMeetingTime(enrollments, timestamp);

        // Check if MeetingTime matches requested roomNum
        Section section = getSectionByMeetingTimeAndRoomNum(meetingTime, roomNum);
        
        // Create and return AttendanceLog
        AttendanceLog newLog = new AttendanceLog(student.getId(), section.getId(), timestamp, false);

        return attendanceLogRepository.save(newLog);
    }

    /**
     * Find a User by their studentCardId
     *
     * @param studentCardId studentCardId of the request student
     * @return User if they exist
     * @throws InvalidCredentialsException if the studentCardId isn't associated with a User
     */
    private User getUserByStudentCardId(String studentCardId) throws InvalidCredentialsException {
        Optional<User> userOptional = userRepository.findByStudentCardId(studentCardId);

        if (userOptional.isEmpty()) {
            throw new InvalidCredentialsException("Invalid studentCardId");
        }
        return userOptional.get();
    }

    /**
     * Find all Enrollments for a User
     *
     * @param student User to search for associated Enrollments
     * @return List<Enrollment> entries if the User has Enrollments
     * @throws InvalidEnrollmentException if the User is not enrolled in any Sections
     */
    private List<Enrollment> getStudentEnrollments(User student) throws InvalidEnrollmentException {
        int studentId = student.getId();
        List<Enrollment> enrollments = enrollmentRepository.findAllByStudentId(studentId);

        if (enrollments.isEmpty()) {
            throw new InvalidEnrollmentException("No Enrollments exist for this User");
        }

        return enrollments;
    }

    /**
     * Find a MeetingTime that is in session at the same time as the request, or starts within 10 minutes of the request
     *
     * @param enrollments List<Enrollment> to compare the timestamp to
     * @param timestamp Timestamp to compare the enrollment MeetingTimes to
     * @return MeetingTime if found
     */
    private MeetingTime findMatchingMeetingTime(List<Enrollment> enrollments, Timestamp timestamp) {
        // Extract day and time data from timestamp
        long timeMillis = timestamp.getTime();
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(timeMillis);
        int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
        LocalTime time = LocalTime.of(cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), cal.get(Calendar.SECOND));

        // Get list of MeetingTime for each enrollment
        for (Enrollment enrollment : enrollments) {
            List<MeetingTime> meetingTimes = meetingTimeRepository.findAllBySectionId(enrollment.getSectionId());

            // Check if any MeetingTime time-frame fits current time
            for (MeetingTime meetingTime : meetingTimes) {
                LocalTime startTime = meetingTime.getStartTime().toLocalTime();
                LocalTime endTime = meetingTime.getEndTime().toLocalTime();

                if (meetingTime.getDayOfWeek() == dayOfWeek &&                             // day of week matches
                    ((startTime.isBefore(time) && endTime.isAfter(time)) ||             // and within session
                    (startTime.minusMinutes(10).isBefore(time)))) {     // or within 10 min of session start
                    return meetingTime;
                }
            }
        }

        throw new InvalidEnrollmentException("User is not Enrolled in any Sections at this time");
    }

    /**
     * Find a Section associated with a MeetingTime and roomNum
     *
     * @param meetingTime MeetingTime data of when the Section meets
     * @param roomNum string of the room the section meets in
     * @return Section if found
     */
    private Section getSectionByMeetingTimeAndRoomNum(MeetingTime meetingTime, String roomNum) {
        int sectionId = meetingTime.getSectionId();
        Optional<Section> section = sectionRepository.findById(sectionId);

        Section validSection = section.get(); // In theory could throw an error, but should be impossible

        if (!validSection.getRoomNum().equals(roomNum)) {
            throw new InvalidEnrollmentException("User is enrolled in a Section at this time but roomNum does not match");
        }

        return validSection;
    }
}
