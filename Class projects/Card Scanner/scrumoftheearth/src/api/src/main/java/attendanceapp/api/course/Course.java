//----------------------------------------------------------------------------------------------
// Name: Gavin Papenthien
// Project: Attendance App - This is a full stack attendance tracking and management software.
// Purpose: Create an entity to represent Courses from the database.
//----------------------------------------------------------------------------------------------

package attendanceapp.api.course;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

//----------------------------------------------------------------------------------------------
// An entity that represents Courses from the database.
//----------------------------------------------------------------------------------------------
@Table(name = "courses", schema = "#{@environment.getProperty('attendanceapp.schema')}")
@RequiredArgsConstructor
@Getter
@Setter
public class Course {
        @Id
        @Column("id")
        private Integer id;

        @Column("name")
        @NonNull
        private String name;

        @Column("section_count")
        @NonNull
        private Integer sectionCount;
}
