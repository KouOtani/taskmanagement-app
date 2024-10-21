package katachi.spring.exercise.form;

import java.util.Date;

import org.hibernate.validator.constraints.Length;
import org.springframework.format.annotation.DateTimeFormat;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class EntryForm {

    @NotBlank
    @Length(max = 100)
    private String itemName;	//項目名

    private Integer userId;		//担当者

    @NotNull
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date expireDate;	//期限日

    private Boolean isFinished;	//完了のチェック
}
