package com.setjetting.api.domain.post;

import com.setjetting.api.domain.post.model.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    @Query("SELECT p FROM Post p " +
            "JOIN FETCH p.user " +
            "LEFT JOIN FETCH p.images " +
            "WHERE p.idx = :idx")
    Optional<Post> findByIdWithUserAndImages(@Param("idx") Long idx);

}