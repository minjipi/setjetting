<template>
    <ion-page>
        <ion-content :fullscreen="true" class="home-content">
            <div class="page-layout">

                <!-- ── 데스크탑 사이드바 (≥1024px) ── -->
                <aside class="sidebar">
                    <div class="sidebar-logo">
                        <svg width="34" height="34" viewBox="0 0 30 30" xmlns="http://www.w3.org/2000/svg">
                            <circle cx="15" cy="15" r="15" fill="#14BCED"/>
                            <path d="M15 7 L12 15 L18 15 L11 23 L17 16 L13 16 Z" fill="#fff"/>
                        </svg>
                        <span class="sidebar-logo-text">성지맵</span>
                    </div>

                    <nav class="sidebar-nav">
                        <button class="sidebar-nav-btn active">
                            <ion-icon name="home"/>
                            <span>홈</span>
                        </button>
                        <button class="sidebar-nav-btn" @click="goMap">
                            <ion-icon name="map-outline"/>
                            <span>지도</span>
                        </button>
                        <button class="sidebar-nav-btn">
                            <ion-icon name="bookmark-outline"/>
                            <span>저장</span>
                        </button>
                        <button class="sidebar-nav-btn" @click="goAuth">
                            <ion-icon name="person-outline"/>
                            <span>{{ isLoggedIn ? '프로필' : '로그인' }}</span>
                        </button>
                    </nav>

                    <div class="sidebar-footer">
                        <div class="sidebar-membership">
                            <span>✨</span>
                            <div>
                                <p class="sm-title">멤버십</p>
                                <p class="sm-sub">더 많은 성지 탐색</p>
                            </div>
                        </div>
                    </div>
                </aside>

                <!-- ── 메인 콘텐츠 영역 ── -->
                <div class="main-wrap">

                    <!-- 상단 바 -->
                    <div class="top-bar">
                        <!-- 모바일에서만 로고 표시 -->
                        <div class="logo-wrap">
                            <svg width="30" height="30" viewBox="0 0 30 30" xmlns="http://www.w3.org/2000/svg">
                                <circle cx="15" cy="15" r="15" fill="#14BCED"/>
                                <path d="M15 7 L12 15 L18 15 L11 23 L17 16 L13 16 Z" fill="#fff"/>
                            </svg>
                            <span class="logo-text">성지맵</span>
                        </div>
                        <div class="search-bar" @click="focusSearch">
                            <ion-icon name="search-outline" class="search-icon"/>
                            <input
                                    ref="searchInputEl"
                                    v-model="searchQuery"
                                    type="text"
                                    placeholder="작품명, 장소 검색..."
                                    class="search-input"
                            />
                        </div>
                        <!-- 데스크탑 우측 액션 -->
                        <div class="top-bar-actions">
                            <template v-if="isLoggedIn">
                                <button class="top-action-btn">
                                    <ion-icon name="notifications-outline"/>
                                </button>
                                <div class="top-avatar" @click="goProfile">
                                    {{ userInitial }}
                                </div>
                            </template>
                            <button v-else class="login-btn" @click="goAuth">
                                로그인
                            </button>
                        </div>
                    </div>

                    <!-- 카카오 지도 -->
                    <MapSection :map-height="homeMapHeight" :places="mapPlaces"/>

                    <!-- 콘텐츠 본문 -->
                    <div class="content-body">

                        <!-- 내 근처 성지 -->
                        <section class="section nearby-section">
                            <div class="section-header">
                                <h3 class="section-title">내 근처 성지</h3>
                                <button
                                    class="locate-btn"
                                    :class="{ loading: locationStatus === 'loading' }"
                                    :disabled="locationStatus === 'loading'"
                                    @click="requestLocation"
                                >
                                    <ion-icon :name="locationStatus === 'granted' ? 'navigate' : 'navigate-outline'"/>
                                    {{ locationStatus === 'loading' ? '위치 확인 중...' : locationStatus === 'granted' ? '새로고침' : '내 위치 찾기' }}
                                </button>
                            </div>

                            <div v-if="locationStatus === 'idle'" class="nearby-empty">
                                <ion-icon name="navigate-outline" class="empty-icon"/>
                                <p>위치 권한을 허용하면 근처 성지를 볼 수 있어요</p>
                            </div>
                            <div v-else-if="locationStatus === 'denied'" class="nearby-empty nearby-denied">
                                <ion-icon name="close-circle-outline" class="empty-icon"/>
                                <p>위치 권한이 거부되었습니다. 브라우저 설정에서 허용해주세요.</p>
                            </div>
                            <div v-else-if="locationStatus === 'loading'" class="nearby-empty">
                                <div class="locate-pulse"/>
                                <p>위치를 확인하는 중...</p>
                            </div>
                            <div v-else-if="nearbyPlaces.length === 0" class="nearby-empty">
                                <ion-icon name="location-outline" class="empty-icon"/>
                                <p>근처 50km 이내에 성지가 없습니다</p>
                            </div>
                            <div v-else class="nearby-carousel">
                                <button class="carousel-btn carousel-btn--prev" @click="scrollNearby(-1)" aria-label="이전">
                                    <ion-icon name="chevron-back-outline"/>
                                </button>
                                <div ref="nearbyScrollEl" class="nearby-scroll">
                                    <div
                                        v-for="item in nearbyPlaces"
                                        :key="item.placeIdx"
                                        class="nearby-card"
                                        @click="onPlaceClick(item)"
                                    >
                                        <div class="nearby-thumb">
                                            <img v-if="item.placeImageUrl" :src="item.placeImageUrl" :alt="item.name"/>
                                            <div v-else class="nearby-thumb-fallback">📍</div>
                                        </div>
                                        <div class="nearby-info">
                                            <p class="nearby-name">{{ item.name }}</p>
                                            <p class="nearby-address">{{ item.address }}</p>
                                            <span class="nearby-dist">
                                                <ion-icon name="navigate-outline"/>
                                                {{ formatDist(item.distance) }}
                                            </span>
                                        </div>
                                    </div>
                                </div>
                                <button class="carousel-btn carousel-btn--next" @click="scrollNearby(1)" aria-label="다음">
                                    <ion-icon name="chevron-forward-outline"/>
                                </button>
                            </div>
                        </section>

                        <!-- 콘텐츠 내부 래퍼 (데스크탑 2컬럼) -->
                        <div class="sections-wrap">

                            <!-- 최근 업데이트 -->
                            <section class="section">
                                <div class="section-header">
                                    <h3 class="section-title">인기 작품</h3>
                                    <button class="see-all-btn">
                                        전체보기
                                        <ion-icon name="chevron-forward-outline"/>
                                    </button>
                                </div>
                                <div class="media-grid">
                                    <MediaCard
                                            v-for="item in popularContents"
                                            :key="item.contentIdx"
                                            :item="item"
                                            @click="onContentClick(item)"
                                    />
                                </div>
                            </section>

                            <!-- 인기 성지 -->
                            <section class="section">
                                <div class="section-header">
                                    <h3 class="section-title">인기 성지</h3>
                                    <button class="see-all-btn">
                                        전체보기
                                        <ion-icon name="chevron-forward-outline"/>
                                    </button>
                                </div>
                                <div class="media-grid">
                                    <MediaCard
                                            v-for="item in popularPlace"
                                            :key="item.placeIdx"
                                            :item="item"
                                            @click="onPlaceClick(item)"
                                    />
                                </div>
                            </section>

                        </div>

                        <!-- 구름 구분선 -->
                        <div class="cloud-divider" aria-hidden="true">
                            <svg viewBox="0 0 390 28" xmlns="http://www.w3.org/2000/svg" preserveAspectRatio="none">
                                <path d="M0,14 Q30,2 60,12 Q90,22 120,10 Q150,0 180,12 Q210,22 240,10 Q270,0 300,12 Q330,22 360,10 Q380,4 390,14 L390,28 L0,28 Z"
                                      fill="#e8f9fd"/>
                            </svg>
                        </div>

                        <!-- 멤버십 배너 -->
                        <div v-if="!isLoggedIn" class="membership-banner">
                            <svg class="banner-cloud left" viewBox="0 0 90 50" xmlns="http://www.w3.org/2000/svg">
                                <path d="M10,38 Q6,38 6,33 Q6,24 14,24 Q14,14 24,14 Q28,7 37,10 Q44,4 53,11 Q61,6 68,15 Q75,14 77,22 Q83,22 83,28 Q83,34 77,34 Z"
                                      fill="white" opacity="0.15"/>
                            </svg>
                            <svg class="banner-cloud right" viewBox="0 0 70 40" xmlns="http://www.w3.org/2000/svg">
                                <path d="M8,30 Q5,30 5,26 Q5,19 11,19 Q11,12 19,12 Q22,7 29,9 Q35,5 42,10 Q48,6 54,13 Q59,12 61,18 Q65,18 65,23 Q65,28 61,28 Z"
                                      fill="white" opacity="0.12"/>
                            </svg>
                            <div class="banner-content">
                                <div class="banner-icon">✨</div>
                                <div class="banner-text">
                                    <p class="banner-title">Membership</p>
                                    <p class="banner-desc">더 많은 성지를 탐색하세요</p>
                                </div>
                                <button class="banner-btn" @click="goAuth">시작하기</button>
                            </div>
                        </div>

                        <div class="bottom-spacer"/>
                    </div>
                </div>
            </div>
        </ion-content>

        <!-- 모바일 하단 탭바 -->
        <div class="bottom-nav">
            <button class="nav-btn active">
                <ion-icon name="home"/>
                <span>홈</span>
            </button>
            <button class="nav-btn">
                <ion-icon name="map-outline"/>
                <span>지도</span>
            </button>
            <button class="nav-btn">
                <ion-icon name="bookmark-outline"/>
                <span>저장</span>
            </button>
            <button class="nav-btn" @click="goAuth">
                <ion-icon name="person-outline"/>
                <span>{{ isLoggedIn ? '프로필' : '로그인' }}</span>
            </button>
        </div>
    </ion-page>
