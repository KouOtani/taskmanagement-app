package katachi.spring.exercise.domain.user.dto;

import java.util.Date;

public class NotificationDTO {

	private String type; // "INVITATION" または "COMMENT" または "REACTION"
	private Object object; // 具体的な通知情報を保持する
	private Date createdAt; // 作成日時

	public NotificationDTO(String type, Object object, Date createdAt) {
		this.type = type;
		this.object = object;
		this.createdAt = createdAt;
	}

	public String getType() {
		return type;
	}

	public Object getObject() {
		return object;
	}

	public Date getCreatedAt() {
		return createdAt;
	}
}
