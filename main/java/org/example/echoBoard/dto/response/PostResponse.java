package org.example.echoBoard.dto.response;

import lombok.Getter;
import org.example.echoBoard.model.Post;

import java.time.LocalDateTime;

@Getter
public class PostResponse {

    private Long id;
    private String title;
    private String content;
    private long viewCount;
    private String author;
    private LocalDateTime createdAt;

    public PostResponse(Post post, long viewCount) {
        this.id = post.getId();
        this.title = post.getTitle();
        this.content = post.getContent();
        this.viewCount = viewCount;
        this.author = post.getUser().getUsername();
        this.createdAt = post.getCreatedAt();
    }
}