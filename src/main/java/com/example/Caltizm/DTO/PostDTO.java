package com.example.Caltizm.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class PostDTO {

    private String subject;
    private String title;
    private String content;
    private int user_id;
    private String created_at;
    private String updated_at;
    private int views;
    private int likes;
    private boolean is_deleted;


    public PostDTO(String subject, String title, String content) {
        this.subject = subject;
        this.title = title;
        this.content = content;
    }

}
