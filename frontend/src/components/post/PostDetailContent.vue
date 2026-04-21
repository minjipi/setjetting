<template>
  <!-- 로딩 -->
  <div v-if="loading" class="center-state">
    <div class="spinner"/>
  </div>

  <!-- 에러 -->
  <div v-else-if="error" class="center-state">
    <p class="error-text">게시글을 불러올 수 없어요.</p>
    <button class="back-btn-text" @click="$emit('goBack')">돌아가기</button>
  </div>

  <div v-else-if="post" class="detail-inner">

    <!-- ── 메인 포스트 카드 ── -->
    <div class="post-card">

      <!-- 작성자 행 (PC: 카드 오른쪽 상단 / 모바일: 이미지 위 오버레이) -->
      <div class="author-row">
        <div class="avatar" :style="{ background: avatarColor(post.userNickname) }">
          {{ post.userNickname?.[0] ?? '?' }}
        </div>
        <div class="author-info">
          <p class="author-name">{{ post.userNickname }}</p>
          <p class="post-time">{{ timeAgo(post.createdAtMs) }}</p>
        </div>
      </div>

      <!-- 이미지 캐러셀 -->
      <div class="card-image">
        <div v-if="post.images?.length" class="carousel-wrap">
          <div class="carousel" ref="carouselEl" @scroll.passive="onScroll">
            <div v-for="(img, i) in post.images" :key="i" class="carousel-slide">
              <img :src="resolveImageUrl(img.imageUrl)" :alt="img.description || post.title" class="carousel-img"/>
            </div>
          </div>
          <template v-if="post.images.length > 1">
            <button class="carousel-btn carousel-btn--prev" @click="slideTo(currentSlide - 1)" :disabled="currentSlide === 0">
              <ion-icon name="chevron-back-outline"/>
            </button>
            <button class="carousel-btn carousel-btn--next" @click="slideTo(currentSlide + 1)" :disabled="currentSlide === post.images.length - 1">
              <ion-icon name="chevron-forward-outline"/>
            </button>
            <div class="carousel-dots">
              <span
                v-for="(_, i) in post.images" :key="i"
                class="dot" :class="{ active: currentSlide === i }"
                @click="slideTo(i)"
              />
            </div>
          </template>
        </div>
        <div v-else class="no-image-placeholder"><span>📍</span></div>
      </div>

      <!-- 액션 버튼 -->
      <div class="actions-row">
        <div class="actions-left">
          <button class="action-btn" aria-label="좋아요"><ion-icon name="heart-outline"/></button>
          <button class="action-btn" aria-label="댓글"><ion-icon name="chatbubble-outline"/></button>
          <button class="action-btn" aria-label="공유"><ion-icon name="paper-plane-outline"/></button>
        </div>
        <button class="action-btn" aria-label="저장"><ion-icon name="bookmark-outline"/></button>
      </div>

      <!-- 캡션 / 본문 -->
      <div class="caption-area">
        <p class="caption-text">
          <span class="caption-user">{{ post.userNickname }}</span>
          {{ post.title }}
        </p>
        <p v-if="post.description" class="post-desc">{{ post.description }}</p>

        <div v-if="captionedImages.length" class="image-captions">
          <div v-for="(img, i) in captionedImages" :key="i" class="caption-item">
            <img :src="resolveImageUrl(img.imageUrl)" class="caption-thumb" :alt="img.description"/>
            <p class="caption-item-text">{{ img.description }}</p>
          </div>
        </div>
      </div>

      <!-- 성지 카드 -->
      <div v-if="post.placeName" class="place-card" @click="$emit('goPlace', post.placeIdx)">
        <ion-icon name="location-outline" class="place-icon"/>
        <div class="place-info">
          <p class="place-name">{{ post.placeName }}</p>
          <p v-if="post.contentTitle" class="place-content">
            {{ post.contentTitle }}
            <span v-if="post.contentType"> · {{ contentTypeLabel(post.contentType) }}</span>
          </p>
        </div>
        <ion-icon name="chevron-forward-outline" class="place-arrow"/>
      </div>

      <!-- 댓글 입력 -->
      <div class="comment-input-wrap">
        <div class="comment-avatar-sm"/>
        <span class="comment-placeholder">댓글 달기...</span>
      </div>

    </div>

    <!-- ── 모바일: 다음 게시글 ── -->
    <div v-if="nextFeedPost" class="next-post-section" @click="$emit('goPost', nextFeedPost.postIdx)">
      <div class="next-divider"><span class="next-label">다음 게시글</span></div>
      <div class="author-row next-author-row">
        <div class="avatar" :style="{ background: avatarColor(nextFeedPost.userName) }">
          {{ nextFeedPost.userName?.[0] ?? '?' }}
        </div>
        <div class="author-info">
          <p class="author-name">{{ nextFeedPost.userName }}</p>
          <p class="post-time">{{ timeAgo(nextFeedPost.createdAtMs) }}</p>
        </div>
      </div>
      <div class="next-image-wrap" :style="!nextFeedPost.mainImageUrl ? { background: 'linear-gradient(135deg,#e0f2fe,#bae6fd)' } : {}">
        <img v-if="nextFeedPost.mainImageUrl" :src="resolveImageUrl(nextFeedPost.mainImageUrl)" class="next-img"/>
        <span v-else class="next-img-empty">📍</span>
      </div>
      <div class="next-actions">
        <div class="actions-left">
          <button class="action-btn" @click.stop aria-label="좋아요"><ion-icon name="heart-outline"/></button>
          <button class="action-btn" @click.stop aria-label="댓글"><ion-icon name="chatbubble-outline"/></button>
          <button class="action-btn" @click.stop aria-label="공유"><ion-icon name="paper-plane-outline"/></button>
        </div>
        <button class="action-btn" @click.stop aria-label="저장"><ion-icon name="bookmark-outline"/></button>
      </div>
      <p class="next-caption">
        <span class="caption-user">{{ nextFeedPost.userName }}</span>
        {{ nextFeedPost.title }}
      </p>
      <div v-if="nextFeedPost.placeName" class="next-place">📍 {{ nextFeedPost.placeName }}</div>
    </div>

    <!-- ── 다른 게시글 ── -->
    <div v-if="otherPosts.length" class="more-section">
      <p class="more-title">
        <span class="more-username">{{ post.userNickname }}</span>님의 다른 게시글
      </p>
      <div class="more-grid">
        <div
          v-for="p in otherPosts" :key="p.postIdx"
          class="more-thumb"
          @click="$emit('goPost', p.postIdx)"
        >
          <img v-if="p.mainImageUrl" :src="resolveImageUrl(p.mainImageUrl)" :alt="p.title" class="more-thumb-img"/>
          <div v-else class="more-thumb-empty">📍</div>
          <div class="more-thumb-overlay">
            <ion-icon name="images-outline" v-if="p.imageCount > 1" class="multi-icon"/>
          </div>
          <div class="more-thumb-caption">
            <p v-if="p.placeName" class="more-thumb-place">{{ p.placeName }}</p>
            <p class="more-thumb-title">{{ p.title }}</p>
          </div>
        </div>
      </div>
    </div>

  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch } from 'vue';
