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
import katachi.spring.exercise.domain.user.model.ExtendedUser;
import katachi.spring.exercise.domain.user.model.Project;
import katachi.spring.exercise.domain.user.model.Project.ProjectStatus;
import katachi.spring.exercise.domain.user.model.Task;
import katachi.spring.exercise.domain.user.model.Task.TaskPriority;
import katachi.spring.exercise.domain.user.model.Task.TaskStatus;
import katachi.spring.exercise.domain.user.service.ProjectService;
import katachi.spring.exercise.domain.user.service.UserService;

@Controller
@RequestMapping("/project")
public class ProjectController {

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

	/* プロジェクトリスト画面を表示 */
	@GetMapping
	public String showProjectPage(@RequestParam(required = false) String searchQuery,
			@RequestParam(required = false) Integer leaderId,
			@RequestParam(required = false) ProjectStatus status,
			@RequestParam(required = false) String dueDate, // "asc" or "desc"
			@RequestParam(required = false) String tagName, // タグフィルタ用
			Model model) {

		// 現在のユーザー情報を取得
		ExtendedUser userDetails = applicationService.getCurrentUserDetails();

		// 今日期日の個人タスクを取得 処理をまとめるように検討する
		List<Task> personalTodayDueTasks = userService.getPersonalDueTodayTasks(userDetails.getUserId());

		// 今日期日のプロジェクトタスクを取得
		List<Task> projectTodayDueTasks = projectService.getProjectDueTodayTasks(userDetails.getUserId());
		model.addAttribute("projectTodayDueTasks", projectTodayDueTasks);

		// データベースからプロジェクトのリストを取得して同時に進捗も取得
		List<Project> projectList = projectService.getAllProjectsAndProgressAndFiltering(userDetails.getUserId(),
				searchQuery,
				leaderId,
				status,
				dueDate,
				tagName);

		// ユーザーが所属しているプロジェクトのリーダーを重複なく取得
		List<Project> leadersList = projectService.getUniqueLeadersByUserId(userDetails.getUserId());

		// Modelに登録
		model.addAttribute("personalTodayDueTasks", personalTodayDueTasks);//処理をまとめるように検討する
		model.addAttribute("projectList", projectList);
		model.addAttribute("leadersList", leadersList);
		model.addAttribute("searchQuery", searchQuery);
		model.addAttribute("leaderId", leaderId);
		model.addAttribute("status", status);
		model.addAttribute("dueDate", dueDate);
		model.addAttribute("selectedTag", tagName); // 選択したタグを表示
		model.addAttribute("currentDate", LocalDate.now()); // 現在の日付を取得
		model.addAttribute("oneWeekFromNow", LocalDate.now().plusWeeks(1)); // // 一週間後の日付を計算
		model.addAttribute("userId", userDetails.getUserId());

		// 作業一覧画面を表示
		return "project/project-list";
	}

}
