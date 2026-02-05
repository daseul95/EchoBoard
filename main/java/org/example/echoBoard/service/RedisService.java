package org.example.echoBoard.service;

import lombok.RequiredArgsConstructor;
import org.example.echoBoard.model.Post;
import org.example.echoBoard.model.PostViewStat;
import org.example.echoBoard.repository.PostRepository;
import org.example.echoBoard.repository.PostViewStatRepository;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class RedisService {

    private final RedisTemplate<String, String> redisTemplate;
    private final PostViewStatRepository postViewStatRepository;

    public RedisService(RedisTemplate<String, String> redisTemplate
            , PostViewStatRepository postViewStatRepository) {
        this.redisTemplate = redisTemplate;
        this.postViewStatRepository = postViewStatRepository;
    }

    private final String TOP_POSTS_KEY = "top:posts";
    private final String POST_VIEW_STAT_KEY_PREFIX = "postviewstat:";

    public void incrementView(Long postId) {
        // Redis 조회수 증가
        redisTemplate.opsForValue().increment(POST_VIEW_STAT_KEY_PREFIX + postId + ":views");

        redisTemplate.opsForZSet()
                .incrementScore("top:posts", postId.toString(), 1);
    }

    public Map<Long,Long> getAllViewCount(List<Long> postIds) {

        List<String> keys = postIds.stream().map(id -> "postviewstat:"+id+":views")
                .toList();

        List<String> values = redisTemplate.opsForValue().multiGet(keys);

        Map<Long,Long> result = new HashMap<>();

        for (int i = 0; i<postIds.size();i++){
            String value = values.get(i);
            result.put(postIds.get(i),value == null?0L:Long.parseLong(value));
        }

        return result;

    }

    @Scheduled(fixedRate = 60000) // 1분마다
    public void updateTopPosts() {
        Set<String> allKeys = redisTemplate.keys(POST_VIEW_STAT_KEY_PREFIX + "*:views");
        Map<String, Double> scores = new HashMap<>();
        for(String key : allKeys){
            String postId = key.split(":")[1];
            String val = redisTemplate.opsForValue().get(key);
            scores.put(postId, val == null ? 0 : Double.parseDouble(val));
        }
        // ZSET 갱신
        redisTemplate.delete(TOP_POSTS_KEY);
        scores.forEach((postId, score) -> {
            redisTemplate.opsForZSet().add(TOP_POSTS_KEY, postId, score);
        });
    }

    // Top N 인기 게시글 가져오기
    public List<Long> getTopPosts(int n) {
        // ZSET에서 score 기준으로 내림차순
        Set<String> topIds = redisTemplate.opsForZSet()
                .reverseRange(TOP_POSTS_KEY, 0, n - 1);
        if(topIds == null) return List.of();

        // String -> Long 변환
        List<Long> topIdsList= topIds.stream()
                .map(Long::parseLong)
                .toList();

        return topIdsList;
    }

    public Set<String> getAllPostViewKeys() {
        return redisTemplate.keys(POST_VIEW_STAT_KEY_PREFIX + "*:views");
    }

    public void syncTopPostsFromRedis() {
        Set<String> keys = redisTemplate.keys("postviewstat:*:views");

        if (keys == null) return;

        for (String key : keys) {
            String postId = key.replace("postviewstat:", "")
                    .replace(":views", "");

            Integer views = Integer.parseInt(
                    redisTemplate.opsForValue().get(key)
            );

            redisTemplate.opsForZSet()
                    .add("top:posts", postId, views);
        }
    }


}