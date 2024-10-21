package katachi.spring.exercise.controller;

import java.time.LocalDate;
import java.util.List;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpServletRequest;
import katachi.spring.exercise.application.service.UserApplicationService;
import katachi.spring.exercise.domain.user.model.ProjectMember;
import katachi.spring.exercise.domain.user.model.Task;
import katachi.spring.exercise.domain.user.model.Task.TaskPriority;
import katachi.spring.exercise.domain.user.model.Task.TaskStatus;
import katachi.spring.exercise.domain.user.service.ProjectService;
import katachi.spring.exercise.domain.user.service.UserService;
import katachi.spring.exercise.form.TaskRegistrationForm;

@Controller
@RequestMapping("/entry")
public class ProjectTaskRegistrationController {

	@Autowired
	private UserService userService;

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

	//プロジェクトタスク登録画面を表示
	@GetMapping("/project-task/{projectId}")
	public String showProjectTaskRegisterForm(@PathVariable("projectId") Integer projectId,
			Model model,
			Locale locale,
			@ModelAttribute TaskRegistrationForm form) {

		model.addAttribute("status", TaskStatus.values());
		model.addAttribute("priorities", TaskPriority.values());

		// プロジェクトに属するメンバーを取得
		List<ProjectMember> projectMembers = projectService.getProjectMembers(projectId);

		// プロジェクトメンバーをモデルに追加
		model.addAttribute("projectMembers", projectMembers);

		//プロジェクトIDをモデルに追加
		model.addAttribute("projectId", projectId);

		//個人タスク登録画面を表示
		return "project/project-task-registration";
	}

	/*プロジェクトタスク登録処理*/
	@Transactional
	@PostMapping("/project-task")
	public String postProjectTask(Model model, Locale locale,
			@ModelAttribute @Validated TaskRegistrationForm form,
			BindingResult bindingResult,
			RedirectAttributes redirectAttributes) {

		// 入力チェック結果
		if (bindingResult.hasErrors()) {
			// NG: プロジェクトタスク登録画面に戻る
			System.out.println("エラーです");
			return showProjectTaskRegisterForm(form.getProjectId(), model, locale, form);
		}

		// formをTaskクラスに変換し、登録日を設定
		Task task = modelMapper.map(form, Task.class);
		task.setRegistrationDate(LocalDate.now());

		// タスクをDBに登録
		userService.tasksEntry(task);

		// タスクをAssignedToテーブルに登録
		applicationService.saveAssignedTo(task, form.getAssigneeId());

		redirectAttributes.addFlashAttribute("message", "タスクを登録しました。");

		// プロジェクトのタスク画面にリダイレクト
		return "redirect:/project-task/" + form.getProjectId();
	}

}
