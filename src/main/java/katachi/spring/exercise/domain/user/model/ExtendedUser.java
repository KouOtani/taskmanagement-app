package katachi.spring.exercise.domain.user.model;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

public class ExtendedUser extends User {

	private Integer userId;
	private String email;
	private String fullName;

	public ExtendedUser(Integer userId, String email, String password, Collection<? extends GrantedAuthority> authorities, String familyName, String firstName) {
		super(email, password, authorities);

		this.userId = userId;
		this.email = email;
		this.fullName = familyName + firstName;
	}

	public Integer getUserId() {
		return userId;
	}

	public String getEmail() {
		return email;
	}

	public String getFullName() {
		return fullName;
	}

}
