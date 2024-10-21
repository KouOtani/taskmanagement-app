package katachi.spring.exercise.controller;

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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpServletRequest;
import katachi.spring.exercise.application.service.UserApplicationService;
import katachi.spring.exercise.domain.user.model.ExtendedUser;
import katachi.spring.exercise.domain.user.model.MUser;
import katachi.spring.exercise.domain.user.model.Project;
import katachi.spring.exercise.domain.user.model.ProjectMember;
import katachi.spring.exercise.domain.user.service.ProjectService;
import katachi.spring.exercise.domain.user.service.UserService;

@Controller
@RequestMapping("/project")
public class InvitationController {

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

	/* メンバー招待ページを表示 */
	@GetMapping("/invite/{projectId}")
	public String handleInviteRequest(@PathVariable("projectId") Integer projectId,
			@RequestParam(required = false) String email,
			Model model) {

		// 現在のユーザー情報を取得
		ExtendedUser userDetails = applicationService.getCurrentUserDetails();

		// プロジェクトとメンバーの情報を取得
		Project project = projectService.getProjectForInvitationById(projectId);
		List<ProjectMember> projectMembers = projectService.getMembersByProjectId(projectId);

		// email パラメータが提供されている場合はユーザー検索とバリデーション
		if (isValidEmailForInvitation(email, userDetails.getEmail(), projectId, model)) {
			MUser searchedUser = userService.getUserByEmail(email);
			model.addAttribute("searchedUser", searchedUser);

			if (searchedUser == null) {
				model.addAttribute("errorMessage", "該当のユーザーが見つかりません。");
			} else {
				handleUserInvitationChecks(email, projectId, model);
			}
		}

		// プロジェクトとメンバー情報と検索アドレスをモデルに追加
		model.addAttribute("email", email);
		model.addAttribute("project", project);
		model.addAttribute("projectMembers", projectMembers);

		// 招待ページを表示
		return "project/invite";
	}

	/**
	 * 招待に対して有効なメールアドレスかどうかを確認する
	 */
	private boolean isValidEmailForInvitation(String email, String currentUserEmail, Integer projectId, Model model) {
		if (email != null && !email.isEmpty()) {
			if (email.equals(currentUserEmail)) {
				model.addAttribute("errorMessage", "自分を招待することはできません。");
				return false;
			}
			return true;
		}
		return false;
	}

	/**
	 * 既にプロジェクトに参加しているか、招待が保留中かを確認し、必要に応じてエラーメッセージを追加
	 */
	private void handleUserInvitationChecks(String email, Integer projectId, Model model) {
		boolean isAlreadyMember = projectService.isMemberOfProject(projectId, email);
		if (isAlreadyMember) {
			model.addAttribute("errorMessage", "このユーザーはすでにプロジェクトに参加しています。");
		} else {
			boolean isAlreadyInvited = projectService.isUserAlreadyInvited(projectId, email);
			if (isAlreadyInvited) {
				model.addAttribute("errorMessage", "このユーザーにはすでに招待を送信しています。");
			}
		}
	}

	// メンバー招待
	@PostMapping("/invite/{projectId}/add")
	public String inviteUserToProject(@PathVariable("projectId") Integer projectId,
			@RequestParam Integer inviteeId, // 招待されるユーザーのID
			@RequestParam String email, // 招待されるユーザーのemail
			Model model,
			RedirectAttributes redirectAttributes) {

		// 現在のユーザー情報を取得
		ExtendedUser userDetails = applicationService.getCurrentUserDetails();
		Integer inviterId = userDetails.getUserId();

		// 招待を作成
		projectService.createInvitation(projectId, inviteeId, inviterId);

		MUser inviteeUser = userService.getUserByEmail(email);

		// フラッシュメッセージを設定
		redirectAttributes.addFlashAttribute("message", inviteeUser.getFullName() + "をプロジェクトに招待しました。");

		// リダイレクト
		return "redirect:/project/invite/" + projectId;
	}

}
