<template>
  <ion-page>
    <ion-content :fullscreen="true" class="camera-content">

      <!-- ── 캡처 전: 카메라 뷰 ── -->
      <div v-if="!capturedPhoto" class="camera-view">

        <!-- 상단 바 -->
        <div class="cam-top-bar">
          <button class="cam-icon-btn" @click="goBack">
            <ion-icon name="arrow-back-outline"/>
          </button>
          <span class="cam-title">{{ scene?.title ?? '성지 촬영' }}</span>
          <button class="cam-icon-btn" @click="toggleGuide">
            <ion-icon :name="showGuide ? 'eye-off-outline' : 'eye-outline'"/>
          </button>
        </div>

        <!-- 카메라 뷰파인더 (웹) -->
        <div class="viewfinder-wrap">
          <video
            v-if="!isNative"
            ref="videoEl"
            class="viewfinder"
            autoplay
            playsinline
            muted
          />
          <!-- 네이티브: 플레이스홀더 (Capacitor가 처리) -->
          <div v-else class="viewfinder native-placeholder">
            <ion-icon name="camera-outline" class="native-cam-icon"/>
            <p>버튼을 눌러 촬영하세요</p>
          </div>

          <!-- 참고 장면 오버레이 -->
          <transition name="fade">
            <div v-if="showGuide && scene" class="guide-overlay">
              <div class="guide-badge">참고 장면</div>
              <div class="guide-scene" :style="{ background: scene.gradient }">
                <span class="guide-emoji">{{ scene.emoji }}</span>
              </div>
              <p class="guide-hint">이 장면과 맞춰서 촬영해보세요</p>
            </div>
          </transition>

          <!-- 촬영 격자 (구도 보조) -->
          <div class="grid-overlay" aria-hidden="true">
            <div class="grid-line h1"/><div class="grid-line h2"/>
            <div class="grid-line v1"/><div class="grid-line v2"/>
          </div>
        </div>

        <!-- 카메라 에러 -->
        <div v-if="cameraError" class="camera-error">
          <ion-icon name="warning-outline"/>
          <p>{{ cameraError }}</p>
          <button @click="initCamera" class="retry-btn">다시 시도</button>
        </div>

        <!-- 하단 컨트롤 -->
        <div class="cam-bottom-bar">
          <div class="side-btn" />

          <!-- 촬영 버튼 -->
          <button
            class="shutter-btn"
            :disabled="!cameraReady"
            @click="capture"
          >
            <div class="shutter-inner"/>
          </button>

          <!-- 카메라 전환 (웹에서는 숨김) -->
          <button v-if="isNative" class="side-btn" @click="flipCamera">
            <ion-icon name="camera-reverse-outline"/>
            <span>전환</span>
          </button>
          <div v-else class="side-btn" />
        </div>

      </div>

      <!-- ── 캡처 후: 결과 화면 ── -->
      <div v-else class="result-view">

        <!-- 상단 바 -->
        <div class="cam-top-bar">
          <button class="cam-icon-btn" @click="retake">
            <ion-icon name="arrow-back-outline"/>
          </button>
          <span class="cam-title">촬영 결과</span>
          <div style="width:40px"/>
        </div>

        <!-- 비교 영역 -->
        <div class="compare-wrap">
          <!-- 참고 장면 -->
          <div class="compare-panel">
            <div class="compare-label">참고 장면</div>
            <div class="compare-img reference" :style="{ background: scene?.gradient }">
              <span class="compare-emoji">{{ scene?.emoji }}</span>
            </div>
          </div>

          <!-- VS -->
          <div class="vs-badge">VS</div>

          <!-- 내 사진 -->
          <div class="compare-panel">
            <div class="compare-label">내 사진</div>
            <img :src="capturedPhoto.dataUrl" class="compare-img captured" alt="촬영 사진"/>
          </div>
        </div>

        <!-- 분석 결과 -->
        <div class="analysis-wrap">
          <div v-if="analyzing" class="analyzing-state">
            <div class="analyzing-spinner"/>
            <p>장면 유사도 분석 중...</p>
          </div>
          <div v-else class="score-wrap">
            <div class="score-ring" :style="scoreRingStyle">
              <span class="score-value">{{ similarityScore }}</span>
              <span class="score-unit">점</span>
            </div>
            <div class="score-info">
              <p class="score-grade">{{ scoreGrade }}</p>
              <p class="score-desc">{{ scoreDesc }}</p>
              <p class="score-point">+{{ earnedPoints }} 포인트 적립!</p>
            </div>
          </div>
        </div>

        <!-- 액션 버튼 -->
        <div class="result-actions">
          <button class="retake-btn" @click="retake">
            <ion-icon name="camera-outline"/> 다시 찍기
          </button>
          <button class="submit-btn" :disabled="analyzing" @click="onSubmit">
            <ion-icon name="checkmark-outline"/> 기록 저장
          </button>
        </div>

      </div>

    </ion-content>
  </ion-page>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted } from 'vue';
