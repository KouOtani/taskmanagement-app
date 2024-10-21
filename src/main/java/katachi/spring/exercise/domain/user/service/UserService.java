package katachi.spring.exercise.domain.user.service;

import java.util.List;

import katachi.spring.exercise.domain.user.model.AssignedTo;
import katachi.spring.exercise.domain.user.model.MUser;
import katachi.spring.exercise.domain.user.model.Project;
import katachi.spring.exercise.domain.user.model.Tag;
import katachi.spring.exercise.domain.user.model.Task;
import katachi.spring.exercise.domain.user.model.Task.TaskPriority;
import katachi.spring.exercise.domain.user.model.Task.TaskStatus;
import katachi.spring.exercise.domain.user.model.TaskTag;

public interface UserService {

	/*ユーザー登録*/
	public void registerUser(MUser user);

	/*ログインユーザー情報取得*/
	public MUser getUserByEmail(String email);

	/*メールアドレスの重複をチェックする*/
	public boolean isEmailRegistered(String email);

	/*個人タスクを登録*/
	public void tasksEntry(Task task);

	/*登録するタグがすでに存在しているかどうかを確認する*/
	public Tag isTagRegistered(String tag);

	/*タグ登録*/
	public void saveTag(Tag tag);

	/*タスクタグ登録*/
	public void saveTaskTag(TaskTag taskTag);

	/*タスクをAssignedToテーブルに登録*/
	public void saveAssignedTo(AssignedTo assignedTo);

	/*期限が過ぎた個人タスクを複数件取得*/
	public List<Task> getPersonalPastDueTasks(Integer userId);

	/*今日が期日の個人タスクを複数件取得*/
	public List<Task> getPersonalDueTodayTasks(Integer userId);

	/*期日まで一週間の個人タスクを複数件取得*/
	public List<Task> getPersonalDueOneWeekTasks(Integer userId);

	/*個人タスクを複数件取得*/
	public List<Task> getPersonalTasks(Integer userId,
			String searchQuery,
			Boolean completed,
			TaskStatus status,
			TaskPriority priority,
			String dueDateOrder,
			String tagName);

	/*個人タスクを取得(1件)*/
	public Task getPersonalTaskOne(Integer taskId);

	/*タスク情報を更新するメソッド*/
	public void updateTask(Task task);

	/*タスクに関連付けられたタグを削除するメソッド*/
	public void removeTaskTags(Integer taskId);

	/*利用していないタグを削除する*/
	public void removeUnusedTags();

	/*個人タスクを削除*/
	public void deletePersonalTask(Integer taskId);

	/*個人タスク完了*/
	public void markTaskAsCompleted(Integer taskId);

	/*個人タスクを'進行中'に戻す*/
	public void markAsInProgress(Integer taskId);

	/*プロジェクトを登録*/
	public void projectEntry(Project project);

}
