package org.example.echoBoard.dto.response;

import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentResponse {

    private Long id;
    private String content;
    private String username;
    private Long userId;
    private LocalDateTime createdAt;
    private Long parentId; // 대댓글이면 parent 댓글 id
    @Builder.Default
    private List<CommentResponse> replies = new ArrayList<>();
}