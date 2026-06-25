//----------------------------------------------------------------------------------------------
// Name: Gavin Papenthien
// Project: Attendance App - This is a full stack attendance tracking and management software.
// Purpose: Provide an interface that allows Sections to be queried to and from the database.
//----------------------------------------------------------------------------------------------

package attendanceapp.api.section;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

//----------------------------------------------------------------------------------------------
// An interface that allows sections to be queried to and from the database.
//----------------------------------------------------------------------------------------------
public interface SectionRepository extends CrudRepository<Section, Integer>, PagingAndSortingRepository<Section, Integer> {

    /**
     * Custom query to search for all Sections with a specified courseId
     *
     * @param courseId ID of the associated Course
     * @param pageable Pageable object containing  page number size and Sorting rule
     * @return Page containing found Sections
     */
    Page<Section> findAllByCourseId(int courseId, Pageable pageable);
}
