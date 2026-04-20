<template>
    <ion-page>
        <ion-content :fullscreen="true" class="sns-content">
            <div class="page-layout">

                <Sidebar active="sns"/>

                <!-- ── 메인 영역 ── -->
                <div class="main-wrap">

                    <!-- 상단 바 -->
                    <TopBar v-model="searchQuery" placeholder="인증샷, 성지, 작품 검색..."/>

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

                    <!-- 인증샷 그리드 -->
                    <div class="photo-grid">
                        <div
                            v-for="post in filteredPosts"
                            :key="post.id"
                            class="photo-card"
                            @click="openPost(post)"
                        >
                            <div class="photo-thumb" :style="{ background: post.gradient }">
                                <span class="photo-emoji">{{ post.emoji }}</span>
                                <span class="content-badge">{{ post.contentType }}</span>
                            </div>
                            <div class="card-body">
                                <div class="card-user">
                                    <div class="user-avatar-sm" :style="{ background: post.avatarColor }">
                                        {{ post.userName[0] }}
                                    </div>
                                    <span class="user-name-sm">{{ post.userName }}</span>
                                    <span class="time-ago">{{ post.timeAgo }}</span>
                                </div>
                                <p class="card-place">📍 {{ post.placeName }}</p>
                                <p class="card-content">{{ post.contentTitle }}</p>
                                <div class="card-stats">
                                    <span class="stat"><ion-icon name="heart-outline"/>{{ post.likeCount }}</span>
                                    <span class="stat"><ion-icon name="chatbubble-outline"/>{{ post.commentCount }}</span>
                                </div>
                            </div>
                        </div>
                    </div>

                    <div class="bottom-spacer"/>
                </div>
            </div>
        </ion-content>

        <!-- ── 포스트 상세 시트 ── -->
        <Transition name="sheet">
            <div v-if="selectedPost" class="sheet-backdrop" @click.self="closePost">
                <div class="post-sheet">
                    <div class="sheet-handle"/>
                    <div class="sheet-photo" :style="{ background: selectedPost.gradient }">
                        <span class="sheet-emoji">{{ selectedPost.emoji }}</span>
                        <button class="sheet-close" @click="closePost">
                            <ion-icon name="close-outline"/>
                        </button>
                    </div>
                    <div class="sheet-body">
                        <div class="sheet-user-row">
                            <div class="user-avatar-lg" :style="{ background: selectedPost.avatarColor }">
                                {{ selectedPost.userName[0] }}
                            </div>
                            <div class="sheet-user-info">
                                <p class="sheet-username">{{ selectedPost.userName }}</p>
                                <p class="sheet-time">{{ selectedPost.timeAgo }}</p>
                            </div>
                            <button class="follow-btn">팔로우</button>
                        </div>

                        <p class="sheet-caption">{{ selectedPost.caption }}</p>

                        <div class="sheet-place-card">
                            <ion-icon name="location-outline" class="place-icon"/>
                            <div class="place-info">
                                <p class="place-name">{{ selectedPost.placeName }}</p>
                                <p class="place-content">{{ selectedPost.contentTitle }} · {{ selectedPost.contentType }}</p>
                            </div>
                            <button class="place-link-btn" @click="goPlace(selectedPost)">
                                <ion-icon name="chevron-forward-outline"/>
                            </button>
                        </div>

                        <div class="sheet-actions">
                            <button class="action-btn liked" @click="toggleLike(selectedPost)">
                                <ion-icon :name="selectedPost.liked ? 'heart' : 'heart-outline'"/>
                                <span>{{ selectedPost.likeCount }}</span>
                            </button>
                            <button class="action-btn">
                                <ion-icon name="chatbubble-outline"/>
                                <span>{{ selectedPost.commentCount }}</span>
                            </button>
                            <button class="action-btn">
                                <ion-icon name="share-social-outline"/>
                                <span>공유</span>
                            </button>
                        </div>

                        <div class="comments-section">
                            <p class="comments-title">댓글 {{ selectedPost.commentCount }}개</p>
                            <div
                                v-for="c in selectedPost.comments"
                                :key="c.id"
                                class="comment-item"
                            >
                                <div class="comment-avatar" :style="{ background: c.avatarColor }">{{ c.user[0] }}</div>
                                <div class="comment-content">
                                    <span class="comment-user">{{ c.user }}</span>
                                    <span class="comment-text">{{ c.text }}</span>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </Transition>

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
import { ref, computed } from 'vue';
import { IonPage, IonContent, IonIcon, useIonRouter } from '@ionic/vue';
import { addIcons } from 'ionicons';
import {
    homeOutline, mapOutline, people, personOutline,
    heartOutline, heart, chatbubbleOutline, shareSocialOutline,
    locationOutline, chevronForwardOutline, closeOutline,
} from 'ionicons/icons';
import { useAuth } from '@/composables/useAuth';
import TopBar from '@/components/common/TopBar.vue';
import Sidebar from '@/components/common/Sidebar.vue';

