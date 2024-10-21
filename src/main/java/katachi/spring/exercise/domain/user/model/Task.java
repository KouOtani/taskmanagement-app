package katachi.spring.exercise.domain.user.model;

import java.util.Date;

import lombok.Data;

@Data
public class Items {

    private Integer id;				//ID
    private Integer userId;			//ユーザーID
    private String itemName;		//項目名
    private Date registrationDate;	//登録日
    private Date expireDate;		//期限日
    private Date finishedDate;		//完了日 NULLのとき、未完了とする。
    private Date createDateTime;	//レコードの登録日時
    private Date updateDateTime;	//レコードの更新日時

    private String fullName;		//familyName+firstName

}
