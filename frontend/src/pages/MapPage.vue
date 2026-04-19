<template>
    <ion-page>
        <!-- 헤더 -->
        <div class="map-header">
            <h2 class="map-title">지도</h2>
        </div>

        <!-- 지도 영역 -->
        <div class="map-body">
            <div ref="mapEl" class="map-el"/>

            <!-- 로딩 -->
            <div v-if="loading" class="map-overlay">
                <div class="loading-pulse"/>
                <p>지도 로딩 중...</p>
            </div>

            <!-- 선택된 장소 바텀시트 -->
            <Transition name="sheet">
                <div v-if="selectedPlace" class="place-sheet">
                    <button class="close-btn" @click="selectedPlace = null">
                        <ion-icon name="close-outline"/>
                    </button>
                    <div class="sheet-inner">
                        <div class="place-thumb">
                            <img v-if="selectedPlace.placeImageUrl" :src="selectedPlace.placeImageUrl" :alt="selectedPlace.name"/>
                            <div v-else class="thumb-fallback">📍</div>
                        </div>
                        <div class="place-info">
                            <h3 class="place-name">{{ selectedPlace.name }}</h3>
                            <p class="place-english">{{ selectedPlace.englishName }}</p>
                            <p class="place-address">
                                <ion-icon name="location-outline"/>
                                {{ selectedPlace.address }}
                            </p>
                        </div>
                        <button class="detail-btn" @click="goPlaceDetail(selectedPlace.placeIdx)">
                            상세보기
                            <ion-icon name="chevron-forward-outline"/>
                        </button>
                    </div>
                </div>
            </Transition>
        </div>

        <!-- 하단 네비게이션 -->
        <BottomNav active="map"/>
    </ion-page>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { IonPage, IonIcon, useIonRouter } from '@ionic/vue';
import { addIcons } from 'ionicons';
import { locationOutline, closeOutline, chevronForwardOutline } from 'ionicons/icons';
import { placeApi } from '@/api/placeApi';
import { useKakaoMap } from '@/composables/useKakaoMap';
import BottomNav from '@/components/common/BottomNav.vue';

addIcons({ 'location-outline': locationOutline, 'close-outline': closeOutline, 'chevron-forward-outline': chevronForwardOutline });

const router = useIonRouter();
const { load, createMarker } = useKakaoMap();

function goPlaceDetail(id: number) { router.push({ name: 'PlaceDetail', params: { id }, state: { hidePanel: true } }); }

const mapEl = ref<HTMLDivElement | null>(null);
const loading = ref(true);
const selectedPlace = ref<any>(null);

