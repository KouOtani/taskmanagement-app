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
import jakarta.servlet.http.HttpSession;
import katachi.spring.exercise.application.service.UserApplicationService;
import katachi.spring.exercise.domain.user.model.Project;
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

	@Autowired
	private HttpSession session;

	@ModelAttribute
	public void addAttributes(HttpServletRequest request, Model model) {
		model.addAttribute("currentUri", request.getRequestURI());
		model.addAttribute("queryString", request.getQueryString());
		model.addAttribute("statusList", TaskStatus.values());
		model.addAttribute("priorities", TaskPriority.values());

		System.out.println(request.getRequestURI());

	}

	/* プロジェクトタスクリストを表示 */
	@GetMapping("/{projectId}")
	public String showProjectTaskPage(@PathVariable("projectId") Integer projectId,
			@RequestParam(required = false) String searchQuery,
			@RequestParam(required = false) Boolean completed,
			@RequestParam(required = false) Integer memberId,
			@RequestParam(required = false) TaskStatus status,
			@RequestParam(required = false) TaskPriority priority,
			@RequestParam(required = false) String dueDate,
			@RequestParam(name = "page", defaultValue = "1") Integer page,
			@RequestParam(name = "size", defaultValue = "10") Integer size,
			Model model) {

		// 現在のユーザー情報を取得
		Integer currentUserId = applicationService.getCurrentUserDetails().getUserId();

		// 今日期日の個人タスクを取得
		List<Task> personalTodayDueTasks = userService.getPersonalDueTodayTasks(currentUserId);
		model.addAttribute("personalTodayDueTasks", personalTodayDueTasks);

		// 今日期日のプロジェクトタスクを取得
		List<Task> projectTodayDueTasks = projectService.getProjectDueTodayTasks(currentUserId);
		model.addAttribute("projectTodayDueTasks", projectTodayDueTasks);

		// 所属しているプロジェクトを取得する
		List<Project> myProjects = projectService.getAllProjectsAndProgress(currentUserId);
		model.addAttribute("myProjects", myProjects);

		// プロジェクトIDに関連するタスクを取得（フィルタリングなども行う）
		List<Task> projectTasksList = projectService.getProjectTasks(projectId,
				searchQuery,
				completed,
				memberId,
				status,
				priority,
				dueDate,
				page,
				size);

		// タスクの総数を取得し、総ページ数を計算
		int totalTasks = projectService.countProjectTasksByUserId(projectId, memberId, searchQuery, completed, status, priority);
		int totalPages = Math.max(1, (int) Math.ceil((double) totalTasks / size));

		// プロジェクトに属するメンバーを取得
		List<ProjectMember> projectMembers = projectService.getProjectMembers(projectId);

		// projectIdからプロジェクトの情報を取得
		Project project = projectService.getProjectByProjectId(projectId);

		// プロジェクトに基づいたプロジェクトタスクの通知を削除
		projectService.clearProjectTaskNotifications(currentUserId, projectId);

		// ユーザーの未処理の招待数を取得
		int pendingInvitations = projectService.countPendingInvitationsForUser(currentUserId);

		// ユーザーの未読コメント通知数を取得
		int unreadComments = projectService.countUnreadCommentsForUser(currentUserId);

		// ユーザーの未確認リアクション通知数を取得
		int unconfirmedReactions = projectService.countUnconfirmedReactionsForUser(currentUserId);

		// ユーザーの未確認プロジェクトタスク通知数を取得
		int unconfirmedProjectTasks = projectService.countUnconfirmedProjectTasksForUser(currentUserId);

		// 未処理の招待数、未読コメント通知数、未確認リアクション通知数、未確認プロジェクトタスク通知数を合計
		int totalNotifications = pendingInvitations + unreadComments + unconfirmedReactions + unconfirmedProjectTasks;

		// セッションに通知の総数を保存
		session.setAttribute("totalNotifications", totalNotifications);

		// Modelに登録
		model.addAttribute("projectTasksList", projectTasksList);
		model.addAttribute("countIncompleteProjectTasks", totalTasks);
		model.addAttribute("projectMembers", projectMembers); // プロジェクトメンバーをモデルに追加
		model.addAttribute("project", project); // プロジェクト情報をモデルに追加
		model.addAttribute("projectId", projectId); // プロジェクトIDをモデルに追加
		model.addAttribute("completed", completed);
		model.addAttribute("searchQuery", searchQuery);
		model.addAttribute("memberId", memberId);
		model.addAttribute("status", status);
		model.addAttribute("priority", priority);
		model.addAttribute("dueDate", dueDate);
		model.addAttribute("currentDate", LocalDate.now());
		model.addAttribute("oneWeekFromNow", LocalDate.now().plusWeeks(1));
		model.addAttribute("currentUserId", currentUserId);
		model.addAttribute("page", page);
		model.addAttribute("size", size);
		model.addAttribute("totalPages", totalPages);

		// プロジェクト内タスクリストを表示
		return "project/project-task-list";
	}

}
