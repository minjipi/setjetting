package com.setjetting.api.domain.trend;

import com.setjetting.api.domain.content.ContentRepository;
import com.setjetting.api.domain.content.model.Content;
import com.setjetting.api.domain.place.PlaceRepository;
import com.setjetting.api.domain.place.model.Place;
import com.setjetting.api.domain.trend.model.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TrendService {

    private final PlaceActivityRepository placeActivityRepository;
    private final ContentActivityRepository contentActivityRepository;
    private final PlaceTrendScoreRepository placeTrendScoreRepository;
    private final ContentTrendScoreRepository contentTrendScoreRepository;
    private final PlaceRepository placeRepository;
    private final ContentRepository contentRepository;

    @Transactional
    public void recordPlaceActivity(Long placeIdx, Long userId, ActivityType type) {
        placeActivityRepository.save(PlaceActivity.builder()
                .placeIdx(placeIdx)
                .userId(userId)
                .activityType(type)
                .build());
    }

    @Transactional
    public void recordContentActivity(Long contentIdx, Long userId, ActivityType type) {
        contentActivityRepository.save(ContentActivity.builder()
                .contentIdx(contentIdx)
                .userId(userId)
                .activityType(type)
                .build());
    }

    @Transactional
    public void computePlaceTrends() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime day1Ago = now.minusDays(1);
        LocalDateTime day7Ago = now.minusDays(7);
        LocalDateTime day30Ago = now.minusDays(30);

        List<PlaceActivity> activities = placeActivityRepository.findByCreatedAtAfter(day30Ago);

        // Anti-fraud dedup keys
        Set<String> uniqueKeys = new HashSet<>();
        Map<String, Integer> viewCountMap = new HashMap<>();

        Map<Long, Double> placeScores = new HashMap<>();

        for (PlaceActivity a : activities) {
            double typeScore = placeTypeScore(a.getActivityType());
            double weight = timeWeight(a.getCreatedAt(), day1Ago, day7Ago);

            if (a.getActivityType() == ActivityType.VIEW) {
                // Max 3 view credits per user per place per day
                if (a.getUserId() != null) {
                    String key = a.getUserId() + "_" + a.getPlaceIdx() + "_" + a.getCreatedAt().toLocalDate();
                    int count = viewCountMap.getOrDefault(key, 0);
                    if (count >= 3) continue;
                    viewCountMap.put(key, count + 1);
                }
            } else {
                // VERIFY / LIKE / VISIT: once per user per place per type
                if (a.getUserId() != null) {
                    String key = a.getUserId() + "_" + a.getPlaceIdx() + "_" + a.getActivityType();
                    if (!uniqueKeys.add(key)) continue;
                }
            }

            placeScores.merge(a.getPlaceIdx(), typeScore * weight, Double::sum);
        }

        double avgScore = placeScores.values().stream().mapToDouble(d -> d).average().orElse(1.0);

        // Trending bonus: recent 1h score ratio
        LocalDateTime hour1Ago = now.minusHours(1);
        Map<Long, Double> recentScores = new HashMap<>();
        for (PlaceActivity a : activities) {
            if (!a.getCreatedAt().isAfter(hour1Ago)) continue;
            recentScores.merge(a.getPlaceIdx(), placeTypeScore(a.getActivityType()), Double::sum);
        }

        placeTrendScoreRepository.deleteAllInBatch();

        List<PlaceTrendScore> scores = placeScores.entrySet().stream()
                .map(e -> {
                    double raw = e.getValue();
                    double hot = recentScores.getOrDefault(e.getKey(), 0.0) / avgScore;
                    return PlaceTrendScore.builder()
                            .placeIdx(e.getKey())
                            .rawScore(raw)
                            .trendScore(raw + hot * 10)
                            .computedAt(now)
                            .build();
                })
                .collect(Collectors.toList());

        placeTrendScoreRepository.saveAll(scores);
    }

    @Transactional
    public void computeContentTrends() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime day1Ago = now.minusDays(1);
        LocalDateTime day7Ago = now.minusDays(7);
        LocalDateTime day30Ago = now.minusDays(30);

        List<ContentActivity> activities = contentActivityRepository.findByCreatedAtAfter(day30Ago);

        Set<String> uniqueKeys = new HashSet<>();
        Map<String, Integer> viewCountMap = new HashMap<>();

        Map<Long, Double> contentScores = new HashMap<>();

        for (ContentActivity a : activities) {
            double typeScore = contentTypeScore(a.getActivityType());
            double weight = timeWeight(a.getCreatedAt(), day1Ago, day7Ago);

            if (a.getActivityType() == ActivityType.VIEW) {
                if (a.getUserId() != null) {
                    String key = a.getUserId() + "_" + a.getContentIdx() + "_" + a.getCreatedAt().toLocalDate();
                    int count = viewCountMap.getOrDefault(key, 0);
                    if (count >= 3) continue;
                    viewCountMap.put(key, count + 1);
                }
            } else {
                if (a.getUserId() != null) {
                    String key = a.getUserId() + "_" + a.getContentIdx() + "_" + a.getActivityType();
                    if (!uniqueKeys.add(key)) continue;
                }
            }

            contentScores.merge(a.getContentIdx(), typeScore * weight, Double::sum);
        }

        double avgScore = contentScores.values().stream().mapToDouble(d -> d).average().orElse(1.0);

        LocalDateTime hour1Ago = now.minusHours(1);
        Map<Long, Double> recentScores = new HashMap<>();
        for (ContentActivity a : activities) {
            if (!a.getCreatedAt().isAfter(hour1Ago)) continue;
            recentScores.merge(a.getContentIdx(), contentTypeScore(a.getActivityType()), Double::sum);
        }

        contentTrendScoreRepository.deleteAllInBatch();

        List<ContentTrendScore> scores = contentScores.entrySet().stream()
                .map(e -> {
                    double raw = e.getValue();
                    double hot = recentScores.getOrDefault(e.getKey(), 0.0) / avgScore;
                    return ContentTrendScore.builder()
                            .contentIdx(e.getKey())
                            .rawScore(raw)
                            .trendScore(raw + hot * 10)
                            .computedAt(now)
                            .build();
                })
                .collect(Collectors.toList());

        contentTrendScoreRepository.saveAll(scores);
    }

    @Transactional(readOnly = true)
    public List<TrendDto.PlaceTrendRes> getTopPlaces() {
        List<PlaceTrendScore> scores = placeTrendScoreRepository.findTop5ByOrderByTrendScoreDesc();
        Map<Long, Double> scoreMap = scores.stream()
                .collect(Collectors.toMap(PlaceTrendScore::getPlaceIdx, PlaceTrendScore::getTrendScore));

        List<TrendDto.PlaceTrendRes> result = placeRepository.findAllById(scoreMap.keySet()).stream()
                .map(p -> toPlaceTrendResWithScore(p, scoreMap.get(p.getIdx())))
                .sorted(Comparator.comparingDouble(TrendDto.PlaceTrendRes::getTrendScore).reversed())
                .collect(Collectors.toList());

        if (result.size() < 5) {
            Set<Long> scoredIds = scoreMap.keySet();
            placeRepository.findAll().stream()
                    .filter(p -> !scoredIds.contains(p.getIdx()))
                    .limit(5 - result.size())
                    .map(this::toPlaceTrendRes)
                    .forEach(result::add);
        }

        return result;
    }

    @Transactional(readOnly = true)
    public List<TrendDto.ContentTrendRes> getTopContents() {
        List<ContentTrendScore> scores = contentTrendScoreRepository.findTop5ByOrderByTrendScoreDesc();
        Map<Long, Double> scoreMap = scores.stream()
                .collect(Collectors.toMap(ContentTrendScore::getContentIdx, ContentTrendScore::getTrendScore));

        List<TrendDto.ContentTrendRes> result = contentRepository.findAllById(scoreMap.keySet()).stream()
                .map(c -> toContentTrendResWithScore(c, scoreMap.get(c.getIdx())))
                .sorted(Comparator.comparingDouble(TrendDto.ContentTrendRes::getTrendScore).reversed())
                .collect(Collectors.toList());

        if (result.size() < 5) {
            Set<Long> scoredIds = scoreMap.keySet();
            contentRepository.findAll().stream()
                    .filter(c -> !scoredIds.contains(c.getIdx()))
                    .limit(5 - result.size())
                    .map(this::toContentTrendRes)
                    .forEach(result::add);
        }

        return result;
    }

    private double placeTypeScore(ActivityType type) {
        return switch (type) {
            case VERIFY -> 5.0;
            case VISIT -> 3.0;
            case LIKE -> 2.0;
            case VIEW -> 1.0;
        };
    }

    private double contentTypeScore(ActivityType type) {
        return switch (type) {
            case VERIFY -> 4.0;
            case LIKE -> 3.0;
            case VIEW -> 1.0;
            default -> 1.0;
        };
    }

    private double timeWeight(LocalDateTime createdAt, LocalDateTime day1Ago, LocalDateTime day7Ago) {
        if (createdAt.isAfter(day1Ago)) return 1.5;
        if (createdAt.isAfter(day7Ago)) return 1.0;
        return 0.3;
    }

    private TrendDto.PlaceTrendRes toPlaceTrendRes(Place p) {
        return toPlaceTrendResWithScore(p, 0.0);
    }

    private TrendDto.PlaceTrendRes toPlaceTrendResWithScore(Place p, double score) {
        return TrendDto.PlaceTrendRes.builder()
                .placeIdx(p.getIdx())
                .name(p.getName())
                .englishName(p.getEnglishName())
                .address(p.getAddress())
                .latitude(p.getLatitude())
                .longitude(p.getLongitude())
                .placeImageUrl(p.getPlaceImageUrl())
                .contentTitle(p.getContent() != null ? p.getContent().getTitle() : null)
                .contentIdx(p.getContent() != null ? p.getContent().getIdx() : null)
                .trendScore(score)
                .build();
    }

    private TrendDto.ContentTrendRes toContentTrendRes(Content c) {
        return toContentTrendResWithScore(c, 0.0);
    }

    private TrendDto.ContentTrendRes toContentTrendResWithScore(Content c, double score) {
        return TrendDto.ContentTrendRes.builder()
                .contentIdx(c.getIdx())
                .title(c.getTitle())
                .englishTitle(c.getEnglishTitle())
                .posterImageUrl(c.getPosterImageUrl())
                .contentType(c.getContentType())
                .trendScore(score)
                .build();
    }
}
