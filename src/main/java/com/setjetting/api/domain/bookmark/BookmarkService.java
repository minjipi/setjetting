package com.setjetting.api.domain.bookmark;

import com.setjetting.api.common.exception.BaseException;
import com.setjetting.api.common.model.BaseResponseStatus;
import com.setjetting.api.domain.bookmark.model.Bookmark;
import com.setjetting.api.domain.bookmark.model.BookmarkDto;
import com.setjetting.api.domain.place.PlaceRepository;
import com.setjetting.api.domain.place.model.Place;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class BookmarkService {

    private final BookmarkRepository bookmarkRepository;
    private final PlaceRepository placeRepository;

    public BookmarkDto.ToggleRes toggle(Long userIdx, Long placeIdx) {
        Optional<Bookmark> existing = bookmarkRepository.findByUserIdxAndPlace_Idx(userIdx, placeIdx);
        if (existing.isPresent()) {
            bookmarkRepository.delete(existing.get());
            return new BookmarkDto.ToggleRes(false);
        }
        Place place = placeRepository.findById(placeIdx)
                .orElseThrow(() -> BaseException.of(BaseResponseStatus.PLACE_NOT_FOUND));
        bookmarkRepository.save(Bookmark.of(userIdx, place));
        return new BookmarkDto.ToggleRes(true);
    }

    @Transactional(readOnly = true)
    public List<BookmarkDto.BookmarkPlaceRes> getBookmarkedPlaces(Long userIdx) {
        return bookmarkRepository.findByUserIdxOrderByCreatedAtDesc(userIdx)
                .stream()
                .map(BookmarkDto.BookmarkPlaceRes::of)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public boolean isBookmarked(Long userIdx, Long placeIdx) {
        return bookmarkRepository.existsByUserIdxAndPlace_Idx(userIdx, placeIdx);
    }
}
