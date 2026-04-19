<template>
    <ion-page>
        <div class="page-layout">

            <DetailSidebar/>

            <div class="main-area">

                <div class="detail-header">
                    <h2 class="detail-title">지도</h2>
                </div>

                <div class="content-row">

                    <!-- 장소 섹션 패널 -->
                    <Transition name="panel">
                        <div v-if="selectedPlace" class="content-panel" :style="{ height: sheetHeight + 'px' }">
                            <div class="mobile-drag" @mousedown="startMobileDrag" @touchstart.prevent="startMobileDrag">
                                <div class="drag-bar"/>
                            </div>

                            <div class="panel-body">
                                <div class="panel-header">
                                    <h3 class="panel-title">장소 정보</h3>
                                    <button class="close-btn" @click="selectedPlace = null">
                                        <ion-icon name="close-outline"/>
                                    </button>
                                </div>

                                <!-- 장소 카드 -->
                                <section class="panel-section">
                                    <div class="place-card">
                                        <div class="place-thumb">
                                            <img v-if="selectedPlace.placeImageUrl" :src="selectedPlace.placeImageUrl" :alt="selectedPlace.name" class="thumb-img"/>
                                            <div v-else class="thumb-placeholder">📍</div>
                                        </div>
                                        <div class="place-meta">
                                            <h3 class="place-title">{{ selectedPlace.name }}</h3>
                                            <p class="place-english">{{ selectedPlace.englishName }}</p>
                                            <p class="place-address">
                                                <ion-icon name="location-outline" class="addr-icon"/>
                                                {{ selectedPlace.address }}
                                            </p>
                                            <button class="detail-btn" @click="goPlaceDetail(selectedPlace.placeIdx)">
                                                상세 페이지로
                                                <ion-icon name="chevron-forward-outline"/>
                                            </button>
                                        </div>
                                    </div>
                                </section>

                                <!-- 관련 작품 -->
                                <section v-if="placeDetail" class="panel-section">
                                    <h3 class="section-heading">
                                        <ion-icon name="film-outline" class="section-icon"/>
                                        관련 작품
                                    </h3>
                                    <div
                                        class="content-card"
                                        @click="router.push({ name: 'ContentDetail', params: { id: placeDetail.contentIdx } })"
                                    >
                                        <div class="content-poster">
                                            <img v-if="placeDetail.posterImageUrl" :src="placeDetail.posterImageUrl" :alt="placeDetail.title" class="poster-img"/>
                                            <div v-else class="poster-placeholder">🎬</div>
                                        </div>
                                        <div class="content-meta">
                                            <p class="content-name">{{ placeDetail.title }}</p>
                                            <p class="content-english">{{ placeDetail.englishTitle }}</p>
                                            <p class="content-desc">{{ placeDetail.description }}</p>
                                            <span class="place-count">
                                                <ion-icon name="location-outline"/>
                                                성지 {{ placeDetail.placeCount }}곳
                                            </span>
                                        </div>
                                    </div>
                                </section>

                                <!-- 관련 장소 목록 -->
                                <section v-if="placeDetail?.places?.length" class="panel-section">
                                    <h3 class="section-heading">
                                        <ion-icon name="location-outline" class="section-icon"/>
                                        관련 장소
                                        <span class="count-badge">{{ placeDetail.places.length }}</span>
                                    </h3>
                                    <div class="place-list">
                                        <div
                                            v-for="p in placeDetail.places"
                                            :key="p.placeIdx"
                                            class="place-item"
                                            :class="{ 'place-item--current': p.placeIdx === selectedPlace.placeIdx }"
                                            @click="selectPlace(p)"
                                        >
                                            <div class="item-thumb">
                                                <img v-if="p.placeImageUrl" :src="p.placeImageUrl" :alt="p.name"/>
                                                <div v-else class="thumb-placeholder"/>
                                            </div>
                                            <div class="place-info">
                                                <h4 class="place-name">{{ p.name }}</h4>
                                                <p class="place-address-sm">{{ p.address }}</p>
                                                <span v-if="p.placeIdx === selectedPlace.placeIdx" class="tag-current">현재 장소</span>
                                                <span v-else class="tag-match">
                                                    <ion-icon name="checkmark-circle-outline"/>
                                                    성지 매칭
                                                </span>
                                            </div>
                                        </div>
                                    </div>
                                </section>

                                <div v-if="detailLoading" class="detail-loading">
                                    <div class="loading-pulse"/>
                                </div>
                            </div>
                        </div>
                    </Transition>

                    <!-- 지도 -->
                    <div class="map-area">
                        <div ref="mapEl" class="map-el"/>
                        <div v-if="loading" class="map-overlay">
                            <div class="loading-pulse"/>
                            <p>지도 로딩 중...</p>
                        </div>
                    </div>

                </div>
            </div>
        </div>

        <BottomNav active="map"/>
    </ion-page>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { IonPage, IonIcon, useIonRouter } from '@ionic/vue';
