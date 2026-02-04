package org.example.echoBoard.controller.api;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.example.echoBoard.model.LoginHistory;
import org.example.echoBoard.model.User;
import org.example.echoBoard.repository.SessionRepository;
import org.example.echoBoard.service.LoginHistoryService;
import org.example.echoBoard.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/auth")
public class LoginController {

    private final SessionRepository sessionRepository;

    private final LoginHistoryService loginHistoryservice;

    private final UserService userService;

    public LoginController(SessionRepository sessionRepository,LoginHistoryService loginHistoryservice, UserService userService) {
        this.sessionRepository = sessionRepository;
        this.loginHistoryservice =loginHistoryservice;
        this.userService = userService;
    }


    @PostMapping("/login")
    public String login(@RequestParam String username,
                                   @RequestParam String password,
                                   RedirectAttributes redirectAttributes,
                                   HttpServletRequest request) {

        try {
            userService.login(username, password, request);
            return "redirect:/"; //로그인 성공
        } catch (IllegalArgumentException | IllegalStateException e) {
            redirectAttributes.addAttribute("error", e.getMessage());
            return "redirect:/login";
        }
    }

    @PostMapping("/logout")
    public String logout(HttpServletRequest request) {

        HttpSession session = request.getSession(false);
        if (session != null) {
            // DB 세션 삭제
            sessionRepository.updateIsActiveBySessionId(session.getId());

            // 서버 세션 무효화
            session.invalidate();
        }
        return "redirect:/login";
    }


    @PostMapping("/record")
    public LoginHistory recordLogin(@RequestParam Long userId,
                                    @RequestParam String ip,
                                    @RequestParam String ua) {
        User user = userService.findById(userId); // User 엔티티 조회
        if (user == null) {
            throw new RuntimeException("User not found");
        }

        LoginHistory history = LoginHistory.builder()
                .user(user)
                .ipAddress(ip)
                .userAgent(ua)
                .loginTime(LocalDateTime.now())
                .success(true)
                .build();

        return loginHistoryservice.save(history);
    }
    @GetMapping("/all")
    public List<LoginHistory> getAllLogins() {
        return loginHistoryservice.findAll();
    }
}