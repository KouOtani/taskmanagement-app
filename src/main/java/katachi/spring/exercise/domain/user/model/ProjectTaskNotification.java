package katachi.spring.exercise.domain.user.model;

import java.util.Date;

import lombok.Data;

@Data
public class ProjectTaskNotification {

	private Integer id; // 一意のID
	private Integer taskId; // 関連するタスクのID
	private Integer assignerId; // タスクを振ったユーザーのID
	private Integer assigneeId; // タスクを振られたユーザーのID
	private Date notificationDate; // タスク追加日

	private Integer projectId; // 振られたプロジェクトのID
	private String projectName; // 振られたタスクがあるプロジェクト名
	private String taskTitle; // 振られたタスク名
	private String assignerName; //　タスクを自分に振ったユーザー

}
