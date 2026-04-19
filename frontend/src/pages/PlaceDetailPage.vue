<template>
    <ion-page>
        <div class="page-layout">

            <!-- ── 아이콘 사이드바 (데스크탑) ── -->
            <DetailSidebar/>

            <!-- ── 메인 영역 (헤더 + 콘텐츠 행) ── -->
            <div class="main-area">

                <!-- 헤더 -->
                <div class="detail-header">
                    <button class="back-btn" @click="goHome">
                        <ion-icon name="arrow-back-outline"/>
                    </button>
                    <h2 class="detail-title">
                        <span class="brand-text">'{{ place?.name }}'</span> 성지 정보
                    </h2>
                </div>

                <!-- 콘텐츠 행 (섹션 사이드바 + 지도) -->
                <div class="content-row">

                    <!-- 지도 -->
                    <div class="map-area">
                        <div class="map-wrap">
                            <MapSection
                                v-if="place"
                                :center-lat="place.latitude"
                                :center-lng="place.longitude"
                                :zoom-level="5"
                                :places="parentContent?.places ?? []"
                            />
                            <div v-else class="map-placeholder"/>
                        </div>
                    </div>

                    <!-- 콘텐츠 패널 (모바일: 바텀시트 / 데스크탑: 왼쪽 사이드바) -->
                    <div v-if="!hidePanel" class="content-panel" :style="{ height: sheetHeight + 'px' }">

                        <!-- 모바일 드래그 핸들 -->
                        <div class="mobile-drag" @mousedown="startMobileDrag" @touchstart.prevent="startMobileDrag">
                            <div class="drag-bar"/>
                        </div>

                        <div class="panel-body">

                            <!-- 장소 정보 -->
                            <section class="panel-section">
                                <h3 class="section-heading">
                                    <ion-icon name="location-outline" class="section-icon"/>
                                    장소 정보
                                </h3>
                                <div class="place-card">
                                    <div class="place-thumb">
                                        <img v-if="place?.placeImageUrl" :src="place.placeImageUrl" :alt="place.name" class="thumb-img"/>
                                        <div v-else class="thumb-placeholder">📍</div>
                                    </div>
                                    <div class="place-meta">
                                        <h3 class="place-title">{{ place?.name }}</h3>
                                        <p class="place-english">{{ place?.englishName }}</p>
                                        <p class="place-address">
                                            <ion-icon name="location-outline" class="addr-icon"/>
                                            {{ place?.address }}
                                        </p>
                                        <button class="map-btn">
                                            <ion-icon name="navigate-outline"/>
                                            지도에서 보기
                                        </button>
                                    </div>
                                </div>
                            </section>

                            <!-- 관련 작품 -->
                            <section class="panel-section" v-if="parentContent">
                                <h3 class="section-heading">
                                    <ion-icon name="film-outline" class="section-icon"/>
                                    관련 작품
                                </h3>
                                <div
                                    class="content-card"
                                    @click="router.push({ name: 'ContentDetail', params: { id: parentContent.idx } })"
                                >
                                    <div class="content-poster">
                                        <img v-if="parentContent.posterImageUrl" :src="parentContent.posterImageUrl" :alt="parentContent.title" class="poster-img"/>
                                        <div v-else class="poster-placeholder">🎬</div>
                                        <span class="match-badge">
                                            <ion-icon name="checkmark-circle-outline"/>
                                            성지 매칭
                                        </span>
                                    </div>
                                    <div class="content-meta">
                                        <p class="content-name">{{ parentContent.title }}</p>
                                        <p class="content-english">{{ parentContent.englishTitle }}</p>
                                        <p class="content-desc">{{ parentContent.description }}</p>
                                        <span class="place-count">
                                            <ion-icon name="location-outline"/>
                                            성지 {{ parentContent.placeCount }}곳
                                        </span>
                                    </div>
                                </div>
                            </section>

                            <!-- 작품의 장소 목록 -->
                            <section class="panel-section" v-if="parentContent?.places?.length">
                                <h3 class="section-heading">
                                    <ion-icon name="location-outline" class="section-icon"/>
                                    관련 장소
                                    <span class="count-badge">{{ parentContent.places.length }}</span>
                                </h3>
                                <div class="place-list">
                                    <div
                                        v-for="p in parentContent.places"
                                        :key="p.placeIdx"
                                        class="place-item"
                                        :class="{ 'place-item--current': p.placeIdx === placeId }"
                                        @click="router.push({ name: 'PlaceDetail', params: { id: p.placeIdx } })"
                                    >
                                        <div class="place-thumb">
                                            <img v-if="p.placeImageUrl" :src="p.placeImageUrl" :alt="p.name"/>
                                            <div v-else class="thumb-placeholder"/>
                                        </div>
                                        <div class="place-info">
                                            <h4 class="place-name">{{ p.name }}</h4>
                                            <p class="place-address">{{ p.address }}</p>
                                            <div class="place-tags">
                                                <span class="tag-content">{{ parentContent.title }}</span>
                                                <span v-if="p.placeIdx === placeId" class="tag-current">현재 장소</span>
                                                <span v-else class="tag-match">
                                                    <ion-icon name="checkmark-circle-outline"/>
                                                    성지 매칭
                                                </span>
                                            </div>
                                        </div>
                                        <button class="nav-btn" @click.stop>
                                            <ion-icon name="navigate-outline"/>
                                        </button>
                                    </div>
                                </div>
                            </section>

                        </div>
                    </div>
                </div>
            </div>
        </div>

        <BottomNav active="map"/>
    </ion-page>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue';
