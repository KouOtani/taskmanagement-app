package katachi.spring.exercise.controller;

import java.time.LocalDate;
import java.util.Locale;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpServletRequest;
import katachi.spring.exercise.application.service.UserApplicationService;
import katachi.spring.exercise.domain.user.model.ExtendedUser;
import katachi.spring.exercise.domain.user.model.Project;
import katachi.spring.exercise.domain.user.model.ProjectMember;
import katachi.spring.exercise.domain.user.model.Task.TaskStatus;
import katachi.spring.exercise.domain.user.service.ProjectService;
import katachi.spring.exercise.form.ProjectRegistrationForm;

@Controller
@RequestMapping("/entry")
public class ProjectRegistrationController {

	@Autowired
	private ProjectService projectService;

	@Autowired
	private UserApplicationService applicationService;

	@Autowired
	private ModelMapper modelMapper;

	@ModelAttribute
	public void addAttributes(HttpServletRequest request, Model model) {
		model.addAttribute("currentUri", request.getRequestURI());
		model.addAttribute("queryString", request.getQueryString());
	}

	//作業登録画面を表示
	@GetMapping("/project")
	public String getProjectEntry(Model model,
			Locale locale,
			@ModelAttribute ProjectRegistrationForm form) {

		model.addAttribute("status", TaskStatus.values());

		//プロジェクト登録画面を表示
		return "project/project-registration";
	}

	/* プロジェクト登録処理 */
	@Transactional
	@PostMapping("/project")
	public String postProjectEntry(Model model, Locale locale,
			@ModelAttribute @Validated ProjectRegistrationForm form,
			BindingResult bindingResult,
			RedirectAttributes redirectAttributes) {

		// 入力チェック結果
		if (bindingResult.hasErrors()) {
			// NG: 作業登録画面に戻る
			return getProjectEntry(model, locale, form);
		}

		// 現在のユーザー情報を取得
		ExtendedUser userDetails = applicationService.getCurrentUserDetails();

		// formをProjectクラスに変換し、登録日、リーダー、進行度を設定
		Project project = modelMapper.map(form, Project.class);
		project.setRegistrationDate(LocalDate.now());
		project.setLeaderId(userDetails.getUserId());

		// プロジェクトをDBに登録
		projectService.projectEntry(project);

		// リーダーをProjectMemberテーブルに登録
		ProjectMember projectMember = new ProjectMember();
		projectMember.setProjectId(project.getId()); // 作成されたプロジェクトのID
		projectMember.setUserId(userDetails.getUserId()); // リーダー（現在のユーザー）のID
		projectMember.setRole("リーダー"); // リーダーの役割を設定

		// ProjectMemberテーブルにリーダーを保存
		projectService.saveProjectMember(projectMember);

		// タグの処理
		applicationService.processTags(form.getName(), project);

		// メッセージを設定
		redirectAttributes.addFlashAttribute("message", "プロジェクトを登録しました。");

		// ホーム画面にリダイレクト
		return "redirect:/project";
	}

}
