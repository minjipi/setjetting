<template>
  <ion-page>
    <ion-content :fullscreen="true" class="my-content">
      <div class="page-layout">

        <Sidebar active="profile"/>

        <!-- ── 메인 콘텐츠 ── -->
        <div class="main-wrap">

          <!-- 상단 바 -->
          <div class="top-bar">
            <button class="back-btn" @click="goHome">
              <ion-icon name="arrow-back-outline"/>
            </button>
            <span class="top-title">마이페이지</span>
            <button class="top-settings-btn" @click="router.push('/camera')">
              <ion-icon name="camera-outline"/>
            </button>
          </div>

          <!-- 프로필 헤더 -->
          <div class="profile-header">
            <div class="avatar-wrap">
              <div class="avatar">{{ userInitial }}</div>
              <button class="avatar-edit-btn" aria-label="프로필 사진 변경">
                <ion-icon name="camera-outline"/>
              </button>
            </div>
            <div class="profile-info">
              <h2 class="profile-name">{{ user?.name ?? '사용자' }}</h2>
              <p class="profile-email">{{ user?.email ?? '' }}</p>
            </div>
            <div class="profile-stats">
              <div class="stat-item">
                <span class="stat-value">{{ visitedSpots.length }}</span>
                <span class="stat-label">방문 장소</span>
              </div>
              <div class="stat-divider"/>
              <div class="stat-item">
                <span class="stat-value">{{ totalPhotos }}</span>
                <span class="stat-label">사진</span>
              </div>
              <div class="stat-divider"/>
              <div class="stat-item">
                <span class="stat-value">{{ savedCount }}</span>
                <span class="stat-label">저장</span>
              </div>
            </div>
          </div>

          <!-- 탭 -->
          <div class="tab-bar">
            <button
              class="tab-btn"
              :class="{ 'tab-active': activeTab === 'spots' }"
              @click="activeTab = 'spots'"
            >
              <ion-icon name="location-outline"/> 방문한 성지
            </button>
            <button
              class="tab-btn"
              :class="{ 'tab-active': activeTab === 'photos' }"
              @click="activeTab = 'photos'"
            >
              <ion-icon name="images-outline"/> 내 사진
            </button>
            <button
              class="tab-btn"
              :class="{ 'tab-active': activeTab === 'saved' }"
              @click="activeTab = 'saved'"
            >
              <ion-icon name="bookmark-outline"/> 저장한 성지
            </button>
          </div>

          <!-- 탭 콘텐츠 -->
          <div class="tab-content">

            <!-- 방문한 성지 -->
            <div v-if="activeTab === 'spots'" class="spots-wrap">
              <div v-if="visitedSpots.length === 0" class="empty-state">
                <div class="empty-icon">📍</div>
                <p class="empty-title">아직 방문한 성지가 없어요</p>
                <p class="empty-desc">지도에서 성지를 찾아 방문 기록을 남겨보세요</p>
                <button class="empty-action-btn" @click="goHome">성지 탐색하기</button>
              </div>
              <div v-else class="spots-grid">
                <VisitedSpotCard
                  v-for="spot in visitedSpots"
                  :key="spot.id"
                  :spot="spot"
                  @click="onSpotClick"
                />
              </div>
            </div>

            <!-- 내 사진 -->
            <div v-if="activeTab === 'photos'" class="photos-wrap">
              <div v-if="myPhotos.length === 0" class="empty-state">
                <div class="empty-icon">📷</div>
                <p class="empty-title">아직 등록한 사진이 없어요</p>
                <p class="empty-desc">방문한 성지에서 찍은 사진을 올려보세요</p>
              </div>
              <div v-else class="photos-grid">
                <div
                  v-for="photo in myPhotos"
                  :key="photo.id"
                  class="photo-item"
                  @click="onPhotoClick(photo)"
                >
                  <div class="photo-thumb" :style="{ background: photo.gradient }">
                    <span class="photo-emoji">{{ photo.emoji }}</span>
                  </div>
                  <div class="photo-overlay">
                    <p class="photo-spot">{{ photo.spotName }}</p>
                    <p class="photo-date">{{ photo.takenAt }}</p>
                  </div>
                </div>
              </div>
            </div>

            <!-- 저장한 성지 -->
            <div v-if="activeTab === 'saved'" class="spots-wrap">
              <div v-if="savedSpots.length === 0" class="empty-state">
                <div class="empty-icon">🔖</div>
                <p class="empty-title">저장한 성지가 없어요</p>
                <p class="empty-desc">마음에 드는 성지를 저장해두세요</p>
                <button class="empty-action-btn" @click="goHome">성지 탐색하기</button>
              </div>
              <template v-else>
                <!-- 보기 모드 토글 -->
                <div class="view-toggle">
                  <button
                    class="vt-btn"
                    :class="{ active: !groupByContent }"
                    @click="groupByContent = false"
                  >
                    <ion-icon name="grid-outline"/>
                    전체
                  </button>
                  <button
                    class="vt-btn"
                    :class="{ active: groupByContent }"
                    @click="groupByContent = true"
                  >
                    <ion-icon name="albums-outline"/>
                    작품별
                  </button>
                </div>

                <!-- 전체 그리드 -->
                <div v-if="!groupByContent" class="spots-grid">
                  <VisitedSpotCard
                    v-for="spot in savedSpots"
                    :key="spot.id"
                    :spot="spot"
                    @click="onSpotClick"
                  />
                </div>

                <!-- 작품별 그룹 -->
                <div v-else class="content-groups">
                  <div
                    v-for="group in groupedSaved"
                    :key="group.contentIdx"
                    class="content-group"
                  >
                    <!-- 작품 헤더 -->
                    <div
                      class="group-header"
                      @click="router.push({ name: 'Map', query: { contentId: group.contentIdx } })"
                    >
                      <div class="group-poster">
                        <img v-if="group.posterImageUrl" :src="group.posterImageUrl" :alt="group.contentTitle"/>
                        <div v-else class="group-poster-ph">🎬</div>
                      </div>
                      <div class="group-info">
                        <span class="group-type-badge">{{ group.contentType }}</span>
                        <p class="group-title">{{ group.contentTitle }}</p>
                        <p class="group-count">
                          <ion-icon name="bookmark-outline"/>
                          성지 {{ group.places.length }}곳 저장됨
                        </p>
                      </div>
                      <ion-icon name="chevron-forward-outline" class="group-arrow"/>
                    </div>

                    <!-- 성지 가로 스크롤 -->
                    <div class="group-places">
                      <div
                        v-for="place in group.places"
                        :key="place.id"
                        class="gp-card"
                        @click="router.push({ name: 'Map', query: { placeId: place.id } })"
                      >
                        <div class="gp-thumb" :style="{ background: place.gradient }">
                          <span class="gp-emoji">📍</span>
                        </div>
                        <p class="gp-name">{{ place.spotName }}</p>
                      </div>
                    </div>
                  </div>
                </div>
              </template>
            </div>

          </div>



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
      <button class="nav-btn" @click="goSns">
        <ion-icon name="people-outline"/>
        <span>SNS</span>
      </button>
      <button class="nav-btn active">
        <ion-icon name="person"/>
        <span>내 정보</span>
      </button>
    </div>
  </ion-page>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue';
