package katachi.spring.exercise.form;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import katachi.spring.exercise.domain.user.model.Task.TaskPriority;
import katachi.spring.exercise.domain.user.model.Task.TaskStatus;
import lombok.Data;

@Data
public class TaskRegistrationForm {

	private Integer id;

	@NotBlank
	@Size(max = 100)
	private String title; // タスクのタイトル

	private String description; // タスクの説明

	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private LocalDate dueDate; // 期限日

	@NotNull
	private TaskStatus status; // タスクの状態

	@NotNull
	private TaskPriority priority; // タスクの優先度

	private String name; //タスクのタグ

	private Integer assigneeId; // 担当者のID

	private Integer projectId; //プロジェクトのID

}
