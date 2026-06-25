//----------------------------------------------------------------------------------------------
// Name: Gavin Papenthien
// Project: Attendance App - This is a full stack attendance tracking and management software.
// Purpose: Provide an interface that allows Users to be queried to and from the database.
//----------------------------------------------------------------------------------------------

package attendanceapp.api.user;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Optional;


//----------------------------------------------------------------------------------------------
// An interface that allows Users to be queried to and from the database.
//----------------------------------------------------------------------------------------------
public interface UserRepository extends CrudRepository<User, Integer>, PagingAndSortingRepository<User, Integer> {

    /**
     * Custom query to search for students by their card ID in the database.
     *
     * @param studentCardId int card id of the student being requested.
     * @return User student if they exist.
     */
    Optional<User> findByStudentCardId(String studentCardId);

    /**
     * Custom query to search for Users by their username in the database
     *
     * @param username String username of requested User
     * @return User if they exist
     */
    Optional<User> findByUsername(String username);

    /**
     * Custom query to search for all Users with a specified Role
     *
     * @param roleId ID associated with the desired Role
     * @param pageable Pageable object containing page number, size and Sorting rule with default ids asc
     * @return Page containing found Users
     */
    Page<User> findAllByRoleId(int roleId, Pageable pageable);
}