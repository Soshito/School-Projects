//----------------------------------------------------------------------------------------------
// Name: Gavin Papenthien
// Project: Attendance App - This is a full stack attendance tracking and management software.
// Purpose: Provide automatic absence detection and create absent logs
//----------------------------------------------------------------------------------------------

package attendanceapp.api.attendancelog;

import attendanceapp.api.enrollment.Enrollment;
import attendanceapp.api.enrollment.EnrollmentRepository;
import attendanceapp.api.meetingtime.MeetingTime;
import attendanceapp.api.meetingtime.MeetingTimeRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.List;

//---------------------------------------------------------------
// Provide automatic absence detection and create absent logs.
//---------------------------------------------------------------
@RequiredArgsConstructor
@Component
public class AbsenceManager {
    private final Logger logger = LoggerFactory.getLogger(AbsenceManager.class);
    private final AttendanceLogRepository attendanceLogRepository;
    private final MeetingTimeRepository meetingTimeRepository;
    private final EnrollmentRepository enrollmentRepository;
    private static final String scheduleCron = "59 11 * * *"; // Every day at 11:59 pm

    /**
     * Once per day at 11:59pm, iterate through all Enrollments that have a MeetingTime on the current day
     * and create an absent AttendanceLog for all Students that didn't check into their Enrolled Course
     */
    @Scheduled(cron = scheduleCron)
    public void detectAbsences() {
        logger.info("Beginning normal process of automatic absence detection");

        // Retrieve all MeetingTimes for the current day
        int dayOfWeek = getDayOfWeek();
        Timestamp ts = Timestamp.from(Instant.now());
        LocalDate currentDate = ts.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        List<MeetingTime> meetingTimes = meetingTimeRepository.findAllByDayOfWeek(dayOfWeek);

        // For each meeting time, find all the corresponding Enrollments for the overlying Section
        for (MeetingTime meetingTime : meetingTimes) {
            List<Enrollment> enrollments = enrollmentRepository.findAllBySectionId(meetingTime.getSectionId());

            // Check if User in Enrollment has an AttendanceLog matching the current day
            // for the specified Section
            for (Enrollment enrollment : enrollments) {
                boolean logged = false;
                List<AttendanceLog> attendanceLogs = attendanceLogRepository.findAllByStudentIdAndSectionId(enrollment.getStudentId(), enrollment.getSectionId());

                for (AttendanceLog attendanceLog : attendanceLogs) {
                    LocalDate logLocalDate = attendanceLog.getDateTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

                    if (logLocalDate.equals(currentDate)) {
                        logged = true;
                    }
                }
                if (!logged) { // If none found, create a log with is_absent = true
                    AttendanceLog absentLog = new AttendanceLog(enrollment.getStudentId(), enrollment.getSectionId(), ts, true);
                    attendanceLogRepository.save(absentLog);
                }
            }
        }

        logger.info("Completed normal process of automatic absence detection");
    }

    /**
     * Get the current day of the week
     * @return int representing the day of the week
     */
    private int getDayOfWeek() {
        Timestamp timestamp = Timestamp.from(Instant.now());
        long timeMillis = timestamp.getTime();
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(timeMillis);

        return cal.get(Calendar.DAY_OF_WEEK);
    }
}