</template>

<script setup lang="ts">
import {ref, computed, onMounted} from 'vue';
import {IonPage, IonContent, IonIcon, useIonRouter} from '@ionic/vue';
import {addIcons} from 'ionicons';
import {
    searchOutline, home, mapOutline, bookmarkOutline,
    chevronForwardOutline, chevronBackOutline, notificationsOutline, personOutline,
    navigateOutline, navigate, locationOutline, closeCircleOutline,
} from 'ionicons/icons';
import MapSection from '@/components/home/MapSection.vue';
import MediaCard from '@/components/home/MediaCard.vue';
import {useAuth} from '@/composables/useAuth';
import { contentApi } from '@/api/contentApi';
import { placeApi } from '@/api/placeApi';

addIcons({
    'search-outline': searchOutline,
    'home': home,
    'map-outline': mapOutline,
    'bookmark-outline': bookmarkOutline,
    'chevron-forward-outline': chevronForwardOutline,
    'notifications-outline': notificationsOutline,
    'person-outline': personOutline,
    'navigate-outline': navigateOutline,
    'navigate': navigate,
    'location-outline': locationOutline,
    'close-circle-outline': closeCircleOutline,
    'chevron-back-outline': chevronBackOutline,
});

const router = useIonRouter();
const { isLoggedIn, user } = useAuth();

