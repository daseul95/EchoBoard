package org.example.echoBoard.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

import org.example.echoBoard.model.User;

@Entity
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class LoginHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, columnDefinition="TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime loginTime;

    @Column(length = 255)
    private String userAgent;

    @Column(length = 45)
    private String ipAddress;

    private Boolean success;


    public static LoginHistory fail(Long userId) {
        LoginHistory history = new LoginHistory();
        history.id = userId;
        history.success = false;
        history.loginTime = LocalDateTime.now();
        return history;
    }

    public static LoginHistory success(Long userId) {
        LoginHistory history = new LoginHistory();
        history.id = userId;
        history.success = true;
        history.loginTime = LocalDateTime.now();
        return history;
    }
}
