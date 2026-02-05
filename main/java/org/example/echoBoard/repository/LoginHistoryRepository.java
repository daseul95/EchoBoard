package org.example.echoBoard.repository;

import org.example.echoBoard.model.LoginHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface LoginHistoryRepository extends JpaRepository<LoginHistory, Long> {


    @Query(value = """
    SELECT COUNT(*)
    FROM login_history l
    WHERE l.user_id = :userId
      AND l.success = false
      AND l.login_time >= :since
      AND (
          (SELECT MAX(login_time)
           FROM login_history
           WHERE user_id = :userId
             AND success = true) IS NULL
          OR l.login_time >
             (SELECT MAX(login_time)
              FROM login_history
              WHERE user_id = :userId
                AND success = true)
      )
""", nativeQuery = true)
    int countFailAfterLastSuccess(
            @Param("userId") Long userId,
            @Param("since") LocalDateTime since
    );
}