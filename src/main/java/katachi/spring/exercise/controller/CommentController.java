package katachi.spring.exercise.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import katachi.spring.exercise.application.service.UserApplicationService;
import katachi.spring.exercise.domain.user.model.Comment;
import katachi.spring.exercise.domain.user.model.ExtendedUser;
import katachi.spring.exercise.domain.user.model.Project;
import katachi.spring.exercise.domain.user.service.ProjectService;

@Controller
@RequestMapping("/project")
public class CommentController {

	@Autowired
	private ProjectService projectService;

	@Autowired
	private UserApplicationService applicationService;

	@Autowired
	private HttpSession session;

	@ModelAttribute
	public void addAttributes(HttpServletRequest request, Model model) {
		model.addAttribute("currentUri", request.getRequestURI());
		model.addAttribute("queryString", request.getQueryString());

	}

	// チャットページの表示
	@GetMapping("/chat/{projectId}")
	public String getChatPage(@PathVariable("projectId") Integer projectId,
			Model model) {

		// 現在のユーザー情報を取得
		ExtendedUser userDetails = applicationService.getCurrentUserDetails();
		Integer userId = userDetails.getUserId();

		List<Comment> comments = projectService.getCommentsByProjectId(projectId);

		// 各コメントに対して、ユーザーがリアクションしているかどうかを確認
		for (Comment comment : comments) {
			boolean hasReacted = projectService.checkIfUserReacted(comment.getId(), userId);
			comment.setHasReacted(hasReacted); // hasReactedフラグをコメントにセット
		}

		// プロジェクトに基づいたコメントの通知を削除
		projectService.clearCommentNotifications(userId, projectId);

		// プロジェクトに基づいたリアクションの通知を削除
		projectService.clearReactionNotifications(userId, projectId);

		// projectIdからプロジェクトの情報を取得
		Project project = projectService.getProjectByProjectId(projectId);

		// ユーザーの未処理の招待数を取得
		int pendingInvitations = projectService.countPendingInvitationsForUser(userId);

		// ユーザーの未読コメント通知数を取得
		int unreadComments = projectService.countUnreadCommentsForUser(userId);

		// ユーザーの未確認リアクション通知数を取得
		int unconfirmedReactions = projectService.countUnconfirmedReactionsForUser(userId);

		// ユーザーの未確認プロジェクトタスク通知数を取得
		int unconfirmedProjectTasks = projectService.countUnconfirmedProjectTasksForUser(userId);

		// 未処理の招待数、未読コメント通知数、未確認リアクション通知数、未確認プロジェクトタスク通知数を合計
		int totalNotifications = pendingInvitations + unreadComments + unconfirmedReactions + unconfirmedProjectTasks;

		// セッションに通知の総数を保存
		session.setAttribute("totalNotifications", totalNotifications);

		model.addAttribute("project", project); // プロジェクト情報をモデルに追加
		model.addAttribute("projectId", projectId);
		model.addAttribute("comments", comments);

		return "project/chat";

	}

	// コメントの投稿および更新処理
	@PostMapping("/comments")
	public String postOrUpdateComment(@RequestParam("projectId") Integer projectId,
			@RequestParam("userId") Integer userId,
			@RequestParam("content") String content,
			@RequestParam(required = false) Integer commentId,
			@RequestParam("attachments") MultipartFile[] attachments) throws IOException {

		if (commentId != null) {
			// コメントを更新
			projectService.updateComment(commentId, content);
			// 添付ファイルの処理
			if (attachments != null && attachments.length > 0) {
				projectService.saveAttachments(commentId, attachments);
			}
		} else {
			// 新しいコメントを保存し、生成されたコメントIDを取得
			Integer newCommentId = projectService.saveComment(projectId, userId, content);

			// 添付ファイルの処理
			if (attachments != null && attachments.length > 0) {
				projectService.saveAttachments(newCommentId, attachments);
			}

			// 通知を作成 (コメントをしたユーザー以外のプロジェクトメンバーに通知を送信)
			projectService.createCommentNotifications(newCommentId, projectId, userId);
		}
		return "redirect:/project/chat/" + projectId; // チャットページにリダイレクト
	}

	// コメント編集処理
	@GetMapping("/comments/edit/{commentId}")
	public String getEditCommentPage(@PathVariable Integer commentId,
			@RequestParam Integer projectId,
			Model model) {

		Comment comment = projectService.getCommentById(commentId);

		List<Comment> comments = projectService.getCommentsByProjectId(projectId);

		// projectIdからプロジェクトの情報を取得
		Project project = projectService.getProjectByProjectId(projectId);

		model.addAttribute("editComment", comment); // 編集対象のコメント
		model.addAttribute("comments", comments); // コメント一覧
		model.addAttribute("projectId", projectId); // プロジェクトID
		model.addAttribute("project", project); // プロジェクト情報をモデルに追加

		return "project/chat"; // チャットページに戻る
	}

	// コメント削除処理
	@PostMapping("/comments/delete/{commentId}")
	public String deleteComment(@PathVariable("commentId") Integer commentId,
			@RequestParam("projectId") Integer projectId) {

		// コメント削除処理
		projectService.deleteComment(commentId);
		return "redirect:/project/chat/" + projectId; // チャットページにリダイレクト
	}

	// リアクション処理
	@PostMapping("/comments/react/{commentId}")
	public String toggleReaction(@PathVariable("commentId") Integer commentId,
			@RequestParam("userId") Integer userId,
			@RequestParam("projectId") Integer projectId) {

		projectService.toggleReaction(commentId, userId);
		return "redirect:/project/chat/" + projectId + "#" + commentId;
	}

}
