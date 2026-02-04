package org.example.echoBoard.service;

import lombok.RequiredArgsConstructor;
import org.example.echoBoard.model.User;
import org.example.echoBoard.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.annotation.Propagation;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UserLockService {

    private final UserRepository userRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void lockUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow();

        user.setLocked(true);
        user.setLockedUntil(LocalDateTime.now().plusMinutes(10));
        userRepository.save(user);
    }
}