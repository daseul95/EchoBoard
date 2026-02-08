package org.example.echoBoard.controller.api;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.example.echoBoard.dto.request.PostCreateRequest;
import org.example.echoBoard.dto.response.PostResponse;
import org.example.echoBoard.model.User;
import org.example.echoBoard.service.CommentService;
import org.example.echoBoard.service.PostService;
import org.example.echoBoard.service.RedisService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/posts")
public class PostController {

    private final PostService postService;
    private final RedisService redisService;
    private final CommentService commentService;
    // 글 작성
    @PostMapping("/new")
    public Long createPost(@RequestBody PostCreateRequest request,
                           @SessionAttribute("USER_ID") User user) {
        return postService.create(request, user.getId());
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<Void> deletePost(@PathVariable Long postId) {
        commentService.deleteCommentsByPostId(postId);
        redisService.deletePostRedis(postId);
        postService.deletePost(postId);

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long commentId) {
        System.out.println("댓글 삭제 요청중");
        commentService.deleteComment(commentId);
        return ResponseEntity.noContent().build();
    }
}