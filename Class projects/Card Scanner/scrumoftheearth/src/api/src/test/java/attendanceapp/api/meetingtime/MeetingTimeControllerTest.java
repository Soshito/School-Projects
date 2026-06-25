//----------------------------------------------------------------------------------------------
// Name: Gavin Papenthien
// Project: Attendance App - This is a full stack attendance tracking and management software.
// Purpose: Test proper functionality and outputs of endpoints from MeetingTimeController
//----------------------------------------------------------------------------------------------

package attendanceapp.api.meetingtime;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;

import java.net.URI;

import static attendanceapp.api.utils.HeadersGenerator.getAdminHeaders;
import static attendanceapp.api.utils.HeadersGenerator.getStudentHeaders;
import static org.assertj.core.api.Assertions.assertThat;

//----------------------------------------------------------------------------------------------
// A testing class to test for proper functionality and outputs of endpoints from MeetingTimeController
//----------------------------------------------------------------------------------------------
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class  MeetingTimeControllerTest {

    @Autowired
    TestRestTemplate restTemplate;

    /**
     * Ensure that a MeetingTime is created when all data and configuration is valid
     */
    @Test
    void shouldCreateANewMeetingTime() {
        HttpHeaders adminHeader = getAdminHeaders(restTemplate);

        int sectionId = 1;
        int dayOfWeek = 1;
        String startTime = "13:00";
        String endTime = "13:50";

        MeetingTimeDTO newMeetingTime = new MeetingTimeDTO(sectionId, dayOfWeek, startTime, endTime);
        HttpEntity<MeetingTimeDTO> request = new HttpEntity<>(newMeetingTime, adminHeader);
        ResponseEntity<MeetingTime> createResponse = restTemplate.postForEntity("/meetingtimes", request, MeetingTime.class);

        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        // Verify location header points to created object
        URI locationOfNewCourse = createResponse.getHeaders().getLocation();
        HttpEntity<Void> getRequest = new HttpEntity<>(adminHeader);
        ResponseEntity<String> getResponse = restTemplate.exchange(locationOfNewCourse, HttpMethod.GET, getRequest, String.class);
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    /**
     * Ensure that a MeetingTime is not created if the Section ID is invalid
     */
    @Test
    void shouldNotCreateANewMeetingTimeIfSectionIdInvalid() {
        HttpHeaders adminHeader = getAdminHeaders(restTemplate);

        int sectionId = 99999;
        int dayOfWeek = 1;
        String startTime = "13:00";
        String endTime = "13:50";

        MeetingTimeDTO newMeetingTime = new MeetingTimeDTO(sectionId, dayOfWeek, startTime, endTime);
        HttpEntity<MeetingTimeDTO> request = new HttpEntity<>(newMeetingTime, adminHeader);
        ResponseEntity<MeetingTime> createResponse = restTemplate.postForEntity("/meetingtimes", request, MeetingTime.class);

        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void shouldNotCreateANewMeetingTimeIfDayOfWeekIsInvalid() {
        HttpHeaders adminHeader = getAdminHeaders(restTemplate);

        int sectionId = 1;
        int dayOfWeek = 8; // Doesn't correspond to a weekday
        String startTime = "13:00";
        String endTime = "13:50";

        MeetingTimeDTO newMeetingTime = new MeetingTimeDTO(sectionId, dayOfWeek, startTime, endTime);
        HttpEntity<MeetingTimeDTO> request = new HttpEntity<>(newMeetingTime, adminHeader);
        ResponseEntity<MeetingTime> createResponse = restTemplate.postForEntity("/meetingtimes", request, MeetingTime.class);

        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void shouldNotCreateANewMeetingTimeIfStartTimeIsInvalid() {
        HttpHeaders adminHeader = getAdminHeaders(restTemplate);

        int sectionId = 1;
        int dayOfWeek = 1;
        String startTime = "hi im not a time";
        String endTime = "13:50";

        MeetingTimeDTO newMeetingTime = new MeetingTimeDTO(sectionId, dayOfWeek, startTime, endTime);
        HttpEntity<MeetingTimeDTO> request = new HttpEntity<>(newMeetingTime, adminHeader);
        ResponseEntity<MeetingTime> createResponse = restTemplate.postForEntity("/meetingtimes", request, MeetingTime.class);

        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void shouldNotCreateANewMeetingTimeIfEndTimeIsInvalid() {
        HttpHeaders adminHeader = getAdminHeaders(restTemplate);

        int sectionId = 1;
        int dayOfWeek = 1;
        String startTime = "13:00";
        String endTime = "asdf";

        MeetingTimeDTO newMeetingTime = new MeetingTimeDTO(sectionId, dayOfWeek, startTime, endTime);
        HttpEntity<MeetingTimeDTO> request = new HttpEntity<>(newMeetingTime, adminHeader);
        ResponseEntity<MeetingTime> createResponse = restTemplate.postForEntity("/meetingtimes", request, MeetingTime.class);

        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    /**
     * Ensure that a MeetingTime is not created if the request is coming from a non-admin User
     */
    @Test
    void shouldNotCreateANewMeetingTimeIfNotRequestByAdmin() {
        HttpHeaders adminHeader = getStudentHeaders(restTemplate);

        int sectionId = 1;
        int dayOfWeek = 1;
        String startTime = "13:00";
        String endTime = "13:50";

        MeetingTimeDTO newMeetingTime = new MeetingTimeDTO(sectionId, dayOfWeek, startTime, endTime);
        HttpEntity<MeetingTimeDTO> request = new HttpEntity<>(newMeetingTime, adminHeader);
        ResponseEntity<MeetingTime> createResponse = restTemplate.postForEntity("/meetingtimes", request, MeetingTime.class);

        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    /**
     * Ensure that a MeetingTime is not created if the request is coming from an unauthenticated User
     */
    @Test
    void shouldNotCreateANewMeetingTimeIfNotAuthenticated() {
        int sectionId = 1;
        int dayOfWeek = 1;
        String startTime = "13:00";
        String endTime = "13:50";

        MeetingTimeDTO newMeetingTime = new MeetingTimeDTO(sectionId, dayOfWeek, startTime, endTime);
        HttpEntity<MeetingTimeDTO> request = new HttpEntity<>(newMeetingTime);
        ResponseEntity<MeetingTime> createResponse = restTemplate.postForEntity("/meetingtimes", request, MeetingTime.class);

        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    /**
     * Ensure that a MeetingTime is not returned if the ID does not correspond to any existing MeetingTimes
     */
    @Test
    void shouldNotReturnAMeetingTimeIfIdInvalid() {
        HttpHeaders headers = getAdminHeaders(restTemplate);
        int id = 999999999;

        HttpEntity<Void> getRequest = new HttpEntity<>(headers);
        ResponseEntity<String> getResponse = restTemplate.exchange("/meetingtimes/" + id, HttpMethod.GET, getRequest, String.class);
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }
}
