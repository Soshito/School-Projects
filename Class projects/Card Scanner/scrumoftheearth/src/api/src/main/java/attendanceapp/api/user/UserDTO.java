//----------------------------------------------------------------------------------------------
// Name: Gavin Papenthien
// Project: Attendance App - This is a full stack attendance tracking and management software.
// Purpose: Create an entity to represent the version of Users used for data transfer to the controller from clients.
//----------------------------------------------------------------------------------------------

package attendanceapp.api.user;

//----------------------------------------------------------------------------------------------
// An entity to represent the version of Users used for data transfer to the controller from clients.
//----------------------------------------------------------------------------------------------

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;

@AllArgsConstructor
@Getter
public class UserDTO {

    @NonNull
    private String firstName;

    @NonNull
    private String lastName;

    private String studentCardId;

    @NonNull
    private String username;

    @NonNull
    private String password;

    @NonNull
    private int roleId;
}
