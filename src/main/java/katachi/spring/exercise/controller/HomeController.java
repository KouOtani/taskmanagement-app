package katachi.spring.exercise.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import katachi.spring.exercise.application.service.UserApplicationService;
import katachi.spring.exercise.domain.user.model.ExtendedUser;
import katachi.spring.exercise.domain.user.model.Project;
import katachi.spring.exercise.domain.user.model.Task;
import katachi.spring.exercise.domain.user.service.ProjectService;
import katachi.spring.exercise.domain.user.service.UserService;

@Controller
@RequestMapping("/home")
public class HomeController {

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

	/* ホーム画面を表示 */
	@GetMapping
	public String showHomePage(Model model, HttpSession session) {

		// 現在のユーザー情報を取得
		ExtendedUser userDetails = applicationService.getCurrentUserDetails();

		session.setAttribute("userId", userDetails.getUserId());

		// 期日が過ぎた個人タスクを一覧で取得
		List<Task> personalPastDueTasks = userService.getPersonalPastDueTasks(userDetails.getUserId());

		// 今日期日の個人タスクを取得
		List<Task> personalTodayDueTasks = userService.getPersonalDueTodayTasks(userDetails.getUserId());

		//　期日一週間前の個人タスクを取得
		List<Task> personalOneWeekDueTasks = userService.getPersonalDueOneWeekTasks(userDetails.getUserId());

		// 期日が過ぎたプロジェクトタスクを一覧で取得
		List<Task> projectPastDueTasks = projectService.getProjectPastDueTasks(userDetails.getUserId());

		// 今日期日のプロジェクトタスクを取得
		List<Task> projectTodayDueTasks = projectService.getProjectDueTodayTasks(userDetails.getUserId());

		//　期日一週間前のプロジェクトタスクを取得
		List<Task> projectOneWeekDueTasks = projectService.getProjectDueOneWeekTasks(userDetails.getUserId());

		// 所属しているプロジェクトを取得する
		List<Project> myProjects = projectService.getAllProjectsAndProgress(userDetails.getUserId());

		// Modelに登録
		model.addAttribute("personalPastDueTasks", personalPastDueTasks);
		model.addAttribute("personalTodayDueTasks", personalTodayDueTasks);
		model.addAttribute("personalOneWeekDueTasks", personalOneWeekDueTasks);
		model.addAttribute("projectPastDueTasks", projectPastDueTasks);
		model.addAttribute("projectTodayDueTasks", projectTodayDueTasks);
		model.addAttribute("projectOneWeekDueTasks", projectOneWeekDueTasks);
		model.addAttribute("myProjects", myProjects);

		// ホーム画面を表示
		return "user/home";
	}

}
