package katachi.spring.exercise.domain.user.model;

import java.util.Date;

import lombok.Data;

@Data
public class CommentAttachment {

	private Integer id; // コメントの一意な識別子
	private Integer commentId; // コメントに対応するID
	private String filePath; // ファイルが保存されているディレクトリとファイル名のパス
	private String fileName; // オリジナルのファイル名
	private Date uploadedAt; // アップロード日時

}
