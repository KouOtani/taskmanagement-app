package katachi.spring.exercise.domain.user.service.Impl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
import katachi.spring.exercise.domain.user.model.Task;
import katachi.spring.exercise.domain.user.model.Task.TaskPriority;
import katachi.spring.exercise.domain.user.model.Task.TaskStatus;
import katachi.spring.exercise.domain.user.service.ProjectService;
import katachi.spring.exercise.repository.ProjectMapper;

@Service
public class ProjectServiceImpl implements ProjectService {

	@Autowired
	private ProjectMapper projectMapper;

	/*プロジェクトを登録*/
	@Override
	public void projectEntry(Project project) {
		projectMapper.insertProject(project);
	}

	/*タスクタグ登録*/
	@Override
	public void saveProjectTag(ProjectTag projectTag) {
		projectMapper.insertProjectTag(projectTag);
	}

	/*プロジェクトメンバー登録(リーダー)*/
	@Override
	public void saveProjectMember(ProjectMember projectMember) {
		projectMapper.addProjectMember(projectMember);
	}

	/*所属しているプロジェクトを複数取得(ホーム画面で表示用)*/
	@Override
	public List<Project> getAllProjectsAndProgress(Integer userId) {
		return getAllProjectsAndProgressAndFiltering(userId, null, null, null, null, null);
	}

	/*所属しているプロジェクトを複数取得(フィルタリング)*/
	@Override
	public List<Project> getAllProjectsAndProgressAndFiltering(Integer userId,
			String searchQuery,
			Integer leaderId,
			ProjectStatus status,
			String dueDateOrder,
			String tagName) {

		// ユーザーが属しているすべてのプロジェクトを取得
		List<Project> projects = projectMapper.findAllProjects(userId, searchQuery, leaderId, status, dueDateOrder, tagName);
		for (Project project : projects) {
			// プロジェクトごとのタスク数と完了済みタスク数を取得
			Integer totalTasks = projectMapper.countAllTasksByProjectId(project.getId());
			Integer completedTasks = projectMapper.countCompletedTasksByProjectId(project.getId());

			// 取得したタスク情報をプロジェクトにセット
			project.setTotalTasks(totalTasks);
			project.setCompletedTasks(completedTasks);

			// 進捗率を計算
			int progress = totalTasks != 0 ? (int) ((completedTasks * 100) / totalTasks) : 0;
			project.setProgress(progress); // 進捗率をセット
		}

		return projects;
	}

	/*所属しているプロジェクトのリーダーを重複なく取得*/
	@Override
	public List<Project> getUniqueLeadersByUserId(Integer userId) {
		return projectMapper.findLeadersByUserId(userId);
	}

	/*プロジェクトを検索(１件)*/ //招待画面を表示する
	@Override
	public Project getProjectForInvitationById(Integer projectId) {
		return projectMapper.findOneProjectForInvitationById(projectId);
	}

	/*プロジェクトを検索(１件)*/ //編集画面を表示する
	@Override
	public Project getProjectForEditingById(Integer projectId) {
		return projectMapper.findOneProjectForEditingById(projectId);
	}

	/*プロジェクト情報を更新するメソッド*/
	@Override
	public void updateProject(Project project) {
		projectMapper.updateProject(project);
	}

	/*プロジェクトに関連付けられたタグを削除するメソッド*/
	@Override
	public void removeProjectTags(Integer projectId) {
		projectMapper.deleteByProjectId(projectId);
	}

	/*プロジェクトを削除*/
	@Override
	public void deleteProject(Integer projectId) {
		projectMapper.deleteProject(projectId);
	}

	/*プロジェクトのメンバーを取得*/
	@Override
	public List<ProjectMember> getMembersByProjectId(Integer projectId) {
		return projectMapper.findMembersByProjectId(projectId);
	}

	/*プロジェクトのメンバーにそのユーザーが含まれているかチェック*/
	@Override
	public boolean isMemberOfProject(Integer projectId, String email) {
		return projectMapper.isMemberOfProjectByEmail(projectId, email) > 0;
	}

	/*招待が存在し、まだ未承諾（保留中）の状態かを確認*/
	@Override
	public boolean isUserAlreadyInvited(Integer projectId, String email) {
		return projectMapper.isUserAlreadyInvitedByEmail(projectId, email) > 0;
	}

