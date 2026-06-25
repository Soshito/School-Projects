//----------------------------------------------------------------------------------------------
// Name: Gavin Papenthien
// Project: Attendance App - This is a full stack attendance tracking and management software.
// Purpose: Create an entity to represent MeetingTimes from the database.
//----------------------------------------------------------------------------------------------

package attendanceapp.api.meetingtime;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.sql.Time;

//----------------------------------------------------------------------------------------------
// An entity that represents MeetingTimes from the database.
//----------------------------------------------------------------------------------------------
@Table(name = "meeting_times", schema = "#{@environment.getProperty('attendanceapp.schema')}")
@RequiredArgsConstructor
@Getter
@Setter
public class MeetingTime {

        @Id
        @Column("id")
        private Integer id;

        @Column("section_id")
        @NonNull
        private int sectionId;

        @Column("day_of_week")
        @NonNull
        private int dayOfWeek;

        @Column("start_time")
        @NonNull
        private Time startTime;

        @Column("end_time")
        @NonNull
        private Time endTime;
}
