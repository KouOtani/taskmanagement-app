package katachi.spring.exercise.controller;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpServletRequest;
import katachi.spring.exercise.application.service.UserApplicationService;
import katachi.spring.exercise.domain.user.model.Project;
import katachi.spring.exercise.domain.user.model.ProjectMember;
import katachi.spring.exercise.domain.user.model.Tag;
import katachi.spring.exercise.domain.user.model.Task;
import katachi.spring.exercise.domain.user.model.Task.TaskPriority;
import katachi.spring.exercise.domain.user.model.Task.TaskStatus;
import katachi.spring.exercise.domain.user.service.ProjectService;
import katachi.spring.exercise.domain.user.service.UserService;
import katachi.spring.exercise.form.ProjectRegistrationForm;
import katachi.spring.exercise.form.TaskRegistrationForm;

@Controller
@RequestMapping("/update")
public class UpdateController {

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
		model.addAttribute("status", TaskStatus.values());
		model.addAttribute("priorities", TaskPriority.values());
		model.addAttribute("currentUri", request.getRequestURI());
		model.addAttribute("queryString", request.getQueryString());
	}

	@GetMapping("/personal-task/{taskId}")
	public String getPersonalTask(TaskRegistrationForm form,
			Model model,
			@PathVariable("taskId") Integer taskId,
			Locale locale) {

		//編集に該当するタスクを取得
		Task task = userService.getPersonalTaskOne(taskId);

		// タグのリストをカンマ区切りの文字列に変換
		String tagsString = task.getTagsList().stream()
				.map(Tag::getName) // Tagオブジェクトから名前を取得
				.collect(Collectors.joining(", ")); // カンマで区切って結合

		//taskをformに変換
		modelMapper.getConfiguration().setAmbiguityIgnored(true); //あいまいなマッピングを無視する
		form = modelMapper.map(task, TaskRegistrationForm.class);
		form.setProjectId(task.getProjectId());
		form.setName(tagsString);

		model.addAttribute("taskRegistrationForm", form);

		//タスクの編集画面を表示
		return "user/personal-task-update";
	}

	/*個人タスク更新処理*/
	@PostMapping("/personal-task")
	public String updateTask(Model model, Locale locale,
			@ModelAttribute @Validated TaskRegistrationForm form,
			BindingResult bindingResult,
			RedirectAttributes redirectAttributes) {

		// 入力チェック結果
		if (bindingResult.hasErrors()) {
			// NG: タスク更新画面に戻る
			return "user/personal-task-update";
		}

		// formをTaskクラスに変換
		Task task = modelMapper.map(form, Task.class);

		// タスク情報を更新
		userService.updateTask(task);

		// タグの関連付けを更新
		updateTaskTags(task, form.getName());

		//利用していないタグをテーブルから削除
		userService.removeUnusedTags();

		redirectAttributes.addFlashAttribute("message", "タスクを更新しました。");

		// ホーム画面にリダイレクト
		return "redirect:/personal-task";
	}

	/**
	 * タスクとタグの関連付けを更新するメソッド
	 *
	 * @param task 対象タスク
	 * @param newTagNames 新しいタグ名（カンマ区切り）
	 */
	private void updateTaskTags(Task task, String newTagNames) {
		// 現在のタグ関連付けを削除
		userService.removeTaskTags(task.getId());

		// 新しいタグの関連付けを処理
		applicationService.processTags(newTagNames, task);
	}

	@PostMapping("/completed/{taskId}")
	public String completedTask(@PathVariable("taskId") Integer taskId,
			@RequestParam(required = false) String currentUri,
			RedirectAttributes redirectAttributes) {

		userService.markTaskAsCompleted(taskId);
		redirectAttributes.addFlashAttribute("message", "'完了済みタスク'に移動しました。");

		return ("/personal-task".equals(currentUri)) ? "redirect:/personal-task" : "redirect:/home";

	}

	@PostMapping("/undo/{taskId}")
	public String undoTask(@PathVariable("taskId") Integer taskId,
			RedirectAttributes redirectAttributes) {
		// タスクIDに基づいてタスクのステータスを「進行中」に戻す
		userService.markAsInProgress(taskId);
		redirectAttributes.addFlashAttribute("message", "'未完了タスク'に戻しました。");

		return "redirect:/personal-task"; // 完了済みタスクページから進行中タスクページにリダイレクト
	}

	@GetMapping("/project/{projectId}")
	public String getProject(ProjectRegistrationForm form,
			Model model,
			@PathVariable("projectId") Integer projectId,
			Locale locale) {

		//編集に該当するタスクを取得
		Project project = projectService.getProjectForEditingById(projectId);

		// タグのリストをカンマ区切りの文字列に変換
		String tagsString = project.getTagsList().stream()
				.map(Tag::getName) // Tagオブジェクトから名前を取得
				.collect(Collectors.joining(", ")); // カンマで区切って結合

		//taskをformに変換
		form = modelMapper.map(project, ProjectRegistrationForm.class);
		form.setName(tagsString);

		model.addAttribute("projectRegistrationForm", form);

		//タスクの編集画面を表示
		return "project/project-update";
	}

	/*プロジェクト更新処理*/
	@PostMapping("/project")
	public String updateProject(Model model, Locale locale,
			@ModelAttribute @Validated ProjectRegistrationForm form,
			BindingResult bindingResult,
			RedirectAttributes redirectAttributes) {

		// 入力チェック結果
		if (bindingResult.hasErrors()) {
			// NG: タスク更新画面に戻る
			return "project/project-update";
		}

		// formをTaskクラスに変換
		Project project = modelMapper.map(form, Project.class);

		// タスク情報を更新
		projectService.updateProject(project);

		// タグの関連付けを更新
		updateProjectTags(project, form.getName());

		//利用していないタグをテーブルから削除
		userService.removeUnusedTags();

		redirectAttributes.addFlashAttribute("message", "プロジェクトを更新しました。");

		// ホーム画面にリダイレクト
		return "redirect:/project";
	}

	/**
	 * プロジェクトとタグの関連付けを更新するメソッド
	 *
	 * @param project 対象タスク
	 * @param newTagNames 新しいタグ名（カンマ区切り）
	 */
	private void updateProjectTags(Project project, String newTagNames) {
		// 現在のタグ関連付けを削除
		projectService.removeProjectTags(project.getId());

		// 新しいタグの関連付けを処理
		applicationService.processTags(newTagNames, project);
	}

	/*プロジェクトタスク更新画面を表示*/
	@GetMapping("/project-task/{taskId}")
	public String getProjectTask(TaskRegistrationForm form,
			Model model,
			@PathVariable("taskId") Integer taskId,
			Locale locale) {

		//編集に該当するタスクを取得
		Task task = projectService.getProjectTaskOneByTaskId(taskId);

		//taskをformに変換
		modelMapper.getConfiguration().setAmbiguityIgnored(true); //あいまいなマッピングを無視する
		form = modelMapper.map(task, TaskRegistrationForm.class);
		form.setProjectId(task.getProjectId());
		form.setAssigneeId(task.getUser().getId());

		// プロジェクトに属するメンバーを取得
		List<ProjectMember> projectMembers = projectService.getProjectMembers(task.getProjectId());

		// プロジェクトメンバーをモデルに追加
		model.addAttribute("projectMembers", projectMembers);

		model.addAttribute("taskRegistrationForm", form);

		//タスクの編集画面を表示
		return "project/project-task-update";
	}

	/*プロジェクトタスク更新処理*/
	@PostMapping("/project-task")
	public String updateProjectTask(Model model, Locale locale,
			@ModelAttribute @Validated TaskRegistrationForm form,
			BindingResult bindingResult,
			RedirectAttributes redirectAttributes) {

		// 入力チェック結果
		if (bindingResult.hasErrors()) {
			// NG: プロジェクトタスク更新画面に戻る
			return "project/project-task-update";
		}

		// formをTaskクラスに変換
		Task task = modelMapper.map(form, Task.class);

		// プロジェクトタスク情報を更新
		projectService.updateTask(task, form.getAssigneeId());

		redirectAttributes.addFlashAttribute("message", "プロジェクトタスクを更新しました。");

		// プロジェクトタスク画面にリダイレクト
		return "redirect:/project-task/" + form.getProjectId();

	}

	@PostMapping("/completed/project-task/{taskId}")
	public String completedProjectTask(@PathVariable("taskId") Integer taskId,
			@RequestParam(required = false) String currentUri,
			@RequestParam(required = false) Integer projectId,
			RedirectAttributes redirectAttributes) {

		userService.markTaskAsCompleted(taskId);
		redirectAttributes.addFlashAttribute("message", "'完了済みタスク'に移動しました。");

		return (("/project-task/" + projectId).equals(currentUri)) ? "redirect:/project-task/" + projectId : "redirect:/home";

	}

	@PostMapping("/undo/project-task/{taskId}")
	public String undoProjectTask(@PathVariable("taskId") Integer taskId,
			@RequestParam(required = false) Integer projectId,
			RedirectAttributes redirectAttributes) {
		// タスクIDに基づいてタスクのステータスを「進行中」に戻す
		userService.markAsInProgress(taskId);
		redirectAttributes.addFlashAttribute("message", "'未完了タスク'に戻しました。");

		return "redirect:/project-task/" + projectId; // 完了済みタスクページから進行中タスクページにリダイレクト
	}

}
