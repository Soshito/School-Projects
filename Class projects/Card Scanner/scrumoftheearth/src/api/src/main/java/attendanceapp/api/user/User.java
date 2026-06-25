//----------------------------------------------------------------------------------------------
// Name: Gavin Papenthien
// Project: Attendance App - This is a full stack attendance tracking and management software.
// Purpose: Create an entity to represent Users from the database.
//----------------------------------------------------------------------------------------------

package attendanceapp.api.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

//----------------------------------------------------------------------------------------------
// An entity that represents Users from the database.
//----------------------------------------------------------------------------------------------
@Table(name = "users", schema = "#{@environment.getProperty('attendanceapp.schema')}")
@AllArgsConstructor
@Getter
public class User implements UserDetails {
    @Id
    @Column("id")
    private Integer id;

    @Column("first_name")
    @NonNull
    private String firstName;

    @Column("last_name")
    @NonNull
    private String lastName;

    // Nullable, only students have these
    @Column("student_card_id")
    private String studentCardId;

    @Column("username")
    @NonNull
    private String username;

    @Column("password")
    @NonNull
    private String password;

    @Column("role_id")
    @NonNull
    private int roleId;

//----------------------------------------------------------------------------------------------
// UserDetails interface provided by Spring Security to build upon Users for various Auth features
// Javadoc comment only supplied for methods I actually implement
//----------------------------------------------------------------------------------------------
    /**
     * Get a User's Authorities
     *
     * @return List of User Roles
     */
    @Override
    @JsonIgnore // Prevent Json from attempting to deserialize during tests - will always crash tests otherwise
    public Collection<? extends GrantedAuthority> getAuthorities() {
        String strRoleId = String.valueOf(roleId);
        return List.of(new SimpleGrantedAuthority(strRoleId));
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    // ------------------------------------------------------------
    // These are all necessary methods for the interface extension
    // However their functionality will not be needed for this application MVP
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
    // -------------------------------------------------------------
}