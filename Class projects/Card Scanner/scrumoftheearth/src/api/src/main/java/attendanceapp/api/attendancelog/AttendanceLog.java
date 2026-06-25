//----------------------------------------------------------------------------------------------
// Name: Gavin Papenthien
// Project: Attendance App - This is a full stack attendance tracking and management software.
// Purpose: Create an entity to represent Attendance Logs from the database.
//----------------------------------------------------------------------------------------------

package attendanceapp.api.attendancelog;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.sql.Timestamp;

//----------------------------------------------------------------------------------------------
// An entity to represent Attendance Logs from the database.
//----------------------------------------------------------------------------------------------

@Table(name = "attendance_logs", schema = "#{@environment.getProperty('attendanceapp.schema')}")
@Getter
@Setter
@RequiredArgsConstructor
public class AttendanceLog {
        @Id
        @Column("id")
        Integer id;

        @Column("student_id")
        @NonNull
        private int studentId;


        @Column("section_id")
        @NonNull
        private int sectionId;

        @Column("date_time")
        @NonNull
        private Timestamp dateTime;

        @Column("excused")
        private Boolean excused;

        @Column("is_absent")
        @NonNull
        private boolean absent;
}