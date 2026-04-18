package com.setjetting.api.domain.contents;

import com.setjetting.api.domain.contents.model.Content;
import com.setjetting.api.domain.contents.model.ContentListRes;
import com.setjetting.api.domain.contents.model.CreateContentReq;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ContentService {

    private final ContentRepository contentRepository;

    // 작품 등록
    @Transactional
    public Long createContent(CreateContentReq dto) {
        Content content = dto.toEntity();
        return contentRepository.save(content).getIdx();
    }

    // 작품 목록 조회
    public List<ContentListRes> getContentList(String type) {
        List<Content> contents = (type != null)
                ? contentRepository.findAllByContentType(type)
                : contentRepository.findAll();

        return contents.stream()
                .map(ContentListRes::of)
                .collect(Collectors.toList());
    }
}