<template>
    <ion-page>
        <ion-content :fullscreen="true" class="map-content">
            <div class="page-layout">

                <!-- ── 데스크탑 사이드바 (≥1024px) ── -->
                <Sidebar active="map"/>

                <!-- ── 메인 영역 ── -->
                <div class="main-wrap">

                    <!-- 상단 바 -->
                    <TopBar v-model="searchQuery" :title="headerTitle" @back="handleHeaderBack"/>

                    <!-- 지도 + 패널 영역 -->
                    <div class="content-row">

                        <!-- 장소/작품 상세 패널 -->
                        <Transition name="panel">
                            <div v-if="selectedPlace" class="content-panel" :style="{ height: sheetHeight + 'px' }">
                                <div class="mobile-drag" @mousedown="startMobileDrag" @touchstart="startMobileDrag">
                                    <div class="drag-bar"/>
                                </div>
                                <div class="panel-body">

                                    <!-- ── 장소 상세 뷰 ── -->
                                    <template v-if="panelView === 'place'">
                                        <div class="panel-header">
                                            <h3 class="panel-title">장소 정보</h3>
                                            <div class="panel-actions">
                                                <button class="icon-btn" :class="{ 'icon-btn--active': bookmarked }" @click="toggleBookmark" :title="bookmarked ? '북마크 해제' : '북마크'">
                                                    <ion-icon :name="bookmarked ? 'bookmark' : 'bookmark-outline'"/>
                                                </button>
                                                <button class="icon-btn" @click="closePanel">
                                                    <ion-icon name="close-outline"/>
                                                </button>
                                            </div>
                                        </div>

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
                                                </div>
                                            </div>
                                        </section>

                                        <section v-if="currentPlaceScenes.length" class="panel-section scene-section">
                                            <div
                                                class="scene-image-wrap"
                                                @touchstart.passive="onSceneTouchStart"
                                                @touchend.passive="onSceneTouchEnd"
                                            >
                                                <Transition name="scene-fade" mode="out-in">
                                                    <img
                                                        v-if="currentPlaceScene.sceneImageUrl"
                                                        :key="currentSceneIndex"
                                                        :src="currentPlaceScene.sceneImageUrl"
                                                        alt="장면 이미지"
                                                        class="scene-image"
                                                    />
                                                    <div v-else :key="'ph-' + currentSceneIndex" class="scene-image-placeholder"/>
                                                </Transition>

                                                <button v-if="currentSceneIndex > 0" class="scene-nav scene-nav--prev" @click.stop="currentSceneIndex--">
                                                    <ion-icon name="chevron-back-outline"/>
                                                </button>
                                                <button v-if="currentSceneIndex < currentPlaceScenes.length - 1" class="scene-nav scene-nav--next" @click.stop="currentSceneIndex++">
                                                    <ion-icon name="chevron-forward-outline"/>
                                                </button>

                                                <div class="scene-info-bar">
                                                    <span v-if="currentPlaceScene.episodeNumber" class="scene-badge episode-badge">
                                                        {{ currentPlaceScene.episodeNumber }}화
                                                    </span>
                                                    <span v-if="currentPlaceScene.appearTime != null" class="scene-timecode">
                                                        {{ formatTimestamp(currentPlaceScene.appearTime) }}
                                                    </span>
                                                    <span v-if="currentPlaceScene.sceneDescription" class="scene-description">
                                                        {{ currentPlaceScene.sceneDescription }}
                                                    </span>
                                                </div>

                                                <div v-if="currentPlaceScenes.length > 1" class="scene-dots">
                                                    <span
                                                        v-for="(_, i) in currentPlaceScenes"
                                                        :key="i"
                                                        class="scene-dot"
                                                        :class="{ 'scene-dot--active': i === currentSceneIndex }"
                                                        @click.stop="currentSceneIndex = i"
                                                    />
                                                </div>
                                            </div>
                                        </section>

                                        <section v-if="placeDetail && !detailLoading" class="panel-section">
                                            <h3 class="section-heading">
                                                <ion-icon name="film-outline" class="section-icon"/>
                                                관련 작품
                                            </h3>
                                            <div class="content-card" @click="switchToContentView()">
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
                                                <ion-icon name="chevron-forward-outline" class="card-arrow"/>
                                            </div>
                                        </section>

                                        <section v-if="placeDetail?.places?.length && !detailLoading" class="panel-section">
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
                                                    @click="switchToPlace(p)"
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
                                    </template>

                                    <!-- ── 작품 상세 뷰 ── -->
                                    <template v-else-if="panelView === 'content'">
                                        <div class="panel-header">
                                            <button class="icon-btn" @click="panelView = 'place'">
                                                <ion-icon name="arrow-back-outline"/>
                                            </button>
                                            <h3 class="panel-title panel-title--content">{{ placeDetail?.title }}</h3>
                                            <button class="icon-btn" @click="closePanel">
                                                <ion-icon name="close-outline"/>
                                            </button>
                                        </div>

                                        <section class="panel-section">
                                            <div class="content-card-full">
                                                <div class="content-poster-lg">
                                                    <img v-if="placeDetail?.posterImageUrl" :src="placeDetail.posterImageUrl" :alt="placeDetail.title" class="poster-img"/>
                                                    <div v-else class="poster-placeholder">🎬</div>
                                                </div>
                                                <div class="content-meta">
                                                    <p class="content-english">{{ placeDetail?.englishTitle }}</p>
                                                    <p class="content-desc content-desc--full">{{ placeDetail?.description }}</p>
                                                    <span class="place-count">
                                                        <ion-icon name="location-outline"/>
                                                        성지 {{ placeDetail?.placeCount }}곳
                                                    </span>
                                                </div>
                                            </div>
                                        </section>

                                        <section class="panel-section">
                                            <h3 class="section-heading">
                                                <ion-icon name="location-outline" class="section-icon"/>
                                                성지 목록
                                                <span class="count-badge">{{ placeDetail?.places?.length }}</span>
                                            </h3>
                                            <div class="place-list">
                                                <div
                                                    v-for="p in placeDetail?.places"
                                                    :key="p.placeIdx"
                                                    class="place-item"
                                                    :class="{ 'place-item--current': p.placeIdx === selectedPlace.placeIdx }"
                                                    @click="switchToPlace(p)"
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
                                    </template>

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
        </ion-content>

        <!-- 모바일 하단 탭바 -->
        <div class="bottom-nav">
            <button class="nav-btn" @click="goHome">
                <ion-icon name="home"/>
                <span>홈</span>
            </button>
            <button class="nav-btn active">
                <ion-icon name="map-outline"/>
                <span>지도</span>
            </button>
            <button class="nav-btn" @click="goSns">
                <ion-icon name="people-outline"/>
                <span>SNS</span>
            </button>
            <button class="nav-btn" @click="goAuth">
                <ion-icon name="person-outline"/>
                <span>{{ isLoggedIn ? '프로필' : '로그인' }}</span>
            </button>
        </div>
    </ion-page>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue';
