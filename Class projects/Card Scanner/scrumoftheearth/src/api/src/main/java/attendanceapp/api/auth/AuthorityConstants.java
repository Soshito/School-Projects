//----------------------------------------------------------------------------------------------
// Name: Gavin Papenthien
// Project: Attendance App - This is a full stack attendance tracking and management software.
// Purpose: Provide constants to reference the @preAuthorize() expected strings for each Role
//---------------------------------------------------------------------------------------------

package attendanceapp.api.auth;

import lombok.Generated;

//---------------------------------------------------------------
// Provide constants to reference the @preAuthorize() expected strings for each Role
//---------------------------------------------------------------
@Generated
public class AuthorityConstants {
    public static final String ADMIN_AUTHORITY = "hasAuthority('1')";
    public static final String PROFESSOR_AUTHORITY = "hasAuthority('2')";
    public static final String STUDENT_AUTHORITY = "hasAuthority('3')";
}
