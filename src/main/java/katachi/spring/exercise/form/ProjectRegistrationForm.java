package katachi.spring.exercise.form;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import katachi.spring.exercise.domain.user.model.Project.ProjectStatus;
import lombok.Data;

@Data
public class ProjectRegistrationForm {

	private Integer id;

	@NotBlank
	@Size(max = 100)
	private String title; // プロジェクトのタイトル

	private String description; // プロジェクトの説明

	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private LocalDate dueDate; // プロジェクトの期限日

	@NotNull
	private ProjectStatus status; // プロジェクトの優先度

	private String name; // プロジェクトのタグ

}