import { addIcons } from 'ionicons';
import {
    locationOutline, closeOutline, chevronForwardOutline,
    filmOutline, checkmarkCircleOutline,
} from 'ionicons/icons';
import { placeApi } from '@/api/placeApi';
import { useKakaoMap } from '@/composables/useKakaoMap';
import { useBottomSheet } from '@/composables/useBottomSheet';
import BottomNav from '@/components/common/BottomNav.vue';
import DetailSidebar from '@/components/common/DetailSidebar.vue';

addIcons({
    'location-outline': locationOutline,
    'close-outline': closeOutline,
    'chevron-forward-outline': chevronForwardOutline,
    'film-outline': filmOutline,
    'checkmark-circle-outline': checkmarkCircleOutline,
});

const router = useIonRouter();
const { load, createMarker } = useKakaoMap();
const { sheetHeight, startDrag: startMobileDrag } = useBottomSheet();

function goPlaceDetail(id: number) { router.push({ name: 'PlaceDetail', params: { id } }); }

const mapEl = ref<HTMLDivElement | null>(null);
const loading = ref(true);
const selectedPlace = ref<any>(null);
const placeDetail = ref<any>(null);
const detailLoading = ref(false);

async function selectPlace(place: any) {
    selectedPlace.value = place;
    placeDetail.value = null;
    detailLoading.value = true;
    try {
        placeDetail.value = await placeApi.getDetail(place.placeIdx);
    } finally {
        detailLoading.value = false;
    }
}

