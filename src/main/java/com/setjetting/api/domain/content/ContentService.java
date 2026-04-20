package com.setjetting.api.domain.content;

import com.setjetting.api.domain.content.model.*;
import com.setjetting.api.domain.place.PlaceRepository;
import com.setjetting.api.domain.place.model.entity.Place;
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
    private final PlaceRepository placeRepository;

    // 작품 등록
    @Transactional
    public CreateContentRes createContent(CreateContentReq dto) {
        Content content = dto.toEntity();
        Long savedIdx = contentRepository.save(content).getIdx();
        return CreateContentRes.of(savedIdx);
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

    // 1. 작품 ID로 상세 정보 조회
    public ContentDetailRes getContentDetail(Long idx) {
        // findById 대신 fetch join이 적용된 findByIdWithPlaces 사용
        Content content = contentRepository.findByIdWithPlaces(idx)
                .orElseThrow(() -> new IllegalArgumentException("해당 작품을 찾을 수 없습니다."));

        return ContentDetailRes.of(content);
    }

    // 2. 장소 ID로 해당 작품의 상세 정보 조회 (PlaceController에서 사용)
    public ContentDetailRes getContentDetailByPlace(Long placeIdx) {
        Place place = placeRepository.findById(placeIdx)
                .orElseThrow(() -> new IllegalArgumentException("해당 장소를 찾을 수 없습니다."));

        // 장소와 연관된 Content 객체를 그대로 사용하여 상세 정보 생성
        // 이미 Place를 가져올 때 Content가 로딩되거나,
        // 다시 fetch join으로 깔끔하게 가져오려면 아래와 같이 contentIdx로 재호출하는 것이 안전합니다.
        return getContentDetail(place.getContent().getIdx());
    }
}