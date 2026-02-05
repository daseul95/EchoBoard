package org.example.echoBoard.service;

import lombok.RequiredArgsConstructor;
import org.example.echoBoard.dto.request.PostCreateRequest;
import org.example.echoBoard.dto.response.PostDetailResponse;
import org.example.echoBoard.dto.response.PostResponse;
import org.example.echoBoard.model.Post;
import org.example.echoBoard.model.PostViewStat;
import org.example.echoBoard.model.User;
import org.example.echoBoard.repository.PostRepository;
import org.example.echoBoard.repository.PostViewStatRepository;
import org.example.echoBoard.repository.UserRepository;
import org.springframework.boot.autoconfigure.cache.CacheProperties;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final PostViewStatRepository postViewStatRepository;
    private final UserRepository userRepository;

    private final RedisService redisService;

    @Transactional
    public Long create(PostCreateRequest request, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("유저 없음"));

        Post post = postRepository.save(
                new Post(request.getTitle(), request.getContent(), user)
        );

        postViewStatRepository.save(new PostViewStat(post));

        return post.getId();
    }

    public Post findById(Long id){
        return postRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("포스트 없음"));
    }

    @Transactional(readOnly = true)
    public List<PostResponse> findAll() {
        return postRepository.findAllByOrderByCreatedAtDesc().stream()
                .map(post -> new PostResponse(post, getViewCount(post.getId())))
                .toList();
    }

    public long getViewCount(Long postId) {
        return postViewStatRepository.findByPostId(postId)
                .map(PostViewStat::getViewCount)
                .orElse(0);
    }

    public PostDetailResponse findDetail(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글 없음"));

        PostViewStat stat = postViewStatRepository.findByPostId(postId)
                .orElseThrow(() -> new IllegalStateException("조회수 정보 없음"));

        redisService.incrementView(postId);

        return PostDetailResponse.from(post, stat.getViewCount());
    }

    public void increaseViewCount(Long postId) {
        PostViewStat stat = postViewStatRepository.findByPostId(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));
        stat.increase();
    }
}