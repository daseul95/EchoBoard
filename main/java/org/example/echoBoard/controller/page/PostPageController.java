package org.example.echoBoard.controller.page;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.example.echoBoard.dto.response.CommentResponse;
import org.example.echoBoard.dto.response.PostDetailResponse;
import org.example.echoBoard.dto.response.PostResponse;
import org.example.echoBoard.model.Post;
import org.example.echoBoard.service.CommentService;
import org.example.echoBoard.service.PostService;
import org.example.echoBoard.service.RedisService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
@RequestMapping("/posts")
public class PostPageController {

    private final PostService postService;
    private final RedisService redisService;
    private final CommentService commentService;

    // 글 목록
    @GetMapping
    public String postList(@PageableDefault(size = 10) Pageable pageable, Model model) {
        Page<PostResponse> page = postService.findAll(pageable);
        model.addAttribute("posts", page.getContent());
        model.addAttribute("page",page);
        model.addAttribute("topPosts", postService.findTop5Post()); // TOP N
        return "post";
    }

    // 글 작성 페이지
    @GetMapping("/new")
    public String newPostForm() {
        return "post/new";
    }

    // 글 상세
    @GetMapping("/{id}")
    public String postDetail(@PathVariable Long id, Model model,HttpSession session) {

        postService.increaseViewCount(id);

        PostDetailResponse postResponse = postService.findDetail(id);// 조회수 증가;
        List<CommentResponse> comments = commentService.findByPostId(id);

        Long userId = (Long) session.getAttribute("USER_ID");

        model.addAttribute("post", postResponse);
        model.addAttribute("comments", comments);
        model.addAttribute("USER_ID",userId);
        return "post/detail";
    }

    @PostMapping("/{id}/comments")
    @ResponseBody
    public  CommentResponse addComment(
            @PathVariable Long id,
            @RequestParam String content,
            @RequestParam(required = false) Long parentId,
            HttpSession session
    ) {
        Long userId = (Long) session.getAttribute("USER_ID");
        return commentService.addComment(id, userId, content,parentId);
    }

    @GetMapping("/{id}/comments")
    @ResponseBody
    public List<CommentResponse> getComments(@PathVariable Long id) {
        return commentService.findByPostId(id);
    }

    // 인기 게시글 Top 5
    @GetMapping("/top")
    public List<Post> getTopPosts() {
       List<Long> topIds = redisService.getTopPosts(5);
        return topIds.stream()
                .map(postService::findById)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }
}
