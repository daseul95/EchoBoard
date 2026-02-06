package org.example.echoBoard.repository;

import org.example.echoBoard.model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByPostIdOrderByCreatedAtAsc(Long postId);
    //부모 댓글만
    List<Comment> findByPostIdAndParentIsNullOrderByCreatedAtAsc(Long postId);

    // 대댓글
    List<Comment> findByParentIdOrderByCreatedAtAsc(Long parentId);
}
