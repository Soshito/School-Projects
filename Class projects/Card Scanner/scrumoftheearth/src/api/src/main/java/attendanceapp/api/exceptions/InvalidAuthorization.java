//----------------------------------------------------------------------------------------------
// Name: Gavin Papenthien
// Project: Attendance App - This is a full stack attendance tracking and management software.
// Purpose: Provide custom Exceptions for errors that may occur in the API.
//----------------------------------------------------------------------------------------------

package attendanceapp.api.exceptions;

//----------------------------------------------------------------------------------------------
// Custom Exception for requests with insufficient authorization that cannot be pre-filtered
//----------------------------------------------------------------------------------------------
public class InvalidAuthorization extends RuntimeException {
    public InvalidAuthorization(String message) {
        super(message);
    }
}
