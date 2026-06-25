//----------------------------------------------------------------------------------------------
// Name: Gavin Papenthien
// Project: Attendance App - This is a full stack attendance tracking and management software.
// Purpose: Create an entity that represents Sections from the database.
//----------------------------------------------------------------------------------------------

package attendanceapp.api.section;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;


//----------------------------------------------------------------------------------------------
// An entity that represents Sections from the database.
//----------------------------------------------------------------------------------------------
@Table(name = "sections", schema = "#{@environment.getProperty('attendanceapp.schema')}")
@RequiredArgsConstructor
@Getter
@Setter
public class Section {
    @Id
    @Column("id")
    private Integer id;

    @Column("room_num")
    @NonNull
    private String roomNum;

    @Column("num_students")
    @NonNull
    private int numStudents;

    @Column("course_id")
    @NonNull
    private int courseId;

    @Column("professor_id")
    @NonNull
    private int professorId;
}
