package org.example.echoBoard.config;

import org.example.echoBoard.model.Post;
import org.example.echoBoard.model.PostViewStat;
import org.example.echoBoard.repository.PostRepository;
import org.example.echoBoard.repository.PostViewStatRepository;
import org.example.echoBoard.service.RedisService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class RedisToDbSync {

    private final RedisService redisService;
    private final PostRepository postRepository;
    private final PostViewStatRepository postViewStatRepository;

    public RedisToDbSync(RedisService redisService, PostRepository postRepository,
                         PostViewStatRepository postViewStatRepository) {
        this.redisService = redisService;
        this.postRepository = postRepository;
        this.postViewStatRepository = postViewStatRepository;
    }

    @Scheduled(fixedRate = 300_000) // 5분마다
    public void syncViewsToDb() {
        List<Post> posts = postRepository.findAll();

        for (Post post : posts) {
            int redisViews = redisService.getViewCount(post.getId());
            if(redisViews == 0) continue;

            PostViewStat stat = postViewStatRepository.findById(post.getId()).orElse(new PostViewStat());
            stat.setPost(post);
            stat.setViewCount(redisViews);
            postViewStatRepository.save(stat);
        }
    }
}