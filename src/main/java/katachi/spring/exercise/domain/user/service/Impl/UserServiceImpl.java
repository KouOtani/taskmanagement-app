package katachi.spring.exercise.domain.user.service.Impl;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import katachi.spring.exercise.domain.user.model.Items;
import katachi.spring.exercise.domain.user.model.MUser;
import katachi.spring.exercise.domain.user.service.UserService;
import katachi.spring.exercise.repository.UserMapper;

@Service
public class UserServiceImpl implements UserService {

	@Autowired
	private UserMapper mapper;

	/*ログインユーザー情報取得*/
	@Override
	public MUser getLoginUser(String userId) {
		return mapper.findLoginUser(userId);
	}

	/*作業項目登録*/
	@Override
	public void itemsEntry(Items item) {
		mapper.insertItems(item);
	}

	/*担当者取得*/
	@Override
	public List<MUser> getManager() {
		return mapper.getUsers();
	}

	/*作業項目取得*/
	@Override
	public List<Items> getItems(String items) {
		return mapper.findMany(items);
	}

	/*作業項目取得(1件)*/
	@Override
	public Items getItemOne(Integer id) {
		return mapper.findOne(id);
	}

	/*作業項目更新(1件)*/
	@Override
	public void updateItem(Items item) {
		mapper.updateItem(item);
	}

	/*作業項目削除(1件)*/
	@Override
	public void deleteItem(Integer id) {
		mapper.deleteItem(id);
	}

	/*作業項目完了（１件）*/
	@Override
	public void completeItemNow(Integer id) {
		mapper.completeItem(id, new Date());
	}

}
