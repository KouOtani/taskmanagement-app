package katachi.spring.exercise.domain.user.model;

import java.util.Date;

import lombok.Data;

@Data
public class CommentNotification {

	private Integer id; // 通知ID
	private Integer commentId; // コメントID
	private Integer recipientUserId; // 通知を受け取るユーザーID
	private Date notificationDate; // 通知日時

	private String commentContent; // コメント内容
	private Integer projectId; // プロジェクトID
	private String projectTitle; // プロジェクト名
	private String commenterName; // コメント投稿者の名前

}