const userInitial = computed(() => user.value?.name?.charAt(0).toUpperCase() ?? 'U');

function goAuth() { router.push(isLoggedIn.value ? '/mypage' : '/auth'); }
function goProfile() { router.push('/mypage'); }
function goMap() { router.push('/map'); }

const homeMapHeight = computed(() => {
    if (window.innerWidth >= 1024) return '440px';
    if (window.innerWidth >= 768) return '360px';
    return '260px';
});

const searchQuery = ref('');
const searchInputEl = ref<HTMLInputElement | null>(null);

function focusSearch() {
    searchInputEl.value?.focus();
}

function onContentClick(item: any) {
    router.push({ name: 'ContentDetail', params: { id: item.contentIdx }, state: { content: item } });
}

function onPlaceClick(item: any) {
    router.push({ name: 'PlaceDetail', params: { id: item.placeIdx }, state: { place: item } });
}

// ── 위치 기반 근처 성지 ──
type LocationStatus = 'idle' | 'loading' | 'granted' | 'denied';
const locationStatus = ref<LocationStatus>('idle');
const userLat = ref<number | null>(null);
const userLng = ref<number | null>(null);

function haversineKm(lat1: number, lng1: number, lat2: number, lng2: number) {
    const R = 6371;
    const dLat = (lat2 - lat1) * Math.PI / 180;
    const dLng = (lng2 - lng1) * Math.PI / 180;
    const a = Math.sin(dLat / 2) ** 2 +
              Math.cos(lat1 * Math.PI / 180) * Math.cos(lat2 * Math.PI / 180) * Math.sin(dLng / 2) ** 2;
    return R * 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
}

