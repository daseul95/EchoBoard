package org.example.echoBoard.controller.page;

import lombok.RequiredArgsConstructor;
import org.example.echoBoard.dto.response.PostDetailResponse;
import org.example.echoBoard.service.PostService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/posts")
public class PostPageController {

    private final PostService postService;

    // 글 목록
    @GetMapping
    public String postList(Model model) {
        model.addAttribute("posts", postService.findAll());
        return "post";
    }

    // 글 작성 페이지
    @GetMapping("/new")
    public String newPostForm() {
        return "post/new";
    }

    // 글 상세
    @GetMapping("/{id}")
    public String postDetail(@PathVariable Long id, Model model) {
        PostDetailResponse postResponse = postService.findDetailAndIncreaseView(id); // 조회수 증가;
        model.addAttribute("post", postResponse);
        return "post/detail";
    }
}