import { IonPage, IonContent, IonIcon, useIonRouter } from '@ionic/vue';
import { addIcons } from 'ionicons';
import {
  arrowBackOutline, cameraOutline, eyeOutline, eyeOffOutline,
  cameraReverseOutline, warningOutline, checkmarkOutline,
} from 'ionicons/icons';
import { useCamera } from '@/composables/useCamera';

addIcons({
  'arrow-back-outline': arrowBackOutline,
  'camera-outline': cameraOutline,
  'eye-outline': eyeOutline,
  'eye-off-outline': eyeOffOutline,
  'camera-reverse-outline': cameraReverseOutline,
  'warning-outline': warningOutline,
  'checkmark-outline': checkmarkOutline,
});

const router = useIonRouter();
const { isNative, captureNative, captureFromVideo, startStream, stopStream } = useCamera();

// ── 상태 ──
const videoEl = ref<HTMLVideoElement | null>(null);
const stream = ref<MediaStream | null>(null);
const cameraReady = ref(false);
const cameraError = ref('');
const showGuide = ref(true);
const capturedPhoto = ref<{ dataUrl: string; format: string } | null>(null);
const analyzing = ref(false);
const similarityScore = ref(0);

// ── 임시 참고 장면 (나중에 라우트 파라미터 or API로 받아옴) ──
const scene = ref({
  id: 1,
  title: '너의 이름은. - 스가 신사',
  emoji: '⛩️',
  gradient: 'linear-gradient(145deg,#cffafe,#a5f3fc)',
});

// ── 카메라 초기화 (웹) ──
async function initCamera() {
  if (isNative) { cameraReady.value = true; return; }
  cameraError.value = '';
  try {
    if (!videoEl.value) return;
    stream.value = await startStream(videoEl.value);
    cameraReady.value = true;
  } catch {
    cameraError.value = '카메라에 접근할 수 없습니다. 브라우저 카메라 권한을 허용해주세요.';
  }
}

// ── 촬영 ──
async function capture() {
  if (isNative) {
    try {
      capturedPhoto.value = await captureNative();
      stopStream(stream.value);
      runAnalysis();
    } catch {
      cameraError.value = '촬영에 실패했습니다.';
    }
  } else {
    if (!videoEl.value) return;
    capturedPhoto.value = captureFromVideo(videoEl.value);
    stopStream(stream.value);
    runAnalysis();
  }
}

// ── 카메라 전환 (네이티브) ──
async function flipCamera() {
  // TODO: facingMode 전환
}

// ── 분석 (임시 목업: 나중에 API 연동) ──
function runAnalysis() {
  analyzing.value = true;
  similarityScore.value = 0;
  setTimeout(() => {
    similarityScore.value = Math.floor(Math.random() * 30) + 65; // 65~94
    analyzing.value = false;
  }, 2000);
}

// ── 점수 파생값 ──
const scoreGrade = computed(() => {
  const s = similarityScore.value;
  if (s >= 90) return '완벽한 성지순례!';
  if (s >= 75) return '훌륭해요!';
  if (s >= 60) return '잘 찍으셨어요';
  return '다시 도전해보세요';
});

const scoreDesc = computed(() => {
  const s = similarityScore.value;
  if (s >= 90) return '참고 장면과 거의 일치합니다';
  if (s >= 75) return '구도와 앵글이 비슷해요';
  if (s >= 60) return '장소는 맞지만 각도가 조금 달라요';
  return '장소나 각도를 맞춰보세요';
});

