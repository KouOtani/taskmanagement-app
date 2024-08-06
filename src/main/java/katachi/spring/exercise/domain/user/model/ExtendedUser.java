package katachi.spring.exercise.domain.user.model;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

public class ExtendedUser extends User {

    private String fullName;

    public ExtendedUser(String username, String password, Collection<? extends GrantedAuthority> authorities, String familyName, String firstName) {
        super(username, password, authorities);

        this.fullName = familyName + firstName;
    }

    public String getFullName() {
        return fullName;
    }

}