import { IonPage, IonContent, IonIcon, useIonRouter, onIonViewWillEnter } from '@ionic/vue';
import { addIcons } from 'ionicons';
import {
  homeOutline, mapOutline, peopleOutline, person,
  locationOutline, imagesOutline, settingsOutline,
  arrowBackOutline, cameraOutline, logOutOutline, personOutline,
  gridOutline, albumsOutline, bookmarkOutline, chevronForwardOutline,
} from 'ionicons/icons';
import { useAuth } from '@/composables/useAuth';
import VisitedSpotCard from '@/components/mypage/VisitedSpotCard.vue';
import Sidebar from '@/components/common/Sidebar.vue';
import { bookmarkApi } from '@/api/bookmarkApi';

addIcons({
  'home-outline': homeOutline,
  'map-outline': mapOutline,
  'people-outline': peopleOutline,
  'person': person,
  'person-outline': personOutline,
  'location-outline': locationOutline,
  'images-outline': imagesOutline,
  'settings-outline': settingsOutline,
  'arrow-back-outline': arrowBackOutline,
  'camera-outline': cameraOutline,
  'log-out-outline': logOutOutline,
  'grid-outline': gridOutline,
  'albums-outline': albumsOutline,
  'bookmark-outline': bookmarkOutline,
  'chevron-forward-outline': chevronForwardOutline,
});

const router = useIonRouter();
const { user, logout } = useAuth();

const activeTab = ref<'spots' | 'photos' | 'saved'>('spots');

const userInitial = computed(() => user.value?.name?.charAt(0).toUpperCase() ?? 'U');

function goHome() { router.push('/home'); }
function goMap() { router.push('/map'); }
function goSns() { router.push('/sns'); }

