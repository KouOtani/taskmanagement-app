package katachi.spring.exercise.domain.user.service;

import java.io.IOException;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import katachi.spring.exercise.domain.user.model.Comment;
import katachi.spring.exercise.domain.user.model.CommentAttachment;
import katachi.spring.exercise.domain.user.model.CommentNotification;
import katachi.spring.exercise.domain.user.model.CommentReactionNotification;
import katachi.spring.exercise.domain.user.model.Invitation;
import katachi.spring.exercise.domain.user.model.Invitation.InvitationStatus;
import katachi.spring.exercise.domain.user.model.Project;
import katachi.spring.exercise.domain.user.model.Project.ProjectStatus;
import katachi.spring.exercise.domain.user.model.ProjectMember;
import katachi.spring.exercise.domain.user.model.ProjectTag;
import katachi.spring.exercise.domain.user.model.ProjectTaskNotification;
import katachi.spring.exercise.domain.user.model.Tag;
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
			Integer tagId);

	/*所属しているプロジェクトのリーダーを重複なく取得*/
	public List<Project> getUniqueLeadersByUserId(Integer userId);

	/*Projectテーブルの情報を取得*/
	public Project getProjectByProjectId(Integer projectId);

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
			Integer memberId,
			TaskStatus status,
			TaskPriority priority,
			String dueDateOrder,
			Integer page,
			Integer size);

	/*プロジェクトタスクの総数を取得*/
	public int countProjectTasksByUserId(Integer projectId,
			Integer memberId,
			String searchQuery,
			Boolean completed,
			TaskStatus status,
			TaskPriority priority);

	/*プロジェクトタスク情報を更新するメソッド*/
	public void updateTask(Task task, Integer assigneeId);

	/*プロジェクトに属するメンバーを取得*/
	public List<ProjectMember> getProjectMembers(Integer projectId);

	/*自分が所属しているプロジェクトのタグ一覧の取得*/
	public List<Tag> getTagsForProjects(Integer userId);

	/*プロジェクトタスクを取得(１件)*/
	public Task getProjectTaskOneByTaskId(Integer taskId);

	/*プロジェクトタスク追加の通知を作成*/
	public void createProjectTaskNotification(ProjectTaskNotification notification);

	/*プロジェクチャットト内のコメントを取得*/
	public List<Comment> getCommentsByProjectIdWithPagination(Integer projectId, Integer page, Integer size);

	/*リアクションや添付ファイルは別クエリで取得し、コメント情報とコード上で合成*/
	public void addReactionsAndAttachmentsToComments(List<Comment> comments);

	/*コメントの総数を取得*/
	public int countCommentsByProjectId(Integer projectId);

	/*プロジェクトチャット内のコメントを保存*/
	public Integer saveComment(Integer projectId, Integer userId, String content);

	/*プロジェクトチャット内のコメントを更新*/
	public void updateComment(Integer commentId, String content);

	/*プロジェクトチャット内のファイルを保存*/
	public void saveAttachments(Integer commentId, MultipartFile[] attachments) throws IOException;

	/*ファイルの情報を取得する*/
	public CommentAttachment getCommentAttachmentById(Integer attachmentId);

	/*対象のコメントを取得*/
	public Comment getCommentById(Integer commentId);

	/*コメントを削除*/
	public void deleteComment(Integer commentId);

	/*コメントに対するリアクションがあるかどうかを確認*/
	public boolean checkIfUserReacted(Integer commentId, Integer userId);

	/*リアクションの追加または削除を行う*/
	public void toggleReaction(Integer commentId, Integer userId);

	/*コメント通知の作成*/
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

	/*プロジェクトタスクを振られた通知の取得*/
	public List<ProjectTaskNotification> getProjectTaskNotifications(Integer userId);

	/*プロジェクトタスク通知の削除*/
	public void clearProjectTaskNotifications(Integer userId, Integer projectId);

	/*未確認プロジェクトタスクの数を取得する*/
	public Integer countUnconfirmedProjectTasksForUser(Integer userId);

	/*期限が過ぎたプロジェクトタスクを複数件取得*/
	public List<Task> getProjectPastDueTasks(Integer userId);

	/*今日が期日のプロジェクトタスクを複数件取得*/
	public List<Task> getProjectDueTodayTasks(Integer userId);

	/*期日まで一週間のプロジェクトタスクを複数件取得*/
	public List<Task> getProjectDueOneWeekTasks(Integer userId);

}
