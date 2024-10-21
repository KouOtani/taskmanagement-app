package katachi.spring.exercise.domain.user.service;

import java.util.List;

import katachi.spring.exercise.domain.user.model.Comment;
import katachi.spring.exercise.domain.user.model.CommentNotification;
import katachi.spring.exercise.domain.user.model.CommentReactionNotification;
import katachi.spring.exercise.domain.user.model.Invitation;
import katachi.spring.exercise.domain.user.model.Invitation.InvitationStatus;
import katachi.spring.exercise.domain.user.model.Project;
import katachi.spring.exercise.domain.user.model.Project.ProjectStatus;
import katachi.spring.exercise.domain.user.model.ProjectMember;
import katachi.spring.exercise.domain.user.model.ProjectTag;
import katachi.spring.exercise.domain.user.model.Task;
import katachi.spring.exercise.domain.user.model.Task.TaskPriority;
import katachi.spring.exercise.domain.user.model.Task.TaskStatus;

public interface ProjectService {

	/*プロジェクトを登録*/
	public void projectEntry(Project project);

	/*タスクタグ登録*/
	public void saveProjectTag(ProjectTag projectTag);

	/*プロジェクトメンバー登録(リーダー)*/
	public void saveProjectMember(ProjectMember projectMember);

	/*所属しているプロジェクトを複数取得(ホーム画面で表示用)*/
	public List<Project> getAllProjectsAndProgress(Integer userId);

	/*所属しているプロジェクトを複数取得(フィルタリング)*/
	public List<Project> getAllProjectsAndProgressAndFiltering(Integer userId,
			String searchQuery,
			Integer leaderId,
			ProjectStatus status,
			String dueDateOrder,
			String tagName);

	/*所属しているプロジェクトのリーダーを重複なく取得*/
	public List<Project> getUniqueLeadersByUserId(Integer userId);

	/*プロジェクトを検索(１件)*/ //招待画面を表示する
	public Project getProjectForInvitationById(Integer projectId);

	/*プロジェクトを検索(１件)*/ //編集画面を表示する
	public Project getProjectForEditingById(Integer projectId);

	/*プロジェクト情報を更新するメソッド*/
	public void updateProject(Project project);

	/*プロジェクトに関連付けられたタグを削除するメソッド*/
	public void removeProjectTags(Integer projectId);

	/*プロジェクトを削除*/
	public void deleteProject(Integer projectId);

	/*プロジェクトのメンバーを取得*/
	public List<ProjectMember> getMembersByProjectId(Integer projectId);

	/*プロジェクトのメンバーにそのユーザーが含まれているかチェック*/
	public boolean isMemberOfProject(Integer projectId, String email);

	/*招待が存在し、まだ未承諾（保留中）の状態かを確認*/
	public boolean isUserAlreadyInvited(Integer projectId, String email);

	/*ユーザーの通知リストを取得*/
	public List<Invitation> getInvitationsByUserId(Integer userId);

	/*招待するプロジェクト、招待者、招待されたユーザーを作成する*/
	public void createInvitation(Integer projectId, Integer inviteeId, Integer inviterId);

	/*招待のステータスを更新し、必要に応じてプロジェクトメンバーを追加*/
	public void handleInvitation(Integer invitationId, Integer userId, InvitationStatus status);

	/*招待の数を取得する*/
	public Integer countPendingInvitationsForUser(Integer userId);

	/*プロジェクトタスクを複数件取得*/
	public List<Task> getProjectTasks(Integer projectId,
			String searchQuery,
			Boolean completed,
			Integer userId,
			TaskStatus status,
			TaskPriority priority,
			String dueDateOrder);

	/*プロジェクトタスク情報を更新するメソッド*/
	public void updateTask(Task task, Integer assigneeId);

	/*プロジェクトに属するメンバーを取得*/
	public List<ProjectMember> getProjectMembers(Integer projectId);

	/*プロジェクトタスクを取得(１件)*/
	public Task getProjectTaskOneByTaskId(Integer taskId);

	/*プロジェクト内のコメントを取得*/
	public List<Comment> getCommentsByProjectId(Integer projectId);

	/*プロジェクト内のコメントを保存*/
	public Integer saveComment(Integer projectId, Integer userId, String content);

	/*プロジェクト内のコメントを更新*/
	public void updateComment(Integer commentId, String content);

	/*対象のコメントを取得*/
	public Comment getCommentById(Integer commentId);

	/*コメントを削除*/
	public void deleteComment(Integer commentId);

	/*コメントに対するリアクションがあるかどうかを確認*/
	public boolean checkIfUserReacted(Integer commentId, Integer userId);

	/*リアクションの追加または削除を行う*/
	public void toggleReaction(Integer commentId, Integer userId);

	/*通知の作成*/
	public void createCommentNotifications(Integer commentId,
			Integer projectId,
			Integer commentingUserId);

	/*コメント通知の取得*/
	public List<CommentNotification> getCommentNotifications(Integer userId);

	/*コメント通知の削除*/
	public void clearCommentNotifications(Integer userId, Integer projectId);

	/*未読コメントの数を取得する*/
	public Integer countUnreadCommentsForUser(Integer userId);

	/*リアクション通知の取得*/
	public List<CommentReactionNotification> getReactionNotifications(Integer userId);

	/*リアクション通知の削除*/
	public void clearReactionNotifications(Integer userId, Integer projectId);

	/*未確認リアクションの数を取得する*/
	public Integer countUnconfirmedReactionsForUser(Integer userId);

	/*期限が過ぎたプロジェクトタスクを複数件取得*/
	public List<Task> getProjectPastDueTasks(Integer userId);

	/*今日が期日のプロジェクトタスクを複数件取得*/
	public List<Task> getProjectDueTodayTasks(Integer userId);

	/*期日まで一週間のプロジェクトタスクを複数件取得*/
	public List<Task> getProjectDueOneWeekTasks(Integer userId);

}
