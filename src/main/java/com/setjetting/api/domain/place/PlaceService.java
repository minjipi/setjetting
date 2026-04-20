package com.setjetting.api.domain.place;

import com.setjetting.api.domain.content.ContentRepository;
import com.setjetting.api.domain.content.model.Content;
import com.setjetting.api.domain.place.model.dto.CreatePlaceReq;
import com.setjetting.api.domain.place.model.dto.CreatePlaceRes;
import com.setjetting.api.domain.place.model.entity.Place;
import com.setjetting.api.domain.place.model.dto.PlaceListRes;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PlaceService {

    private final PlaceRepository placeRepository;
    private final ContentRepository contentRepository;

    @Transactional
    public CreatePlaceRes createPlace(CreatePlaceReq req) {
        // 1. 연관된 작품 조회 (없으면 예외 발생)
        Content content = contentRepository.findById(req.getContentIdx())
                .orElseThrow(() -> new IllegalArgumentException("해당 작품(Content)을 찾을 수 없습니다."));

        // 2. Place 엔티티 생성 및 연관관계 설정
        Place place = Place.builder()
                .content(content)
                .name(req.getName())
                .englishName(req.getEnglishName())
                .address(req.getAddress())
                .latitude(req.getLatitude())
                .longitude(req.getLongitude())
                .placeImageUrl(req.getPlaceImageUrl())
                .description(req.getDescription())
                .build();

        // 3. DB 저장
        Long savedIdx = placeRepository.save(place).getIdx();
        return CreatePlaceRes.of(savedIdx);
    }

    public List<PlaceListRes> getAllPlaces() {
        List<Place> places = placeRepository.findAll();
        return places.stream()
                .map(PlaceListRes::of)
                .collect(Collectors.toList());
    }

    public List<PlaceListRes> getPlacesByContentIdx(Long contentIdx) {
        List<Place> places = placeRepository.findAllByContent_Idx(contentIdx);
        return places.stream()
                .map(PlaceListRes::of)
                .collect(Collectors.toList());
    }
}