package katachi.spring.exercise.domain.user.model;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

import lombok.Data;

@Data
public class Task {

	public enum TaskStatus {
		NOT_STARTED("未着手"), IN_PROGRESS("進行中"), COMPLETED("完了");

		private final String displayValue;

		TaskStatus(String displayValue) {
			this.displayValue = displayValue;
		}

		public String getDisplayValue() {
			return displayValue;
		}
	}

	public enum TaskPriority {
		LOW("低"), MEDIUM("中"), HIGH("高");

		private final String displayValue;

		TaskPriority(String displayValue) {
			this.displayValue = displayValue;
		}

		public String getDisplayValue() {
			return displayValue;
		}
	}

	private Integer id; // ID
	private Integer projectId; // プロジェクトのID
	private String title; // タスクのタイトル
	private String description; // タスクの詳細な説明
	private TaskStatus status; // タスクの状態（列挙型 Status）
	private TaskPriority priority; // タスクの優先度（列挙型 Priority）
	private LocalDate dueDate; // タスクの期日
	private LocalDate registrationDate; //タスクの登録日
	private List<Tag> tagsList; // タグエンティティのリスト
	private Project project; //プロジェクトのエンティティ
	private MUser user; //ユーザーのエンティティ

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
