package katachi.spring.exercise.controller;

import java.util.ArrayList;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import katachi.spring.exercise.domain.user.model.Items;
import katachi.spring.exercise.domain.user.model.MUser;
import katachi.spring.exercise.domain.user.service.UserService;
import katachi.spring.exercise.form.EntryForm;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping("/todo/entry")
@Slf4j
public class EntryController {

    @Autowired
    private UserService userService;

    @Autowired
    private ModelMapper modelMapper;

    //作業登録画面を表示
    @GetMapping("")
    public String getEntry(Model model,
            Locale locale,
            @ModelAttribute EntryForm form) {

        //担当者を取得
        List<MUser> managerList = new ArrayList<>();
        managerList = userService.getManager();

        model.addAttribute("managerList", managerList);

        //作業登録画面を表示
        return "todo/entry";
    }

    /*作業登録処理*/
    @PostMapping("")
    public String postEntry(Model model, Locale locale,
            @ModelAttribute @Validated EntryForm form,
            BindingResult bindingResult) {

        //入力チェック結果
        if (bindingResult.hasErrors()) {
            //NG:作業登録画面に戻る
            return getEntry(model, locale, form);
        }

        log.info(form.toString());

        //formをItemsクラスに変換
        Items item = modelMapper.map(form, Items.class);

        item.setRegistrationDate(new Date());

        if (form.getIsFinished()) {
            item.setFinishedDate(new Date());
        }

        // 登録内容をDBに登録
        userService.itemsEntry(item);

        //作業一覧画面にリダイレクト
        return "redirect:/todo/list";
    }
}
