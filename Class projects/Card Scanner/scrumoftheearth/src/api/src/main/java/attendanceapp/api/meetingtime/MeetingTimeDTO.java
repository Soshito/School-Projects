//----------------------------------------------------------------------------------------------
// Name: Gavin Papenthien
// Project: Attendance App - This is a full stack attendance tracking and management software.
// Purpose: Create an entity to represent the version of MeetingsTimes used for data transfer to the controller from clients.
//----------------------------------------------------------------------------------------------

package attendanceapp.api.meetingtime;

//----------------------------------------------------------------------------------------------
// An entity to represent the version of MeetingTimes used for data transfer to the controller from clients.
//----------------------------------------------------------------------------------------------

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class MeetingTimeDTO {

    @NonNull
    private int sectionId;

    @NonNull
    private int dayOfWeek;

    // Times in hh:mm format
    @NonNull
    private String startTime;

    @NonNull
    private String endTime;
}