onMounted(async () => {
    try {
        await load();
        if (!mapEl.value) return;

        const center = new window.kakao.maps.LatLng(36.450701, 126.570667);
        const map = new window.kakao.maps.Map(mapEl.value, { center, level: 13, maxLevel: 12 });

        const clusterer = new window.kakao.maps.MarkerClusterer({
            map,
            averageCenter: true,
            minLevel: 6,
            disableClickZoom: false,
            styles: [{
                width: '40px', height: '40px',
                background: 'rgba(20, 188, 237, 0.85)',
                borderRadius: '50%', color: '#fff',
                textAlign: 'center', fontWeight: '700',
                lineHeight: '40px', fontSize: '14px',
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
                window.kakao.maps.event.addListener(marker, 'click', () => selectPlace(p));
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
/* ══════════════════════════════════════
   베이스 (모바일)
══════════════════════════════════════ */
.page-layout {
    position: relative;
    width: 100%;
    height: 100%;
}

.main-area {
    position: absolute;
    inset: 0;
    display: flex;
    flex-direction: column;
}

.detail-header {
    flex-shrink: 0;
    position: relative;
    z-index: 100;
    display: flex;
    align-items: center;
    padding: 14px 16px;
    background: rgba(255, 255, 255, 0.92);
    backdrop-filter: blur(8px);
    border-bottom: 1px solid rgba(20, 188, 237, 0.12);
}

.detail-title {
    font-size: 17px; font-weight: 700; color: #111; margin: 0;
}

.content-row {
    flex: 1;
    min-height: 0;
    position: relative;
}

/* 지도: 전체 */
.map-area {
    position: absolute;
    inset: 0;
    z-index: 1;
}

.map-el { width: 100%; height: 100%; }

.map-overlay {
    position: absolute; inset: 0;
    display: flex; flex-direction: column;
    align-items: center; justify-content: center; gap: 10px;
    background: rgb(229 231 235);
    pointer-events: none; z-index: 10;
}

.loading-pulse {
    width: 36px; height: 36px; border-radius: 50%;
    background: var(--brand, #14BCED);
    animation: pulse 1.2s ease-in-out infinite;
}

@keyframes pulse {
    0%, 100% { transform: scale(0.8); opacity: 0.5; }
    50% { transform: scale(1.1); opacity: 1; }
}

.map-overlay p { font-size: 13px; color: #6b7280; margin: 0; }

/* 패널: 모바일 바텀시트 */
.content-panel {
    position: absolute;
    bottom: 0; left: 0; right: 0;
    z-index: 50;
    background: #fff;
    border-radius: 20px 20px 0 0;
    box-shadow: 0 -4px 24px rgba(0, 0, 0, 0.14);
    display: flex; flex-direction: column;
    min-height: 72px;
}

.mobile-drag {
    flex-shrink: 0;
    display: flex; justify-content: center; align-items: center;
    height: 28px; cursor: row-resize;
    touch-action: none; user-select: none;
}

.drag-bar {
    width: 40px; height: 4px;
    border-radius: 99px; background: #d1d5db;
}

.panel-body {
    flex: 1; overflow-y: auto; overscroll-behavior: contain;
}

.panel-header {
    display: flex; align-items: center; justify-content: space-between;
    padding: 4px 16px 0;
}

.panel-title {
    font-size: 14px; font-weight: 700; color: #1f2937; margin: 0;
}

.close-btn {
    width: 30px; height: 30px; border-radius: 50%;
    background: #f3f4f6; border: none;
    display: flex; align-items: center; justify-content: center;
    font-size: 16px; color: #6b7280;
    cursor: pointer; transition: background 0.15s;
}

.close-btn:hover { background: #e5e7eb; }

/* BottomNav */
:deep(.bottom-nav) { position: static; flex-shrink: 0; }

/* ── 패널 트랜지션 ── */
.panel-enter-active, .panel-leave-active {
    transition: transform 0.28s cubic-bezier(0.25, 0.46, 0.45, 0.94), opacity 0.28s ease;
}
.panel-enter-from, .panel-leave-to {
    transform: translateY(100%); opacity: 0;
}

/* ══════════════════════════════════════
   섹션 공통
══════════════════════════════════════ */
.panel-section {
    padding: 12px 16px;
    border-bottom: 1px solid #f3f4f6;
}

.panel-section:last-child { border-bottom: none; }

.section-heading {
    display: flex; align-items: center; gap: 6px;
    font-size: 13px; font-weight: 700; color: #1f2937;
    margin: 0 0 10px; padding-bottom: 8px;
    border-bottom: 1px solid #f3f4f6;
}

.section-icon { color: var(--brand, #14BCED); font-size: 15px; }

.count-badge {
    font-size: 11px; font-weight: 400; color: #6b7280;
    background: #f3f4f6; padding: 1px 8px; border-radius: 99px; margin-left: 2px;
}

.place-card { display: flex; gap: 12px; }

.place-thumb {
    width: 72px; flex-shrink: 0; aspect-ratio: 1;
    border-radius: 10px; overflow: hidden;
    background: #f3f4f6; border: 1px solid #e5e7eb;
    display: flex; align-items: center; justify-content: center;
}

.thumb-img { width: 100%; height: 100%; object-fit: cover; }
.thumb-placeholder { font-size: 24px; }

.place-meta { flex: 1; min-width: 0; display: flex; flex-direction: column; gap: 4px; }

.place-title { font-size: 15px; font-weight: 800; color: #111; margin: 0; }
.place-english { font-size: 11px; color: #6b7280; margin: 0; }

.place-address {
    display: flex; align-items: flex-start; gap: 3px;
    font-size: 11px; color: #4b5563; margin: 0; line-height: 1.5;
}

.addr-icon { font-size: 11px; color: var(--brand, #14BCED); flex-shrink: 0; margin-top: 1px; }

.detail-btn {
    display: flex; align-items: center; gap: 4px;
    margin-top: 2px; padding: 5px 12px;
    background: var(--brand, #14BCED); color: #fff;
    border: none; border-radius: 20px;
    font-size: 11px; font-weight: 700; cursor: pointer;
    width: fit-content; transition: background 0.15s;
}

.detail-btn:hover { background: #0fa8d4; }

.content-card { display: flex; gap: 12px; cursor: pointer; }

.content-poster {
    width: 70px; flex-shrink: 0; aspect-ratio: 2/3;
    border-radius: 8px; overflow: hidden;
    background: #f3f4f6; border: 1px solid #e5e7eb;
}

.poster-img { width: 100%; height: 100%; object-fit: cover; }
.poster-placeholder { width: 100%; height: 100%; display: flex; align-items: center; justify-content: center; font-size: 22px; }

.content-meta { flex: 1; min-width: 0; display: flex; flex-direction: column; gap: 4px; }
.content-name { font-size: 13px; font-weight: 700; color: #1f2937; margin: 0; }
.content-english { font-size: 11px; color: #6b7280; margin: 0; }
.content-desc {
    font-size: 11px; color: #4b5563; margin: 0; line-height: 1.5;
    display: -webkit-box; -webkit-line-clamp: 3; -webkit-box-orient: vertical; overflow: hidden;
}
.place-count { display: flex; align-items: center; gap: 3px; font-size: 11px; font-weight: 600; color: var(--brand, #14BCED); }

.place-list { display: flex; flex-direction: column; gap: 4px; }

.place-item {
    display: flex; align-items: center; gap: 10px;
    padding: 7px 8px; border-radius: 10px;
    border: 1px solid transparent; cursor: pointer;
    transition: background 0.15s, border-color 0.15s;
}

.place-item:hover { background: rgba(20,188,237,0.05); border-color: rgba(20,188,237,0.2); }
.place-item--current { background: rgba(20,188,237,0.06); border-color: rgba(20,188,237,0.25); }

.item-thumb {
    width: 48px; height: 48px; flex-shrink: 0;
    border-radius: 8px; overflow: hidden; background: #e5e7eb;
}

.item-thumb img { width: 100%; height: 100%; object-fit: cover; }

.place-info { flex: 1; min-width: 0; }
.place-name { font-size: 12px; font-weight: 700; color: #1f2937; margin: 0 0 2px; }
.place-address-sm { font-size: 10px; color: #6b7280; margin: 0 0 3px; overflow: hidden; white-space: nowrap; text-overflow: ellipsis; }

.tag-current { font-size: 10px; background: rgba(20,188,237,0.12); color: var(--brand,#14BCED); padding: 2px 7px; border-radius: 6px; font-weight: 600; }
.tag-match { display: flex; align-items: center; gap: 3px; font-size: 10px; color: var(--brand,#14BCED); font-weight: 600; }

.detail-loading {
    display: flex; justify-content: center; padding: 20px;
}

/* ══════════════════════════════════════
   데스크탑 (≥1024px)
══════════════════════════════════════ */
@media (min-width: 1024px) {
    .page-layout {
        display: flex; flex-direction: row;
        position: static; height: 100%; width: 100%;
    }

    .main-area {
        flex: 1; min-width: 0; position: static;
    }

    .detail-header {
        background: #fff; backdrop-filter: none;
        border-bottom: 1px solid rgba(20,188,237,0.1);
        padding: 14px 24px;
    }

    .content-row {
        flex: 1; min-height: 0; position: static;
        display: flex; flex-direction: row; overflow: hidden;
    }

    /* 패널: 왼쪽 사이드바 */
    .content-panel {
        position: static !important;
        width: 360px; flex-shrink: 0;
        height: 100% !important;
        border-radius: 0; box-shadow: none;
        border-right: 1px solid rgba(20,188,237,0.12);
        order: 1;
    }

    .mobile-drag { display: none; }
    .panel-body { overflow-y: auto; height: 100%; }

    /* 패널 트랜지션: 데스크탑은 왼쪽에서 슬라이드 */
    .panel-enter-from, .panel-leave-to {
        transform: translateX(-100%); opacity: 0;
    }

    /* 지도 */
    .map-area {
        flex: 1; min-width: 0; position: relative; order: 2;
    }

    :deep(.bottom-nav) { display: none; }
}
</style>