import { IonPage, IonContent, IonIcon, useIonRouter, onIonViewWillEnter, onIonViewDidEnter } from '@ionic/vue';
import { useRoute } from 'vue-router';
import { addIcons } from 'ionicons';
import {
    home, mapOutline, peopleOutline, personOutline,
    locationOutline, closeOutline, chevronForwardOutline, chevronBackOutline,
    filmOutline, checkmarkCircleOutline, arrowBackOutline,
    bookmark, bookmarkOutline, timeOutline,
} from 'ionicons/icons';
import { placeApi } from '@/api/placeApi';
import { contentApi } from '@/api/contentApi';
import { bookmarkApi } from '@/api/bookmarkApi';
import { useKakaoMap } from '@/composables/useKakaoMap';
import { useBottomSheet } from '@/composables/useBottomSheet';
import { useAuth } from '@/composables/useAuth';
import TopBar from '@/components/common/TopBar.vue';
import Sidebar from '@/components/common/Sidebar.vue';

addIcons({
    'home': home, 'map-outline': mapOutline,
    'people-outline': peopleOutline, 'person-outline': personOutline,
    'location-outline': locationOutline, 'close-outline': closeOutline,
    'chevron-forward-outline': chevronForwardOutline,
    'film-outline': filmOutline, 'checkmark-circle-outline': checkmarkCircleOutline,
    'arrow-back-outline': arrowBackOutline,
    'bookmark': bookmark, 'bookmark-outline': bookmarkOutline,
    'time-outline': timeOutline,
    'chevron-back-outline': chevronBackOutline,
});

