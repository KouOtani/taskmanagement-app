package katachi.spring.exercise.controller;

import java.util.ArrayList;
import java.util.Comparator;
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

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import katachi.spring.exercise.application.service.UserApplicationService;
import katachi.spring.exercise.domain.user.dto.NotificationDTO;
import katachi.spring.exercise.domain.user.model.CommentNotification;
import katachi.spring.exercise.domain.user.model.CommentReactionNotification;
import katachi.spring.exercise.domain.user.model.ExtendedUser;
import katachi.spring.exercise.domain.user.model.Invitation;
import katachi.spring.exercise.domain.user.model.Invitation.InvitationStatus;
import katachi.spring.exercise.domain.user.model.Task;
import katachi.spring.exercise.domain.user.service.ProjectService;
import katachi.spring.exercise.domain.user.service.UserService;

@Controller
@RequestMapping("/notifications")
public class NotificationController {

	@Autowired
	private UserService userService;

	@Autowired
	private ProjectService projectService;

	@Autowired
	private UserApplicationService applicationService;

	@ModelAttribute
	public void addAttributes(HttpServletRequest request, Model model) {
		model.addAttribute("currentUri", request.getRequestURI());
		model.addAttribute("queryString", request.getQueryString());
	}

	@GetMapping
	public String showNotifications(Model model) {

		// 現在のユーザー情報を取得
		ExtendedUser userDetails = applicationService.getCurrentUserDetails();

		// 今日期日の個人タスクを取得
		List<Task> personalTodayDueTasks = userService.getPersonalDueTodayTasks(userDetails.getUserId());
		model.addAttribute("personalTodayDueTasks", personalTodayDueTasks);
		// 今日期日のプロジェクトタスクを取得
		List<Task> projectTodayDueTasks = projectService.getProjectDueTodayTasks(userDetails.getUserId());
		model.addAttribute("projectTodayDueTasks", projectTodayDueTasks);

		// 招待の通知を取得
		List<Invitation> invitationDetailsList = projectService.getInvitationsByUserId(userDetails.getUserId());

		// コメントの通知を取得
		List<CommentNotification> commentNotifications = projectService.getCommentNotifications(userDetails.getUserId());

		// リアクションの通知を取得
		List<CommentReactionNotification> reactionNotifications = projectService.getReactionNotifications(userDetails.getUserId());

		// 3つのリストを統合
		List<NotificationDTO> notifications = new ArrayList<>();

		// 招待の通知をDTOリストに変換
		for (Invitation invitation : invitationDetailsList) {
			notifications.add(new NotificationDTO("INVITATION", invitation, invitation.getInvitationDate()));
		}

		// コメントの通知をDTOリストに変換
		for (CommentNotification commentNotification : commentNotifications) {
			notifications.add(new NotificationDTO("COMMENT", commentNotification, commentNotification.getNotificationDate()));
		}

		// コメントリアクションの通知をDTOリストに変換
		for (CommentReactionNotification reactionNotification : reactionNotifications) {
			notifications.add(new NotificationDTO("REACTION", reactionNotification, reactionNotification.getNotificationDate()));
		}

		// 作成日時(createdAt)でソート（新しい順）
		notifications.sort(Comparator.comparing(NotificationDTO::getCreatedAt).reversed());

		// 統合された通知リストをビューに渡す
		model.addAttribute("notifications", notifications);

		return "user/notification";
	}

	@PostMapping("/{invitationId}/response")
	public String respondToInvitation(@PathVariable("invitationId") Integer invitationId,
			@RequestParam("response") String response,
			HttpSession session) {

		ExtendedUser userDetails = applicationService.getCurrentUserDetails();

		if (response.equals("accept")) {
			// 招待を承認してプロジェクトメンバーに追加する
			projectService.handleInvitation(invitationId, userDetails.getUserId(), InvitationStatus.ACCEPTED);
		} else if (response.equals("reject")) {
			// 招待を拒否するロジック
			projectService.handleInvitation(invitationId, null, InvitationStatus.REJECTED);
		}

		// 未処理の招待数を更新
		int pendingInvitations = projectService.countPendingInvitationsForUser(userDetails.getUserId());
		session.setAttribute("pendingInvitations", pendingInvitations);

		return "redirect:/notifications";
	}

}
