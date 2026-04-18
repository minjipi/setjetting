package com.setjetting.api.domain.contents;

import com.setjetting.api.domain.contents.model.Content;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContentRepository extends JpaRepository<Content, Long> {
    List<Content> findAllByContentType(String contentType);
}