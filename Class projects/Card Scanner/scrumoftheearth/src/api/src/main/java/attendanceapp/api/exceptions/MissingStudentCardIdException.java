//----------------------------------------------------------------------------------------------
// Name: Gavin Papenthien
// Project: Attendance App - This is a full stack attendance tracking and management software.
// Purpose: Provide custom Exceptions for errors that may occur in the API.
//----------------------------------------------------------------------------------------------

package attendanceapp.api.exceptions;


//----------------------------------------------------------------------------------------------
// Custom Exception for when a POST /users requests to create a Student but doesn't provide a studentCardId
//----------------------------------------------------------------------------------------------
public class MissingStudentCardIdException extends Exception {
    public MissingStudentCardIdException(String message) {
        super(message);
    }
}