onMounted(async () => {
    try {
        await load();
        if (!mapEl.value) return;

        const center = new window.kakao.maps.LatLng(36.450701, 126.570667);
        const map = new window.kakao.maps.Map(mapEl.value, { center, level: 13 });

        const clusterer = new window.kakao.maps.MarkerClusterer({
            map,
            averageCenter: true,
            minLevel: 6,
            disableClickZoom: false,
            styles: [{
                width: '40px', height: '40px',
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

        const places = await placeApi.getList();
        loading.value = false;

        if (!places?.length) return;

        const markers = places
            .filter((p: any) => p.latitude && p.longitude)
            .map((p: any) => {
                const marker = createMarker(map, p.latitude, p.longitude, p.name);
                window.kakao.maps.event.addListener(marker, 'click', () => {
                    goPlaceDetail(p.placeIdx);
                });
                return marker;
            });

        clusterer.addMarkers(markers);
    } catch (e) {
        console.error(e);
        loading.value = false;
    }
});
</script>

<style scoped>
/* ion-page는 이미 flex column — 헤더/지도/네비가 순서대로 쌓임 */

/* BottomNav의 position:fixed를 flow로 되돌려 레이아웃에 포함 */
:deep(.bottom-nav) {
    position: static;
    flex-shrink: 0;
}

.map-header {
    flex-shrink: 0;
    display: flex;
    align-items: center;
    padding: 14px 20px;
    background: #fff;
    border-bottom: 1px solid rgba(20, 188, 237, 0.12);
    box-shadow: 0 2px 8px rgba(20, 188, 237, 0.06);
}

.map-title {
    font-size: 17px;
    font-weight: 700;
    color: #111;
    margin: 0;
}

.map-body {
    flex: 1;
    min-height: 0;
    position: relative;
    overflow: hidden;
}

.map-el {
    width: 100%;
    height: 100%;
}

.map-overlay {
    position: absolute;
    inset: 0;
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    gap: 10px;
    background: rgb(229 231 235);
    pointer-events: none;
    z-index: 10;
}

.loading-pulse {
    width: 36px; height: 36px;
    border-radius: 50%;
    background: var(--brand, #14BCED);
    animation: pulse 1.2s ease-in-out infinite;
}

@keyframes pulse {
    0%, 100% { transform: scale(0.8); opacity: 0.5; }
    50% { transform: scale(1.1); opacity: 1; }
}

.map-overlay p {
    font-size: 13px;
    color: #6b7280;
    margin: 0;
}

/* ── 장소 바텀시트 ── */
.place-sheet {
    position: absolute;
    bottom: 0;
    left: 0;
    right: 0;
    z-index: 100;
    background: #fff;
    border-radius: 20px 20px 0 0;
    box-shadow: 0 -4px 24px rgba(0, 0, 0, 0.16);
    padding: 16px 16px 20px;
}

.close-btn {
    position: absolute;
    top: 12px;
    right: 12px;
    width: 30px; height: 30px;
    border-radius: 50%;
    background: #f3f4f6;
    border: none;
    display: flex; align-items: center; justify-content: center;
    font-size: 16px;
    color: #6b7280;
    cursor: pointer;
    transition: background 0.15s;
}

.close-btn:hover { background: #e5e7eb; }

.sheet-inner {
    display: flex;
    align-items: center;
    gap: 14px;
    padding-right: 32px;
}

.place-thumb {
    width: 64px; height: 64px;
    flex-shrink: 0;
    border-radius: 12px;
    overflow: hidden;
    background: #f3f4f6;
    border: 1px solid #e5e7eb;
}

.place-thumb img {
    width: 100%; height: 100%;
    object-fit: cover;
}

.thumb-fallback {
    width: 100%; height: 100%;
    display: flex; align-items: center; justify-content: center;
    font-size: 28px;
}

.place-info {
    flex: 1;
    min-width: 0;
    display: flex;
    flex-direction: column;
    gap: 3px;
}

.place-name {
    font-size: 15px;
    font-weight: 700;
    color: #111;
    margin: 0;
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
}

.place-english {
    font-size: 11px;
    color: #6b7280;
    margin: 0;
}

.place-address {
    display: flex;
    align-items: flex-start;
    gap: 3px;
    font-size: 11px;
    color: #4b5563;
    margin: 0;
    line-height: 1.4;
}

.place-address ion-icon {
    font-size: 11px;
    color: var(--brand, #14BCED);
    flex-shrink: 0;
    margin-top: 1px;
}

.detail-btn {
    flex-shrink: 0;
    display: flex;
    align-items: center;
    gap: 4px;
    padding: 8px 14px;
    background: var(--brand, #14BCED);
    color: #fff;
    border: none;
    border-radius: 20px;
    font-size: 12px;
    font-weight: 700;
    cursor: pointer;
    white-space: nowrap;
    transition: background 0.15s;
}

.detail-btn:hover { background: #0fa8d4; }

/* ── 바텀시트 트랜지션 ── */
.sheet-enter-active,
.sheet-leave-active {
    transition: transform 0.25s ease, opacity 0.25s ease;
}

.sheet-enter-from,
.sheet-leave-to {
    transform: translateY(100%);
    opacity: 0;
}

/* ── 데스크탑 ── */
@media (min-width: 1024px) {
    :deep(.bottom-nav) {
        display: none;
    }

    .place-sheet {
        left: auto;
        right: 24px;
        bottom: 24px;
        width: 360px;
        border-radius: 16px;
        box-shadow: 0 8px 32px rgba(0, 0, 0, 0.16);
    }
}
</style>
