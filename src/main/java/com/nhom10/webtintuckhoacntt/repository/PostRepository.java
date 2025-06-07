package com.nhom10.webtintuckhoacntt.repository;

import com.nhom10.webtintuckhoacntt.enums.PostStatus;
import com.nhom10.webtintuckhoacntt.model.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {
    @Query("SELECT p FROM Post p WHERE p.status = :status ORDER BY p.pined DESC, p.createdAt DESC")
    List<Post> findPublishedPostsOrderByPined(@Param("status") PostStatus status);
}