const nearbyPlaces = computed(() => {
    if (userLat.value === null || userLng.value === null) return [];
    return mapPlaces.value
        .filter(p => p.latitude && p.longitude)
        .map(p => ({ ...p, distance: haversineKm(userLat.value!, userLng.value!, p.latitude, p.longitude) }))
        .filter(p => p.distance <= 50)
        .sort((a, b) => a.distance - b.distance)
        .slice(0, 20);
});

function formatDist(km: number) {
    return km < 1 ? `${Math.round(km * 1000)}m` : `${km.toFixed(1)}km`;
}

const nearbyScrollEl = ref<HTMLDivElement | null>(null);

function scrollNearby(dir: 1 | -1) {
    const el = nearbyScrollEl.value;
    if (!el) return;

    const distance = dir * 480;
    const start = el.scrollLeft;
    const target = Math.max(0, Math.min(start + distance, el.scrollWidth - el.clientWidth));
    const duration = 420;
    let startTime: number | null = null;

    function easeInOutCubic(t: number) {
        return t < 0.5 ? 4 * t * t * t : 1 - Math.pow(-2 * t + 2, 3) / 2;
    }

    function step(timestamp: number) {
        if (!startTime) startTime = timestamp;
        const elapsed = timestamp - startTime;
        const progress = Math.min(elapsed / duration, 1);
        el.scrollLeft = start + (target - start) * easeInOutCubic(progress);
        if (progress < 1) requestAnimationFrame(step);
    }

    requestAnimationFrame(step);
}

function requestLocation() {
    if (!navigator.geolocation) { locationStatus.value = 'denied'; return; }
    locationStatus.value = 'loading';
    navigator.geolocation.getCurrentPosition(
        (pos) => {
            userLat.value = pos.coords.latitude;
            userLng.value = pos.coords.longitude;
            locationStatus.value = 'granted';
        },
        () => { locationStatus.value = 'denied'; },
        { timeout: 10000 },
    );
}

const popularContents = ref([]);
const popularPlace = ref([]);
const mapPlaces = ref([]);

async function loadContents() {
    const data = await contentApi.getList();
    if (Array.isArray(data)) {
        popularContents.value = data;
    }
}

async function loadPlaces() {
    const data = await placeApi.getList();
    if (Array.isArray(data)) {
        mapPlaces.value = data;
        popularPlace.value = data.map(p => ({
            placeIdx: p.placeIdx,
            title: p.name,
            posterImageUrl: p.placeImageUrl,
        }));
    }
}

onMounted(() => {
    loadContents();
    loadPlaces();
})
</script>

<style scoped>
/* ═══════════════════════════════════════
   베이스 (모바일 우선)
═══════════════════════════════════════ */
.home-content {
    --background: rgb(255 255 255);
}

/* 전체 페이지 레이아웃 래퍼 */
.page-layout {
    display: flex;
    min-height: 100%;
}

/* ── 사이드바: 모바일에서 숨김 ── */
.sidebar {
    display: none;
}

/* ── 메인 콘텐츠: 전체 너비 ── */
.main-wrap {
    flex: 1;
    min-width: 0;
    display: flex;
    flex-direction: column;
}

