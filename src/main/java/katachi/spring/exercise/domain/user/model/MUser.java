package katachi.spring.exercise.domain.user.model;


import java.util.Date;

import lombok.Data;

@Data
public class MUser {

    private int id;				//ユーザーID
    private String user;		//ログイン時のユーザー名(プログラム側で一意)
    private String pass;		//ログイン時のパスワード
    private String familyName;		//ログインユーザーの名字
    private String firstName;		//ログインユーザーの名前
    private Integer isAdmin;			//0:管理者権限なし　1:管理者権限あり
    private Integer isDeleted;			//0:未削除 1:削除済み
    private Date createDateTime;	//レコードの登録日時
    private Date updateDateTime;	//レコードの更新日時
    private Items items;

    public String getIsAdmin() {
        String isAdminStr = String.valueOf(isAdmin);
        return isAdminStr;
    }

}