import { IonIcon } from '@ionic/vue';
import { addIcons } from 'ionicons';
import {
  locationOutline, chevronForwardOutline, chevronBackOutline,
  heartOutline, chatbubbleOutline, paperPlaneOutline, bookmarkOutline, imagesOutline,
} from 'ionicons/icons';
import { postApi } from '@/api/postApi';
import { resolveImageUrl } from '@/utils/imageUrl';

addIcons({
  'location-outline': locationOutline,
  'chevron-forward-outline': chevronForwardOutline,
  'chevron-back-outline': chevronBackOutline,
  'heart-outline': heartOutline,
  'chatbubble-outline': chatbubbleOutline,
  'paper-plane-outline': paperPlaneOutline,
  'bookmark-outline': bookmarkOutline,
  'images-outline': imagesOutline,
});

const props = defineProps<{
  postIdx: number | null;
  feedPosts?: any[];
}>();
const emit = defineEmits<{
  goBack: [];
  goPost: [idx: number];
  goPlace: [placeIdx: number];
}>();

const loading = ref(false);
const error = ref(false);
const post = ref<any>(null);
const otherPosts = ref<any[]>([]);
const currentSlide = ref(0);
const carouselEl = ref<HTMLElement | null>(null);

const captionedImages = computed(() =>
  (post.value?.images ?? []).filter((img: any) => img.description)
);

