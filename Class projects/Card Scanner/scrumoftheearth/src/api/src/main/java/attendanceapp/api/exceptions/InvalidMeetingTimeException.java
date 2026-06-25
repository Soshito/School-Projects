//----------------------------------------------------------------------------------------------
// Name: Gavin Papenthien
// Project: Attendance App - This is a full stack attendance tracking and management software.
// Purpose: Provide custom Exceptions for errors that may occur in the API.
//----------------------------------------------------------------------------------------------

package attendanceapp.api.exceptions;

//----------------------------------------------------------------------------------------------
// Custom Exception for when a request contains an ID that isn't associated with any MeetingTimes
//----------------------------------------------------------------------------------------------
public class InvalidMeetingTimeException extends RuntimeException {
    public InvalidMeetingTimeException(String message) {
        super(message);
    }
}
