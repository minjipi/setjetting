package com.setjetting.api.domain.place;

import com.setjetting.api.domain.place.model.Place;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlaceRepository extends JpaRepository<Place, Long> {
    List<Place> findAllByContent_Idx(Long contentIdx);
}