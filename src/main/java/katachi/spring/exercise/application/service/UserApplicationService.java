package katachi.spring.exercise.application.service;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import katachi.spring.exercise.domain.user.model.AssignedTo;
import katachi.spring.exercise.domain.user.model.ExtendedUser;
import katachi.spring.exercise.domain.user.model.Project;
import katachi.spring.exercise.domain.user.model.ProjectTag;
import katachi.spring.exercise.domain.user.model.Tag;
import katachi.spring.exercise.domain.user.model.Task;
import katachi.spring.exercise.domain.user.model.TaskTag;
import katachi.spring.exercise.domain.user.service.ProjectService;
import katachi.spring.exercise.domain.user.service.UserService;

/**
 * ユーザーアプリケーションのサービスを提供するクラスです。
 */
@Service
public class UserApplicationService {

	@Autowired
	private UserService userService;

	@Autowired
	private ProjectService projectService;

	@Autowired
	private ModelMapper modelMapper;

	/**
	 * 現在の認証されたユーザーの詳細を取得します。
	 *
	 * このメソッドは、Spring SecurityのSecurityContextから現在の認証情報を取得し、
	 * 認証されたユーザーの詳細（ExtendedUserオブジェクト）を返します。
	 *
	 * @return 現在認証されているユーザーの詳細（ExtendedUserオブジェクト）
	 * @throws ClassCastException 認証されたユーザーがExtendedUser型にキャストできない場合
	 */
	public ExtendedUser getCurrentUserDetails() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		return (ExtendedUser) authentication.getPrincipal();
	}

	/**
	* タグの処理を行うメソッド
	*
	* @param tagNames カンマ区切りのタグ名
	* @param task 対象タスク
	*/
	public void processTags(String tagNames, Object entity) {
		if (tagNames == null || tagNames.trim().isEmpty()) {
			return;
		}

		String[] tagArray = tagNames.split(",\\s*|、\\s*|\\s+|　");

		for (String tagName : tagArray) {
			Tag tag = modelMapper.map(tagName.trim(), Tag.class);
			tag.setName(tagName.trim());

			// タグが既に存在するかチェック
			Tag existingTag = userService.isTagRegistered(tagName.trim());
			if (existingTag == null) {
				// タグが存在しない場合、新規登録
				userService.saveTag(tag);
				attachEntityToTag(entity, tag);
			} else {
				// 既存のタグIDで関連付け
				attachEntityToTag(entity, existingTag);
			}
		}
	}

	private void attachEntityToTag(Object entity, Tag tag) {
		if (entity instanceof Task) {
			attachTaskToTag((Task) entity, tag);
		} else if (entity instanceof Project) {
			attachProjectToTag((Project) entity, tag);
		}
	}

	/**
	* タスクとタグの関連付けを行うヘルパーメソッド
	*
	* @param task 対象タスク
	* @param tag 関連付けるタグ
	*/
	private void attachTaskToTag(Task task, Tag tag) {
		TaskTag taskTag = new TaskTag();
		taskTag.setTaskId(task.getId()); // タスクIDを設定
		taskTag.setTagId(tag.getId()); // タグIDを設定
		userService.saveTaskTag(taskTag);
	}

	/**
	* プロジェクトとタグの関連付けを行うヘルパーメソッド
	*
	* @param task 対象タスク
	* @param tag 関連付けるタグ
	*/
	private void attachProjectToTag(Project project, Tag tag) {
		ProjectTag projectTag = new ProjectTag();
		projectTag.setProjectId(project.getId()); // タスクIDを設定
		projectTag.setTagId(tag.getId()); // タグIDを設定
		projectService.saveProjectTag(projectTag);
	}

	/**
	 * AssignedTo テーブルにタスクを保存するメソッド
	 *
	 * @param task 登録されたタスク
	 * @param assigneeId 担当者のID（オプション）
	 */
	public void saveAssignedTo(Task task, @Nullable Integer assigneeId) {
		// 現在のユーザーを取得
		ExtendedUser userDetails = getCurrentUserDetails();

		// AssignedToオブジェクトを作成
		AssignedTo assignedTo = new AssignedTo();
		assignedTo.setTaskId(task.getId()); // タスクIDを設定

		// assigneeId が存在すればそれを使用し、存在しない場合は現在のユーザーIDを使用
		if (assigneeId != null) {
			assignedTo.setUserId(assigneeId);
		} else {
			assignedTo.setUserId(userDetails.getUserId());
		}

		// AssignedTo テーブルに保存
		userService.saveAssignedTo(assignedTo);
	}

}
