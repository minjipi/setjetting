<template>
    <ion-page>
        <ion-content :fullscreen="true" class="sns-content">
            <div class="page-layout">

                <Sidebar active="sns"/>

                <!-- ── 메인 영역 ── -->
                <div class="main-wrap">

                    <!-- 상단 바 -->
                    <TopBar
                        :title="selectedPostIdx ? '게시글' : ''"
                        v-model="searchQuery"
                        placeholder="인증샷, 성지, 작품 검색..."
                        @back="selectedPostIdx = null"
                    />

                    <!-- ── 피드 뷰 ── -->
                    <template v-if="!selectedPostIdx">
                        <!-- 필터 탭 -->
                        <div class="filter-bar">
                            <button
                                v-for="f in filters"
                                :key="f.key"
                                class="filter-tab"
                                :class="{ active: activeFilter === f.key }"
                                @click="activeFilter = f.key"
                            >
                                {{ f.label }}
                            </button>
                        </div>

                        <!-- 로딩 -->
                        <div v-if="loading" class="empty-state">
                            <div class="loading-spinner"/>
                            <p>불러오는 중...</p>
                        </div>

                        <!-- 빈 상태 -->
                        <div v-else-if="filteredPosts.length === 0" class="empty-state">
                            <p class="empty-icon">📭</p>
                            <p class="empty-text">아직 올라온 인증샷이 없어요</p>
                        </div>

                        <!-- 카드 그리드 -->
                        <div v-else class="photo-grid">
                            <div
                                v-for="post in filteredPosts"
                                :key="post.postIdx"
                                class="photo-card"
                                @click="handleCardClick(post)"
                            >
                                <div class="photo-thumb" :style="!post.mainImageUrl ? { background: 'linear-gradient(135deg,#e0f2fe,#bae6fd)' } : {}">
                                    <img v-if="post.mainImageUrl" :src="resolveImageUrl(post.mainImageUrl)" class="thumb-img" :alt="post.title"/>
                                    <span v-else class="photo-emoji">📍</span>
                                    <span v-if="post.contentType" class="content-badge">{{ contentTypeLabel(post.contentType) }}</span>
                                </div>
                                <div class="card-body">
                                    <div class="card-user">
                                        <div class="user-avatar-sm" :style="{ background: avatarColor(post.userName) }">
                                            {{ post.userName?.[0] ?? '?' }}
                                        </div>
                                        <span class="user-name-sm">{{ post.userName }}</span>
                                        <span class="time-ago">{{ timeAgo(post.createdAtMs) }}</span>
                                    </div>
                                    <p class="card-title">{{ post.title }}</p>
                                    <p v-if="post.placeName" class="card-place">{{ post.placeName }}</p>
                                </div>
                            </div>
                        </div>
                        <div class="bottom-spacer"/>
                    </template>

                    <!-- ── 상세 뷰 (PC 인페이지) ── -->
                    <PostDetailContent
                        v-else
                        :post-idx="selectedPostIdx"
                        @go-back="selectedPostIdx = null"
                        @go-post="(idx) => selectedPostIdx = idx"
                        @go-place="(placeIdx) => router.push({ name: 'Map', query: { placeId: placeIdx } })"
                    />

                </div>
            </div>
        </ion-content>

        <!-- 모바일 하단 탭바 -->
        <div class="bottom-nav">
            <button class="nav-btn" @click="goHome">
                <ion-icon name="home-outline"/>
                <span>홈</span>
            </button>
            <button class="nav-btn" @click="goMap">
                <ion-icon name="map-outline"/>
                <span>지도</span>
            </button>
            <button class="nav-btn active">
                <ion-icon name="people"/>
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
import { IonPage, IonContent, IonIcon, useIonRouter, onIonViewDidEnter } from '@ionic/vue';
import { addIcons } from 'ionicons';
import {
    homeOutline, mapOutline, people, personOutline,
    locationOutline, chevronForwardOutline,
    heartOutline, chatbubbleOutline, paperPlaneOutline, bookmarkOutline, imagesOutline,
} from 'ionicons/icons';
import { useAuth } from '@/composables/useAuth';
import { postApi } from '@/api/postApi';
import { resolveImageUrl } from '@/utils/imageUrl';
import TopBar from '@/components/common/TopBar.vue';
import Sidebar from '@/components/common/Sidebar.vue';
import PostDetailContent from '@/components/post/PostDetailContent.vue';

