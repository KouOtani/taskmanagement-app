package katachi.spring.exercise.domain.user.model;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import lombok.Data;

@Data
public class Comment {

	private Integer id; // コメントの一意な識別子
	private Integer projectId; //（外部キー、Project テーブルへの参照） - コメント対象のタスクのID。
	private Integer userId; //（外部キー、User テーブルへの参照） - コメントを投稿したユーザーのID。
	private String content; //コメントの内容。
	private Date createdAt; //コメント投稿日時
	private Date updatedAt; //コメント更新日時

	private MUser user;
	private Integer reactorCount; // リアクションしたユーザーの合計数
	private boolean hasReacted; // 現在のユーザーがリアクションしているかどうか
	private List<CommentReaction> reactors; //コメントにリアクションをしたユーザーのリスト

	// リアクションしたユーザーのフルネームを改行で取得
	public String getReactorsFullNames() {
		return reactors.stream()
				.map(r -> r.getFullName())
				.collect(Collectors.joining("\n"));
	}

}
