package katachi.spring.exercise.controller;

import java.util.Locale;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import katachi.spring.exercise.domain.user.model.MUser;
import katachi.spring.exercise.domain.user.service.UserService;
import katachi.spring.exercise.form.GroupOrder;
import katachi.spring.exercise.form.SignupForm;

/**
 * ユーザーおよびゲストのサインアップに関連するリクエストを処理するコントローラークラスです。
 */
@Controller
@RequestMapping("/user")
public class SignupController {

	@Autowired
	private UserService service;

	@Autowired
	private ModelMapper modelMapper;

	/**
	 * ユーザー登録画面を表示します。
	 *
	 * @param model ビューに渡すためのモデル
	 * @param locale ロケール情報
	 * @param signupForm サインアップフォームのデータを保持するオブジェクト
	 * @return ユーザー登録画面のビュー名
	 */
	@GetMapping("/signup")
	public String showSignupForm(Model model,
			Locale locale,
			@ModelAttribute SignupForm signupForm) {

		// ユーザー登録画面を表示
		return "user/signup";
	}

	/**
	 * ユーザー登録フォームの入力チェックを行い、確認画面に遷移します。
	 *
	 * @param model ビューに渡すためのモデル
	 * @param locale ロケール情報
	 * @param signupForm サインアップフォームのデータを保持するオブジェクト
	 * @param bindingResult 入力チェック結果
	 * @return 入力チェックが成功した場合は確認画面のビュー名、失敗した場合はユーザー登録画面のビュー名
	 */
	@PostMapping("/signup-confirm")
	public String confirmSignup(Model model,
			Locale locale,
			@ModelAttribute @Validated(GroupOrder.class) SignupForm signupForm,
			BindingResult bindingResult) {
		// 入力チェック結果を確認
		if (bindingResult.hasErrors()) {
			// NG:ユーザー登録画面に戻る
			return showSignupForm(model, locale, signupForm);
		}

		// パスワードと確認用パスワードの一致を確認
		if (!signupForm.getPassword().equals(signupForm.getRePassword())) {
			bindingResult.rejectValue("password", "error.signupForm", "確認用のパスワードと一致しません。");
			return showSignupForm(model, locale, signupForm);
		}

		// メールアドレスの重複を確認
		if (service.isEmailRegistered(signupForm.getEmail())) {
			bindingResult.rejectValue("user", "error.signupForm", "このメールアドレスは既に登録されています。");
			return showSignupForm(model, locale, signupForm);
		}

		// 確認画面に遷移
		return "user/signup-confirm";
	}

	/**
	 * ユーザーを登録し、ログイン画面にリダイレクトします。
	 *
	 * @param model ビューに渡すためのモデル
	 * @param locale ロケール情報
	 * @param signupForm サインアップフォームのデータを保持するオブジェクト
	 * @param redirectAttributes リダイレクト先にデータを渡すためのオブジェクト
	 * @return ログイン画面へのリダイレクトURL
	 */
	@PostMapping("/signup")
	public String processSignup(Model model,
			Locale locale,
			@ModelAttribute SignupForm signupForm,
			RedirectAttributes redirectAttributes) {
		// サインアップフォームをMUserクラスにマッピング
		MUser user = modelMapper.map(signupForm, MUser.class);

		// ユーザーを登録
		service.registerUser(user);

		redirectAttributes.addFlashAttribute("message", "ユーザーを登録しました。");

		// ログイン画面にリダイレクト
		return "redirect:/login";
	}

}