const nextFeedPost = computed(() => {
  if (!props.feedPosts?.length || !props.postIdx) return null;
  const idx = props.feedPosts.findIndex((p: any) => p.postIdx === props.postIdx);
  if (idx === -1 || idx >= props.feedPosts.length - 1) return null;
  return props.feedPosts[idx + 1];
});

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
  return `${Math.floor(day / 7)}주 전`;
}

const TYPE_LABEL: Record<string, string> = { DRAMA: '드라마', MOVIE: '영화', ANIME: '애니', MUSIC_VIDEO: 'MV' };
function contentTypeLabel(type: string): string { return TYPE_LABEL[type] ?? type; }

function onScroll() {
  if (!carouselEl.value) return;
  const { scrollLeft, clientWidth } = carouselEl.value;
  currentSlide.value = Math.round(scrollLeft / clientWidth);
}

function slideTo(index: number) {
  if (!carouselEl.value) return;
  const total = post.value?.images?.length ?? 0;
  const target = Math.max(0, Math.min(index, total - 1));
  carouselEl.value.scrollTo({ left: target * carouselEl.value.clientWidth, behavior: 'smooth' });
  currentSlide.value = target;
}

async function load(idx: number) {
  loading.value = true;
  error.value = false;
  post.value = null;
  otherPosts.value = [];
  currentSlide.value = 0;
  try {
    const detail = await postApi.getPostDetail(idx);
    post.value = detail;
    if (window.innerWidth >= 1024 && detail.userIdx) {
      const all = await postApi.getPostsByUser(detail.userIdx);
      otherPosts.value = all.filter((p: any) => p.postIdx !== detail.postIdx).slice(0, 9);
    }
  } catch (e) {
    console.error('게시글 로드 실패', e);
    error.value = true;
  } finally {
    loading.value = false;
  }
}

watch(() => props.postIdx, (idx) => { if (idx) load(idx); }, { immediate: true });
</script>

