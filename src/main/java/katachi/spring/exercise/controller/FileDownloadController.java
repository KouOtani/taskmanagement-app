package katachi.spring.exercise.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import katachi.spring.exercise.domain.user.model.CommentAttachment;
import katachi.spring.exercise.domain.user.service.ProjectService;

@Controller
public class FileDownloadController {

	@Autowired
	private ProjectService projectService;

	@PostMapping("/download")
	public ResponseEntity<InputStreamResource> downloadFile(@RequestParam Integer attachmentId) throws IOException {

		// DBからファイル情報を取得
		CommentAttachment attachment = projectService.getCommentAttachmentById(attachmentId);

		if (attachment == null) {
			return ResponseEntity.notFound().build(); // ファイルが存在しない場合404エラーを返す
		}

		// 保存されているファイルパスを取得
		String uploadDir = "C:/pleiades/2023-09/workspace/SpringTaskManagement/uploads/comments";
		Path filePath = Paths.get(uploadDir, attachment.getFilePath());
		File file = filePath.toFile();

		if (!file.exists()) {
			return ResponseEntity.notFound().build(); // ファイルが存在しない場合404エラーを返す
		}

		// ファイルを読み込む
		InputStreamResource resource = new InputStreamResource(new FileInputStream(file));

		// ファイル名をUTF-8でエンコード
		String encodedFileName = URLEncoder.encode(attachment.getFileName(), StandardCharsets.UTF_8.toString()).replaceAll("\\+", "%20");

		// ファイルのレスポンスを返す
		return ResponseEntity.ok()
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + encodedFileName + "\"")
				.contentType(MediaType.APPLICATION_OCTET_STREAM)
				.contentLength(file.length())
				.body(resource);
	}
}
