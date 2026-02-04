package org.example.echoBoard.service;

import lombok.RequiredArgsConstructor;
import org.example.echoBoard.model.Post;
import org.example.echoBoard.model.PostViewStat;
import org.example.echoBoard.repository.PostRepository;
import org.example.echoBoard.repository.PostViewStatRepository;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final PostViewStatRepository postViewStatRepository;

    public Post savePost(Post post) {
        Post saved = postRepository.save(post);
        // 글 작성 시 PostViewStat 초기화
        PostViewStat stat = PostViewStat.builder()
                .post(saved)
                .viewCount(0)
                .build();
        postViewStatRepository.save(stat);
        return saved;
    }

    @Cacheable(value = "popularPosts")
    public List<Post> getPopularPosts() {
        // 단순 예시: viewCount 기준 상위 10개
        return postViewStatRepository.findAll().stream()
                .sorted((a,b) -> b.getViewCount() - a.getViewCount())
                .limit(10)
                .map(PostViewStat::getPost)
                .toList();
    }
}