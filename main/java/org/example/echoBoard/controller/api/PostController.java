package org.example.echoBoard.controller.api;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.example.echoBoard.dto.request.PostCreateRequest;
import org.example.echoBoard.dto.response.PostResponse;
import org.example.echoBoard.model.User;
import org.example.echoBoard.service.PostService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/posts")
public class PostController {

    private final PostService postService;

    // 글 작성
    @PostMapping("/new")
    public Long createPost(@RequestBody PostCreateRequest request,
                           @SessionAttribute("USER_ID") User user) {
        return postService.create(request, user.getId());
    }
}