async function onLogout() {
  await logout();
  router.replace('/home');
}

function onSpotClick(spot: any) {
  router.push({ name: 'Map', query: { placeId: spot.id } });
}

function onPhotoClick(_photo: any) {
  // TODO: 사진 상세 보기
}

// ── 임시 목업 데이터 ──
const visitedSpots = ref([
  {
    id: 1,
    mediaTitle: '너의 이름은.',
    mediaType: '영화',
    spotName: '스가 신사',
    visitedAt: '2024.11.03',
    emoji: '🌠',
    gradient: 'linear-gradient(145deg,#cffafe,#a5f3fc)',
    photoCount: 5,
  },
  {
    id: 2,
    mediaTitle: '주술회전 2기',
    mediaType: '애니',
    spotName: '시부야 스크램블 교차로',
    visitedAt: '2024.10.21',
    emoji: '🔮',
    gradient: 'linear-gradient(145deg,#ede9fe,#ddd6fe)',
    photoCount: 12,
  },
  {
    id: 3,
    mediaTitle: '이태원 클라쓰',
    mediaType: '드라마',
    spotName: '이태원 단밤 촬영지',
    visitedAt: '2024.09.14',
    emoji: '🍺',
    gradient: 'linear-gradient(145deg,#f0fdf4,#bbf7d0)',
    photoCount: 3,
  },
]);

const rawBookmarks = ref<any[]>([]);
const savedSpots = ref<any[]>([]);
const groupByContent = ref(false);

const contentTypeGradients: Record<string, string> = {
  DRAMA: 'linear-gradient(145deg,#e0f2fe,#bae6fd)',
  MOVIE: 'linear-gradient(145deg,#fef9c3,#fde68a)',
  ANIME: 'linear-gradient(145deg,#fce7f3,#fbcfe8)',
  MUSIC_VIDEO: 'linear-gradient(145deg,#f0fdf4,#bbf7d0)',
};

function mapBookmarkToSpot(b: any) {
  return {
    id: b.placeIdx,
    mediaTitle: b.contentTitle ?? '',
    mediaType: b.contentType ?? '',
    spotName: b.name ?? '',
    visitedAt: '',
    emoji: '📍',
    gradient: contentTypeGradients[b.contentType] ?? 'linear-gradient(145deg,#f1f5f9,#e2e8f0)',
    photoCount: 0,
  };
}

const groupedSaved = computed(() => {
  const map = new Map<number, any>();
  rawBookmarks.value.forEach(b => {
    if (!map.has(b.contentIdx)) {
      map.set(b.contentIdx, {
        contentIdx: b.contentIdx,
        contentTitle: b.contentTitle ?? '',
        contentType: b.contentType ?? '',
        posterImageUrl: b.posterImageUrl ?? '',
        places: [],
      });
    }
    map.get(b.contentIdx).places.push(mapBookmarkToSpot(b));
  });
  return Array.from(map.values());
});

async function loadSavedSpots() {
  const data = await bookmarkApi.getMyPlaces();
  rawBookmarks.value = data ?? [];
  savedSpots.value = rawBookmarks.value.map(mapBookmarkToSpot);
}

onMounted(() => { loadSavedSpots(); });
onIonViewWillEnter(() => { loadSavedSpots(); });

const myPhotos = ref([
  { id: 1, spotName: '스가 신사', takenAt: '2024.11.03', emoji: '⛩️', gradient: 'linear-gradient(145deg,#cffafe,#a5f3fc)' },
  { id: 2, spotName: '스가 신사', takenAt: '2024.11.03', emoji: '🌅', gradient: 'linear-gradient(145deg,#fef3c7,#fde68a)' },
  { id: 3, spotName: '시부야 교차로', takenAt: '2024.10.21', emoji: '🌃', gradient: 'linear-gradient(145deg,#ede9fe,#ddd6fe)' },
  { id: 4, spotName: '시부야 교차로', takenAt: '2024.10.21', emoji: '🚶', gradient: 'linear-gradient(145deg,#f1f5f9,#e2e8f0)' },
  { id: 5, spotName: '이태원 단밤', takenAt: '2024.09.14', emoji: '🏮', gradient: 'linear-gradient(145deg,#f0fdf4,#bbf7d0)' },
]);

const totalPhotos = computed(() => myPhotos.value.length);
const savedCount = computed(() => savedSpots.value.length);
</script>

<style scoped>
.my-content {
  --background: rgb(229 231 235);
}

.page-layout {
  display: flex;
  min-height: 100%;
}