const router = useIonRouter();
const route = useRoute();
const { load, createMarker, createActiveMarkerImage, createNormalMarkerImage } = useKakaoMap();
const { sheetHeight, startDrag: startMobileDrag } = useBottomSheet();
const { isLoggedIn } = useAuth();

function goHome() { router.push('/home'); }
function goAuth() { router.push(isLoggedIn.value ? '/mypage' : '/auth'); }
function goSns() { router.push('/sns'); }

const searchQuery = ref('');

const mapEl = ref<HTMLDivElement | null>(null);
let mapInstance: any = null;
const allPlaces = ref<any[]>([]);
let allMarkers: { placeIdx: number; marker: any }[] = [];
let activeMarkerEntry: { placeIdx: number; marker: any } | null = null;
const loading = ref(true);
const selectedPlace = ref<any>(null);
const placeDetail = ref<any>(null);
const detailLoading = ref(false);
const panelView = ref<'place' | 'content'>('place');
const bookmarked = ref(false);

const currentSceneIndex = ref(0);

const currentPlaceScenes = computed<any[]>(() => {
    const place = placeDetail.value?.places?.find((p: any) => p.placeIdx === selectedPlace.value?.placeIdx);
    return place?.scenes ?? [];
});

const currentPlaceScene = computed(() => currentPlaceScenes.value[currentSceneIndex.value] ?? null);

let sceneTouchStartX = 0;
function onSceneTouchStart(e: TouchEvent) { sceneTouchStartX = e.touches[0].clientX; }
function onSceneTouchEnd(e: TouchEvent) {
    const dx = e.changedTouches[0].clientX - sceneTouchStartX;
    if (Math.abs(dx) < 40) return;
    if (dx < 0 && currentSceneIndex.value < currentPlaceScenes.value.length - 1) currentSceneIndex.value++;
    else if (dx > 0 && currentSceneIndex.value > 0) currentSceneIndex.value--;
}

function formatTimestamp(seconds: number): string {
    const h = Math.floor(seconds / 3600);
    const m = Math.floor((seconds % 3600) / 60);
    const s = seconds % 60;
    if (h > 0) return `${h}시간 ${m}분 ${s}초`;
    return `${m}분 ${s}초`;
}

const headerTitle = computed(() => {
    if (!selectedPlace.value) return '';
    if (panelView.value === 'content') return placeDetail.value?.title ?? '';
    return selectedPlace.value.name ?? '';
});

function handleHeaderBack() {
    if (panelView.value === 'content') {
        panelView.value = 'place';
    } else {
        closePanel();
    }
}

function setActiveMarker(placeIdx: number) {
    if (activeMarkerEntry) {
        activeMarkerEntry.marker.setImage(createNormalMarkerImage());
    }
    const entry = allMarkers.find(m => m.placeIdx === placeIdx) ?? null;
    if (entry) entry.marker.setImage(createActiveMarkerImage());
    activeMarkerEntry = entry;
}

async function selectPlace(place: any) {
    selectedPlace.value = place;
    placeDetail.value = null;
    detailLoading.value = true;
    panelView.value = 'place';
    bookmarked.value = false;
    setActiveMarker(place.placeIdx);
    currentSceneIndex.value = 0;
    try {
        const [detail, status] = await Promise.all([
            placeApi.getDetail(place.placeIdx),
            bookmarkApi.getStatus(place.placeIdx),
        ]);
        placeDetail.value = detail;
        bookmarked.value = status?.bookmarked ?? false;
        if (detail?.places?.length) {
            filterMarkersToPlaces(detail.places.map((p: any) => p.placeIdx));
        }
    } finally {
        detailLoading.value = false;
    }
}

