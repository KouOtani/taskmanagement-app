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
import katachi.spring.exercise.domain.user.model.Task;
import katachi.spring.exercise.domain.user.model.Task.TaskPriority;
import katachi.spring.exercise.domain.user.model.Task.TaskStatus;
import katachi.spring.exercise.domain.user.service.UserService;
import katachi.spring.exercise.form.TaskRegistrationForm;

@Controller
@RequestMapping("/entry")
public class PersonalTaskRegistrationController {

	@Autowired
	private UserService service;

	@Autowired
	private UserApplicationService applicationService;

	@Autowired
	private ModelMapper modelMapper;

	@ModelAttribute
	public void addAttributes(HttpServletRequest request, Model model) {
		model.addAttribute("currentUri", request.getRequestURI());
		model.addAttribute("queryString", request.getQueryString());
	}

	//個人タスク登録画面を表示
	@GetMapping("/personal-task")
	public String getPersonalEntry(Model model,
			Locale locale,
			@ModelAttribute TaskRegistrationForm form) {

		model.addAttribute("status", TaskStatus.values());
		model.addAttribute("priorities", TaskPriority.values());

		//個人タスク登録画面を表示
		return "user/personal-task-registration";
	}

	/*個人タスク登録処理*/
	@Transactional
	@PostMapping("/personal-task")
	public String postEntry(Model model, Locale locale,
			@ModelAttribute @Validated TaskRegistrationForm form,
			BindingResult bindingResult,
			RedirectAttributes redirectAttributes) {

		// 入力チェック結果
		if (bindingResult.hasErrors()) {
			// NG: 作業登録画面に戻る
			return getPersonalEntry(model, locale, form);
		}

		// formをTaskクラスに変換し、登録日を設定
		Task task = modelMapper.map(form, Task.class);
		task.setRegistrationDate(LocalDate.now());

		// タスクをDBに登録
		service.tasksEntry(task);

		// タグの処理
		applicationService.processTags(form.getName(), task);

		// タスクをAssignedToテーブルに登録
		applicationService.saveAssignedTo(task, null);

		redirectAttributes.addFlashAttribute("message", "タスクを登録しました。");

		// ホーム画面にリダイレクト
		return "redirect:/personal-task";
	}

}
