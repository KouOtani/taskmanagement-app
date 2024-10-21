package katachi.spring.exercise.aspect;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import katachi.spring.exercise.application.service.UserApplicationService;
import katachi.spring.exercise.domain.user.model.ExtendedUser;
import katachi.spring.exercise.domain.user.service.ProjectService;

/**
 * 全てのコントローラーで実行されるセッションに関するアスペクトクラスです。
 */
@Aspect
@Component
public class SessionAspect {

	@Autowired
	private UserApplicationService applicationService;

	@Autowired
	private ProjectService projectService;

	@Autowired
	private HttpServletRequest request;

	@Autowired
	private HttpSession session;

	@Before("@within(org.springframework.stereotype.Controller)")
	public void addSessionAttributes() {

		String uri = request.getRequestURI();

		// ログインとログアウトと新規ユーザー登録のパスは除外
		if ("/login".equals(uri) || "/logout".equals(uri)
				|| "/user/signup".equals(uri) || "/user/signup-confirm".equals(uri)) {
			return;
		}

		// 現在のユーザー情報を取得
		ExtendedUser userDetails = applicationService.getCurrentUserDetails();
		Integer userId = userDetails.getUserId();

		// ユーザーの未処理の招待数を取得
		int pendingInvitations = projectService.countPendingInvitationsForUser(userId);

		// ユーザーの未読コメント通知数を取得
		int unreadComments = projectService.countUnreadCommentsForUser(userId);

		// ユーザーの未確認リアクション通知数を取得
		int unconfirmedReactions = projectService.countUnconfirmedReactionsForUser(userId);

		// 未処理の招待数、未読コメント通知数、未確認リアクション通知数を合計
		int totalNotifications = pendingInvitations + unreadComments + unconfirmedReactions;

		// セッションに通知の総数を保存
		session.setAttribute("totalNotifications", totalNotifications);

	}
}
