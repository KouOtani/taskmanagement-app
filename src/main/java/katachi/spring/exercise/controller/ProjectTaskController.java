package katachi.spring.exercise.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpServletRequest;
import katachi.spring.exercise.application.service.UserApplicationService;
import katachi.spring.exercise.domain.user.model.ExtendedUser;
import katachi.spring.exercise.domain.user.model.ProjectMember;
import katachi.spring.exercise.domain.user.model.Task;
import katachi.spring.exercise.domain.user.model.Task.TaskPriority;
import katachi.spring.exercise.domain.user.model.Task.TaskStatus;
import katachi.spring.exercise.domain.user.service.ProjectService;
import katachi.spring.exercise.domain.user.service.UserService;

@Controller
@RequestMapping("/project-task")
public class ProjectTaskController {

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

	/* プロジェクトタスクリストを表示 */
	@GetMapping("/{projectId}")
	public String showProjectTaskPage(@PathVariable("projectId") Integer projectId,
			@RequestParam(required = false) String searchQuery,
			@RequestParam(required = false) Boolean completed,
			@RequestParam(required = false) Integer userId,
			@RequestParam(required = false) TaskStatus status,
			@RequestParam(required = false) TaskPriority priority,
			@RequestParam(required = false) String dueDate,
			Model model) {

		// 現在のユーザー情報を取得
		ExtendedUser userDetails = applicationService.getCurrentUserDetails();

		// 今日期日の個人タスクを取得
		List<Task> personalTodayDueTasks = userService.getPersonalDueTodayTasks(userDetails.getUserId());
		model.addAttribute("personalTodayDueTasks", personalTodayDueTasks);
		// 今日期日のプロジェクトタスクを取得
		List<Task> projectTodayDueTasks = projectService.getProjectDueTodayTasks(userDetails.getUserId());
		model.addAttribute("projectTodayDueTasks", projectTodayDueTasks);

		// プロジェクトIDに関連するタスクを取得（フィルタリングなども行う）
		List<Task> projectTasksList = projectService.getProjectTasks(projectId,
				searchQuery,
				completed,
				userId,
				status,
				priority,
				dueDate);

		// 未完了のプロジェクトタスクの個数を、projectTasksList の要素数に置き換え
		Integer countIncompleteProjectTasks = projectTasksList.size();

		// プロジェクトに属するメンバーを取得
		List<ProjectMember> projectMembers = projectService.getProjectMembers(projectId);

		// Modelに登録
		model.addAttribute("projectTasksList", projectTasksList);
		model.addAttribute("countIncompleteProjectTasks", countIncompleteProjectTasks);
		model.addAttribute("projectMembers", projectMembers); // プロジェクトメンバーをモデルに追加
		model.addAttribute("projectId", projectId); // プロジェクトIDをモデルに追加
		model.addAttribute("completed", completed);
		model.addAttribute("searchQuery", searchQuery);
		model.addAttribute("userId", userId);
		model.addAttribute("status", status);
		model.addAttribute("priority", priority);
		model.addAttribute("dueDate", dueDate);
		model.addAttribute("currentDate", LocalDate.now());
		model.addAttribute("oneWeekFromNow", LocalDate.now().plusWeeks(1));

		// プロジェクト内タスクリストを表示
		return "project/project-task-list";
	}

}
