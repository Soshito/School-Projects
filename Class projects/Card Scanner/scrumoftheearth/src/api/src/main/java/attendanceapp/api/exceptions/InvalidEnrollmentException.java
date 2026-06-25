//----------------------------------------------------------------------------------------------
// Name: Gavin Papenthien
// Project: Attendance App - This is a full stack attendance tracking and management software.
// Purpose: Provide custom Exceptions for errors that may occur in the API.
//----------------------------------------------------------------------------------------------

package attendanceapp.api.exceptions;

//----------------------------------------------------------------------------------------------
// Custom Exception for when a User does not have a valid Enrollment associated with request data.
//----------------------------------------------------------------------------------------------
public class InvalidEnrollmentException extends RuntimeException {

    public InvalidEnrollmentException(String message) {
        super(message);
    }
}
