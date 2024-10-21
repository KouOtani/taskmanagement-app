package katachi.spring.exercise.domain.user.model;

import java.util.Date;

import lombok.Data;

@Data
public class CommentReactionNotification {

	private Integer id; // 一意のID
	private Integer commentId; // リアクションされたコメントのID。
	private Integer reactorId; // リアクションを行ったユーザーのID。
	private Date notificationDate; // 通知日時

	private String reactorName; // リアクションしたユーザー名
	private String commentContent; // リアクションされたコメントの内容
	private Integer projectId; // リアクションしたプロジェクトID
	private String projectName; //　リアクションしたプロジェクト名

}
