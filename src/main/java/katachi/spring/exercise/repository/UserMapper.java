package katachi.spring.exercise.repository;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import katachi.spring.exercise.domain.user.model.Items;
import katachi.spring.exercise.domain.user.model.MUser;

@Mapper
public interface UserMapper {

	/*ログインユーザー取得*/
	public MUser findLoginUser(String userId);

	/*作業項目登録*/
	public int insertItems(Items item);

	/*usersテーブル取得*/
	public List<MUser> getUsers();

	/*作業項目一覧取得*/
	public List<Items> findMany(String items);

	/*作業項目１件取得*/
	public Items findOne(Integer id);

	/*作業項目更新（１件）*/
	public void updateItem(Items item);

	/*作業項目削除（１件）*/
	public int deleteItem(@Param("id") Integer id);

	/*作業項目完了（1件）*/
	public void completeItem(@Param("id") Integer id,
			@Param("finishedDate") Date finishedDate);

}