addIcons({
    'home-outline': homeOutline, 'map-outline': mapOutline,
    'people': people, 'person-outline': personOutline,
    'heart-outline': heartOutline, 'heart': heart,
    'chatbubble-outline': chatbubbleOutline,
    'share-social-outline': shareSocialOutline,
    'location-outline': locationOutline,
    'chevron-forward-outline': chevronForwardOutline,
    'close-outline': closeOutline,
});

const router = useIonRouter();
const { isLoggedIn } = useAuth();

const searchQuery = ref('');

function goHome() { router.push('/home'); }
function goMap() { router.push('/map'); }
function goAuth() { router.push(isLoggedIn.value ? '/mypage' : '/auth'); }
function goPlace(post: any) {
    closePost();
    router.push({ name: 'Map', query: { placeId: post.placeId } });
}

const filters = [
    { key: 'all', label: '전체' },
    { key: '드라마', label: '드라마' },
    { key: '영화', label: '영화' },
    { key: '애니', label: '애니' },
];
const activeFilter = ref('all');

const posts = ref([
    {
        id: 1, placeId: 1, userName: '김민준', avatarColor: '#f093fb',
        placeName: '스가 신사', contentTitle: '너의 이름은.', contentType: '애니',
        gradient: 'linear-gradient(135deg,#f093fb,#f5576c)',
        emoji: '🌠', likeCount: 248, commentCount: 34, timeAgo: '2시간 전',
        liked: false,
        caption: '타키와 미츠하가 서로를 찾아 뛰어다녔던 그 계단... 직접 와보니 감동이 두 배예요 🥹',
        comments: [
            { id: 1, user: '이서연', text: '저도 가보고 싶어요!!', avatarColor: '#4facfe' },
            { id: 2, user: '박지호', text: '성지 인증 부럽다 ㅠㅠ', avatarColor: '#43e97b' },
            { id: 3, user: '최유진', text: '계단 올라가는 길이 진짜 예쁘죠?', avatarColor: '#fa709a' },
        ],
    },
    {
        id: 2, placeId: 2, userName: '이서연', avatarColor: '#4facfe',
        placeName: '이태원 단밤 촬영지', contentTitle: '이태원 클라쓰', contentType: '드라마',
        gradient: 'linear-gradient(135deg,#4facfe,#00f2fe)',
        emoji: '🍺', likeCount: 183, commentCount: 21, timeAgo: '5시간 전',
        liked: false,
        caption: '밤새 고민한 박새로이의 그 가게 앞! 실제로 보니 더 멋있어요 ✨',
        comments: [
            { id: 1, user: '한소이', text: '오 여기 식당 지금도 운영해요?', avatarColor: '#c471f5' },
            { id: 2, user: '김도현', text: '드라마 보면서 항상 가고 싶었어요', avatarColor: '#fd7043' },
        ],
    },
    {
        id: 3, placeId: 3, userName: '박지호', avatarColor: '#43e97b',
        placeName: '가마쿠라 고코마에역', contentTitle: '슬램덩크', contentType: '애니',
        gradient: 'linear-gradient(135deg,#43e97b,#38f9d7)',
        emoji: '🏀', likeCount: 421, commentCount: 67, timeAgo: '어제',
        liked: false,
        caption: '그 유명한 기찻길 건널목!! 실제로 보니 너무 감동적이에요 🚂🏀',
        comments: [
            { id: 1, user: '최유진', text: '필수 성지 인증 완료!', avatarColor: '#a18cd1' },
            { id: 2, user: '정하늘', text: '저도 갔다왔는데 인파가 어마어마해요', avatarColor: '#667eea' },
            { id: 3, user: '나윤서', text: '역 앞에서 찍은 사진 너무 이쁘다', avatarColor: '#fa709a' },
        ],
    },
    {
        id: 4, placeId: 4, userName: '최유진', avatarColor: '#fa709a',
        placeName: '시부야 스크램블 교차로', contentTitle: '주술회전', contentType: '애니',
        gradient: 'linear-gradient(135deg,#fa709a,#fee140)',
        emoji: '🔮', likeCount: 156, commentCount: 18, timeAgo: '어제',
        liked: false,
        caption: '오빠가 싸웠던 그 교차로...! 사람 너무 많아서 혼파망이에요 😂',
        comments: [
            { id: 1, user: '김민준', text: '주술회전 성지 드디어 다녀오셨군요!', avatarColor: '#f093fb' },
        ],
    },
    {
        id: 5, placeId: 5, userName: '정하늘', avatarColor: '#a18cd1',
        placeName: '경복궁 근정전', contentTitle: '킹덤', contentType: '드라마',
        gradient: 'linear-gradient(135deg,#a18cd1,#fbc2eb)',
        emoji: '🏯', likeCount: 302, commentCount: 41, timeAgo: '2일 전',
        liked: false,
        caption: '좀비 왕이 살았던 그 궁궐... 웅장함은 실제로 봐야 알아요 🌿',
        comments: [
            { id: 1, user: '오세진', text: '킹덤 첫 장면 생각나네요!', avatarColor: '#30cfd0' },
            { id: 2, user: '임나래', text: '역사적인 곳이라 더 의미있어 보여요', avatarColor: '#96fbc4' },
        ],
    },
    {
        id: 6, placeId: 6, userName: '한소이', avatarColor: '#c471f5',
        placeName: '인왕산 청운공원', contentTitle: '나의 아저씨', contentType: '드라마',
        gradient: 'linear-gradient(135deg,#ffecd2,#fcb69f)',
        emoji: '🌙', likeCount: 89, commentCount: 12, timeAgo: '2일 전',
        liked: false,
        caption: '박동훈 아저씨가 올라가던 그 계단... 올라가다 숨이 차서 잠깐 쉬었어요 😅',
        comments: [
            { id: 1, user: '강민호', text: '나의 아저씨 다시 보고 싶어지네요', avatarColor: '#fddb92' },
        ],
    },
    {
        id: 7, placeId: 7, userName: '김도현', avatarColor: '#fd7043',
        placeName: '롯데월드 민속마을', contentTitle: '이상한 변호사 우영우', contentType: '드라마',
        gradient: 'linear-gradient(135deg,#a1c4fd,#c2e9fb)',
        emoji: '🐋', likeCount: 274, commentCount: 55, timeAgo: '3일 전',
        liked: false,
        caption: '우영우가 팽이처럼 돌았던 그 곳!!! 저도 따라 돌아봤어요 ㅋㅋ 🐳',
        comments: [
            { id: 1, user: '유수진', text: '팽이 돌기 필수 인증 ㅋㅋㅋ', avatarColor: '#96fbc4' },
            { id: 2, user: '백서영', text: '고래 보러 갔는데 고래가 없어요 ㅜ', avatarColor: '#f5576c' },
            { id: 3, user: '신건우', text: '오 저도 다음 주에 갈 예정!', avatarColor: '#667eea' },
        ],
    },
    {
        id: 8, placeId: 8, userName: '오세진', avatarColor: '#30cfd0',
        placeName: '제주 협재해수욕장', contentTitle: '우리들의 블루스', contentType: '드라마',
        gradient: 'linear-gradient(135deg,#0cebeb,#20e3b2)',
        emoji: '🌊', likeCount: 198, commentCount: 29, timeAgo: '4일 전',
        liked: false,
        caption: '이영희랑 최한수가 바다 보던 그 해변... 제주도 물색깔 실화야? 😍',
        comments: [
            { id: 1, user: '이서연', text: '제주도 가고 싶다ㅠ', avatarColor: '#4facfe' },
        ],
    },
    {
        id: 9, placeId: 9, userName: '나윤서', avatarColor: '#fa709a',
        placeName: '홍대 걷고싶은거리', contentTitle: '그 해 우리는', contentType: '드라마',
        gradient: 'linear-gradient(135deg,#667eea,#764ba2)',
        emoji: '🎨', likeCount: 137, commentCount: 16, timeAgo: '5일 전',
        liked: false,
        caption: '최웅이랑 국연수가 걷던 홍대 골목 찾았어요! 생각보다 금방 찾았음 😊',
        comments: [
            { id: 1, user: '박지호', text: '저도 드라마 보고 찾아봤는데 못 찾았어요 ㅠ', avatarColor: '#43e97b' },
        ],
    },
    {
        id: 10, placeId: 10, userName: '최준혁', avatarColor: '#f83600',
        placeName: '침사추이 해변 산책로', contentTitle: '영웅본색', contentType: '영화',
        gradient: 'linear-gradient(135deg,#f83600,#f9d423)',
        emoji: '🎬', likeCount: 94, commentCount: 8, timeAgo: '6일 전',
        liked: false,
        caption: '홍콩 영화 성지 순례! 주윤발 형님이 걸었던 그 길을 걸어봤어요 🌃',
        comments: [
            { id: 1, user: '김민준', text: '홍콩 영화 성지라니 너무 멋있다', avatarColor: '#f093fb' },
        ],
    },
    {
        id: 11, placeId: 11, userName: '임나래', avatarColor: '#96fbc4',
        placeName: '해운대 해수욕장', contentTitle: '해운대', contentType: '영화',
        gradient: 'linear-gradient(135deg,#43e97b,#38f9d7)',
        emoji: '🌊', likeCount: 165, commentCount: 22, timeAgo: '1주일 전',
        liked: false,
        caption: '해운대 영화 촬영지! 실제로 오니 영화 장면이 떠올라요 🌅',
        comments: [
            { id: 1, user: '정하늘', text: '해운대 너무 좋죠!', avatarColor: '#a18cd1' },
            { id: 2, user: '오세진', text: '부산 가고 싶다', avatarColor: '#30cfd0' },
        ],
    },
    {
        id: 12, placeId: 12, userName: '강민호', avatarColor: '#fddb92',
        placeName: '인사동 쌈지길', contentTitle: '도깨비', contentType: '드라마',
        gradient: 'linear-gradient(135deg,#c471f5,#fa71cd)',
        emoji: '🕯️', likeCount: 341, commentCount: 48, timeAgo: '1주일 전',
        liked: false,
        caption: '도깨비 신부가 걷던 인사동... 여기가 맞는지 한참 찾았어요 🕯✨',
        comments: [
            { id: 1, user: '한소이', text: '도깨비 성지 순례 완료!!', avatarColor: '#c471f5' },
            { id: 2, user: '김도현', text: '공유 씨 생각 났겠다 ㅎㅎ', avatarColor: '#fd7043' },
            { id: 3, user: '나윤서', text: '겨울에 눈 오면 더 예쁠 것 같아요', avatarColor: '#fa709a' },
        ],
    },
]);

const filteredPosts = computed(() => {
    if (activeFilter.value === 'all') return posts.value;
    return posts.value.filter(p => p.contentType === activeFilter.value);
});

const selectedPost = ref<any>(null);

function openPost(post: any) {
    selectedPost.value = { ...post };
}

function closePost() {
    selectedPost.value = null;
}

function toggleLike(post: any) {
    post.liked = !post.liked;
    post.likeCount += post.liked ? 1 : -1;
}
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

/* ── 인증샷 그리드 ── */
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

.photo-emoji {
    font-size: 36px;
}

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

@media (min-width: 768px) and (max-width: 1023px) {
    .photo-grid {
        grid-template-columns: repeat(3, 1fr);
    }
}
</style>
