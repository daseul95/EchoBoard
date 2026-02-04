package org.example.echoBoard.controller.api;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RedisTestRunner implements CommandLineRunner {

    private final StringRedisTemplate redisTemplate;

    @Override
    public void run(String... args) {
        redisTemplate.opsForValue().set("spring:test", "ok");
        String value = redisTemplate.opsForValue().get("spring:test");
        System.out.println("Redis 연결 테스트 = " + value);
    }
}