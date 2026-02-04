package org.example.echoBoard.repository;

import org.example.echoBoard.model.PostViewStat;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PostViewStatRepository extends JpaRepository<PostViewStat, Long> {
    Optional<PostViewStat> findByPostId(Long postId);
}