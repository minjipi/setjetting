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
                        <button class="sidebar-nav-btn">
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
                    <MapSection/>

                    <!-- 콘텐츠 본문 -->
                    <div class="content-body">
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
                                            v-for="item in recentItems"
                                            :key="item.title"
                                            :item="item"
                                            @click="onMediaClick(item)"
                                    />
                                </div>
                            </section>

                            <!-- 인기 작품 -->
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
                                            v-for="item in popularItems"
                                            :key="item.title"
                                            :item="item"
                                            @click="onMediaClick(item)"
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
    chevronForwardOutline, notificationsOutline, personOutline
} from 'ionicons/icons';
import MapSection from '@/components/home/MapSection.vue';
import MediaCard from '@/components/home/MediaCard.vue';
import {useAuth} from '@/composables/useAuth';
import { contentsApi } from '@/api/contentsApi';

addIcons({
    'search-outline': searchOutline,
    'home': home,
    'map-outline': mapOutline,
    'bookmark-outline': bookmarkOutline,
    'chevron-forward-outline': chevronForwardOutline,
    'notifications-outline': notificationsOutline,
    'person-outline': personOutline,
});

const router = useIonRouter();
const {isLoggedIn, user} = useAuth();

const userInitial = computed(() => user.value?.name?.charAt(0).toUpperCase() ?? 'U');

function goAuth() {
    router.push(isLoggedIn.value ? '/mypage' : '/auth');
}

function goProfile() {
    router.push('/mypage');
}

const searchQuery = ref('');
const searchInputEl = ref<HTMLInputElement | null>(null);

function focusSearch() {
    searchInputEl.value?.focus();
}

function onMediaClick(_item: any) {
    // TODO: router.push(`/media/${_item.id}`)
}

const recentItems = ref([]);
const popularItems = ref([]);

async function loadContents() {
    const data = await contentsApi.getList();
    if (Array.isArray(data)) {
        recentItems.value = data;
    }
}

onMounted(() => {
    loadContents();
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

/* 미디어 그리드: 모바일 3열 */
.media-grid {
    display: grid;
    grid-template-columns: repeat(3, 1fr);
    gap: 10px;
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

    /* 태블릿 4열 */
    .media-grid {
        grid-template-columns: repeat(4, 1fr);
        gap: 14px;
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

    /* 데스크탑 5열 */
    .media-grid {
        grid-template-columns: repeat(3, 1fr);
        gap: 12px;
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
