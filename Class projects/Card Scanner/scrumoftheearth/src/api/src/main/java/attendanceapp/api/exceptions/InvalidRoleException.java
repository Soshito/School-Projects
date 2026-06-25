//----------------------------------------------------------------------------------------------
// Name: Gavin Papenthien
// Project: Attendance App - This is a full stack attendance tracking and management software.
// Purpose: Provide custom Exceptions for errors that may occur in the API.
//----------------------------------------------------------------------------------------------

package attendanceapp.api.exceptions;

//----------------------------------------------------------------------------------------------
// Custom Exception for when a POST /users request contains a roleId that isn't associated with any Role
//----------------------------------------------------------------------------------------------
public class InvalidRoleException extends RuntimeException {

    public InvalidRoleException(String message) {
        super(message);
    }
}
