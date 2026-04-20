package com.setjetting.api.domain.bookmark;

import com.setjetting.api.domain.bookmark.model.Bookmark;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {
    Optional<Bookmark> findByUserIdxAndPlace_Idx(Long userIdx, Long placeIdx);
    boolean existsByUserIdxAndPlace_Idx(Long userIdx, Long placeIdx);
    List<Bookmark> findByUserIdxOrderByCreatedAtDesc(Long userIdx);
}