	/*ユーザーの通知リストを取得*/
	@Override
	public List<Invitation> getInvitationsByUserId(Integer userId) {
		return projectMapper.findInvitationsByUserId(userId);
	}

	/*招待するプロジェクト、招待者、招待されたユーザーを作成する*/
	@Transactional
	@Override
	public void createInvitation(Integer projectId,
			Integer inviteeId,
			Integer inviterId) {

		//Invitationエンティティを作成
		Invitation invitation = new Invitation();
		invitation.setProjectId(projectId);
		invitation.setInviteeId(inviteeId);
		invitation.setInviterId(inviterId);
		invitation.setStatus(InvitationStatus.PENDING); // 初期ステータスをPENDINGに設定

		// マッパーを使用してInvitationテーブルにインサート
		projectMapper.insertInvitation(invitation);

	}

	/*招待のステータスを更新し、必要に応じてプロジェクトメンバーを追加*/
	@Transactional
	@Override
	public void handleInvitation(Integer invitationId, Integer userId, InvitationStatus status) {

		// 招待の詳細を取得
		Invitation invitation = projectMapper.findInvitationDetailsById(invitationId);

		// 招待のステータスを更新する
		projectMapper.updateInvitationStatus(invitationId, status);

		// ステータスが承認された場合にのみ、プロジェクトメンバーにユーザーを追加する
		if (status == InvitationStatus.ACCEPTED && userId != null) {
			// プロジェクトメンバーにユーザーを追加
			ProjectMember projectMember = new ProjectMember();
			projectMember.setProjectId(invitation.getProjectId());
			projectMember.setUserId(userId);
			projectMember.setRole("メンバー"); // デフォルトのロールを設定
			projectMapper.addProjectMember(projectMember);
		}
	}

	/*招待の数を取得する*/
	@Override
	public Integer countPendingInvitationsForUser(Integer userId) {
		return projectMapper.countPendingInvitations(userId);
	}

	/*プロジェクトタスクを複数件取得*/
	@Override
	public List<Task> getProjectTasks(Integer projectId,
			String searchQuery,
			Boolean completed,
			Integer userId,
			TaskStatus status,
			TaskPriority priority,
			String dueDateOrder) {
		return projectMapper.findManyTasksByProjectId(projectId, searchQuery, completed, userId, status, priority, dueDateOrder);
	}

	/*プロジェクトタスク情報を更新するメソッド*/
	@Transactional
	@Override
	public void updateTask(Task task, Integer assigneeId) {
		projectMapper.updateProjectTask(task);
		projectMapper.updateAssignee(task, assigneeId);
	}

	/*プロジェクトに属するメンバーを取得*/
	@Override
	public List<ProjectMember> getProjectMembers(Integer projectId) {
		return projectMapper.findMembersByProjectId(projectId);
	}

	/*プロジェクトタスクを取得(１件)*/
	@Override
	public Task getProjectTaskOneByTaskId(Integer taskId) {
		return projectMapper.findProjectTaskOneByTaskId(taskId);
	}

	/*プロジェクトチャット内のコメントを取得*/
	@Override
	public List<Comment> getCommentsByProjectId(Integer projectId) {
		return projectMapper.getCommentsByProjectId(projectId);
	}

	/*プロジェクトチャット内のコメントを保存*/
	@Override
	public Integer saveComment(Integer projectId, Integer userId, String content) {
		Comment comment = new Comment();
		comment.setProjectId(projectId);
		comment.setUserId(userId);
		comment.setContent(content);

		// コメントを挿入し、自動生成されたIDを取得
		projectMapper.insertComment(comment);

		// 挿入されたコメントのIDを返す
		return comment.getId();
	}

	/*プロジェクトチャット内のコメントを更新*/
	@Override
	public void updateComment(Integer commentId, String content) {
		projectMapper.updateComment(commentId, content);
	}

	/*プロジェクトチャットのファイルを保存*/
	@Override
	public void saveAttachments(Integer commentId, MultipartFile[] attachments) throws IOException {
		// プロジェクトのルートディレクトリからのパス
		String uploadDir = "C:/pleiades/2023-09/workspace/SpringTaskManagement/uploads/comments"; // ファイルを保存するディレクトリ

		for (MultipartFile file : attachments) {
			if (!file.isEmpty()) {
				String originalFileName = file.getOriginalFilename();
				String uniqueFileName = System.currentTimeMillis() + "_" + originalFileName; // 一意なファイル名

				// ファイル保存先のパスを作成
				Path filePath = Paths.get(uploadDir, uniqueFileName);

				// ファイルを保存
				Files.copy(file.getInputStream(), filePath);

				// CommentAttachmentエンティティを作成し、DBに保存
				CommentAttachment attachment = new CommentAttachment();
				attachment.setCommentId(commentId);
				attachment.setFilePath(uniqueFileName);
				attachment.setFileName(originalFileName);
				projectMapper.saveCommentAttachment(attachment);
			}
		}
	}

