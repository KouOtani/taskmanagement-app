package katachi.spring.exercise.domain.user.model;

import java.util.Date;

import lombok.Data;

@Data
public class CommentReaction {

	private Integer id;
	private Integer commentId;
	private Integer userId;
	private Date reactedAt;

	private String familyName;
	private String firstName;

	// フルネームを結合して返す
	public String getFullName() {
		return familyName + " " + firstName;
	}
}
