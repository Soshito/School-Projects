package attendanceapp.api.meetingtime;

import attendanceapp.api.exceptions.InvalidCourseException;
import attendanceapp.api.exceptions.InvalidDayOfWeekException;
import attendanceapp.api.exceptions.InvalidMeetingTimeException;
import attendanceapp.api.exceptions.InvalidSectionException;
import attendanceapp.api.section.SectionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class MeetingTimeService {

    private final MeetingTimeRepository meetingTimeRepository;
    private final SectionService sectionService;

    /**
     * Find a MeetingTime by its ID
     *
     * @param id ID of requested MeetingTime
     * @return MeetingTime found
     * @throws InvalidMeetingTimeException No MeetingTime associated with provided ID
     */
    public MeetingTime findById(int id) throws InvalidMeetingTimeException {
        return meetingTimeRepository.findById(id)
                .orElseThrow(() -> new InvalidMeetingTimeException("Requested MeetingTime does not exist"));
    }

    /**
     * Create a new MeetingTime
     *
     * @param request MeetingTimeDTO containing data for new MeetingTime
     * @return new MeetingTime
     * @throws InvalidCourseException ID of course for new MeetingTime does not correlate to any existing Courses
     * @throws InvalidDayOfWeekException Integer representing day of the week does not correlate to a weekday
     * @throws ParseException Provided time String could not be converted into a java.sql.Time object
     */
    public MeetingTime createMeetingTime(MeetingTimeDTO request) throws InvalidCourseException, InvalidDayOfWeekException, ParseException {
        validateSectionId(request.getSectionId());
        validateDayOfWeek(request.getDayOfWeek());
        Time startTime = extractTimeFromString(request.getStartTime());
        Time endTime = extractTimeFromString(request.getEndTime());

        MeetingTime newMeetingTime = new MeetingTime(request.getSectionId(), request.getDayOfWeek(), startTime, endTime);

        return meetingTimeRepository.save(newMeetingTime);
    }

    /**
     * Validate an integers correlation to a day of the week
     * 1 - Sunday
     * 2 - Monday
     * 3 - Tuesday
     * 4 - Wednesday
     * 5 - Thursday
     * 6 - Friday
     * 7 - Saturday
     *
     * @param dayOfWeek integer day of the week
     * @throws InvalidDayOfWeekException Provided integer does not correlate to any days of the week
     */
    private void validateDayOfWeek(int dayOfWeek) throws InvalidDayOfWeekException {
        if (dayOfWeek <= 0 || dayOfWeek > 7) {
            throw new InvalidDayOfWeekException(dayOfWeek + " is not a valid day of the week");
        }
    }

    /**
     * Validate that a Section corresponding with the requested ID exists
     *
     * @param id ID of requested Section
     * @throws InvalidSectionException No Section associated with the requested ID exists
     */
    private void validateSectionId(int id) throws InvalidSectionException {
        sectionService.findById(id);
    }

    /**
     * Extract a java.sql.Time object from a String
     *
     * @param time String representation of the time, expected to be in hh:mm format
     * @return new Time object
     * @throws ParseException String could not be parsed into a Time object
     */
    private Time extractTimeFromString(String time) throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm");
        Date parsedDate = dateFormat.parse(time);
        long timeMillis = parsedDate.getTime();;

        return new Time(timeMillis);
    }
}