addIcons({
    'home-outline': homeOutline, 'map-outline': mapOutline,
    'people': people, 'person-outline': personOutline,
    'location-outline': locationOutline,
    'chevron-forward-outline': chevronForwardOutline,
    'heart-outline': heartOutline,
    'chatbubble-outline': chatbubbleOutline,
    'paper-plane-outline': paperPlaneOutline,
    'bookmark-outline': bookmarkOutline,
    'images-outline': imagesOutline,
});

const router = useIonRouter();
const { isLoggedIn } = useAuth();

const searchQuery = ref('');
const loading = ref(false);
const posts = ref<any[]>([]);
const selectedPostIdx = ref<number | null>(null);

function handleCardClick(post: any) {
    if (window.innerWidth >= 1024) {
        selectedPostIdx.value = post.postIdx;
    } else {
        router.push({ name: 'PostDetail', params: { idx: post.postIdx } });
    }
}

function goHome() { router.push('/home'); }
function goMap() { router.push('/map'); }
function goAuth() { router.push(isLoggedIn.value ? '/mypage' : '/auth'); }

const AVATAR_COLORS = ['#f093fb','#4facfe','#43e97b','#fa709a','#a18cd1','#fd7043','#30cfd0','#fddb92','#667eea','#f83600'];
function avatarColor(name: string): string {
    if (!name) return '#14bcef';
    let hash = 0;
    for (let i = 0; i < name.length; i++) hash = name.charCodeAt(i) + ((hash << 5) - hash);
    return AVATAR_COLORS[Math.abs(hash) % AVATAR_COLORS.length];
}

function timeAgo(ms: number): string {
    if (!ms) return '';
    const diff = Date.now() - ms;
    const min = Math.floor(diff / 60000);
    if (min < 1) return '방금 전';
    if (min < 60) return `${min}분 전`;
    const hr = Math.floor(min / 60);
    if (hr < 24) return `${hr}시간 전`;
    const day = Math.floor(hr / 24);
    if (day === 1) return '어제';
    if (day < 7) return `${day}일 전`;
    const week = Math.floor(day / 7);
    if (week < 5) return `${week}주 전`;
    return `${Math.floor(day / 30)}달 전`;
}

const TYPE_LABEL: Record<string, string> = {
    DRAMA: '드라마', MOVIE: '영화', ANIME: '애니', MUSIC_VIDEO: 'MV',
};
function contentTypeLabel(type: string): string {
    return TYPE_LABEL[type] ?? type;
}

const filters = [
    { key: 'all', label: '전체' },
    { key: 'DRAMA', label: '드라마' },
    { key: 'MOVIE', label: '영화' },
    { key: 'ANIME', label: '애니' },
];
const activeFilter = ref('all');

const filteredPosts = computed(() => {
    const base = searchQuery.value
        ? posts.value.filter(p =>
            p.title?.includes(searchQuery.value) ||
            p.placeName?.includes(searchQuery.value) ||
            p.contentTitle?.includes(searchQuery.value))
        : posts.value;
    if (activeFilter.value === 'all') return base;
    return base.filter(p => p.contentType === activeFilter.value);
});

async function loadFeed() {
    loading.value = true;
    try {
        posts.value = await postApi.getFeed();
    } catch (e) {
        console.error('피드 로드 실패', e);
    } finally {
        loading.value = false;
    }
}

onMounted(loadFeed);
onIonViewDidEnter(loadFeed);

</script>

<style scoped>
.sns-content {
    --background: #f8f9fa;
}

.page-layout {
    display: flex;
    min-height: 100%;
    background: #f8f9fa;
}

.main-wrap {
    flex: 1;
    min-width: 0;
    display: flex;
    flex-direction: column;
}

/* ── 필터 탭 ── */
.filter-bar {
    display: flex;
    gap: 8px;
    padding: 12px 16px 8px;
    overflow-x: auto;
    scrollbar-width: none;
    background: #fff;
    border-bottom: 1px solid rgba(20,188,237,0.08);
}
.filter-bar::-webkit-scrollbar { display: none; }

.filter-tab {
    flex-shrink: 0;
    padding: 6px 16px;
    border-radius: 20px;
    border: 1.5px solid rgba(20,188,237,0.25);
    background: none;
    font-size: 13px;
    font-weight: 600;
    color: var(--text-muted);
    cursor: pointer;
    transition: all 0.18s;
}

.filter-tab.active {
    background: var(--brand);
    border-color: var(--brand);
    color: #fff;
}

/* ── 카드 그리드 ── */
.photo-grid {
    display: grid;
    grid-template-columns: repeat(2, 1fr);
    gap: 12px;
    padding: 14px 12px;
}

