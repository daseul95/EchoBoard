package org.example.echoBoard.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.example.echoBoard.model.Post;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class PostDetailResponse {

    private Long id;
    private String title;
    private String content;
    private String author;
    private Long userId;
    private LocalDateTime createdAt;
    private long viewCount;

    public static PostDetailResponse from(Post post, long viewCount) {
        return new PostDetailResponse(
                post.getId(),
                post.getTitle(),
                post.getContent(),
                post.getUser().getUsername(),
                post.getUser().getId(),
                post.getCreatedAt(),
                viewCount
        );
    }
}