package katachi.spring.exercise.domain.user.model;

import lombok.Data;

@Data
public class ProjectTag {

	private Integer id; // 主キー
	private Integer projectId; // プロジェクトのID。Project テーブルへの参照
	private Integer tagId; // タグのID。Tag テーブルへの参照
}
