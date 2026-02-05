package org.example.echoBoard.repository;

import org.example.echoBoard.model.Post;
import org.example.echoBoard.model.PostViewStat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostViewStatRepository extends JpaRepository<PostViewStat, Long> {
    Optional<PostViewStat> findByPostId(Long postId);
    List<Post> findTop10ByOrderByViewCountDesc();
}