/* ── 메인 ── */
.main-wrap {
  flex: 1;
  min-width: 0;
  display: flex;
  flex-direction: column;
  padding-bottom: 80px;
}

/* 상단 바 */
.top-bar {
  position: sticky;
  top: 0;
  z-index: 100;
  display: flex;
  align-items: center;
  padding: 12px 16px;
  background: #fff;
  border-bottom: 1px solid rgba(20, 188, 237, 0.1);
  gap: 12px;
}

.back-btn,
.top-settings-btn {
  width: 36px;
  height: 36px;
  border: none;
  background: none;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
  color: #374151;
  cursor: pointer;
  border-radius: 50%;
  flex-shrink: 0;
}

.back-btn:hover,
.top-settings-btn:hover {
  background: #f3f4f6;
}

.top-title {
  flex: 1;
  font-size: 16px;
  font-weight: 800;
  color: #111827;
  text-align: center;
}

/* 프로필 헤더 */
.profile-header {
  background: #fff;
  padding: 28px 20px 24px;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12px;
  border-bottom: 8px solid rgb(229 231 235);
}

.avatar-wrap {
  position: relative;
}

.avatar {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  background: #14BCED;
  color: #fff;
  font-size: 32px;
  font-weight: 800;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 4px 16px rgba(20, 188, 237, 0.3);
}

.avatar-edit-btn {
  position: absolute;
  bottom: 0;
  right: 0;
  width: 26px;
  height: 26px;
  border-radius: 50%;
  background: #fff;
  border: 2px solid #e5e7eb;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 13px;
  color: #6b7280;
  cursor: pointer;
}

.profile-info {
  text-align: center;
}

.profile-name {
  font-size: 20px;
  font-weight: 800;
  color: #111827;
  margin: 0 0 4px;
}

.profile-email {
  font-size: 13px;
  color: #9ca3af;
  margin: 0;
}

.profile-stats {
  display: flex;
  align-items: center;
  gap: 24px;
  margin-top: 4px;
}

.stat-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 2px;
}

.stat-value {
  font-size: 20px;
  font-weight: 800;
  color: #111827;
}

.stat-label {
  font-size: 11px;
  color: #9ca3af;
}

.stat-divider {
  width: 1px;
  height: 32px;
  background: #e5e7eb;
}

/* 탭 바 */
.tab-bar {
  display: flex;
  background: #fff;
  border-bottom: 1px solid #e5e7eb;
  position: sticky;
  top: 60px;
  z-index: 90;
}

.tab-btn {
  flex: 1;
  padding: 12px 4px;
  border: none;
  background: none;
  font-size: 12px;
  font-weight: 600;
  color: #9ca3af;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 4px;
  border-bottom: 2px solid transparent;
  transition: color 0.15s, border-color 0.15s;
}

.tab-btn.tab-active {
  color: #14BCED;
  border-bottom-color: #14BCED;
}

/* 탭 콘텐츠 */
.tab-content {
  padding: 16px;
  flex: 1;
}

/* 방문/저장 그리드 */
.spots-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 12px;
}

/* 사진 그리드 */
.photos-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 3px;
}

.photo-item {
  position: relative;
  aspect-ratio: 1;
  overflow: hidden;
  border-radius: 4px;
  cursor: pointer;
}

.photo-thumb {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 28px;
}

.photo-overlay {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  padding: 6px 8px;
  background: linear-gradient(transparent, rgba(0,0,0,0.5));
  opacity: 0;
  transition: opacity 0.15s;
}

.photo-item:hover .photo-overlay {
  opacity: 1;
}

.photo-spot,
.photo-date {
  font-size: 10px;
  color: #fff;
  margin: 0;
}

/* 빈 상태 */
.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 48px 24px;
  text-align: center;
}

.empty-icon {
  font-size: 48px;
  margin-bottom: 16px;
}

.empty-title {
  font-size: 16px;
  font-weight: 700;
  color: #374151;
  margin: 0 0 8px;
}

.empty-desc {
  font-size: 13px;
  color: #9ca3af;
  margin: 0 0 20px;
  line-height: 1.6;
}

.empty-action-btn {
  padding: 10px 24px;
  border-radius: 20px;
  background: #14BCED;
  color: #fff;
  font-size: 14px;
  font-weight: 700;
  border: none;
  cursor: pointer;
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
  color: #9ca3af;
  font-size: 10px;
  cursor: pointer;
  padding: 8px 0;
}

.nav-btn ion-icon {
  font-size: 22px;
}

