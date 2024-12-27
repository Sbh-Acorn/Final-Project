package com.example.Caltizm.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CommentDTO {

    private int comment_id;         // 댓글 ID
    private String content;        // 내용
    private int post_id;            // 게시글 ID
    private int user_id;            // 유저 ID
    private String created_at;  // 작성 시간
    private String updated_at;  // 수정 시간

    public CommentDTO(String content, int post_id) {
        this.content = content;
        this.post_id = post_id;
    }
}
