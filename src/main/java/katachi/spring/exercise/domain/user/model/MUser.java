package katachi.spring.exercise.domain.user.model;

import lombok.Data;

@Data
public class MUser {

	private Integer id; //ユーザーID
	private String email; //ログイン時のユーザー名(プログラム側で一意)
	private String password; //ログイン時のパスワード
	private String familyName; //ユーザーの名字
	private String firstName; //ユーザーの名前
	private String fullName; //ユーザーのフルネーム
	private Integer isAdmin; //0:管理者権限なし　1:管理者権限あり
	private Task task;

	public String getIsAdmin() {
		String isAdminStr = String.valueOf(isAdmin);
		return isAdminStr;
	}

	// フルネームを結合して返す
	public String getFullName() {
		return familyName + " " + firstName;
	}

}