	/*ファイルの情報を取得する*/
	public CommentAttachment getCommentAttachmentById(Integer attachmentId) {
		return projectMapper.findCommentAttachmentById(attachmentId); // DBからデータを取得
	}

	/*対象のコメントを取得*/
	@Override
	public Comment getCommentById(Integer commentId) {
		return projectMapper.getCommentById(commentId);
	}

	/*コメントを削除*/
	@Override
	public void deleteComment(Integer commentId) {
		projectMapper.deleteCommentById(commentId);
	}

	/*コメントに対するリアクションがあるかどうかを確認*/
	@Override
	public boolean checkIfUserReacted(Integer commentId, Integer userId) {
		return projectMapper.checkIfUserReacted(commentId, userId);
	}

	/*リアクションの追加または削除を行う*/
	@Override
	public void toggleReaction(Integer commentId, Integer reactorId) {
		boolean hasReacted = projectMapper.checkIfUserReacted(commentId, reactorId);

		if (hasReacted) {
			// リアクションを削除する
			projectMapper.removeReaction(commentId, reactorId);
		} else {
			// リアクションを追加する
			projectMapper.addReaction(commentId, reactorId);

			// リアクションに対する通知を追加する
			CommentReactionNotification notification = new CommentReactionNotification();
			notification.setCommentId(commentId); // リアクションされたコメント
			notification.setReactorId(reactorId); // リアクションを行ったユーザー

			projectMapper.insertCommentReactionNotification(notification); // 通知を挿入

		}
	}

	/*通知の作成*/
	@Override
	public void createCommentNotifications(Integer commentId,
			Integer projectId,
			Integer commentingUserId) {

		// プロジェクト内の他のメンバーを取得
		List<Integer> otherMemberIds = projectMapper.getOtherMembers(projectId, commentingUserId);

		// 各メンバーに対して通知を作成
		for (Integer memberId : otherMemberIds) {
			projectMapper.insertCommentNotification(commentId, memberId);
		}
	}

	/*コメント通知の取得*/
	@Override
	public List<CommentNotification> getCommentNotifications(Integer userId) {
		return projectMapper.getCommentNotificationsByUserId(userId);
	}

	/*コメント通知の削除*/
	@Override
	public void clearCommentNotifications(Integer userId, Integer projectId) {
		projectMapper.deleteCommentNotificationsByProjectIdAndUserId(userId, projectId);
	}

	/*未読コメントの数を取得する*/
	@Override
	public Integer countUnreadCommentsForUser(Integer userId) {
		return projectMapper.countUnreadComments(userId);
	}

	/*リアクション通知の取得*/
	@Override
	public List<CommentReactionNotification> getReactionNotifications(Integer userId) {
		return projectMapper.getReactionNotificationsByUserId(userId);
	}

	/*コメント通知の削除*/
	@Override
	public void clearReactionNotifications(Integer userId, Integer projectId) {
		projectMapper.deleteReactionNotificationsByProjectIdAndUserId(userId, projectId);
	}

	/*未読コメントの数を取得する*/
	@Override
	public Integer countUnconfirmedReactionsForUser(Integer userId) {
		return projectMapper.countUnconfirmedReactions(userId);
	}

	/*期限が過ぎたプロジェクトタスクを複数件取得*/
	@Override
	public List<Task> getProjectPastDueTasks(Integer userId) {
		return projectMapper.findProjectPastDueTasks(userId);
	}

	/*今日が期日のプロジェクトタスクを複数件取得*/
	public List<Task> getProjectDueTodayTasks(Integer userId) {
		return projectMapper.findProjectTodayDueTasks(userId);
	}

	/*期日まで一週間のプロジェクトタスクを複数件取得*/
	public List<Task> getProjectDueOneWeekTasks(Integer userId) {
		return projectMapper.findProjectDueOneWeekTasks(userId);
	}

}