/* 상단 바 */
.top-bar {
    position: sticky;
    top: 0;
    z-index: 100;
    display: flex;
    align-items: center;
    gap: 10px;
    padding: 12px 16px;
    background: #ffffff;
    border-bottom: 1px solid rgba(20, 188, 237, 0.1);
}

.logo-wrap {
    display: flex;
    align-items: center;
    gap: 6px;
    flex-shrink: 0;
}

.logo-text {
    font-size: 15px;
    font-weight: 800;
    color: var(--brand);
    letter-spacing: -0.3px;
}

.search-bar {
    flex: 1;
    display: flex;
    align-items: center;
    gap: 8px;
    background: #fff;
    border-radius: 24px;
    padding: 9px 16px;
    box-shadow: 0 1px 6px rgba(20, 188, 237, 0.12);
    border: 1px solid rgba(20, 188, 237, 0.15);
    cursor: text;
}

.search-icon {
    font-size: 16px;
    color: var(--brand);
    flex-shrink: 0;
}

.search-input {
    flex: 1;
    background: transparent;
    border: none;
    outline: none;
    font-size: 14px;
    color: var(--text-primary);
}

.search-input::placeholder {
    color: var(--text-muted);
}

/* 데스크탑 액션 버튼들: 모바일 숨김 */
.top-bar-actions {
    display: none;
}

/* 콘텐츠 본문 */
.content-body {
    background: rgb(255 255 255);
    position: relative;
}

/* 장식 구름 */
.deco-clouds {
    display: flex;
    align-items: flex-start;
    justify-content: space-around;
    padding: 4px 8px 0;
    pointer-events: none;
}

.deco-cloud {
    flex-shrink: 0;
}

.c1 {
    width: 100px;
    height: 50px;
    margin-top: -2px;
}

.c2 {
    width: 80px;
    height: 40px;
    margin-top: 8px;
}

.c3 {
    width: 64px;
    height: 32px;
    margin-top: 4px;
}

/* ── 내 근처 성지 ── */
.nearby-section {
    border-bottom: 1px solid #f3f4f6;
}

