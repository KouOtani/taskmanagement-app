package katachi.spring.exercise.domain.user.model;

import java.util.Date;

import lombok.Data;

@Data
public class Invitation {

	public enum InvitationStatus {
		PENDING("保留"), ACCEPTED("了承"), REJECTED("却下");

		private final String displayValue;

		InvitationStatus(String displayValue) {
			this.displayValue = displayValue;
		}

		public String getDisplayValue() {
			return displayValue;
		}
	}

	private Integer id; // 主キー
	private Integer inviterId; //招待者のユーザーID
	private Integer inviteeId; //招待されたユーザーのID
	private Integer projectId; //プロジェクトID
	private InvitationStatus status; // 招待のステータス

	private String projectTitle; // プロジェクト名
	private String projectDescription; // プロジェクトの説明
	private String inviterName; // 招待者名
	private Date invitationDate; //招待日
}
