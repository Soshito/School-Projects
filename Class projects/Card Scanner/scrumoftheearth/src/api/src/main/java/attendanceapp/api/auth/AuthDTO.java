//----------------------------------------------------------------------------------------------
// Name: Gavin Papenthien
// Project: Attendance App - This is a full stack attendance tracking and management software.
// Purpose: Create an entity to represent the authentication data used for data transfer to the controller from clients..
//----------------------------------------------------------------------------------------------

package attendanceapp.api.auth;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

//----------------------------------------------------------------------------------------------
// An entity to represent the authentication data used for data transfer to the controller from clients.
//----------------------------------------------------------------------------------------------
@RequiredArgsConstructor
@Getter
public class AuthDTO {

    @NonNull
    private String username;

    @NonNull
    private String password;
}