function applyPanelOffset() {
    // 데스크탑은 패널이 지도 옆에 위치해 겹치지 않으므로 모바일만 처리
    if (!mapInstance || window.innerWidth >= 1024) return;
    const BOTTOM_NAV_H = 60;
    const offset = Math.round((sheetHeight.value + BOTTOM_NAV_H) / 2);
    mapInstance.panBy(0, offset);
}

async function switchToPlace(place: any) {
    await selectPlace(place);
    if (mapInstance && place.latitude && place.longitude) {
        const pos = new window.kakao.maps.LatLng(place.latitude, place.longitude);
        const needsZoom = mapInstance.getLevel() > 5;
        if (needsZoom) {
            mapInstance.setLevel(4, { anchor: pos, animate: true });
            setTimeout(applyPanelOffset, 320);
        } else {
            mapInstance.panTo(pos);
            setTimeout(applyPanelOffset, 50);
        }
    }
}

function closePanel() {
    selectedPlace.value = null;
    placeDetail.value = null;
    panelView.value = 'place';
    bookmarked.value = false;
    if (activeMarkerEntry) {
        activeMarkerEntry.marker.setImage(createNormalMarkerImage());
        activeMarkerEntry = null;
    }
    showAllMarkers();
}

async function toggleBookmark() {
    if (!isLoggedIn.value) { router.push('/auth'); return; }
    const result = await bookmarkApi.toggle(selectedPlace.value.placeIdx);
    if (result !== null) bookmarked.value = result.bookmarked ?? result;
}

function filterMarkersToPlaces(placeIdxList: number[]) {
    const set = new Set(placeIdxList);
    allMarkers.forEach(({ placeIdx, marker }) => {
        marker.setMap(set.has(placeIdx) ? mapInstance : null);
    });
}

function showAllMarkers() {
    allMarkers.forEach(({ marker }) => marker.setMap(mapInstance));
}

function fitMapToPlaces(places: any[]) {
    if (!mapInstance || !places?.length) return;
    const valid = places.filter(p => p.latitude && p.longitude);
    if (!valid.length) return;
    const bounds = new window.kakao.maps.LatLngBounds();
    valid.forEach(p => bounds.extend(new window.kakao.maps.LatLng(p.latitude, p.longitude)));
    mapInstance.setBounds(bounds);
}

function switchToContentView() {
    panelView.value = 'content';
    fitMapToPlaces(placeDetail.value?.places ?? []);
}

async function handleQueryParams() {
    const places = allPlaces.value;
    if (!places.length) return;

    const queryPlaceId = route.query.placeId;
    const queryContentId = route.query.contentId;
    if (!queryPlaceId && !queryContentId) return;

    // 이전 상태 즉시 초기화
    selectedPlace.value = null;
    placeDetail.value = null;
    panelView.value = 'place';
    bookmarked.value = false;

    if (queryPlaceId) {
        const target = places.find((p: any) => p.placeIdx === Number(queryPlaceId));
        if (target) await switchToPlace(target);
        return;
    }

    if (queryContentId) {
        const content = await contentApi.getDetail(Number(queryContentId));
        if (content?.places?.length) {
            // 장소 뷰 없이 작품 뷰 직접 표시
            placeDetail.value = content;
            selectedPlace.value = { placeIdx: -1 }; // 패널 표시용 sentinel
            panelView.value = 'content';
            filterMarkersToPlaces(content.places.map((p: any) => p.placeIdx));
            fitMapToPlaces(content.places);
        }
    }
}

onIonViewDidEnter(() => {
    if (!mapInstance) return;
    mapInstance.relayout();
    if (allPlaces.value.length) handleQueryParams();
});

