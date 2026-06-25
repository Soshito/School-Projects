//----------------------------------------------------------------------------------------------
// Name: Gavin Papenthien
// Project: Attendance App - This is a full stack attendance tracking and management software.
// Purpose: Create an entity to represent the version of Enrollments used for data transfer to the controller from clients.
//----------------------------------------------------------------------------------------------

package attendanceapp.api.enrollment;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;


//----------------------------------------------------------------------------------------------
// An entity to represent the version of Enrollments used for data transfer to the controller from clients.
//----------------------------------------------------------------------------------------------
@RequiredArgsConstructor
@Getter
public class EnrollmentDTO {
    @NonNull
    private int sectionId;

    @NonNull
    private int studentId;
}
