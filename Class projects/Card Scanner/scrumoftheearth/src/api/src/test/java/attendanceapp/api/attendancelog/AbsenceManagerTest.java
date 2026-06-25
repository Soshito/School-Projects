//----------------------------------------------------------------------------------------------
// Name: Gavin Papenthien
// Project: Attendance App - This is a full stack attendance tracking and management software.
// Purpose: Test proper functionality of the AbsenceManager
//----------------------------------------------------------------------------------------------

package attendanceapp.api.attendancelog;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.sql.Timestamp;
import java.time.Instant;

import static attendanceapp.api.utils.HeadersGenerator.getAdminHeaders;
import static org.assertj.core.api.Assertions.assertThat;

//----------------------------------------------------------------------------------------------
// A testing class to test for proper functionality of the AbsenceManager
//----------------------------------------------------------------------------------------------
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class AbsenceManagerTest {
    @Autowired
    AbsenceManager absenceManager;

    @Autowired
    TestRestTemplate restTemplate;

    @Autowired
    AttendanceLogRepository attendanceLogRepository;

    /**
     * Ensure that an AttendanceLog with absent = true is created
     */
    @Test
    @DirtiesContext
    void shouldCreateAbsentLog() {
        HttpHeaders headers = getAdminHeaders(restTemplate);
        absenceManager.detectAbsences();

        ResponseEntity<AttendanceLog> getResponse = restTemplate.exchange("/attendancelogs/1", HttpMethod.GET, new HttpEntity<>(headers), AttendanceLog.class);
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(getResponse.getBody().isAbsent()).isTrue();
    }

    /**
     * Ensure that an AttendanceLog is not created
     */
    @Test
    @DirtiesContext
    void shouldNotCreateAbsentLog() {
        HttpHeaders headers = getAdminHeaders(restTemplate);
        AttendanceLog log = new AttendanceLog(1, 1, Timestamp.from(Instant.now()), false);
        attendanceLogRepository.save(log);
        absenceManager.detectAbsences(); // Shouldn't create a new one since we made one manually

        // AttendanceLogs are empty prior to running this test
        // ID = 1 will be the log we created manually
        // ID = 2 should be empty because the AbsenceManager won't create an absent log when one already exists for today
        ResponseEntity<AttendanceLog> getResponse = restTemplate.exchange("/attendancelogs/2", HttpMethod.GET, new HttpEntity<>(headers), AttendanceLog.class);
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }
}
