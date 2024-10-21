package katachi.spring.exercise.repository;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import katachi.spring.exercise.domain.user.model.AssignedTo;
import katachi.spring.exercise.domain.user.model.MUser;
import katachi.spring.exercise.domain.user.model.Project;
import katachi.spring.exercise.domain.user.model.Tag;
import katachi.spring.exercise.domain.user.model.Task;
import katachi.spring.exercise.domain.user.model.Task.TaskPriority;
import katachi.spring.exercise.domain.user.model.Task.TaskStatus;
import katachi.spring.exercise.domain.user.model.TaskTag;

@Mapper
public interface UserMapper {

	/*ユーザー登録*/
	public int insertUser(MUser user);

	/*ログインユーザー取得*/
	public MUser findLoginUser(String email);

	/*ユーザー登録時のメールアドレス重複チェック*/
	public String findUserByEmail(String email);

	/*個人タスクを登録*/
	public int insertPersonalTasks(Task task);

	/*タグ登録時の重複チェック*/
	public Tag findTagBytagName(String tag);

	/*タグ登録*/
	public int insertTag(Tag tag);

	/*タグ登録*/
	public int insertTaskTag(TaskTag taskTag);

	/*タスクをAssignedToテーブルに登録*/
	public int insertAssignedTo(AssignedTo assignedTo);

	/*個人タスクを複数件取得*/
	public List<Task> findManyPersonalTasks(@Param("userId") Integer userId,
			@Param("searchQuery") String searchQuery,
			@Param("completed") Boolean completed,
			@Param("status") TaskStatus status,
			@Param("priority") TaskPriority priority,
			@Param("dueDateOrder") String dueDateOrder,
			@Param("tagName") String tagName);

	/*期限が過ぎた個人タスクを複数件取得*/
	public List<Task> findPersonalPastDueTasks(Integer userId);

	/*今日が期日の個人タスクを複数件取得*/
	public List<Task> findPersonalTodayDueTasks(Integer userId);

	/*期日まで一週間の個人タスクを複数件取得*/
	public List<Task> findPersonalDueOneWeekTasks(Integer userId);

	/*個人タスクを１件取得*/
	public Task findPersonalTask(Integer taskId);

	/*タスク情報を更新するメソッド*/
	public void updateTask(Task task);

	/*タスクに関連付けられたタグを削除するメソッド*/
	public int deleteByTaskId(@Param("taskId") Integer taskId);

	/*利用していないタグを削除する*/
	public int deleteUnusedTags();

	/*個人タスクを削除*/
	public int deletePersonalTask(@Param("taskId") Integer taskId);

	/*個人タスクを完了*/
	public void completePresonalTask(@Param("taskId") Integer taskId);

	/*個人タスクを'進行中'に戻す*/
	public void restoreInprogress(@Param("taskId") Integer taskId);

	/*プロジェクトを登録*/
	public int insertProject(Project project);

}
