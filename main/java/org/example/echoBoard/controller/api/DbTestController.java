package org.example.echoBoard.controller.api;

import lombok.RequiredArgsConstructor;
import org.example.echoBoard.model.User;
import org.example.echoBoard.repository.UserRepository;
import org.example.echoBoard.service.RedisService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class DbTestController {

    private final UserRepository userRepository;
    private final RedisService redisService;

    @GetMapping("/test-db")
    public List<User> testDb() {
        return userRepository.findAll();
    }


    @GetMapping("/syn-redis")
    public void synRedis(){
        redisService.syncRedisWithDb();
        redisService.cleanOrphanedPostViewStats();
    }

}