import { IonPage, IonIcon, useIonRouter } from '@ionic/vue';
import { useRoute } from 'vue-router';
import { addIcons } from 'ionicons';
import {
    arrowBackOutline, locationOutline, filmOutline,
    navigateOutline, checkmarkCircleOutline,
} from 'ionicons/icons';
import { placeApi } from '@/api/placeApi';
import MapSection from '@/components/home/MapSection.vue';
import BottomNav from '@/components/common/BottomNav.vue';
import DetailSidebar from '@/components/common/DetailSidebar.vue';
import { useBottomSheet } from '@/composables/useBottomSheet';

addIcons({
    'arrow-back-outline': arrowBackOutline,
    'location-outline': locationOutline,
    'film-outline': filmOutline,
    'navigate-outline': navigateOutline,
    'checkmark-circle-outline': checkmarkCircleOutline,
});

const router = useIonRouter();
const route = useRoute();
const hidePanel = window.history.state?.hidePanel === true;
const { sheetHeight, startDrag: startMobileDrag } = useBottomSheet();

function goHome() { router.push('/home'); }

const placeId = computed(() => Number(route.params.id));
const place = ref<any>(null);
const parentContent = ref<any>(null);

async function loadData() {
    const detail = await placeApi.getDetail(placeId.value);
    if (detail) {
        parentContent.value = detail;
        place.value = detail.places?.find((p: any) => p.placeIdx === placeId.value) ?? detail.places?.[0] ?? null;
    }
}

onMounted(() => { loadData(); });
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
}

.detail-header {
    position: absolute;
    top: 0; left: 0; right: 0;
    z-index: 100;
    display: flex;
    align-items: center;
    gap: 12px;
    padding: 14px 16px;
    background: rgba(255, 255, 255, 0.92);
    backdrop-filter: blur(8px);
    border-bottom: 1px solid rgba(20, 188, 237, 0.12);
}

.back-btn {
    flex-shrink: 0;
    width: 36px; height: 36px;
    border-radius: 50%;
    background: #f3f4f6; border: none;
    display: flex; align-items: center; justify-content: center;
    font-size: 18px; color: #4b5563;
    cursor: pointer; transition: background 0.15s;
}

