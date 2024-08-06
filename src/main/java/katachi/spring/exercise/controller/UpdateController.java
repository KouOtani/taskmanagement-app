package katachi.spring.exercise.controller;

import java.util.Date;
import java.util.List;
import java.util.Locale;

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

import katachi.spring.exercise.domain.user.model.Items;
import katachi.spring.exercise.domain.user.model.MUser;
import katachi.spring.exercise.domain.user.service.UserService;
import katachi.spring.exercise.form.UpdateForm;
import katachi.spring.exercise.repository.UserMapper;

@Controller
@RequestMapping("/todo/update")
public class UpdateController {

	@Autowired
	private UserService userService;

	@Autowired
	private ModelMapper modelMapper;

	@Autowired
	private UserMapper mapper;

	@ModelAttribute("managerList")
	public List<MUser> getManager() {
		return mapper.getUsers();
	}

	@GetMapping("/{id}")
	public String getItem(UpdateForm form, Model model,
			@PathVariable("id") Integer id,
			Locale locale) {

		//作業項目とそのユーザーを1件取得
		Items item = userService.getItemOne(id);

		//Itemsをformに変換
		form = modelMapper.map(item, UpdateForm.class);

		if (item.getFinishedDate() != null) {
			form.setIsFinished(true);
		}

		//Modelに登録
		model.addAttribute("updateForm", form);

		//作業項目の修正画面を表示
		return "todo/update";
	}

	@GetMapping("/complete/{id}")
	public String completedItem(@PathVariable("id") Integer id) {

		userService.completeItemNow(id);

		return "redirect:/todo/list";

	}

	/*ユーザー更新処理*/
	@PostMapping("")
	public String updateItem(Model model,
			Locale locale,
			@ModelAttribute @Validated UpdateForm form,
			BindingResult bindingResult) {

		//入力チェック結果
		if (bindingResult.hasErrors()) {

			//NG:作業修正画面に戻る
			return "todo/update";
		}

		// 完了であれば、今日の日付
		Date finishedDate = null;
		if (form.getIsFinished()) {
			finishedDate = new Date();
		}

		//formをItemsクラスに変換
		Items item = modelMapper.map(form, Items.class);

		item.setFinishedDate(finishedDate);

		// //作業項目を１件更新
		userService.updateItem(item);

		return "redirect:/todo/list";
	}

}