const earnedPoints = computed(() => Math.floor(similarityScore.value * 1.5));

const scoreRingStyle = computed(() => {
  const s = similarityScore.value;
  const color = s >= 90 ? '#14BCED' : s >= 75 ? '#10b981' : s >= 60 ? '#f59e0b' : '#ef4444';
  return { '--score-color': color, '--score-pct': `${s}%` };
});

// ── 다시 찍기 ──
async function retake() {
  capturedPhoto.value = null;
  similarityScore.value = 0;
  await initCamera();
}

// ── 기록 저장 ──
function onSubmit() {
  // TODO: API로 사진 + 점수 저장
  router.replace('/mypage');
}

function goBack() {
  stopStream(stream.value);
  router.back();
}

function toggleGuide() {
  showGuide.value = !showGuide.value;
}

onMounted(() => initCamera());
onUnmounted(() => stopStream(stream.value));
</script>

<style scoped>
.camera-content {
  --background: #000;
}

/* ── 카메라 뷰 ── */
.camera-view {
  display: flex;
  flex-direction: column;
  height: 100%;
  background: #000;
}

/* 상단 바 */
.cam-top-bar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 12px 16px;
  background: rgba(0, 0, 0, 0.6);
  z-index: 10;
}

.cam-icon-btn {
  width: 40px;
  height: 40px;
  border: none;
  background: rgba(255, 255, 255, 0.15);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
  color: #fff;
  cursor: pointer;
}

