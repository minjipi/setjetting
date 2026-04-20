<template>
  <div class="map-wrapper">
    <div ref="mapEl" class="map-container" />

    <div v-if="loading" class="map-loading">
      <div class="loading-pulse" />
      <p>지도 로딩 중...</p>
    </div>

    <div v-if="error" class="map-error">
      <span>🗺️</span>
      <p>지도를 불러올 수 없습니다</p>
      <small>VITE_KAKAO_MAP_KEY를 확인해주세요</small>
    </div>

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
import { ref, watch, onMounted } from 'vue';
import { useIonRouter } from '@ionic/vue';
import { useKakaoMap } from '@/composables/useKakaoMap';

interface Place {
  placeIdx: number;
  name: string;
  latitude: number;
  longitude: number;
}

const props = defineProps<{
  mapHeight?: string;
  centerLat?: number;
  centerLng?: number;
  zoomLevel?: number;
  places?: Place[];
}>();

const router = useIonRouter();
const { load, createMarker } = useKakaoMap();

const mapEl = ref<HTMLDivElement | null>(null);
const loading = ref(true);
const error = ref(false);

let mapInstance: any = null;
let clustererInstance: any = null;

const clusterStyle = [{
  width: '40px', height: '40px',
  background: 'rgba(20, 188, 237, 0.85)',
  borderRadius: '50%', color: '#fff',
  textAlign: 'center', fontWeight: '700',
  lineHeight: '40px', fontSize: '14px',
  border: '2px solid white',
  boxShadow: '0 2px 8px rgba(20,188,237,0.4)',
}];

function drawMarkers(places: Place[]) {
  if (!mapInstance) return;
  if (clustererInstance) clustererInstance.clear();

  const validPlaces = places.filter(p => p.latitude && p.longitude);
  if (!validPlaces.length) return;

  if (!clustererInstance) {
    clustererInstance = new window.kakao.maps.MarkerClusterer({
      map: mapInstance,
      averageCenter: true,
      minLevel: 6,
      disableClickZoom: false,
      styles: clusterStyle,
    });
  }

  const markers = validPlaces.map(p => {
    const marker = createMarker(mapInstance, p.latitude, p.longitude, p.name);
    window.kakao.maps.event.addListener(marker, 'click', () => {
      router.push({ name: 'Map', query: { placeId: p.placeIdx } });
    });
    return marker;
  });

  clustererInstance.addMarkers(markers);
}

watch(() => props.places, (places) => {
  drawMarkers(places ?? []);
}, { deep: true });

onMounted(async () => {
  try {
    await load();
    if (!mapEl.value) return;

    const centerLat = props.centerLat ?? 36.450701;
    const centerLng = props.centerLng ?? 126.570667;
    const center = new window.kakao.maps.LatLng(centerLat, centerLng);
    mapInstance = new window.kakao.maps.Map(mapEl.value, {
      center,
      level: props.zoomLevel ?? 13,
      maxLevel: 12,
    });

    loading.value = false;
    drawMarkers(props.places ?? []);
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
  height: v-bind('props.mapHeight');
  background: rgb(229 231 235);
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
  background: rgb(229 231 235);
  pointer-events: none;
}

.loading-pulse {
  width: 36px; height: 36px;
  border-radius: 50%;
  background: var(--brand);
  animation: pulse 1.2s ease-in-out infinite;
}

@keyframes pulse {
  0%, 100% { transform: scale(0.8); opacity: 0.5; }
  50% { transform: scale(1.1); opacity: 1; }
}

.map-loading p, .map-error p {
  font-size: 13px; color: var(--text-secondary); margin: 0;
}

.map-error span { font-size: 32px; }
.map-error small { font-size: 11px; color: var(--text-muted); }

.cloud-wave-bottom {
  position: absolute;
  bottom: -1px; left: 0; right: 0;
  height: 64px;
  pointer-events: none;
}

.cloud-wave-bottom svg { width: 100%; height: 100%; }
</style>
