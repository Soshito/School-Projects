//----------------------------------------------------------------------------------------------
// Name: Gavin Papenthien
// Project: Attendance App - This is a full stack attendance tracking and management software.
// Purpose: Create an entity to represent the version of Courses used for data transfer to the controller from clients.
//----------------------------------------------------------------------------------------------

package attendanceapp.api.course;

//----------------------------------------------------------------------------------------------
// An entity to represent the version of Courses used for data transfer to the controller from clients.
//----------------------------------------------------------------------------------------------

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;

@AllArgsConstructor
@Getter
public class CourseDTO {

    @NonNull
    private String name;

    // Jackson has a bug where it cannot serialize entities with single args so this is included even though we're not using it yet
    // When implemented, should be @NonNull and swap POJO to @RequiredArgsConstructor
    private Integer professorId;
}
