package katachi.spring.exercise.form;

import org.hibernate.validator.constraints.Length;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class SignupForm {

	@NotBlank(groups = ValidGroup1.class)
	@Email(groups = ValidGroup2.class)
	private String email;

	@NotBlank(groups = ValidGroup1.class)
	@Pattern(regexp = "^[a-zA-Z0-9]+$", groups = ValidGroup2.class)
	@Length(min = 8, groups = ValidGroup2.class)
	private String password;

	@NotBlank(groups = ValidGroup1.class)
	@Pattern(regexp = "^[a-zA-Z0-9]+$", groups = ValidGroup2.class)
	@Length(min = 8, groups = ValidGroup2.class)
	private String rePassword;

	@NotBlank(groups = ValidGroup1.class)
	private String familyName;

	@NotBlank(groups = ValidGroup1.class)
	private String firstName;
}