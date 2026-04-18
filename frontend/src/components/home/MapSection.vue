<template>
  <div class="map-wrapper">
    <div ref="mapEl" class="map-container" />

    <!-- Loading state -->
    <div v-if="loading" class="map-loading">
      <div class="loading-pulse" />
      <p>지도 로딩 중...</p>
    </div>

    <!-- Error state -->
    <div v-if="error" class="map-error">
      <span>🗺️</span>
      <p>지도를 불러올 수 없습니다</p>
      <small>VITE_KAKAO_MAP_KEY를 확인해주세요</small>
    </div>

    <!-- Cloud wave transition to content -->
    <div class="cloud-wave-bottom" aria-hidden="true">
      <svg viewBox="0 0 390 64" xmlns="http://www.w3.org/2000/svg" preserveAspectRatio="none">
        <path
          d="M0,64 L0,44
          Q15,24 35,36 Q48,18 68,30 Q82,12 104,26
          Q118,8 140,22 Q155,6 174,20 Q190,4 210,18
          Q226,2 248,16 Q264,0 284,14
          Q300,2 318,18 Q335,6 354,22 Q370,10 390,28
          L390,64 Z"
          fill="#f0f9fd"
        />
      </svg>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { useKakaoMap } from '@/composables/useKakaoMap';

const mapEl = ref<HTMLDivElement | null>(null);
const loading = ref(true);
const error = ref(false);

const { load, createMarker } = useKakaoMap();

// 성지순례 샘플 장소 (실제 DB 연동 시 API로 교체)
const sampleLocations = [
  { lat: 37.5665, lng: 126.9780, title: '서울 (이태원 클라쓰)' },
  { lat: 35.1796, lng: 129.0756, title: '부산 (파친코)' },
];

onMounted(async () => {
  try {
    await load();
    if (!mapEl.value) return;

    const center = new window.kakao.maps.LatLng(36.450701, 126.570667);
    const map = new window.kakao.maps.Map(mapEl.value, {
      center,
      level: 13,
    });

    // 클러스터러 설정 (SDK clusterer 라이브러리 필요)
    const clusterer = new window.kakao.maps.MarkerClusterer({
      map,
      averageCenter: true,
      minLevel: 6,
      disableClickZoom: false,
      styles: [{
        width: '40px',
        height: '40px',
        background: 'rgba(20, 188, 237, 0.85)',
        borderRadius: '50%',
        color: '#fff',
        textAlign: 'center',
        fontWeight: '700',
        lineHeight: '40px',
        fontSize: '14px',
        border: '2px solid white',
        boxShadow: '0 2px 8px rgba(20,188,237,0.4)',
      }],
    });

    const markers = sampleLocations.map(loc =>
      createMarker(map, loc.lat, loc.lng, loc.title)
    );
    clusterer.addMarkers(markers);

    loading.value = false;
  } catch (e) {
    console.error(e);
    loading.value = false;
    error.value = true;
  }
});
</script>

<style scoped>
.map-wrapper {
  position: relative;
  width: 100%;
}

.map-container {
  width: 100%;
  height: 260px;
  /* bg-gray-200: background-color: rgb(229 231 235) */
  background: rgb(229 231 235);
}

@media (min-width: 768px) {
  .map-container { height: 360px; }
}

@media (min-width: 1024px) {
  .map-container { height: 440px; }
}

.map-loading,
.map-error {
  position: absolute;
  inset: 0;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 8px;
  /* bg-gray-200: background-color: rgb(229 231 235) */
  background: rgb(229 231 235);
  pointer-events: none;
}

.loading-pulse {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  background: var(--brand);
  animation: pulse 1.2s ease-in-out infinite;
}

@keyframes pulse {
  0%, 100% { transform: scale(0.8); opacity: 0.5; }
  50% { transform: scale(1.1); opacity: 1; }
}

.map-loading p,
.map-error p {
  font-size: 13px;
  color: var(--text-secondary);
  margin: 0;
}

.map-error span {
  font-size: 32px;
}

.map-error small {
  font-size: 11px;
  color: var(--text-muted);
}

.cloud-wave-bottom {
  position: absolute;
  bottom: -1px;
  left: 0;
  right: 0;
  height: 64px;
  pointer-events: none;
}

.cloud-wave-bottom svg {
  width: 100%;
  height: 100%;
}
</style>
