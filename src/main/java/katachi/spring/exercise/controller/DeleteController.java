package katachi.spring.exercise.controller;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import katachi.spring.exercise.domain.user.model.Items;
import katachi.spring.exercise.domain.user.service.UserService;
import katachi.spring.exercise.form.UpdateForm;

@Controller
@RequestMapping("/todo/delete")
public class DeleteController {

    @Autowired
    private UserService userService;

    @Autowired
    private ModelMapper modelMapper;

    @GetMapping("/{id}")
    public String getItem(UpdateForm form,
            Model model,
            @PathVariable("id") Integer id) {

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
        return "todo/delete";
    }

    /*ユーザー削除処理*/
    @PostMapping("")
    public String deleteItem(UpdateForm form,
            Model model) {

        //ユーザー削除
        userService.deleteItem(form.getId());

        return "redirect:/todo/list";
    }
}
