package katachi.spring.exercise.domain.user.model;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

import lombok.Data;

@Data
public class Project {

	public enum ProjectStatus {
		NOT_STARTED("未着手"), IN_PROGRESS("進行中"), COMPLETED("完了");

		private final String displayValue;

		ProjectStatus(String displayValue) {
			this.displayValue = displayValue;
		}

		public String getDisplayValue() {
			return displayValue;
		}
	}

	private Integer id; // ID
	private String title; // プロジェクトのタイトル
	private String description; // プロジェクトの詳細な説明
	private ProjectStatus status; // プロジェクトの状態（列挙型 Status）
	private Integer progress; // プロジェクトの進捗状況（0-100%）
	private Integer leaderId; //（外部キー、User テーブルへの参照） - プロジェクトのリーダーとなるユーザーID
	private LocalDate dueDate; // プロジェクトの期日
	private LocalDate registrationDate; // プロジェクトの登録日
	private String leaderName; // プロジェクトのリーダー名
	private List<Tag> tagsList; // タグエンティティのリスト
	private List<ProjectMember> projectMembersList; // プロジェクトメンバーのリスト
	private Integer totalTasks; //プロジェクト内のすべてのタスク
	private Integer completedTasks;// プロジェクト内のタスクの内、完了済みのタスク

	// 締め切り日までの残り日数を計算して返す
	public long getDaysUntilDueDate() {
		if (dueDate != null) {
			LocalDate today = LocalDate.now();
			if (dueDate.isAfter(today)) {
				return ChronoUnit.DAYS.between(today, dueDate);
			}
		}
		return 0; // 締め切り日が今日より過去の場合は 0 日
	}

	// 締め切り日と今日の日付の差を計算して経過日数を返す
	public long getDaysSinceDueDate() {
		if (dueDate != null && dueDate.isBefore(LocalDate.now())) {
			return ChronoUnit.DAYS.between(dueDate, LocalDate.now());
		}
		return 0; // 締め切り日が今日より未来なら 0 日経過
	}
}
