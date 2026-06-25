//----------------------------------------------------------------------------------------------
// Name: Gavin Papenthien
// Project: Attendance App - This is a full stack attendance tracking and management software.
// Purpose: Provide an interface that allows Roles to be queried to and from the database.
//----------------------------------------------------------------------------------------------

package attendanceapp.api.role;

import org.springframework.data.repository.CrudRepository;

//----------------------------------------------------------------------------------------------
// An interface that allows Roles to be queried to and from the database.
//----------------------------------------------------------------------------------------------
public interface RoleRepository extends CrudRepository<Role, Integer> { }
