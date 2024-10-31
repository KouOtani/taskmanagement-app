package katachi.spring.exercise.domain.user.service.Impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import katachi.spring.exercise.domain.user.model.AssignedTo;
import katachi.spring.exercise.domain.user.model.MUser;
import katachi.spring.exercise.domain.user.model.Tag;
import katachi.spring.exercise.domain.user.model.Task;
import katachi.spring.exercise.domain.user.model.Task.TaskPriority;
import katachi.spring.exercise.domain.user.model.Task.TaskStatus;
import katachi.spring.exercise.domain.user.model.TaskTag;
import katachi.spring.exercise.domain.user.service.UserService;
import katachi.spring.exercise.repository.UserMapper;

@Service
public class UserServiceImpl implements UserService {

	@Autowired
	private UserMapper mapper;

	@Autowired
	private PasswordEncoder encoder;

	/**
	 * ユーザーを新規登録します。
	 *
	 * @param user 登録するユーザー情報
	 */
	@Override
	public void registerUser(MUser user) {

		//パスワード暗号化
		String rawPassword = user.getPassword();
		user.setPassword(encoder.encode(rawPassword));
		user.setIsAdmin(0);
		mapper.insertUser(user);
	}

	/*ログインユーザー情報取得*/
	@Override
	public MUser getUserByEmail(String userId) {
		return mapper.findLoginUser(userId);
	}

	/*メールアドレスの重複チェックする*/
	@Override
	public boolean isEmailRegistered(String email) {
		return mapper.findUserByEmail(email) != null;
	}

	/*個人タスクを登録*/
	@Override
	public void tasksEntry(Task task) {
		mapper.insertPersonalTasks(task);
	}

	/*登録するタグがすでに存在しているかどうかを確認する*/
	@Override
	public Tag isTagRegistered(String tag) {
		return mapper.findTagBytagName(tag);
	}

	/*タグ登録*/
	@Override
	public void saveTag(Tag tag) {
		mapper.insertTag(tag);
	}

	/*タスクタグ登録*/
	@Override
	public void saveTaskTag(TaskTag taskTag) {
		mapper.insertTaskTag(taskTag);
	}

	/*タスクをAssignedToテーブルに登録*/
	@Override
	public void saveAssignedTo(AssignedTo assignedTo) {
		mapper.insertAssignedTo(assignedTo);
	}

	/*個人タスクを複数件取得*/
	@Override
	public List<Task> getPersonalTasks(Integer userId,
			String searchQuery,
			Boolean completed,
			TaskStatus status,
			TaskPriority priority,
			String dueDateOrder,
			Integer tagId) {
		return mapper.findManyPersonalTasks(userId, searchQuery, completed, status, priority, dueDateOrder, tagId);
	}

	/*期限が過ぎた個人タスクを複数件取得*/
	@Override
	public List<Task> getPersonalPastDueTasks(Integer userId) {
		return mapper.findPersonalPastDueTasks(userId);
	}

	/*今日が期日の個人タスクを複数件取得*/
	public List<Task> getPersonalDueTodayTasks(Integer userId) {
		return mapper.findPersonalTodayDueTasks(userId);
	}

	/*期日まで一週間の個人タスクを複数件取得*/
	public List<Task> getPersonalDueOneWeekTasks(Integer userId) {
		return mapper.findPersonalDueOneWeekTasks(userId);
	}

	/*個人タスクを取得(1件)*/
	@Override
	public Task getPersonalTaskOne(Integer taskId) {
		return mapper.findPersonalTask(taskId);
	}

	/*タスク情報を更新するメソッド*/
	@Override
	public void updateTask(Task task) {
		mapper.updateTask(task);
	}

	/*タスクに関連付けられたタグを削除するメソッド*/
	@Override
	public void removeTaskTags(Integer taskId) {
		mapper.deleteByTaskId(taskId);
	}

	/*利用していないタグを削除する*/
	@Override
	public void removeUnusedTags() {
		mapper.deleteUnusedTags();
	}

	/*個人タスクを削除*/
	@Override
	public void deletePersonalTask(Integer taskId) {
		mapper.deletePersonalTask(taskId);
	}

	/*個人タスク完了*/
	@Override
	public void markTaskAsCompleted(Integer taskId) {
		mapper.completePresonalTask(taskId);
	}

	/*個人タスクを'進行中'に戻す*/
	@Override
	public void markAsInProgress(Integer taskId) {
		mapper.restoreInprogress(taskId);
	}

	/*ユーザーに基づくタグを一覧で取得する*/
	@Override
	public List<Tag> getTagsForPersonalTasks(Integer userId, Boolean completed) {
		return mapper.selectTagsForPersonalTasks(userId, completed);
	}

}