.cam-title {
  font-size: 15px;
  font-weight: 700;
  color: #fff;
  text-align: center;
  flex: 1;
  padding: 0 8px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

/* 뷰파인더 */
.viewfinder-wrap {
  flex: 1;
  position: relative;
  overflow: hidden;
  background: #111;
}

.viewfinder {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.native-placeholder {
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: #6b7280;
  gap: 12px;
}

.native-cam-icon {
  font-size: 56px;
}

/* 참고 장면 오버레이 */
.guide-overlay {
  position: absolute;
  top: 16px;
  right: 16px;
  width: 120px;
  background: rgba(0, 0, 0, 0.75);
  border-radius: 12px;
  padding: 8px;
  text-align: center;
  backdrop-filter: blur(4px);
}

.guide-badge {
  font-size: 10px;
  font-weight: 700;
  color: #14BCED;
  margin-bottom: 6px;
}

.guide-scene {
  width: 100%;
  aspect-ratio: 16/9;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.guide-emoji {
  font-size: 28px;
}

.guide-hint {
  font-size: 9px;
  color: rgba(255, 255, 255, 0.7);
  margin: 6px 0 0;
  line-height: 1.4;
}

/* 구도 격자 */
.grid-overlay {
  position: absolute;
  inset: 0;
  pointer-events: none;
}

.grid-line {
  position: absolute;
  background: rgba(255, 255, 255, 0.15);
}

.grid-line.h1 { top: 33.3%; left: 0; right: 0; height: 1px; }
.grid-line.h2 { top: 66.6%; left: 0; right: 0; height: 1px; }
.grid-line.v1 { left: 33.3%; top: 0; bottom: 0; width: 1px; }
.grid-line.v2 { left: 66.6%; top: 0; bottom: 0; width: 1px; }

/* 에러 */
.camera-error {
  position: absolute;
  inset: 0;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: #fff;
  gap: 12px;
  background: rgba(0,0,0,0.8);
  font-size: 14px;
  text-align: center;
  padding: 24px;
}

.camera-error ion-icon { font-size: 40px; color: #f59e0b; }

.retry-btn {
  padding: 10px 24px;
  border-radius: 20px;
  background: #14BCED;
  color: #fff;
  font-size: 14px;
  font-weight: 700;
  border: none;
  cursor: pointer;
}

/* 하단 컨트롤 */
.cam-bottom-bar {
  display: flex;
  align-items: center;
  justify-content: space-around;
  padding: 24px 32px 40px;
  background: rgba(0, 0, 0, 0.7);
}

.side-btn {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 4px;
  background: none;
  border: none;
  color: #fff;
  font-size: 10px;
  cursor: pointer;
  width: 56px;
}

.side-btn ion-icon { font-size: 26px; }

/* 셔터 버튼 */
.shutter-btn {
  width: 72px;
  height: 72px;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.3);
  border: 3px solid #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: transform 0.1s;
  flex-shrink: 0;
}

.shutter-btn:active { transform: scale(0.93); }
.shutter-btn:disabled { opacity: 0.4; cursor: not-allowed; }

.shutter-inner {
  width: 54px;
  height: 54px;
  border-radius: 50%;
  background: #fff;
}

/* ── 결과 뷰 ── */
.result-view {
  display: flex;
  flex-direction: column;
  height: 100%;
  background: #fff;
}

/* 비교 영역 */
.compare-wrap {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  padding: 20px 16px 0;
  flex-shrink: 0;
}

.compare-panel {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.compare-label {
  font-size: 11px;
  font-weight: 700;
  color: #6b7280;
  text-align: center;
}

.compare-img {
  width: 100%;
  aspect-ratio: 4/3;
  border-radius: 12px;
  object-fit: cover;
  display: flex;
  align-items: center;
  justify-content: center;
}

.compare-emoji { font-size: 40px; }

.vs-badge {
  font-size: 14px;
  font-weight: 900;
  color: #9ca3af;
  flex-shrink: 0;
}

/* 분석 결과 */
.analysis-wrap {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 20px 24px;
}

/* 분석 중 */
.analyzing-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12px;
  color: #6b7280;
  font-size: 14px;
}

.analyzing-spinner {
  width: 40px;
  height: 40px;
  border: 3px solid #e5e7eb;
  border-top-color: #14BCED;
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
}

@keyframes spin { to { transform: rotate(360deg); } }

/* 점수 */
.score-wrap {
  display: flex;
  align-items: center;
  gap: 24px;
}

.score-ring {
  width: 90px;
  height: 90px;
  border-radius: 50%;
  border: 6px solid var(--score-color, #14BCED);
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  box-shadow: 0 0 0 3px rgba(20, 188, 237, 0.12);
}

.score-value {
  font-size: 28px;
  font-weight: 900;
  color: var(--score-color, #14BCED);
  line-height: 1;
}

.score-unit {
  font-size: 12px;
  color: #9ca3af;
}

.score-grade {
  font-size: 16px;
  font-weight: 800;
  color: #111827;
  margin: 0 0 4px;
}

.score-desc {
  font-size: 12px;
  color: #6b7280;
  margin: 0 0 8px;
  line-height: 1.4;
}

.score-point {
  font-size: 13px;
  font-weight: 700;
  color: #14BCED;
  margin: 0;
}

/* 액션 버튼 */
.result-actions {
  display: flex;
  gap: 12px;
  padding: 0 16px 40px;
  flex-shrink: 0;
}

.retake-btn {
  flex: 1;
  padding: 14px;
  border: 1.5px solid #e5e7eb;
  border-radius: 12px;
  background: #fff;
  color: #374151;
  font-size: 14px;
  font-weight: 700;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
  cursor: pointer;
}

.submit-btn {
  flex: 2;
  padding: 14px;
  border: none;
  border-radius: 12px;
  background: #14BCED;
  color: #fff;
  font-size: 14px;
  font-weight: 700;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
  cursor: pointer;
  transition: background 0.15s;
}

.submit-btn:disabled { background: #9ca3af; cursor: not-allowed; }
.submit-btn:hover:not(:disabled) { background: #0fa8d4; }

/* fade 트랜지션 */
.fade-enter-active, .fade-leave-active { transition: opacity 0.2s; }
.fade-enter-from, .fade-leave-to { opacity: 0; }

/* ── 태블릿/데스크탑 ── */
@media (min-width: 768px) {
  .compare-wrap {
    max-width: 600px;
    margin: 0 auto;
  }

  .analysis-wrap {
    max-width: 600px;
    margin: 0 auto;
    width: 100%;
  }

  .result-actions {
    max-width: 600px;
    margin: 0 auto;
    width: 100%;
  }

  .guide-overlay {
    width: 160px;
  }
}
</style>
