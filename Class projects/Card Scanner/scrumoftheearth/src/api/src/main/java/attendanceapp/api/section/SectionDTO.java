//----------------------------------------------------------------------------------------------
// Name: Gavin Papenthien
// Project: Attendance App - This is a full stack attendance tracking and management software.
// Purpose: Create an entity to represent the version of Sections used for data transfer to the controller from clients.
//----------------------------------------------------------------------------------------------

package attendanceapp.api.section;

//----------------------------------------------------------------------------------------------
// An entity to represent the version of Sections used for data transfer to the controller from clients.
//----------------------------------------------------------------------------------------------

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class SectionDTO {

    @NonNull
    private String roomNum;

    @NonNull
    private int numberOfStudent;

    @NonNull
    private int courseId;

    @NonNull
    private int professorId;
}