.back-btn:hover { background: #e5e7eb; }

.detail-title {
    font-size: 17px; font-weight: 700;
    color: var(--text-primary, #111);
    margin: 0; white-space: nowrap;
    overflow: hidden; text-overflow: ellipsis;
}

.brand-text { color: var(--brand, #14BCED); }

.content-row {
    position: absolute;
    inset: 0;
}

.map-area {
    position: absolute;
    inset: 0;
    z-index: 1;
}

.map-wrap {
    position: absolute;
    inset: 0;
}

.map-wrap :deep(.map-wrapper) { height: 100%; }
.map-wrap :deep(.map-container) { height: 100%; }

.map-placeholder {
    width: 100%; height: 100%;
    background: #e5e7eb;
}

.content-panel {
    position: absolute;
    bottom: 60px; left: 0; right: 0;
    z-index: 50;
    background: #fff;
    border-radius: 20px 20px 0 0;
    box-shadow: 0 -4px 24px rgba(0, 0, 0, 0.14);
    display: flex;
    flex-direction: column;
    min-height: 72px;
}

.mobile-drag {
    flex-shrink: 0;
    display: flex;
    justify-content: center;
    align-items: center;
    height: 28px;
    cursor: row-resize;
    touch-action: none;
    user-select: none;
}

.drag-bar {
    width: 40px; height: 4px;
    border-radius: 99px;
    background: #d1d5db;
}

.panel-body {
    flex: 1;
    overflow-y: auto;
    overscroll-behavior: contain;
}

/* ══════════════════════════════════════
   섹션 공통
══════════════════════════════════════ */
.panel-section {
    padding: 16px 16px 12px;
    border-bottom: 1px solid #f3f4f6;
}

.panel-section:last-child { border-bottom: none; }

.section-heading {
    display: flex; align-items: center; gap: 6px;
    font-size: 14px; font-weight: 700; color: #1f2937;
    margin: 0 0 12px; padding-bottom: 10px;
    border-bottom: 1px solid #f3f4f6;
}

.section-icon { color: var(--brand, #14BCED); font-size: 16px; }

.place-card { display: flex; gap: 14px; }

.place-thumb {
    width: 80px; flex-shrink: 0;
    aspect-ratio: 1;
    border-radius: 12px; overflow: hidden;
    background: #f3f4f6; border: 1px solid #e5e7eb;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.thumb-img { width: 100%; height: 100%; object-fit: cover; }

.thumb-placeholder {
    width: 100%; height: 100%;
    display: flex; align-items: center; justify-content: center;
    font-size: 28px;
}

/* ── 장소 목록 ── */
.place-list { display: flex; flex-direction: column; gap: 6px; }

.place-item {
    display: flex; align-items: center; gap: 12px;
    padding: 8px 10px; border-radius: 12px;
    border: 1px solid transparent; cursor: pointer;
    transition: background 0.15s, border-color 0.15s;
}

.place-item:hover {
    background: rgba(20, 188, 237, 0.05);
    border-color: rgba(20, 188, 237, 0.2);
}

.place-item--current {
    background: rgba(20, 188, 237, 0.06);
    border-color: rgba(20, 188, 237, 0.25);
}

.place-item .place-thumb {
    width: 56px; height: 56px;
    aspect-ratio: unset;
    border-radius: 9px;
    box-shadow: none;
}

.place-item .place-thumb img { width: 100%; height: 100%; object-fit: cover; }

.place-info { flex: 1; min-width: 0; }

.place-name {
    font-size: 13px; font-weight: 700; color: #1f2937;
    margin: 0 0 2px; transition: color 0.15s;
}

.place-item:hover .place-name { color: var(--brand, #14BCED); }

.place-address {
    font-size: 11px; color: #6b7280; margin: 0 0 4px;
    overflow: hidden; white-space: nowrap; text-overflow: ellipsis;
}

.place-tags { display: flex; align-items: center; gap: 6px; }

.tag-content {
    font-size: 10px; background: #f3f4f6; color: #374151;
    padding: 2px 7px; border-radius: 6px;
}

.tag-current {
    font-size: 10px; background: rgba(20, 188, 237, 0.12); color: var(--brand, #14BCED);
    padding: 2px 7px; border-radius: 6px; font-weight: 600;
}

.tag-match {
    display: flex; align-items: center; gap: 3px;
    font-size: 10px; color: var(--brand, #14BCED); font-weight: 600;
}

.nav-btn {
    flex-shrink: 0; width: 30px; height: 30px; border-radius: 50%;
    border: 1px solid #e5e7eb; background: #fff; color: #9ca3af;
    display: flex; align-items: center; justify-content: center;
    font-size: 14px; cursor: pointer;
    transition: color 0.15s, border-color 0.15s;
}

.nav-btn:hover { color: var(--brand, #14BCED); border-color: var(--brand, #14BCED); }

.count-badge {
    font-size: 11px; font-weight: 400; color: #6b7280;
    background: #f3f4f6; padding: 1px 8px; border-radius: 99px; margin-left: 2px;
}

.place-meta {
    flex: 1; min-width: 0;
    display: flex; flex-direction: column; gap: 5px;
}

.place-title { font-size: 16px; font-weight: 800; color: #111; margin: 0; }
.place-english { font-size: 11px; color: #6b7280; margin: 0; }

.place-address {
    display: flex; align-items: flex-start; gap: 4px;
    font-size: 11px; color: #4b5563; margin: 0; line-height: 1.5;
}

.addr-icon { font-size: 12px; color: var(--brand, #14BCED); flex-shrink: 0; margin-top: 1px; }

.map-btn {
    display: flex; align-items: center; gap: 5px;
    margin-top: 4px; padding: 6px 12px;
    background: var(--brand, #14BCED); color: #fff;
    border: none; border-radius: 20px;
    font-size: 11px; font-weight: 700;
    cursor: pointer; width: fit-content;
    transition: background 0.15s;
}

.map-btn:hover { background: #0fa8d4; }

.content-card {
    display: flex; gap: 14px; cursor: pointer;
}

.content-poster {
    width: 80px; flex-shrink: 0;
    aspect-ratio: 2 / 3;
    border-radius: 10px; overflow: hidden;
    background: #f3f4f6; border: 1px solid #e5e7eb;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
    position: relative;
}

.content-poster .poster-img { width: 100%; height: 100%; object-fit: cover; }

.poster-placeholder {
    width: 100%; height: 100%;
    display: flex; align-items: center; justify-content: center;
    font-size: 24px;
}

.match-badge {
    position: absolute; bottom: 5px; left: 50%; transform: translateX(-50%);
    display: flex; align-items: center; gap: 3px;
    font-size: 9px; font-weight: 700; color: #fff;
    background: rgba(20, 188, 237, 0.9);
    padding: 2px 6px; border-radius: 99px;
    white-space: nowrap; backdrop-filter: blur(4px);
}

.match-badge ion-icon { font-size: 9px; }

.content-meta {
    flex: 1; min-width: 0;
    display: flex; flex-direction: column; gap: 5px;
}

.content-name { font-size: 14px; font-weight: 700; color: #1f2937; margin: 0; }
.content-english { font-size: 11px; color: #6b7280; margin: 0; }

.content-desc {
    font-size: 12px; color: #4b5563; margin: 0; line-height: 1.6;
    display: -webkit-box; -webkit-line-clamp: 4; -webkit-box-orient: vertical; overflow: hidden;
}

.place-count {
    display: flex; align-items: center; gap: 4px;
    font-size: 12px; font-weight: 600; color: var(--brand, #14BCED);
}

/* ══════════════════════════════════════
   데스크탑 (≥1024px)
══════════════════════════════════════ */
@media (min-width: 1024px) {
    .page-layout {
        display: flex;
        flex-direction: row;
        height: 100%;
        width: 100%;
        position: static;
    }

    /* main-area: 세로 (헤더 위, 콘텐츠 행 아래) */
    .main-area {
        flex: 1;
        min-width: 0;
        position: static;
        display: flex;
        flex-direction: column;
    }

    /* 헤더: 상단 고정, 일반 흐름 */
    .detail-header {
        position: static;
        flex-shrink: 0;
        background: #fff;
        backdrop-filter: none;
        border-bottom: 1px solid rgba(20, 188, 237, 0.1);
        padding: 14px 24px;
        z-index: auto;
    }

    /* 콘텐츠 행: 섹션(왼쪽) + 지도(오른쪽) */
    .content-row {
        flex: 1;
        min-height: 0;
        position: static;
        display: flex;
        flex-direction: row;
        overflow: hidden;
    }

    /* 섹션 사이드바: 왼쪽 */
    .content-panel {
        position: static !important;
        width: 360px;
        flex-shrink: 0;
        height: 100% !important;
        border-radius: 0;
        box-shadow: none;
        border-right: 1px solid rgba(20, 188, 237, 0.12);
        background: #fff;
        display: flex;
        flex-direction: column;
        overflow: hidden;
        order: 1;
    }

    .mobile-drag { display: none; }

    .panel-body {
        overflow-y: auto;
        height: 100%;
    }

    /* 지도: 나머지 공간 */
    .map-area {
        flex: 1;
        min-width: 0;
        position: relative;
        order: 2;
    }

    .map-wrap {
        position: absolute;
        inset: 0;
    }

    .map-wrap :deep(.map-wrapper) { height: 100%; }
    .map-wrap :deep(.map-container) { height: 100%; }
}
</style>
