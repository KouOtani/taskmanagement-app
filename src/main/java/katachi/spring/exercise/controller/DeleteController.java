package katachi.spring.exercise.controller;

import java.util.Locale;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpServletRequest;
import katachi.spring.exercise.domain.user.model.Project;
import katachi.spring.exercise.domain.user.model.Tag;
import katachi.spring.exercise.domain.user.model.Task;
import katachi.spring.exercise.domain.user.model.Task.TaskPriority;
import katachi.spring.exercise.domain.user.model.Task.TaskStatus;
import katachi.spring.exercise.domain.user.service.ProjectService;
import katachi.spring.exercise.domain.user.service.UserService;
import katachi.spring.exercise.form.ProjectRegistrationForm;
import katachi.spring.exercise.form.TaskRegistrationForm;

@Controller
@RequestMapping("/delete")
public class DeleteController {

	@Autowired
	private UserService usesrService;

	@Autowired
	private ProjectService projectService;

	@Autowired
	private ModelMapper modelMapper;

	@ModelAttribute
	public void addAttributes(HttpServletRequest request, Model model) {
		model.addAttribute("status", TaskStatus.values());
		model.addAttribute("priorities", TaskPriority.values());
		model.addAttribute("currentUri", request.getRequestURI());
		model.addAttribute("queryString", request.getQueryString());
	}

	/*個人タスク削除画面を表示*/
	@GetMapping("/personal-task/{id}")
	public String getPersonalTask(TaskRegistrationForm form,
			Model model,
			@PathVariable("id") Integer taskId,
			Locale locale) {

		//編集に該当するタスクを取得
		Task task = usesrService.getPersonalTaskOne(taskId);

		// タグのリストをカンマ区切りの文字列に変換
		String tagsString = task.getTagsList().stream()
				.map(Tag::getName) // Tagオブジェクトから名前を取得
				.collect(Collectors.joining(", ")); // カンマで区切って結合

		//taskをformに変換
		modelMapper.getConfiguration().setAmbiguityIgnored(true); //あいまいなマッピングを無視する
		form = modelMapper.map(task, TaskRegistrationForm.class);
		form.setName(tagsString);

		model.addAttribute("taskRegistrationForm", form);

		//タスクの編集画面を表示
		return "user/personal-task-delete";
	}

	/*個人タスク削除処理*/
	@PostMapping("/personal-task")
	public String deletePersonalTask(TaskRegistrationForm form,
			RedirectAttributes redirectAttributes) {

		//個人タスクを削除
		usesrService.deletePersonalTask(form.getId());

		redirectAttributes.addFlashAttribute("message", "タスクを削除しました。");

		return "redirect:/home";
	}

	/*プロジェクト削除画面を表示*/
	@GetMapping("/project/{id}")
	public String getProject(ProjectRegistrationForm form,
			Model model,
			@PathVariable("id") Integer projectId,
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
		return "project/project-delete";
	}

	/*プロジェクト削除処理*/
	@PostMapping("/project")
	public String deleteProject(ProjectRegistrationForm form,
			RedirectAttributes redirectAttributes) {

		//個人タスクを削除
		projectService.deleteProject(form.getId());

		redirectAttributes.addFlashAttribute("message", "タスクを削除しました。");

		return "redirect:/home";
	}

	/*プロジェクトタスク削除画面を表示*/
	@GetMapping("/project-task/{taskId}")
	public String getProjectTask(TaskRegistrationForm form,
			Model model,
			@PathVariable("taskId") Integer taskId,
			Locale locale) {

		//削除に該当するタスクを取得
		Task task = projectService.getProjectTaskOneByTaskId(taskId);

		//taskをformに変換
		modelMapper.getConfiguration().setAmbiguityIgnored(true); //あいまいなマッピングを無視する
		form = modelMapper.map(task, TaskRegistrationForm.class);
		form.setProjectId(task.getProjectId());

		model.addAttribute("taskRegistrationForm", form);

		//タスクの編集画面を表示
		return "project/project-task-delete";
	}

	/*プロジェクトタスク削除処理*/
	@PostMapping("/project-task")
	public String deleteProjectTask(TaskRegistrationForm form,
			RedirectAttributes redirectAttributes) {

		//プロジェクトタスクを削除
		usesrService.deletePersonalTask(form.getId());

		redirectAttributes.addFlashAttribute("message", "タスクを削除しました。");

		// プロジェクトタスク画面にリダイレクト
		return "redirect:/project-task/" + form.getProjectId();
	}
}
