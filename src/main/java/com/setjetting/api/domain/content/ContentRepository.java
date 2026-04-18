package com.setjetting.api.domain.content;

import com.setjetting.api.domain.content.model.Content;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

import java.util.Optional;

@Repository
public interface ContentRepository extends JpaRepository<Content, Long> {

    // 1. 기존: 타입별 목록 조회 (성능상 무리 없음)
    List<Content> findAllByContentType(String contentType);

    // 2. 추가: 상세 페이지용 (N+1 문제 해결을 위한 Fetch Join)
    @Query("SELECT c FROM Content c " +
            "LEFT JOIN FETCH c.places " +
            "WHERE c.idx = :idx")
    Optional<Content> findByIdWithPlaces(@Param("idx") Long idx);
}