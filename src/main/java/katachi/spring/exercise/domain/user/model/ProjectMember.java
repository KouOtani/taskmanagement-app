package katachi.spring.exercise.domain.user.model;

import lombok.Data;

@Data
public class ProjectMember {

	private Integer id; //プロジェクトメンバーの一意な識別子
	private Integer projectId; //（外部キー、Project テーブルへの参照） - プロジェクトのID
	private Integer userId; //（外部キー、User テーブルへの参照） - プロジェクトに参加するユーザーのID
	private String role; //プロジェクト内での役割（例：リーダー、メンバー）
	private String memberName; // メンバー名（family_name + first_name）
	private String email; // メンバーのメールアドレス
}