.photo-card {
    background: #fff;
    border-radius: 14px;
    overflow: hidden;
    box-shadow: 0 2px 10px rgba(0,0,0,0.07);
    cursor: pointer;
    transition: transform 0.18s, box-shadow 0.18s;
}

.photo-card:active {
    transform: scale(0.97);
}

.photo-thumb {
    width: 100%;
    aspect-ratio: 1 / 1;
    position: relative;
    display: flex;
    align-items: center;
    justify-content: center;
}

.thumb-img {
    position: absolute;
    inset: 0;
    width: 100%;
    height: 100%;
    object-fit: cover;
}

.photo-emoji {
    font-size: 36px;
}

.card-title {
    font-size: 12px;
    font-weight: 600;
    color: var(--text-primary);
    margin: 0;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
}

.empty-state {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    padding: 60px 20px;
    gap: 12px;
}

.empty-icon { font-size: 40px; margin: 0; }
.empty-text { font-size: 14px; color: var(--text-muted); margin: 0; }

.loading-spinner {
    width: 36px;
    height: 36px;
    border: 3px solid rgba(20,188,237,0.2);
    border-top-color: var(--brand);
    border-radius: 50%;
    animation: spin 0.8s linear infinite;
}

@keyframes spin { to { transform: rotate(360deg); } }

.content-badge {
    position: absolute;
    top: 8px;
    left: 8px;
    background: rgba(0,0,0,0.45);
    color: #fff;
    font-size: 9px;
    font-weight: 700;
    padding: 2px 7px;
    border-radius: 6px;
    backdrop-filter: blur(4px);
}

.card-body {
    padding: 10px 12px 12px;
}

.card-user {
    display: flex;
    align-items: center;
    gap: 6px;
    margin-bottom: 6px;
}

.user-avatar-sm {
    width: 22px;
    height: 22px;
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 11px;
    font-weight: 700;
    color: #fff;
    flex-shrink: 0;
}

.user-name-sm {
    font-size: 11.5px;
    font-weight: 700;
    color: var(--text-primary);
    flex: 1;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
}

.time-ago {
    font-size: 10px;
    color: var(--text-muted);
    flex-shrink: 0;
}

.card-place {
    font-size: 11px;
    color: var(--brand);
    font-weight: 600;
    margin: 0 0 2px;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
}

.card-content {
    font-size: 10.5px;
    color: var(--text-muted);
    margin: 0 0 8px;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
}

.card-stats {
    display: flex;
    gap: 10px;
}

.stat {
    display: flex;
    align-items: center;
    gap: 3px;
    font-size: 11px;
    color: var(--text-muted);
    font-weight: 600;
}

.stat ion-icon {
    font-size: 12px;
}

/* ── 포스트 상세 시트 ── */
.sheet-backdrop {
    position: fixed;
    inset: 0;
    background: rgba(0,0,0,0.45);
    z-index: 500;
    display: flex;
    align-items: flex-end;
}

.post-sheet {
    width: 100%;
    max-height: 90dvh;
    background: #fff;
    border-radius: 20px 20px 0 0;
    overflow-y: auto;
    overscroll-behavior: contain;
}

.sheet-handle {
    width: 40px;
    height: 4px;
    background: #e0e0e0;
    border-radius: 2px;
    margin: 12px auto 0;
}

.sheet-photo {
    width: 100%;
    aspect-ratio: 4 / 3;
    display: flex;
    align-items: center;
    justify-content: center;
    position: relative;
    margin-top: 8px;
}

.sheet-img {
    position: absolute;
    inset: 0;
    width: 100%;
    height: 100%;
    object-fit: cover;
}

.sheet-emoji {
    font-size: 60px;
}

.sheet-close {
    position: absolute;
    top: 12px;
    right: 12px;
    width: 32px;
    height: 32px;
    border-radius: 50%;
    background: rgba(0,0,0,0.35);
    border: none;
    color: #fff;
    font-size: 18px;
    display: flex;
    align-items: center;
    justify-content: center;
    cursor: pointer;
    backdrop-filter: blur(4px);
}

.sheet-body {
    padding: 16px;
}

.sheet-user-row {
    display: flex;
    align-items: center;
    gap: 10px;
    margin-bottom: 12px;
}

.user-avatar-lg {
    width: 40px;
    height: 40px;
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 16px;
    font-weight: 700;
    color: #fff;
    flex-shrink: 0;
}

.sheet-user-info {
    flex: 1;
}

.sheet-username {
    font-size: 14px;
    font-weight: 700;
    color: var(--text-primary);
    margin: 0;
}

.sheet-time {
    font-size: 11px;
    color: var(--text-muted);
    margin: 2px 0 0;
}

