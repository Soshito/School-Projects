//----------------------------------------------------------------------------------------------
// Name: Gavin Papenthien
// Project: Attendance App - This is a full stack attendance tracking and management software.
// Purpose: Provide custom Exceptions for errors that may occur in the API.
//----------------------------------------------------------------------------------------------

package attendanceapp.api.exceptions;

//----------------------------------------------------------------------------------------------
// Custom Exception for when an integer does not correspond to a day of the week.
//----------------------------------------------------------------------------------------------
public class InvalidDayOfWeekException extends RuntimeException {

    public InvalidDayOfWeekException(String message) {
        super(message);
    }
}