.nav-btn.active {
  color: #14BCED;
}

/* ── 태블릿 (≥768px) ── */
@media (min-width: 768px) {
  .my-content {
    --background: #f0f9fd;
  }

  .main-wrap {
    padding-bottom: 0;
  }

  .profile-header {
    flex-direction: row;
    align-items: center;
    gap: 24px;
    padding: 32px 40px;
  }

  .profile-info {
    text-align: left;
    flex: 1;
  }

  .profile-stats {
    margin-top: 0;
  }

  .tab-content {
    padding: 24px 40px;
  }

  .spots-grid {
    grid-template-columns: repeat(3, 1fr);
    gap: 16px;
  }

  .photos-grid {
    grid-template-columns: repeat(4, 1fr);
    gap: 4px;
  }

  .bottom-nav {
    display: none;
  }

  .logout-wrap {
    padding: 8px 40px 32px;
  }
}

/* ── 데스크탑 (≥1024px) ── */
@media (min-width: 1024px) {
  .spots-grid {
    grid-template-columns: repeat(4, 1fr);
  }

  .photos-grid {
    grid-template-columns: repeat(5, 1fr);
  }
}

/* ── 보기 모드 토글 ── */
.view-toggle {
  display: flex;
  gap: 6px;
  margin-bottom: 16px;
  background: #f3f4f6;
  border-radius: 10px;
  padding: 4px;
  width: fit-content;
}

.vt-btn {
  display: flex;
  align-items: center;
  gap: 5px;
  padding: 6px 14px;
  border-radius: 8px;
  border: none;
  background: none;
  font-size: 13px;
  font-weight: 600;
  color: #9ca3af;
  cursor: pointer;
  transition: all 0.15s;
}

.vt-btn.active {
  background: #fff;
  color: var(--brand, #14BCED);
  box-shadow: 0 1px 4px rgba(0,0,0,0.08);
}

.vt-btn ion-icon {
  font-size: 15px;
}

/* ── 작품별 그룹 ── */
.content-groups {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.content-group {
  background: #fff;
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0,0,0,0.06);
}

.group-header {
  display: flex;
  align-items: center;
  gap: 14px;
  padding: 14px 16px;
  cursor: pointer;
  transition: background 0.15s;
  border-bottom: 1px solid #f3f4f6;
}

.group-header:hover {
  background: rgba(20,188,237,0.04);
}

.group-poster {
  width: 52px;
  flex-shrink: 0;
  aspect-ratio: 2 / 3;
  border-radius: 8px;
  overflow: hidden;
  background: #f3f4f6;
  display: flex;
  align-items: center;
  justify-content: center;
}

.group-poster img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.group-poster-ph {
  font-size: 20px;
}

.group-info {
  flex: 1;
  min-width: 0;
  display: flex;
  flex-direction: column;
  gap: 3px;
}

.group-type-badge {
  font-size: 10px;
  font-weight: 700;
  color: var(--brand, #14BCED);
  background: rgba(20,188,237,0.1);
  padding: 2px 8px;
  border-radius: 20px;
  width: fit-content;
}

.group-title {
  font-size: 14px;
  font-weight: 800;
  color: #111827;
  margin: 0;
  overflow: hidden;
  white-space: nowrap;
  text-overflow: ellipsis;
}

.group-count {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 11px;
  color: #9ca3af;
  margin: 0;
}

.group-count ion-icon {
  font-size: 12px;
}

.group-arrow {
  font-size: 16px;
  color: #d1d5db;
  flex-shrink: 0;
}

/* 성지 가로 스크롤 */
.group-places {
  display: flex;
  gap: 10px;
  padding: 12px 16px;
  overflow-x: auto;
  scrollbar-width: none;
}

.group-places::-webkit-scrollbar {
  display: none;
}

.gp-card {
  flex-shrink: 0;
  width: 88px;
  cursor: pointer;
  border-radius: 10px;
  overflow: hidden;
  background: #f9fafb;
  border: 1px solid #f3f4f6;
  transition: transform 0.15s, box-shadow 0.15s;
}

.gp-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(20,188,237,0.15);
}

.gp-thumb {
  aspect-ratio: 1;
  display: flex;
  align-items: center;
  justify-content: center;
}

.gp-emoji {
  font-size: 22px;
}

.gp-name {
  font-size: 11px;
  font-weight: 600;
  color: #374151;
  margin: 0;
  padding: 6px 6px 8px;
  text-align: center;
  overflow: hidden;
  white-space: nowrap;
  text-overflow: ellipsis;
}
</style>
