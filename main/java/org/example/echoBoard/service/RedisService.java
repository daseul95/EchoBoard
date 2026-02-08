package org.example.echoBoard.service;

import lombok.RequiredArgsConstructor;
import org.example.echoBoard.repository.PostRepository;
import org.example.echoBoard.repository.PostViewStatRepository;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
public class RedisService {

    private final RedisTemplate<String, String> redisTemplate;

    private final PostViewStatRepository postViewStatRepository;
    private final PostRepository postRepository;


    private final String TOP_POSTS_KEY = "top:posts";
    private final String POST_VIEW_STAT_KEY_PREFIX = "postviewstat:";

    public void incrementView(Long postId) {
        // Redis 조회수 증가
        redisTemplate.opsForValue().increment(POST_VIEW_STAT_KEY_PREFIX + postId + ":views");

        redisTemplate.opsForZSet()
                .incrementScore("top:posts", postId.toString(), 1);
    }

    public Map<Long, Long> getAllViewCount(List<Long> postIds) {

        List<String> keys = postIds.stream().map(id -> "postviewstat:" + id + ":views")
                .toList();

        List<String> values = redisTemplate.opsForValue().multiGet(keys);

        Map<Long, Long> result = new HashMap<>();

        for (int i = 0; i < postIds.size(); i++) {
            String value = values.get(i);
            result.put(postIds.get(i), value == null ? 0L : Long.parseLong(value));
        }

        return result;

    }

    @Scheduled(fixedRate = 60000) // 1분마다
    public void updateTopPosts() {
        Set<String> allKeys = redisTemplate.keys(POST_VIEW_STAT_KEY_PREFIX + "*:views");
        Map<String, Double> scores = new HashMap<>();
        for (String key : allKeys) {
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
        if (topIds == null) return List.of();

        // String -> Long 변환
        List<Long> topIdsList = topIds.stream()
                .map(Long::parseLong)
                .toList();

        return topIdsList;
    }

    public Long getPostViewByPostId(Long postId) {
        String key = POST_VIEW_STAT_KEY_PREFIX + postId + ":views";
        String strValue = redisTemplate.opsForValue().get(key);
        long viewCount = strValue != null
                ? Long.parseLong(strValue)
                : 0L;

        return viewCount;
    }


    @Transactional(readOnly = true)
    public void deletePostRedis(Long postId) {
        // ZSET에서 해당 포스트만 제거
        redisTemplate.opsForZSet().remove("top:posts", postId.toString());
        redisTemplate.delete(POST_VIEW_STAT_KEY_PREFIX + postId+":views");
    }


    @Transactional
    public void syncRedisWithDb() {
        // 1. DB에 있는 모든 postId 조회
        List<Long> dbPostIds = postRepository.findAllPostIds();

        // 2. Redis TOP ZSET에 있는 postId 조회
        Set<String> redisPostIds = redisTemplate.opsForZSet().range(TOP_POSTS_KEY, 0, -1);

        for (String redisId : redisPostIds) {
            Long postId = Long.valueOf(redisId);
            // DB에 없으면 Redis에서 제거
            if (!dbPostIds.contains(postId)) {
                redisTemplate.opsForZSet().remove(TOP_POSTS_KEY, redisId);
                redisTemplate.delete(POST_VIEW_STAT_KEY_PREFIX + redisId + "views");
            }
        }

    }

    public void cleanOrphanedPostViewStats() {
        // 1. DB에 존재하는 모든 postId 가져오기
        List<Long> dbPostIds = postRepository.findAllPostIds();
        Set<Long> dbPostIdSet = new HashSet<>(dbPostIds);

        // 2. Redis에서 postViewStat:* 키 조회 (SCAN 권장)
        ScanOptions options = ScanOptions.scanOptions().match("postviewstat:*").count(1000).build();
        try (Cursor<byte[]> cursor = (Cursor<byte[]>) redisTemplate.getConnectionFactory().getConnection().scan(options)) {
            while (cursor.hasNext()) {
                String key = new String(cursor.next());
                // key에서 postId 추출
                String[] parts = key.split(":");
                Long postId = Long.valueOf(parts[1]);

                // DB에 없으면 삭제
                if (!dbPostIdSet.contains(postId)) {
                    redisTemplate.delete(key);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}