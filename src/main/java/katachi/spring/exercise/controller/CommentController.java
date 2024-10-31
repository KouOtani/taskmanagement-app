package katachi.spring.exercise.controller;

import java.io.IOException;
import java.util.Collections;
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
	public String getChatPage(
			@PathVariable("projectId") Integer projectId,
			@RequestParam(name = "page", defaultValue = "1") Integer page,
			@RequestParam(name = "size", defaultValue = "20") Integer size,
			Model model) {

		// 現在のユーザー情報を取得
		ExtendedUser userDetails = applicationService.getCurrentUserDetails();
		Integer userId = userDetails.getUserId();

		// コメントをページごとに取得
		List<Comment> comments = projectService.getCommentsByProjectIdWithPagination(projectId, page, size);
		Collections.reverse(comments); // 最新のコメントから古いコメントの順に並ぶため、コメントリストを逆順にする

		// 各コメントにリアクションと添付ファイルを追加
		projectService.addReactionsAndAttachmentsToComments(comments);

		// コメントの総数を取得し、総ページ数を計算
		int totalComments = projectService.countCommentsByProjectId(projectId);
		int totalPages = Math.max(1, (int) Math.ceil((double) totalComments / size));

		// 各コメントに対して、ユーザーがリアクションしているかどうかを確認
		for (Comment comment : comments) {
			boolean hasReacted = projectService.checkIfUserReacted(comment.getId(), userId);
			comment.setHasReacted(hasReacted); // hasReactedフラグをコメントにセット
		}

		// プロジェクトに基づいたコメントの通知を削除
		projectService.clearCommentNotifications(userId, projectId);

		// プロジェクトに基づいたリアクションの通知を削除
		projectService.clearReactionNotifications(userId, projectId);

		// ユーザーの未処理の招待数、未読コメント通知数、未確認リアクション通知数、未確認プロジェクトタスク通知数を取得
		int pendingInvitations = projectService.countPendingInvitationsForUser(userId);
		int unreadComments = projectService.countUnreadCommentsForUser(userId);
		int unconfirmedReactions = projectService.countUnconfirmedReactionsForUser(userId);
		int unconfirmedProjectTasks = projectService.countUnconfirmedProjectTasksForUser(userId);

		// 未処理の招待数、未読コメント通知数、未確認リアクション通知数、未確認プロジェクトタスク通知数を合計
		int totalNotifications = pendingInvitations + unreadComments + unconfirmedReactions + unconfirmedProjectTasks;

		// セッションに通知の総数を保存
		session.setAttribute("totalNotifications", totalNotifications);

		// モデルにプロジェクト情報、コメント、ページ情報を追加
		model.addAttribute("project", projectService.getProjectByProjectId(projectId));
		model.addAttribute("projectId", projectId);
		model.addAttribute("comments", comments);
		model.addAttribute("page", page);
		model.addAttribute("size", size);
		model.addAttribute("totalPages", totalPages);

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
			@RequestParam(name = "page", defaultValue = "1") Integer page,
			@RequestParam(name = "size", defaultValue = "20") Integer size,
			Model model) {

		Comment comment = projectService.getCommentById(commentId);

		// コメントをページごとに取得
		List<Comment> comments = projectService.getCommentsByProjectIdWithPagination(projectId, page, size);
		Collections.reverse(comments); // 最新のコメントから古いコメントの順に並ぶため、コメントリストを逆順にする

		// 各コメントにリアクションと添付ファイルを追加
		projectService.addReactionsAndAttachmentsToComments(comments);

		// コメントの総数を取得し、総ページ数を計算
		int totalComments = projectService.countCommentsByProjectId(projectId);
		int totalPages = (int) Math.ceil((double) totalComments / size);

		model.addAttribute("editComment", comment); // 編集対象のコメント
		model.addAttribute("comments", comments); // コメント一覧
		model.addAttribute("projectId", projectId); // プロジェクトID
		model.addAttribute("project", projectService.getProjectByProjectId(projectId)); // プロジェクト情報をモデルに追加
		model.addAttribute("page", page);
		model.addAttribute("size", size);
		model.addAttribute("totalPages", totalPages);

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