onMounted(async () => {
    try {
        await load();
        if (!mapEl.value) return;

        const center = new window.kakao.maps.LatLng(36.450701, 126.570667);
        const map = new window.kakao.maps.Map(mapEl.value, { center, level: 13, maxLevel: 11 });
        mapInstance = map;

        const clusterer = new window.kakao.maps.MarkerClusterer({
            map, averageCenter: true, minLevel: 6, disableClickZoom: false,
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
                allMarkers.push({ placeIdx: p.placeIdx, marker });
                return marker;
            });

        clusterer.addMarkers(markers);
        allPlaces.value = places;

        await handleQueryParams();
    } catch (e) {
        console.error(e);
        loading.value = false;
    }
});
</script>

<style scoped>
.map-content { --background: #fff; }

/* ══════════════════════════════════════
   베이스 (모바일)
══════════════════════════════════════ */
.page-layout {
    display: flex;
    min-height: 100%;
}

.main-wrap {
    flex: 1; min-width: 0;
    display: flex; flex-direction: column;
    height: 100vh;
}

.content-row {
    flex: 1; min-height: 0;
    position: relative; overflow: hidden;
}

/* 지도 */
.map-area {
    position: absolute; inset: 0; z-index: 1;
}

.map-el { width: 100%; height: 100%; }

.map-overlay {
    position: absolute; inset: 0;
    display: flex; flex-direction: column;
    align-items: center; justify-content: center; gap: 10px;
    background: rgb(229 231 235); pointer-events: none; z-index: 10;
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
    position: absolute; bottom: 0; left: 0; right: 0; z-index: 50;
    background: #fff; border-radius: 20px 20px 0 0;
    box-shadow: 0 -4px 24px rgba(0,0,0,0.14);
    display: flex; flex-direction: column; min-height: 72px;
}

.mobile-drag {
    flex-shrink: 0; display: flex; justify-content: center; align-items: center;
    height: 28px; cursor: row-resize; touch-action: none; user-select: none;
}

.drag-bar { width: 40px; height: 4px; border-radius: 99px; background: #d1d5db; }

.panel-body { flex: 1; overflow-y: auto; overscroll-behavior: contain; }

.panel-header {
    display: flex; align-items: center; justify-content: space-between;
    padding: 4px 16px 0;
}

.panel-title { font-size: 14px; font-weight: 700; color: #1f2937; margin: 0; }

.close-btn {
    width: 30px; height: 30px; border-radius: 50%;
    background: #f3f4f6; border: none;
    display: flex; align-items: center; justify-content: center;
    font-size: 16px; color: #6b7280; cursor: pointer; transition: background 0.15s;
}

.close-btn:hover { background: #e5e7eb; }

/* 패널 트랜지션 */
.panel-enter-active, .panel-leave-active {
    transition: transform 0.28s cubic-bezier(0.25, 0.46, 0.45, 0.94), opacity 0.28s ease;
}
.panel-enter-from, .panel-leave-to { transform: translateY(100%); opacity: 0; }

/* 모바일 하단 탭바 */
.bottom-nav {
    position: fixed; bottom: 0; left: 0; right: 0;
    height: 60px; background: #fff;
    border-top: 1px solid rgba(20,188,237,0.15);
    box-shadow: 0 -2px 16px rgba(20,188,237,0.1);
    display: flex; align-items: center; justify-content: space-around;
    padding-bottom: env(safe-area-inset-bottom, 0); z-index: 200;
}

.nav-btn {
    flex: 1; background: none; border: none;
    display: flex; flex-direction: column; align-items: center; gap: 3px;
    color: var(--text-muted); font-size: 20px; cursor: pointer; padding: 6px 0;
    transition: color 0.2s;
}

.nav-btn span { font-size: 10px; font-weight: 600; }
.nav-btn.active { color: var(--brand); }

/* ══════════════════════════════════════
   섹션 공통
══════════════════════════════════════ */
.panel-section { padding: 12px 16px; border-bottom: 1px solid #f3f4f6; }
.panel-section:last-child { border-bottom: none; }

.section-heading {
    display: flex; align-items: center; gap: 6px;
    font-size: 13px; font-weight: 700; color: #1f2937;
    margin: 0 0 10px; padding-bottom: 8px; border-bottom: 1px solid #f3f4f6;
}

.section-icon { color: var(--brand, #14BCED); font-size: 15px; }
.count-badge { font-size: 11px; font-weight: 400; color: #6b7280; background: #f3f4f6; padding: 1px 8px; border-radius: 99px; margin-left: 2px; }

.place-card { display: flex; gap: 12px; }
.place-thumb { width: 72px; flex-shrink: 0; aspect-ratio: 1; border-radius: 10px; overflow: hidden; background: #f3f4f6; border: 1px solid #e5e7eb; display: flex; align-items: center; justify-content: center; }
.thumb-img { width: 100%; height: 100%; object-fit: cover; }
.thumb-placeholder { font-size: 24px; }
.place-meta { flex: 1; min-width: 0; display: flex; flex-direction: column; gap: 4px; }
.place-title { font-size: 15px; font-weight: 800; color: #111; margin: 0; }
.place-english { font-size: 11px; color: #6b7280; margin: 0; }
.place-address { display: flex; align-items: flex-start; gap: 3px; font-size: 11px; color: #4b5563; margin: 0; line-height: 1.5; }
.addr-icon { font-size: 11px; color: var(--brand, #14BCED); flex-shrink: 0; margin-top: 1px; }
.panel-actions { display: flex; align-items: center; gap: 4px; }

.icon-btn {
    width: 30px; height: 30px; border-radius: 50%;
    background: #f3f4f6; border: none;
    display: flex; align-items: center; justify-content: center;
    font-size: 16px; color: #6b7280; cursor: pointer; transition: background 0.15s, color 0.15s;
}
.icon-btn:hover { background: #e5e7eb; }
.icon-btn--active { color: #f59e0b; background: #fef3c7; }
.icon-btn--active:hover { background: #fde68a; }

.panel-title--content { flex: 1; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }

.content-card { display: flex; gap: 12px; cursor: pointer; }
.content-poster { width: 70px; flex-shrink: 0; aspect-ratio: 2/3; border-radius: 8px; overflow: hidden; background: #f3f4f6; border: 1px solid #e5e7eb; }
.poster-img { width: 100%; height: 100%; object-fit: cover; }
.poster-placeholder { width: 100%; height: 100%; display: flex; align-items: center; justify-content: center; font-size: 22px; }
.content-meta { flex: 1; min-width: 0; display: flex; flex-direction: column; gap: 4px; }
.content-name { font-size: 13px; font-weight: 700; color: #1f2937; margin: 0; }
.content-english { font-size: 11px; color: #6b7280; margin: 0; }
.content-desc { font-size: 11px; color: #4b5563; margin: 0; line-height: 1.5; display: -webkit-box; -webkit-line-clamp: 3; -webkit-box-orient: vertical; overflow: hidden; }
.place-count { display: flex; align-items: center; gap: 3px; font-size: 11px; font-weight: 600; color: var(--brand, #14BCED); }

.place-list { display: flex; flex-direction: column; gap: 4px; }
.place-item { display: flex; align-items: center; gap: 10px; padding: 7px 8px; border-radius: 10px; border: 1px solid transparent; cursor: pointer; transition: background 0.15s, border-color 0.15s; }
.place-item:hover { background: rgba(20,188,237,0.05); border-color: rgba(20,188,237,0.2); }
.place-item--current { background: rgba(20,188,237,0.06); border-color: rgba(20,188,237,0.25); }
.item-thumb { width: 48px; height: 48px; flex-shrink: 0; border-radius: 8px; overflow: hidden; background: #e5e7eb; }
.item-thumb img { width: 100%; height: 100%; object-fit: cover; }
.place-info { flex: 1; min-width: 0; }
.place-name { font-size: 12px; font-weight: 700; color: #1f2937; margin: 0 0 2px; }
.place-address-sm { font-size: 10px; color: #6b7280; margin: 0 0 3px; overflow: hidden; white-space: nowrap; text-overflow: ellipsis; }
.tag-current { font-size: 10px; background: rgba(20,188,237,0.12); color: var(--brand,#14BCED); padding: 2px 7px; border-radius: 6px; font-weight: 600; }
.tag-match { display: flex; align-items: center; gap: 3px; font-size: 10px; color: var(--brand,#14BCED); font-weight: 600; }
.scene-section { padding: 0; overflow: hidden; }

.scene-image-wrap {
    width: 100%; aspect-ratio: 16/9;
    position: relative; overflow: hidden; background: #111;
}
.scene-image { width: 100%; height: 100%; object-fit: cover; display: block; }
.scene-image-placeholder { width: 100%; height: 100%; background: #1f2937; }

.scene-info-bar {
    position: absolute; bottom: 0; left: 0; right: 0;
    display: flex; align-items: center; gap: 8px;
    padding: 28px 12px 10px;
    background: linear-gradient(to bottom, transparent, rgba(0,0,0,0.92));
}

.scene-badge {
    display: inline-flex; align-items: center; flex-shrink: 0;
    font-size: 11px; font-weight: 700; padding: 3px 9px;
    border-radius: 99px;
}

.episode-badge { background: var(--brand, #14BCED); color: #fff; }

.scene-timecode {
    font-size: 12px; font-weight: 600; flex-shrink: 0;
    color: rgba(255,255,255,0.85);
    font-variant-numeric: tabular-nums;
    letter-spacing: 0.3px;
}

.scene-description {
    font-size: 11px; color: rgba(255,255,255,0.55);
    overflow: hidden; white-space: nowrap; text-overflow: ellipsis;
    flex: 1; min-width: 0;
}

.scene-nav {
    position: absolute; top: 50%; transform: translateY(-50%);
    width: 28px; height: 28px; border-radius: 50%;
    background: rgba(0,0,0,0.45); border: none;
    display: flex; align-items: center; justify-content: center;
    color: #fff; font-size: 16px; cursor: pointer;
    transition: background 0.15s; z-index: 2;
}
.scene-nav:hover { background: rgba(0,0,0,0.7); }
.scene-nav--prev { left: 8px; }
.scene-nav--next { right: 8px; }

.scene-dots {
    position: absolute; bottom: 38px; left: 0; right: 0;
    display: flex; justify-content: center; gap: 5px; z-index: 2;
}
.scene-dot {
    width: 5px; height: 5px; border-radius: 50%;
    background: rgba(255,255,255,0.4); cursor: pointer;
    transition: background 0.2s, transform 0.2s;
}
.scene-dot--active { background: #fff; transform: scale(1.3); }

.scene-fade-enter-active, .scene-fade-leave-active { transition: opacity 0.2s ease; }
.scene-fade-enter-from, .scene-fade-leave-to { opacity: 0; }

.card-arrow { font-size: 14px; color: #d1d5db; flex-shrink: 0; align-self: center; }

.content-card-full { display: flex; gap: 14px; }
.content-poster-lg { width: 90px; flex-shrink: 0; aspect-ratio: 2/3; border-radius: 10px; overflow: hidden; background: #f3f4f6; border: 1px solid #e5e7eb; }
.content-desc--full { -webkit-line-clamp: unset; }

.detail-loading { display: flex; justify-content: center; padding: 20px; }

/* ══════════════════════════════════════
   데스크탑 (≥1024px)
══════════════════════════════════════ */
@media (min-width: 1024px) {
    .page-layout { min-height: 100vh; }

    .main-wrap { height: 100vh; }

    .content-row {
        position: static; display: flex; flex-direction: row; overflow: hidden;
    }

    .content-panel {
        position: static !important; width: 360px; flex-shrink: 0;
        height: 100% !important; border-radius: 0; box-shadow: none;
        border-right: 1px solid rgba(20,188,237,0.12); order: 1;
    }

    .mobile-drag { display: none; }
    .panel-body { overflow-y: auto; height: 100%; }

    .panel-enter-from, .panel-leave-to { transform: translateX(-100%); opacity: 0; }

    .map-area { flex: 1; min-width: 0; position: relative; order: 2; }

    .bottom-nav { display: none; }
}


</style>
