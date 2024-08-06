package katachi.spring.exercise.controller;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import katachi.spring.exercise.domain.user.model.Items;
import katachi.spring.exercise.domain.user.service.UserService;
import katachi.spring.exercise.form.SearchForm;

@Controller
@RequestMapping("/todo/list")
public class TodoListController {

    @Autowired
    private UserService userService;

    /*作業一覧画面を表示*/
    @GetMapping("")
    public String getTodoList(@ModelAttribute SearchForm form,
            Model model) {

        //今日の日付を取得
        Date today = new Date();
        model.addAttribute("today", today);

        //作業項目一覧とログインユーザー取得
        List<Items> itemList = userService.getItems(form.getSearchWord());

        //Modelに登録
        model.addAttribute("itemList", itemList);

        //作業一覧画面を表示
        return "todo/list";
    }

}
