package katachi.spring.exercise.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpServletRequest;
import katachi.spring.exercise.application.service.UserApplicationService;
import katachi.spring.exercise.domain.user.model.Project;
import katachi.spring.exercise.domain.user.model.Tag;
import katachi.spring.exercise.domain.user.model.Task;
import katachi.spring.exercise.domain.user.model.Task.TaskPriority;
import katachi.spring.exercise.domain.user.model.Task.TaskStatus;
import katachi.spring.exercise.domain.user.service.ProjectService;
import katachi.spring.exercise.domain.user.service.UserService;

@Controller
@RequestMapping("/personal-task")
public class PersonalTaskController {

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
		model.addAttribute("statusList", TaskStatus.values());
		model.addAttribute("priorities", TaskPriority.values());

	}

	/* 個人タスクリストを表示 */
	@GetMapping
	public String showPersonalTaskPage(@RequestParam(required = false) String searchQuery,
			@RequestParam(required = false) Boolean completed,
			@RequestParam(required = false) TaskStatus status,
			@RequestParam(required = false) TaskPriority priority,
			@RequestParam(required = false) String dueDate, // "asc" or "desc"
			@RequestParam(value = "tag", required = false) Integer tagId, // name="tag" の値を取得
			@RequestParam(required = false) String tagName,
			@RequestParam(name = "page", defaultValue = "1") Integer page,
			@RequestParam(name = "size", defaultValue = "10") Integer size,
			Model model) {

		// 現在のユーザー情報を取得
		Integer userId = applicationService.getCurrentUserDetails().getUserId();

		// 今日期日の個人タスクを取得
		List<Task> personalTodayDueTasks = userService.getPersonalDueTodayTasks(userId);

		// 今日期日のプロジェクトタスクを取得
		List<Task> projectTodayDueTasks = projectService.getProjectDueTodayTasks(userId);

		// 所属しているプロジェクトを取得する
		List<Project> myProjects = projectService.getAllProjectsAndProgress(userId);

		// 個人タスクをフィルタリングして一覧で取得
		List<Task> personalTasksList = userService.getPersonalTasks(userId,
				searchQuery,
				completed,
				status,
				priority,
				dueDate,
				tagId,
				page,
				size);

		// タスクの総数を取得し、総ページ数を計算
		int totalTasks = userService.countTasksByUserId(userId, searchQuery, completed, status, priority, tagId);
		int totalPages = Math.max(1, (int) Math.ceil((double) totalTasks / size));

		// タグ一覧の取得（完了・未完了に応じて）
		List<Tag> tagsList = userService.getTagsForPersonalTasks(userId, completed);

		// Modelに登録
		model.addAttribute("personalTodayDueTasks", personalTodayDueTasks);
		model.addAttribute("personalTasksList", personalTasksList);
		model.addAttribute("countIncompleteTasks", totalTasks);
		model.addAttribute("projectTodayDueTasks", projectTodayDueTasks);
		model.addAttribute("myProjects", myProjects);
		model.addAttribute("completed", completed);
		model.addAttribute("searchQuery", searchQuery);
		model.addAttribute("status", status);
		model.addAttribute("priority", priority);
		model.addAttribute("dueDate", dueDate);
		model.addAttribute("selectedTag", tagName); // 選択したタグを表示
		model.addAttribute("currentDate", LocalDate.now()); // 現在の日付を取得
		model.addAttribute("oneWeekFromNow", LocalDate.now().plusWeeks(1)); // // 一週間後の日付を計算
		model.addAttribute("tagsList", tagsList);
		model.addAttribute("tagId", tagId);
		model.addAttribute("page", page);
		model.addAttribute("size", size);
		model.addAttribute("totalPages", totalPages);

		// 個人タスクリストを表示
		return "user/personal-task-list";
	}

}
