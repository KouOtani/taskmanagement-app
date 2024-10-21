package katachi.spring.exercise.domain.user.model;

import lombok.Data;

@Data
public class AssignedTo {

	private Integer id; // 主キー
	private Integer taskId; //タスクのID。Task テーブルへの参照
	private Integer userId; //ユーザーのID。User テーブルへの参照
}
