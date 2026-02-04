package org.example.echoBoard.service;


import org.springframework.transaction.annotation.Transactional;
import org.example.echoBoard.model.LoginHistory;
import org.example.echoBoard.model.User;
import org.example.echoBoard.repository.LoginHistoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class LoginHistoryService {

    private final LoginHistoryRepository loginHistoryRepository;

    public LoginHistoryService(LoginHistoryRepository loginHistoryRepository) {
        this.loginHistoryRepository = loginHistoryRepository;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void recordLogin(User user, String ip, String ua, boolean success) {
        LoginHistory history = LoginHistory.builder()
                .user(user)
                .ipAddress(ip)
                .userAgent(ua)
                .loginTime(LocalDateTime.now())
                .success(success)
                .build();
        loginHistoryRepository.save(history);
    }

    public LoginHistory save(LoginHistory history){
        loginHistoryRepository.save(history);
        return history;
    }


    public List<LoginHistory> findAll() {
        return loginHistoryRepository.findAll();
    }
}