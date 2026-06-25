package attendanceapp.api.user;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class UserResponse {
    private int id;
    private String firstName;
    private String lastName;
    private String studentCardId;
    private String username;
    private int roleId;
}
