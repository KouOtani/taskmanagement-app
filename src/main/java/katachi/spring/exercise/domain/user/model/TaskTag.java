package katachi.spring.exercise.domain.user.model;

import lombok.Data;

@Data
public class TaskTag {

	private Integer id; // 主キー
	private Integer taskId; // タスクのID。Task テーブルへの参照
	private Integer tagId; // タグのID。Tag テーブルへの参照
}
