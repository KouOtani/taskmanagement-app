package katachi.spring.exercise.repository;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

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
import katachi.spring.exercise.domain.user.model.Task;
import katachi.spring.exercise.domain.user.model.Task.TaskPriority;
import katachi.spring.exercise.domain.user.model.Task.TaskStatus;

@Mapper
public interface ProjectMapper {

	/*プロジェクトを登録*/
	public int insertProject(Project project);

	/*タグ登録*/
	public int insertProjectTag(ProjectTag projectTag);

	/*プロジェクトメンバー登録(リーダー)*/
	public int addProjectMember(ProjectMember projectMember);

	/*所属しているプロジェクトを複数取得*/
	public List<Project> findAllProjects(@Param("userId") Integer userId,
			@Param("searchQuery") String searchQuery,
			@Param("leaderId") Integer leaderId,
			@Param("status") ProjectStatus status,
			@Param("dueDateOrder") String dueDateOrder,
			@Param("tagName") String tagName);

	/*所属しているプロジェクトのリーダーを重複なく取得*/
	public List<Project> findLeadersByUserId(@Param("userId") Integer userId);

	/*完了済みタスク数を取得*/
	public Integer countCompletedTasksByProjectId(@Param("projectId") Integer projectId);

	/*プロジェクト全体のタスク数を取得*/
	public Integer countAllTasksByProjectId(@Param("projectId") Integer projectId);

	/*プロジェクトを検索(１件)*/ //招待画面を表示
	public Project findOneProjectForInvitationById(@Param("projectId") Integer projectId);

	/*プロジェクトを検索(１件)*/ //編集画面を表示
	public Project findOneProjectForEditingById(@Param("projectId") Integer projectId);

	/*プロジェクト情報を更新するメソッド*/
	public void updateProject(Project project);

	/*プロジェクトに関連付けられたタグを削除するメソッド*/
	public int deleteByProjectId(@Param("projectId") Integer projectId);

	/*プロジェクトを削除するメソッド*/
	public int deleteProject(@Param("projectId") Integer projectId);

	/*プロジェクトのメンバーを検索*/
	public List<ProjectMember> findMembersByProjectId(@Param("projectId") Integer projectId);

	/*プロジェクトのメンバーにそのユーザーが含まれているかチェック*/
	public Integer isMemberOfProjectByEmail(@Param("projectId") Integer projectId, @Param("email") String email);

	/*招待が存在し、まだ未承諾（保留中）の状態かを確認*/
	public Integer isUserAlreadyInvitedByEmail(@Param("projectId") Integer projectId, @Param("email") String email);

	/*ユーザーの通知リストを取得するメソッド*/
	List<Invitation> findInvitationsByUserId(@Param("userId") Integer userId);

	/*Invitationテーブルに招待情報をインサートするメソッド*/
	public int insertInvitation(Invitation invitation);

	/*招待の詳細を取得*/
	public Invitation findInvitationDetailsById(Integer invitationId);

	/*招待状のステータスを更新するメソッド*/
	public void updateInvitationStatus(@Param("invitationId") Integer invitationId,
			@Param("status") InvitationStatus status);

	/*招待の数を取得するメソッド*/
	public Integer countPendingInvitations(@Param("userId") Integer userId);

	/*プロジェクトタスクを複数件取得*/
	public List<Task> findManyTasksByProjectId(@Param("projectId") Integer projectId,
			@Param("searchQuery") String searchQuery,
			@Param("completed") Boolean completed,
			@Param("userId") Integer userId,
			@Param("status") TaskStatus status,
			@Param("priority") TaskPriority priority,
			@Param("dueDateOrder") String dueDateOrder);

	/*プロジェクトタスク情報を更新するメソッド*/
	public void updateProjectTask(Task task);

	/*プロジェクトタスクの担当者を更新するメソッド*/
	public void updateAssignee(Task task, @Param("assigneeId") Integer assigneeId);

	/*プロジェクトタスクを取得(１件)*/
	public Task findProjectTaskOneByTaskId(@Param("taskId") Integer taskId);

	/*プロジェクトチャット内のコメントを取得*/
	List<Comment> getCommentsByProjectId(Integer projectId);

	/*プロジェクトチャット内のコメントを保存*/
	public void insertComment(Comment comment);

	/*プロジェクトチャット内のコメントを更新*/
	public void updateComment(@Param("commentId") Integer commentId,
			@Param("content") String content);

	/*プロジェクトチャットのファイルを保存*/
	public void saveCommentAttachment(CommentAttachment attachment);

	/*ファイルの情報を取得する*/
	public CommentAttachment findCommentAttachmentById(@Param("attachmentId") Integer attachmentId);

	/*コメントIDでコメントを取得*/
	public Comment getCommentById(@Param("commentId") Integer commentId);

	/*コメントを削除*/
	public int deleteCommentById(@Param("commentId") Integer commentId);

	/*指定のユーザーがコメントにリアクションしているか確認する*/
	public boolean checkIfUserReacted(@Param("commentId") Integer commentId,
			@Param("userId") Integer userId);

	/*リアクションを追加する*/
	public void addReaction(@Param("commentId") Integer commentId,
			@Param("userId") Integer userId);

	/*リアクションを削除する*/
	public int removeReaction(@Param("commentId") Integer commentId,
			@Param("userId") Integer userId);

	/*他のメンバーを取得するメソッド*/
	List<Integer> getOtherMembers(@Param("projectId") Integer projectId, @Param("commentingUserId") Integer commentingUserId);

	/*通知を挿入するメソッド*/
	public void insertCommentNotification(@Param("commentId") Integer commentId, @Param("userId") Integer userId);

	/*ユーザーIDに基づいてコメント通知を取得するメソッド*/
	List<CommentNotification> getCommentNotificationsByUserId(@Param("userId") Integer userId);

	/*ユーザーIDに基づいてコメント通知を削除するメソッド*/
	public void deleteCommentNotificationsByProjectIdAndUserId(@Param("userId") Integer userId,
			@Param("projectId") Integer projectId);

	/*未読コメントの数を取得するメソッド*/
	public Integer countUnreadComments(@Param("userId") Integer userId);

	/*リアクション通知を挿入するメソッド*/
	public void insertCommentReactionNotification(CommentReactionNotification notification);

	/*ユーザーIDに基づいてリアクション通知を取得するメソッド*/
	List<CommentReactionNotification> getReactionNotificationsByUserId(@Param("userId") Integer userId);

	/*ユーザーIDに基づいてリアクション通知を削除するメソッド*/
	public void deleteReactionNotificationsByProjectIdAndUserId(@Param("userId") Integer userId,
			@Param("projectId") Integer projectId);

	/*未確認リアクションの数を取得するメソッド*/
	public Integer countUnconfirmedReactions(@Param("userId") Integer userId);

	/*期限が過ぎたプロジェクトタスクを複数件取得*/
	public List<Task> findProjectPastDueTasks(Integer userId);

	/*今日が期日のプロジェクトタスクを複数件取得*/
	public List<Task> findProjectTodayDueTasks(Integer userId);

	/*期日まで一週間のプロジェクトタスクを複数件取得*/
	public List<Task> findProjectDueOneWeekTasks(Integer userId);

}
