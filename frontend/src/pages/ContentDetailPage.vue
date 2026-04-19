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
                        <span class="brand-text">'{{ content?.title }}'</span> 성지 정보
                    </h2>
                </div>

                <!-- 콘텐츠 행 (섹션 사이드바 + 지도) -->
                <div class="content-row">

                    <!-- 지도 -->
                    <div class="map-area">
                        <div class="map-wrap">
                            <MapSection :places="places"/>
                        </div>
                    </div>

                    <!-- 콘텐츠 패널 (모바일: 바텀시트 / 데스크탑: 왼쪽 사이드바) -->
                    <div v-if="!hidePanel" class="content-panel" :style="{ height: sheetHeight + 'px' }">

                        <!-- 모바일 드래그 핸들 -->
                        <div class="mobile-drag" @mousedown="startMobileDrag" @touchstart.prevent="startMobileDrag">
                            <div class="drag-bar"/>
                        </div>

                        <div class="panel-body">

                            <!-- 작품 정보 -->
                            <section class="panel-section">
                                <h3 class="section-heading">
                                    <ion-icon name="film-outline" class="section-icon"/>
                                    작품 정보
                                </h3>
                                <div class="content-card">
                                    <div class="content-poster">
                                        <img v-if="content?.posterImageUrl" :src="content.posterImageUrl" :alt="content.title" class="poster-img"/>
                                        <div v-else class="poster-placeholder">🎬</div>
                                    </div>
                                    <div class="content-meta">
                                        <p class="content-english">{{ content?.englishTitle }}</p>
                                        <p class="content-desc">{{ content?.description }}</p>
                                        <span class="place-count">
                                            <ion-icon name="location-outline"/>
                                            성지 {{ content?.placeCount }}곳
                                        </span>
                                    </div>
                                </div>
                            </section>

                            <!-- 관련 장소 -->
                            <section class="panel-section">
                                <h3 class="section-heading">
                                    <ion-icon name="location-outline" class="section-icon"/>
                                    관련 장소
                                    <span class="count-badge">{{ places.length }}</span>
                                </h3>
                                <div class="place-list">
                                    <div
                                        v-for="place in places"
                                        :key="place.placeIdx"
                                        class="place-item"
                                        @click="router.push({ name: 'PlaceDetail', params: { id: place.placeIdx }, state: { place } })"
                                    >
                                        <div class="place-thumb">
                                            <img v-if="place.placeImageUrl" :src="place.placeImageUrl" :alt="place.name"/>
                                            <div v-else class="thumb-placeholder"/>
                                        </div>
                                        <div class="place-info">
                                            <h4 class="place-name">{{ place.name }}</h4>
                                            <p class="place-address">{{ place.address }}</p>
                                            <div class="place-tags">
                                                <span class="tag-content">{{ content?.title }}</span>
                                                <span class="tag-match">
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
    arrowBackOutline, filmOutline, locationOutline,
    navigateOutline, checkmarkCircleOutline,
} from 'ionicons/icons';
import { contentApi } from '@/api/contentApi';
import MapSection from '@/components/home/MapSection.vue';
import BottomNav from '@/components/common/BottomNav.vue';
import DetailSidebar from '@/components/common/DetailSidebar.vue';
import { useBottomSheet } from '@/composables/useBottomSheet';

addIcons({
    'film-outline': filmOutline,
    'location-outline': locationOutline,
    'navigate-outline': navigateOutline,
    'checkmark-circle-outline': checkmarkCircleOutline,
    'arrow-back-outline': arrowBackOutline,
});

const router = useIonRouter();
const route = useRoute();

const hidePanel = window.history.state?.hidePanel === true;
const { sheetHeight, startDrag: startMobileDrag } = useBottomSheet();

function goHome() { router.push('/home'); }

const contentId = computed(() => Number(route.params.id));
const content = ref<any>(null);
const places = ref<any[]>([]);

async function loadData() {
    const detail = await contentApi.getDetail(contentId.value);
    if (detail) {
        content.value = detail;
        places.value = detail.places ?? [];
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


/* 모바일: main-area가 전체화면 */
.main-area {
    position: absolute;
    inset: 0;
}

/* 헤더: 지도 위 반투명 오버레이 */
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

/* content-row: 모바일에서 전체화면 */
.content-row {
    position: absolute;
    inset: 0;
}

/* 지도: 전체화면 */
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

/* 콘텐츠 패널: 바텀시트 */
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

.count-badge {
    font-size: 11px; font-weight: 400; color: #6b7280;
    background: #f3f4f6;
    padding: 1px 8px; border-radius: 99px; margin-left: 2px;
}

.content-card { display: flex; gap: 14px; }

.content-poster {
    width: 90px; flex-shrink: 0;
    aspect-ratio: 2 / 3;
    border-radius: 10px; overflow: hidden;
    background: #f3f4f6; border: 1px solid #e5e7eb;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.content-poster .poster-img { width: 100%; height: 100%; object-fit: cover; }

.poster-placeholder {
    width: 100%; height: 100%;
    display: flex; align-items: center; justify-content: center;
    font-size: 24px;
}

.content-meta {
    flex: 1; min-width: 0;
    display: flex; flex-direction: column; gap: 5px;
}

.content-english { font-size: 11px; color: #6b7280; margin: 0; }

.content-desc {
    font-size: 12px; color: #4b5563; margin: 0; line-height: 1.6;
    display: -webkit-box; -webkit-line-clamp: 4; -webkit-box-orient: vertical; overflow: hidden;
}

.place-count {
    display: flex; align-items: center; gap: 4px;
    font-size: 12px; font-weight: 600; color: var(--brand, #14BCED);
}

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

.place-thumb {
    width: 56px; height: 56px;
    border-radius: 9px; overflow: hidden; flex-shrink: 0; background: #e5e7eb;
}

.place-thumb img { width: 100%; height: 100%; object-fit: cover; }
.thumb-placeholder { width: 100%; height: 100%; background: #e5e7eb; }

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

/* ══════════════════════════════════════
   데스크탑 (≥1024px)
══════════════════════════════════════ */
@media (min-width: 1024px) {
    /* 전체: 가로 (nav | main-area) */
    .page-layout {
        display: flex;
        flex-direction: row;
        height: 100%;
        width: 100%;
        position: static;
    }

    /* ── main-area: 세로 (헤더 위, 콘텐츠 행 아래) ── */
    .main-area {
        flex: 1;
        min-width: 0;
        position: static;
        display: flex;
        flex-direction: column;
    }

    /* ── 헤더: 일반 흐름, 상단 고정 ── */
    .detail-header {
        position: static;
        flex-shrink: 0;
        background: #fff;
        backdrop-filter: none;
        border-bottom: 1px solid rgba(20, 188, 237, 0.1);
        padding: 14px 24px;
        z-index: auto;
    }

    /* ── 콘텐츠 행: 섹션(왼쪽) + 지도(오른쪽) ── */
    .content-row {
        flex: 1;
        min-height: 0;
        position: static;
        display: flex;
        flex-direction: row;
        overflow: hidden;
    }

    /* ── 섹션 사이드바: 왼쪽 ── */
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

    /* ── 지도: 나머지 공간 채우기 ── */
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