.locate-btn {
    display: flex;
    align-items: center;
    gap: 5px;
    padding: 6px 14px;
    border-radius: 20px;
    border: 1.5px solid var(--brand, #14BCED);
    background: transparent;
    color: var(--brand, #14BCED);
    font-size: 12px;
    font-weight: 700;
    cursor: pointer;
    transition: background 0.15s, color 0.15s;
    white-space: nowrap;
}

.locate-btn:hover:not(:disabled) {
    background: var(--brand, #14BCED);
    color: #fff;
}

.locate-btn:disabled { opacity: 0.6; cursor: default; }

.locate-btn ion-icon { font-size: 14px; }

.locate-btn.loading ion-icon {
    animation: spin 1s linear infinite;
}

@keyframes spin { to { transform: rotate(360deg); } }

.nearby-empty {
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: 8px;
    padding: 24px 0;
    color: #9ca3af;
}

.nearby-empty p { font-size: 13px; margin: 0; text-align: center; }
.nearby-denied { color: #ef4444; }
.empty-icon { font-size: 32px; opacity: 0.5; }

.locate-pulse {
    width: 28px; height: 28px;
    border-radius: 50%;
    background: var(--brand, #14BCED);
    opacity: 0.7;
    animation: pulse 1.2s ease-in-out infinite;
}

.nearby-carousel {
    position: relative;
}

.carousel-btn {
    display: none;
}

.nearby-scroll {
    display: flex;
    flex-direction: row;
    gap: 10px;
    overflow-x: auto;
    overflow-y: hidden;
    scroll-snap-type: x mandatory;
    -webkit-overflow-scrolling: touch;
    padding-bottom: 4px;
}

.nearby-scroll::-webkit-scrollbar { display: none; }

.nearby-card {
    flex-shrink: 0;
    width: 220px;
    display: flex;
    flex-direction: column;
    gap: 8px;
    padding: 10px;
    border-radius: 14px;
    border: 1px solid #f3f4f6;
    cursor: pointer;
    scroll-snap-align: start;
    transition: background 0.15s, border-color 0.15s;
}

.nearby-card:hover {
    background: rgba(20, 188, 237, 0.05);
    border-color: rgba(20, 188, 237, 0.2);
}

.nearby-thumb {
    width: 100%; height: 120px;
    flex-shrink: 0;
    border-radius: 10px;
    overflow: hidden;
    background: #f3f4f6;
    border: 1px solid #e5e7eb;
    display: flex; align-items: center; justify-content: center;
}

.nearby-thumb img { width: 100%; height: 100%; object-fit: cover; }
.nearby-thumb-fallback { font-size: 28px; }

.nearby-info {
    display: flex;
    flex-direction: column;
    gap: 3px;
    min-width: 0;
}

.nearby-name {
    font-size: 13px; font-weight: 700; color: #1f2937;
    margin: 0;
    overflow: hidden; white-space: nowrap; text-overflow: ellipsis;
}

.nearby-address {
    font-size: 11px; color: #6b7280; margin: 0;
    overflow: hidden; white-space: nowrap; text-overflow: ellipsis;
}

.nearby-dist {
    display: flex; align-items: center; gap: 3px;
    font-size: 11px; font-weight: 700; color: var(--brand, #14BCED);
}

.nearby-dist ion-icon { font-size: 11px; }

/* 섹션 래퍼: 모바일은 단순 스택 */
.sections-wrap {
    display: flex;
    flex-direction: column;
}

.section {
    padding: 20px 16px 12px;
}

.section-header {
    display: flex;
    align-items: center;
    justify-content: space-between;
    margin-bottom: 14px;
}

.section-title {
    font-size: 16px;
    font-weight: 800;
    color: var(--text-primary);
    margin: 0;
}

.see-all-btn {
    display: flex;
    align-items: center;
    gap: 2px;
    background: none;
    border: none;
    font-size: 12px;
    font-weight: 600;
    color: var(--brand);
    cursor: pointer;
    padding: 0;
}

.see-all-btn ion-icon {
    font-size: 14px;
}

/* 미디어 그리드: 모바일 가로 스크롤 */
.media-grid {
    display: flex;
    flex-direction: row;
    gap: 10px;
    overflow-x: auto;
    overflow-y: hidden;
    scroll-snap-type: x mandatory;
    -webkit-overflow-scrolling: touch;
    padding-bottom: 4px;
}

.media-grid::-webkit-scrollbar { display: none; }

.media-grid :deep(.media-card) {
    flex-shrink: 0;
    width: 110px;
    scroll-snap-align: start;
}

/* 구름 구분선 */
.cloud-divider {
    width: 100%;
    height: 28px;
    pointer-events: none;
    display: block;
    margin-bottom: -1px;
}

.cloud-divider svg {
    width: 100%;
    height: 100%;
}

/* 멤버십 배너 */
.membership-banner {
    margin: 16px 16px 0;
    background: linear-gradient(135deg, #14BCED, #0fa8d4);
    border-radius: 18px;
    padding: 20px;
    position: relative;
    overflow: hidden;
    box-shadow: 0 6px 24px rgba(20, 188, 237, 0.35);
}

.banner-cloud {
    position: absolute;
    pointer-events: none;
}

.banner-cloud.left {
    top: -6px;
    left: -8px;
    width: 90px;
}

.banner-cloud.right {
    bottom: -8px;
    right: -10px;
    width: 70px;
}

.banner-content {
    display: flex;
    align-items: center;
    gap: 12px;
    position: relative;
    z-index: 1;
}

.banner-icon {
    font-size: 28px;
    flex-shrink: 0;
}

.banner-text {
    flex: 1;
}

.banner-title {
    font-size: 15px;
    font-weight: 800;
    color: #fff;
    margin: 0 0 2px;
}

.banner-desc {
    font-size: 12px;
    color: rgba(255, 255, 255, 0.85);
    margin: 0;
}

.banner-btn {
    flex-shrink: 0;
    background: #fff;
    color: var(--brand);
    border: none;
    border-radius: 20px;
    padding: 8px 16px;
    font-size: 13px;
    font-weight: 700;
    cursor: pointer;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
}

.bottom-spacer {
    height: 80px;
}

/* 모바일 하단 탭바 */
.bottom-nav {
    position: fixed;
    bottom: 0;
    left: 0;
    right: 0;
    height: 60px;
    background: #fff;
    border-top: 1px solid rgba(20, 188, 237, 0.15);
    box-shadow: 0 -2px 16px rgba(20, 188, 237, 0.10);
    display: flex;
    align-items: center;
    justify-content: space-around;
    padding-bottom: env(safe-area-inset-bottom, 0);
    z-index: 200;
}

.nav-btn {
    flex: 1;
    background: none;
    border: none;
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: 3px;
    color: var(--text-muted);
    font-size: 20px;
    cursor: pointer;
    padding: 6px 0;
    transition: color 0.2s;
}

.nav-btn span {
    font-size: 10px;
    font-weight: 600;
}

.nav-btn.active {
    color: var(--brand);
}

/* ═══════════════════════════════════════
   태블릿 (≥ 768px)
═══════════════════════════════════════ */
@media (min-width: 768px) {
    .top-bar {
        padding: 14px 28px;
    }

    .search-bar {
        max-width: 480px;
    }

    .section {
        padding: 24px 28px 16px;
    }

    /* 태블릿: 그리드로 복원 */
    .media-grid {
        display: grid;
        grid-template-columns: repeat(4, 1fr);
        gap: 14px;
        overflow: visible;
        scroll-snap-type: none;
        padding-bottom: 0;
    }

    .media-grid :deep(.media-card) {
        width: auto;
        flex-shrink: unset;
        scroll-snap-align: none;
    }

    .membership-banner {
        margin: 20px 28px 0;
        border-radius: 20px;
    }

    .bottom-spacer {
        height: 90px;
    }
}

/* ═══════════════════════════════════════
   데스크탑 (≥ 1024px)
═══════════════════════════════════════ */
@media (min-width: 1024px) {

    /* ── 근처 성지 캐러셀 ── */
    .nearby-carousel {
        display: flex;
        align-items: center;
        gap: 8px;
    }

    .carousel-btn {
        display: flex;
        flex-shrink: 0;
        align-items: center;
        justify-content: center;
        width: 36px; height: 36px;
        border-radius: 50%;
        border: 1.5px solid rgba(20, 188, 237, 0.3);
        background: #fff;
        color: var(--brand, #14BCED);
        font-size: 18px;
        cursor: pointer;
        transition: background 0.15s, border-color 0.15s, box-shadow 0.15s;
        box-shadow: 0 2px 8px rgba(0,0,0,0.08);
    }

    .carousel-btn:hover {
        background: var(--brand, #14BCED);
        color: #fff;
        border-color: var(--brand, #14BCED);
        box-shadow: 0 4px 12px rgba(20,188,237,0.3);
    }

    .nearby-scroll {
        flex: 1;
        scroll-snap-type: none;
    }

    /* 사이드바 표시 */
    .sidebar {
        display: flex;
        flex-direction: column;
        width: 220px;
        flex-shrink: 0;
        background: #fff;
        border-right: 1px solid rgba(20, 188, 237, 0.12);
        height: 100vh;
        position: sticky;
        top: 0;
        padding: 24px 16px;
        box-sizing: border-box;
        box-shadow: 2px 0 12px rgba(20, 188, 237, 0.06);
    }

    .sidebar-logo {
        display: flex;
        align-items: center;
        gap: 10px;
        padding: 4px 8px 28px;
    }

    .sidebar-logo-text {
        font-size: 18px;
        font-weight: 800;
        color: var(--brand);
        letter-spacing: -0.4px;
    }

    .sidebar-nav {
        display: flex;
        flex-direction: column;
        gap: 4px;
        flex: 1;
    }

    .sidebar-nav-btn {
        display: flex;
        align-items: center;
        gap: 12px;
        width: 100%;
        padding: 12px 14px;
        border: none;
        border-radius: 12px;
        background: transparent;
        font-size: 14px;
        font-weight: 600;
        color: var(--text-secondary);
        cursor: pointer;
        text-align: left;
        transition: all 0.15s ease;
    }

    .sidebar-nav-btn ion-icon {
        font-size: 20px;
        flex-shrink: 0;
    }

    .sidebar-nav-btn:hover {
        background: var(--brand-light);
        color: var(--brand);
    }

    .sidebar-nav-btn.active {
        background: var(--brand-light);
        color: var(--brand);
        font-weight: 700;
    }

    .sidebar-footer {
        padding-top: 16px;
        border-top: 1px solid var(--border);
    }

    .sidebar-membership {
        display: flex;
        align-items: center;
        gap: 10px;
        padding: 12px;
        background: linear-gradient(135deg, #e0f7fd, #cff2fc);
        border-radius: 12px;
        cursor: pointer;
    }

    .sidebar-membership span {
        font-size: 20px;
    }

    .sm-title {
        font-size: 13px;
        font-weight: 700;
        color: var(--brand);
        margin: 0 0 1px;
    }

    .sm-sub {
        font-size: 11px;
        color: var(--text-secondary);
        margin: 0;
    }

    /* 모바일 로고 숨김 (사이드바에 있으므로) */
    .logo-wrap {
        display: none;
    }

    /* 데스크탑 상단 바 */
    .top-bar {
        padding: 16px 32px;
        background: #fff;
        border-bottom: 1px solid rgba(20, 188, 237, 0.1);
    }

    .search-bar {
        max-width: 560px;
    }

    /* 상단 액션 버튼 표시 */
    .top-bar-actions {
        display: flex;
        align-items: center;
        gap: 8px;
        flex-shrink: 0;
    }

    .top-action-btn {
        background: none;
        border: none;
        font-size: 22px;
        color: var(--text-secondary);
        cursor: pointer;
        padding: 6px;
        border-radius: 8px;
        transition: color 0.15s;
    }

    .top-action-btn:hover {
        color: var(--brand);
    }

    .top-avatar {
        width: 36px;
        height: 36px;
        border-radius: 50%;
        background: var(--brand);
        color: #fff;
        font-size: 14px;
        font-weight: 700;
        display: flex;
        align-items: center;
        justify-content: center;
        cursor: pointer;
    }

    .login-btn {
        padding: 8px 18px;
        border-radius: 20px;
        background: var(--brand);
        color: #fff;
        font-size: 13px;
        font-weight: 700;
        border: none;
        cursor: pointer;
        transition: background 0.15s;
        white-space: nowrap;
    }

    .login-btn:hover {
        background: #0fa8d4;
    }

    /* 콘텐츠 본문 배경 */
    .content-body {
        background: rgb(255 255 255);
    }

    /* 데스크탑: 두 섹션을 2열로 */
    .sections-wrap {
        flex-direction: row;
        gap: 0;
        align-items: flex-start;
    }

    .sections-wrap .section {
        flex: 1;
        min-width: 0;
        padding: 28px 24px 20px;
    }

    /* 데스크탑: 그리드로 복원 */
    .media-grid {
        display: grid;
        grid-template-columns: repeat(3, 1fr);
        gap: 12px;
        overflow: visible;
        scroll-snap-type: none;
        padding-bottom: 0;
    }

    .media-grid :deep(.media-card) {
        width: auto;
        flex-shrink: unset;
        scroll-snap-align: none;
    }

    /* 멤버십 배너 */
    .membership-banner {
        margin: 20px 32px 0;
        border-radius: 22px;
        padding: 24px 32px;
    }

    .banner-title {
        font-size: 17px;
    }

    .banner-desc {
        font-size: 13px;
    }

    .banner-btn {
        padding: 10px 24px;
        font-size: 14px;
    }

    /* 하단 탭바 숨김 */
    .bottom-nav {
        display: none;
    }

    .bottom-spacer {
        height: 40px;
    }
}

/* ═══════════════════════════════════════
   와이드 데스크탑 (≥ 1280px)
═══════════════════════════════════════ */
@media (min-width: 1280px) {
    .sidebar {
        width: 240px;
    }

    .media-grid {
        grid-template-columns: repeat(4, 1fr);
        gap: 14px;
    }

    .section-title {
        font-size: 18px;
    }
}
</style>
