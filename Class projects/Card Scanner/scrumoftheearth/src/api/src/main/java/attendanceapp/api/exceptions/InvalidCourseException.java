//----------------------------------------------------------------------------------------------
// Name: Gavin Papenthien
// Project: Attendance App - This is a full stack attendance tracking and management software.
// Purpose: Provide custom Exceptions for errors that may occur in the API.
//----------------------------------------------------------------------------------------------

package attendanceapp.api.exceptions;

//----------------------------------------------------------------------------------------------
// Custom Exception for when a request contains credentials not associated with any Courses
//----------------------------------------------------------------------------------------------
public class InvalidCourseException extends RuntimeException {
    public InvalidCourseException(String message) {
        super(message);
    }
}
