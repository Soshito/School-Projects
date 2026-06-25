//----------------------------------------------------------------------------------------------
// Name: Gavin Papenthien
// Project: Attendance App - This is a full stack attendance tracking and management software.
// Purpose: Create an entity to represent the version of Attendance Logs used for data transfer to the controller from clients..
//----------------------------------------------------------------------------------------------

package attendanceapp.api.attendancelog;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

//----------------------------------------------------------------------------------------------
// An entity to represent the version of Attendance Logs used for data transfer to the controller from clients.
//----------------------------------------------------------------------------------------------
@RequiredArgsConstructor
@Getter
public class AttendanceLogDTO {

    @NonNull
    private String studentCardId;

    @NonNull
    private String roomNum;
}