.follow-btn {
    padding: 6px 16px;
    border-radius: 20px;
    border: 1.5px solid var(--brand);
    background: none;
    color: var(--brand);
    font-size: 12px;
    font-weight: 700;
    cursor: pointer;
}

.sheet-title {
    font-size: 16px;
    font-weight: 700;
    color: var(--text-primary);
    margin: 0 0 8px;
}

.sheet-caption {
    font-size: 13.5px;
    color: var(--text-primary);
    line-height: 1.6;
    margin: 0 0 14px;
}

.sheet-place-card {
    display: flex;
    align-items: center;
    gap: 10px;
    background: #f0fbff;
    border: 1px solid rgba(20,188,237,0.2);
    border-radius: 12px;
    padding: 12px 14px;
    margin-bottom: 14px;
}

.place-icon {
    font-size: 20px;
    color: var(--brand);
    flex-shrink: 0;
}

.place-info {
    flex: 1;
    min-width: 0;
}

.place-name {
    font-size: 13px;
    font-weight: 700;
    color: var(--text-primary);
    margin: 0;
}

.place-content {
    font-size: 11px;
    color: var(--text-muted);
    margin: 2px 0 0;
}

.place-link-btn {
    background: none;
    border: none;
    font-size: 18px;
    color: var(--text-muted);
    cursor: pointer;
    display: flex;
    align-items: center;
    padding: 4px;
}

.sheet-actions {
    display: flex;
    gap: 8px;
    margin-bottom: 20px;
}

.action-btn {
    flex: 1;
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 6px;
    padding: 10px;
    border-radius: 10px;
    border: 1.5px solid rgba(20,188,237,0.2);
    background: none;
    font-size: 13px;
    font-weight: 600;
    color: var(--text-muted);
    cursor: pointer;
    transition: all 0.15s;
}

.action-btn ion-icon {
    font-size: 18px;
}

.action-btn.liked {
    border-color: #ff4757;
    color: #ff4757;
}

.comments-section {
    border-top: 1px solid #f0f0f0;
    padding-top: 16px;
}

.comments-title {
    font-size: 13px;
    font-weight: 700;
    color: var(--text-primary);
    margin: 0 0 12px;
}

.comment-item {
    display: flex;
    gap: 10px;
    margin-bottom: 12px;
}

.comment-avatar {
    width: 30px;
    height: 30px;
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 12px;
    font-weight: 700;
    color: #fff;
    flex-shrink: 0;
}

.comment-content {
    background: #f8f9fa;
    border-radius: 10px;
    padding: 8px 12px;
    flex: 1;
}

.comment-user {
    font-size: 12px;
    font-weight: 700;
    color: var(--text-primary);
    margin-right: 8px;
}

.comment-text {
    font-size: 12px;
    color: var(--text-primary);
}

/* ── 트랜지션 ── */
.sheet-enter-active, .sheet-leave-active {
    transition: opacity 0.25s;
}
.sheet-enter-active .post-sheet, .sheet-leave-active .post-sheet {
    transition: transform 0.3s cubic-bezier(0.34,1.56,0.64,1);
}
.sheet-enter-from, .sheet-leave-to {
    opacity: 0;
}
.sheet-enter-from .post-sheet, .sheet-leave-to .post-sheet {
    transform: translateY(100%);
}

/* ── 하단 탭바 ── */
.bottom-nav {
    position: fixed;
    bottom: 0; left: 0; right: 0;
    height: 60px;
    background: #fff;
    border-top: 1px solid rgba(20,188,237,0.15);
    box-shadow: 0 -2px 16px rgba(20,188,237,0.1);
    display: flex;
    align-items: center;
    justify-content: space-around;
    padding-bottom: env(safe-area-inset-bottom, 0);
    z-index: 200;
}

.nav-btn {
    flex: 1;
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: 3px;
    background: none;
    border: none;
    color: var(--text-muted);
    font-size: 20px;
    cursor: pointer;
    height: 100%;
    justify-content: center;
    padding: 0;
}

.nav-btn span {
    font-size: 10px;
    font-weight: 600;
}

.nav-btn.active {
    color: var(--brand);
}

.bottom-spacer { height: 80px; }

/* ── 데스크탑 ── */
@media (min-width: 1024px) {
    .bottom-nav { display: none; }

    .photo-grid {
        grid-template-columns: repeat(3, 1fr);
        padding: 20px;
        gap: 16px;
    }

    .sheet-backdrop { align-items: center; justify-content: center; }

    .post-sheet {
        width: 480px;
        max-height: 85dvh;
        border-radius: 20px;
    }

    .sheet-handle { display: none; }
}
</style>
