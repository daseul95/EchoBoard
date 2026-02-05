package org.example.echoBoard.service;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Set;


@Component
@RequiredArgsConstructor
public class RedisInitService {

    private final RedisService redisService;

    @PostConstruct
    public void init() {
        redisService.syncTopPostsFromRedis();
    }
}