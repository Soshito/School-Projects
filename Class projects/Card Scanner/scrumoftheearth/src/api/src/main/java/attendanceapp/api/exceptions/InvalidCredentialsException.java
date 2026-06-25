//----------------------------------------------------------------------------------------------
// Name: Gavin Papenthien
// Project: Attendance App - This is a full stack attendance tracking and management software.
// Purpose: Provide custom Exceptions for errors that may occur in the API.
//----------------------------------------------------------------------------------------------

package attendanceapp.api.exceptions;

//----------------------------------------------------------------------------------------------
// Custom Exception for when a request contains credentials not associated with any Users
//----------------------------------------------------------------------------------------------
public class InvalidCredentialsException extends RuntimeException {

    public InvalidCredentialsException(String message) {
        super(message);
    }
}
