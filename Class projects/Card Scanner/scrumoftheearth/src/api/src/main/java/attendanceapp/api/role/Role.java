//----------------------------------------------------------------------------------------------
// Name: Gavin Papenthien
// Project: Attendance App - This is a full stack attendance tracking and management software.
// Purpose: Create an entity that represents Roles from the database.
//----------------------------------------------------------------------------------------------

package attendanceapp.api.role;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

//----------------------------------------------------------------------------------------------
// An entity that represents roles from the database.
//----------------------------------------------------------------------------------------------
@Table(name = "roles", schema = "#{@environment.getProperty('attendanceapp.schema')}")
@AllArgsConstructor
@Getter
public class Role {
   @Id
   @Column("id")
   private Integer id;

   @Column("name")
   @NonNull
   private String name;
}
