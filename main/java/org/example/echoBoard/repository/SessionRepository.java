package org.example.echoBoard.repository;

import org.example.echoBoard.model.SessionEntity;
import org.example.echoBoard.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface SessionRepository extends JpaRepository<SessionEntity, Long> {
    Optional<SessionEntity> findBySessionId(String sessionId);
    Optional<SessionEntity> findBySessionIdAndIsActiveTrue(String sessionId);

    @Modifying
    @Query("DELETE FROM SessionEntity s WHERE s.user = :user")
    void deleteAllByUser(@Param("user") User user);

    @Modifying
    @Transactional
    @Query("UPDATE SessionEntity s SET s.isActive = false WHERE s.sessionId = :sessionId")
    void updateIsActiveBySessionId(@Param("sessionId") String sessionId);

}