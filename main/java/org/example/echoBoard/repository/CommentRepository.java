package org.example.echoBoard.repository;

import org.example.echoBoard.model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByPostIdOrderByCreatedAtAsc(Long postId);
    //부모 댓글만
    List<Comment> findByPostIdAndParentIsNullOrderByCreatedAtAsc(Long postId);

    // 대댓글
    List<Comment> findByParentIdOrderByCreatedAtAsc(Long parentId);

    List<Comment> findAllByParent(Comment parent);

    @Modifying
    @Query("delete from Comment c where c.parent.id = :commentId")
    void deleteRepliesByParentId(@Param("commentId") Long commentId);

    @Modifying
    @Query("delete from Comment c where c.id = :commentId")
    void deleteByIdDirect(@Param("commentId") Long commentId);

    @Modifying
    @Query("delete from Comment c where c.parent is not null and c.post.id = :postId")
    void deleteRepliesByPostId(@Param("postId") Long postId);

    @Modifying
    @Query("delete from Comment c where c.parent is null and c.post.id = :postId")
    void deleteParentCommentsByPostId(@Param("postId") Long postId);

}