<style scoped>
.center-state {
  display: flex; flex-direction: column;
  align-items: center; justify-content: center;
  min-height: 50vh; gap: 16px;
}
.spinner {
  width: 36px; height: 36px;
  border: 3px solid rgba(20,188,237,0.2);
  border-top-color: #14bcef;
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
}
@keyframes spin { to { transform: rotate(360deg); } }
.error-text { font-size: 15px; color: #6b7280; }
.back-btn-text {
  padding: 8px 20px; border-radius: 20px; background: #14bcef;
  color: #fff; border: none; font-size: 14px; font-weight: 700; cursor: pointer;
}

/* ── 레이아웃 ── */
.detail-inner {
  display: flex; flex-direction: column; gap: 24px;
  padding-bottom: 48px;
}

/* 모바일: 세로 스택, 여백 없음 */
.post-card {
  display: flex; flex-direction: column;
  background: #fff;
}

/* 작성자 행 */
.author-row {
  display: flex; align-items: center; gap: 10px;
  padding: 12px 14px;
}
.avatar {
  width: 34px; height: 34px; border-radius: 50%;
  display: flex; align-items: center; justify-content: center;
  font-size: 14px; font-weight: 700; color: #fff; flex-shrink: 0;
}
.author-info { flex: 1; }
.author-name { font-size: 13px; font-weight: 700; color: #111827; margin: 0; }
.post-time { font-size: 11px; color: #9ca3af; margin: 1px 0 0; }

/* 이미지 */
.card-image { background: #000; position: relative; }

.carousel-wrap { position: relative; }
.carousel { display: flex; overflow-x: auto; scroll-snap-type: x mandatory; scrollbar-width: none; }
.carousel::-webkit-scrollbar { display: none; }
.carousel-slide { flex-shrink: 0; width: 100%; aspect-ratio: 1/1; scroll-snap-align: start; }
.carousel-img { width: 100%; height: 100%; object-fit: cover; display: block; }

.carousel-btn {
  position: absolute; top: 50%; transform: translateY(-50%);
  width: 32px; height: 32px; border-radius: 50%;
  border: none; background: rgba(0,0,0,0.4); color: #fff;
  font-size: 18px; display: flex; align-items: center; justify-content: center;
  cursor: pointer; backdrop-filter: blur(4px);
  transition: background 0.15s, opacity 0.15s; z-index: 10;
}
.carousel-btn:hover { background: rgba(0,0,0,0.65); }
.carousel-btn:disabled { opacity: 0.2; cursor: default; }
.carousel-btn--prev { left: 10px; }
.carousel-btn--next { right: 10px; }

.carousel-dots {
  position: absolute; bottom: 10px; left: 50%; transform: translateX(-50%);
  display: flex; gap: 5px;
}
.dot { width: 5px; height: 5px; border-radius: 50%; background: rgba(255,255,255,0.5); cursor: pointer; transition: background 0.2s, transform 0.2s; }
.dot.active { background: #fff; transform: scale(1.3); }

.no-image-placeholder {
  aspect-ratio: 1/1; background: linear-gradient(135deg,#e0f2fe,#bae6fd);
  display: flex; align-items: center; justify-content: center; font-size: 56px;
}

/* 액션 버튼 */
.actions-row {
  display: flex; align-items: center; justify-content: space-between;
  padding: 6px 8px 2px;
}
.actions-left { display: flex; gap: 2px; }
.action-btn {
  width: 40px; height: 40px; border: none; background: none;
  display: flex; align-items: center; justify-content: center;
  font-size: 24px; color: #111827; cursor: pointer; border-radius: 50%;
  transition: color 0.15s;
}
.action-btn:hover { color: #14bcef; }

/* 캡션 */
.caption-area { padding: 2px 14px 8px; }
.caption-text {
  font-size: 13.5px; color: #374151; line-height: 1.6; margin: 0 0 6px;
}
.caption-user { font-weight: 700; color: #111827; margin-right: 4px; }
.post-desc { font-size: 13px; color: #6b7280; line-height: 1.7; margin: 0; white-space: pre-wrap; }

.image-captions { display: flex; flex-direction: column; gap: 10px; border-top: 1px solid #f3f4f6; padding-top: 10px; margin-top: 8px; }
.caption-item { display: flex; gap: 10px; align-items: flex-start; }
.caption-thumb { width: 60px; height: 60px; object-fit: cover; border-radius: 8px; flex-shrink: 0; }
.caption-item-text { font-size: 12px; color: #374151; line-height: 1.6; margin: 0; }

/* 성지 카드 */
.place-card {
  display: flex; align-items: center; gap: 10px;
  background: #f0fbff; border-top: 1px solid rgba(20,188,237,0.15);
  border-bottom: 1px solid rgba(20,188,237,0.15);
  padding: 12px 14px; margin: 4px 0;
  cursor: pointer; transition: background 0.15s;
}
.place-card:hover { background: #e0f7fe; }
.place-icon { font-size: 20px; color: #14bcef; flex-shrink: 0; }
.place-info { flex: 1; min-width: 0; }
.place-name { font-size: 13px; font-weight: 700; color: #111827; margin: 0; }
.place-content { font-size: 11px; color: #6b7280; margin: 2px 0 0; }
.place-arrow { font-size: 16px; color: #d1d5db; flex-shrink: 0; }

/* 댓글 입력 */
.comment-input-wrap {
  display: flex; align-items: center; gap: 10px;
  padding: 10px 14px 14px; border-top: 1px solid #f3f4f6;
}
.comment-avatar-sm { width: 28px; height: 28px; border-radius: 50%; background: #e5e7eb; flex-shrink: 0; }
.comment-placeholder { font-size: 13px; color: #9ca3af; }

/* 모바일: 다음 게시글 */
.next-post-section {
  background: #fff;
  cursor: pointer;
  border-top: 8px solid #f1f5f9;
}
.next-divider {
  display: flex; align-items: center; justify-content: center;
  padding: 10px 0 4px;
}
.next-label {
  font-size: 11px; font-weight: 700; color: #9ca3af; letter-spacing: 0.5px;
}
.next-author-row { padding: 10px 14px 6px; }
.next-image-wrap {
  position: relative; width: 100%; aspect-ratio: 1/1;
  overflow: hidden; display: flex; align-items: center; justify-content: center;
}
.next-img { width: 100%; height: 100%; object-fit: cover; display: block; }
.next-img-empty { font-size: 48px; }
.next-actions {
  display: flex; align-items: center; justify-content: space-between;
  padding: 6px 8px 2px;
}
.next-caption {
  padding: 2px 14px 6px; font-size: 13.5px; color: #374151; line-height: 1.6; margin: 0;
}
.next-place {
  padding: 0 14px 14px; font-size: 12px; color: #14bcef; font-weight: 600; margin: 0;
}

/* 다른 게시글 (PC 전용) */
.more-section { display: none; padding: 0 14px 16px; }
.more-title { font-size: 14px; font-weight: 700; color: #374151; margin: 0 0 14px; }
.more-username { color: #111827; }

.more-grid { display: grid; grid-template-columns: repeat(3, 1fr); gap: 3px; }
.more-thumb { position: relative; aspect-ratio: 1/1; cursor: pointer; overflow: hidden; background: #f3f4f6; border-radius: 4px; }
.more-thumb-img { width: 100%; height: 100%; object-fit: cover; transition: transform 0.2s; }
.more-thumb:hover .more-thumb-img { transform: scale(1.04); }
.more-thumb-empty { width: 100%; height: 100%; display: flex; align-items: center; justify-content: center; background: linear-gradient(135deg,#e0f2fe,#bae6fd); font-size: 24px; }
.more-thumb-overlay { position: absolute; top: 0; left: 0; right: 0; display: flex; align-items: flex-start; justify-content: flex-end; padding: 6px; pointer-events: none; }
.multi-icon { font-size: 16px; color: #fff; }
.more-thumb-caption { display: none; }

/* ── PC: 2열 그리드 레이아웃 ── */
@media (min-width: 1024px) {
  .detail-inner { max-width: 980px; margin: 0 auto; padding: 40px 32px 64px; gap: 32px; }

  .post-card {
    display: grid;
    grid-template-columns: 55% 45%;
    grid-template-rows: auto 1fr auto auto auto auto;
    border-radius: 13px;
    overflow: hidden;
    box-shadow: 0 2px 16px rgba(0,0,0,0.08);
    max-height: 640px;
  }

  /* 이미지: 왼쪽 열 전체 */
  .card-image {
    grid-column: 1;
    grid-row: 1 / -1;
    display: flex; flex-direction: column;
  }
  .carousel-wrap { flex: 1; display: flex; flex-direction: column; }
  .carousel { flex: 1; height: 100%; }
  .carousel-slide { aspect-ratio: unset; height: 100%; }
  .no-image-placeholder { flex: 1; aspect-ratio: unset; }

  /* 오른쪽 열: 나머지 요소들 */
  .author-row {
    grid-column: 2; grid-row: 1;
    border-bottom: 1px solid #f3f4f6;
  }
  .actions-row {
    grid-column: 2; grid-row: 3;
  }
  .caption-area {
    grid-column: 2; grid-row: 2;
    overflow-y: auto; padding: 14px 16px;
  }
  .place-card {
    grid-column: 2; grid-row: 4;
    margin: 0; border-radius: 0;
  }
  .comment-input-wrap {
    grid-column: 2; grid-row: 5;
  }

  .next-post-section { display: none; }

  .more-section {
    display: block;
    padding: 0;
    border-top: 1px solid #e5e7eb;
    padding-top: 28px;
  }
  .more-title { font-size: 15px; margin-bottom: 18px; }
  .more-grid {
    grid-template-columns: repeat(3, 1fr);
    gap: 12px;
  }
  .more-thumb { border-radius: 12px; aspect-ratio: 4/3; }
  .more-thumb-caption {
    display: block;
    position: absolute;
    bottom: 0; left: 0; right: 0;
    padding: 32px 12px 12px;
    background: linear-gradient(to top, rgba(0,0,0,0.65), transparent);
    pointer-events: none;
  }
  .more-thumb-place {
    font-size: 10px; font-weight: 700; color: rgba(255,255,255,0.8);
    margin: 0 0 2px; text-transform: uppercase; letter-spacing: 0.5px;
  }
  .more-thumb-title {
    font-size: 13px; font-weight: 700; color: #fff;
    margin: 0; line-height: 1.4;
    display: -webkit-box; -webkit-line-clamp: 2; -webkit-box-orient: vertical; overflow: hidden;
  }
}

@media (min-width: 768px) and (max-width: 1023px) {
  .detail-inner { padding: 24px 24px 48px; }
  .post-card { border-radius: 13px; overflow: hidden; box-shadow: 0 2px 16px rgba(0,0,0,0.08); }
  .carousel-slide { aspect-ratio: 4/3; }
  .more-section { padding: 0 0 16px; }
}
</style>
