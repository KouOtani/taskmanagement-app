package katachi.spring.exercise.domain.user.service;

import java.util.List;

import katachi.spring.exercise.domain.user.model.Items;
import katachi.spring.exercise.domain.user.model.MUser;

public interface UserService {

    /*ログインユーザー情報取得*/
    public MUser getLoginUser(String userId);

    /*作業項目登録*/
    public void itemsEntry(Items item);
    /*担当者取得*/
    public List<MUser> getManager();

    /*作業項目取得*/
    public List<Items> getItems(String items);

    /*作業項目取得(1件)*/
    public Items getItemOne(Integer id);

    /*作業項目更新(1件)*/
    public void updateItem(Items item);

    /*作業項目削除(1件)*/
    public void deleteItem(Integer id);

    /*作業項目完了（１件）*/
    public void completeItemNow(Integer id);

}
