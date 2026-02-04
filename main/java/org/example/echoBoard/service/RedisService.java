package org.example.echoBoard.service;

import lombok.RequiredArgsConstructor;
import org.example.echoBoard.model.Post;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class RedisService {

    private static final String POST_RANKING_KEY = "post:ranking";

    private final StringRedisTemplate redisTemplate;

    public void increasePostView(Long postId) {
        redisTemplate.opsForZSet()
                .incrementScore(POST_RANKING_KEY, postId.toString(), 1);
    }

    public Set<String> getTopPostIds(int limit) {
        return redisTemplate.opsForZSet()
                .reverseRange(POST_RANKING_KEY, 0, limit - 1);
    }

}