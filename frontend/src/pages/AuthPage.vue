<template>
  <ion-page>
    <ion-content :fullscreen="true" class="auth-content">
      <div class="auth-layout">

        <!-- ── 데스크탑 좌측 브랜드 패널 (≥768px) ── -->
        <div class="brand-panel">
          <!-- 배경 구름들 -->
          <svg class="bp-cloud bp-c1" viewBox="0 0 160 80" xmlns="http://www.w3.org/2000/svg">
            <path d="M18,62 Q12,62 12,54 Q12,42 24,42 Q24,28 38,28 Q44,16 58,21 Q68,10 82,19 Q96,12 106,26 Q118,24 122,36 Q132,36 132,46 Q132,56 122,56 Z" fill="white" opacity="0.15"/>
          </svg>
          <svg class="bp-cloud bp-c2" viewBox="0 0 120 60" xmlns="http://www.w3.org/2000/svg">
            <path d="M14,46 Q9,46 9,39 Q9,30 18,30 Q18,19 29,19 Q34,11 44,15 Q52,8 63,15 Q72,10 80,20 Q89,18 92,27 Q99,27 99,34 Q99,42 92,42 Z" fill="white" opacity="0.10"/>
          </svg>
          <svg class="bp-cloud bp-c3" viewBox="0 0 90 45" xmlns="http://www.w3.org/2000/svg">
            <path d="M10,34 Q7,34 7,29 Q7,22 14,22 Q14,14 22,14 Q26,8 34,11 Q40,6 49,11 Q56,7 62,15 Q68,14 70,21 Q75,21 75,26 Q75,31 70,31 Z" fill="white" opacity="0.12"/>
          </svg>

          <div class="bp-content">
            <div class="bp-logo">
              <svg width="56" height="56" viewBox="0 0 30 30" xmlns="http://www.w3.org/2000/svg">
                <circle cx="15" cy="15" r="15" fill="rgba(255,255,255,0.25)" />
                <path d="M15 7 L12 15 L18 15 L11 23 L17 16 L13 16 Z" fill="#fff" />
              </svg>
            </div>
            <h1 class="bp-title">성지맵</h1>
            <p class="bp-subtitle">영화·드라마·애니 속 그 장소로</p>

            <div class="bp-features">
              <div class="bp-feature">
                <span class="bp-feature-icon">🗺️</span>
                <div>
                  <p class="bp-feature-title">실제 장소를 지도에서</p>
                  <p class="bp-feature-desc">작품 속 배경지를 직접 확인하세요</p>
                </div>
              </div>
              <div class="bp-feature">
                <span class="bp-feature-icon">🎬</span>
                <div>
                  <p class="bp-feature-title">다양한 작품 지원</p>
                  <p class="bp-feature-desc">애니·드라마·영화 모두 한 곳에</p>
                </div>
              </div>
              <div class="bp-feature">
                <span class="bp-feature-icon">📍</span>
                <div>
                  <p class="bp-feature-title">나만의 성지 저장</p>
                  <p class="bp-feature-desc">방문한 곳을 기록하고 공유하세요</p>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- ── 우측 폼 패널 ── -->
        <div class="form-panel">

          <!-- 모바일에서만 표시되는 로고 헤더 -->
          <div class="mobile-brand-header">
            <div class="logo-circle">
              <svg width="28" height="28" viewBox="0 0 28 28" xmlns="http://www.w3.org/2000/svg">
                <path d="M14 3 L10 14 L18 14 L10 25 L18 16 L12 16 Z" fill="#fff"/>
              </svg>
            </div>
            <h1 class="brand-name">성지맵</h1>
            <p class="brand-tagline">영화·드라마·애니 속 그 장소로</p>
          </div>

          <!-- 인증 카드 -->
          <div class="auth-card">

            <!-- 탭 스위처 -->
            <div class="tab-switcher">
              <button
                class="tab-btn"
                :class="activeTab === 'login' ? 'tab-active' : 'tab-inactive'"
                @click="activeTab = 'login'"
              >
                로그인
              </button>
              <button
                class="tab-btn"
                :class="activeTab === 'signup' ? 'tab-active' : 'tab-inactive'"
                @click="activeTab = 'signup'"
              >
                회원가입
              </button>
            </div>

            <div class="form-area">
              <LoginForm
                v-show="activeTab === 'login'"
                @forgot-password="onForgotPassword"
                @go-signup="activeTab = 'signup'"
              />
              <SignupForm
                v-show="activeTab === 'signup'"
                @signup-success="onSignupSuccess"
              />
            </div>

          </div>
        </div>
      </div>
    </ion-content>
  </ion-page>
</template>

<script setup lang="ts">
import { ref } from 'vue';
import { IonPage, IonContent } from '@ionic/vue';
import LoginForm from '@/components/auth/LoginForm.vue';
import SignupForm from '@/components/auth/SignupForm.vue';

const activeTab = ref<'login' | 'signup'>('login');

function onForgotPassword() {
  // TODO: 비밀번호 찾기 페이지로 이동
}

function onSignupSuccess() {
  activeTab.value = 'login';
}
</script>

<style scoped>
/* ═══════════════════════════════════════
   베이스 (모바일)
═══════════════════════════════════════ */
.auth-content {
  --background: rgb(255 255 255);
}

/* 전체 레이아웃: 모바일은 단일 컬럼 */
.auth-layout {
  display: flex;
  min-height: 100%;
}

/* 브랜드 패널: 모바일 숨김 */
.brand-panel {
  display: none;
}

/* 폼 패널: 모바일 전체 너비 */
.form-panel {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 40px 20px 32px;
  box-sizing: border-box;
  min-height: 100%;
}

/* 모바일 로고 헤더 */
.mobile-brand-header {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 6px;
  margin-bottom: 28px;
}

.logo-circle {
  width: 52px;
  height: 52px;
  border-radius: 50%;
  background: #14BCED;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 4px 16px rgba(20, 188, 237, 0.35);
  margin-bottom: 4px;
}

.brand-name {
  font-size: 22px;
  font-weight: 800;
  color: #111827;
  margin: 0;
  letter-spacing: -0.4px;
}

.brand-tagline {
  font-size: 13px;
  color: #6b7280;
  margin: 0;
}

/* 인증 카드 */
.auth-card {
  width: 100%;
  max-width: 420px;
  background: #ffffff;
  border-radius: 20px;
  box-shadow: 0 10px 40px rgba(0,0,0,0.08), 0 2px 8px rgba(0,0,0,0.04);
  overflow: hidden;
}

/* 탭 스위처 */
.tab-switcher {
  display: flex;
  background: #f3f4f6;
  border-radius: 12px;
  padding: 4px;
  margin: 20px 20px 0;
}

.tab-btn {
  flex: 1;
  padding: 10px 0;
  border-radius: 12px;
  font-size: 14px;
  font-weight: 700;
  transition: all 0.2s ease;
  border: none;
  cursor: pointer;
  background: transparent;
}

.tab-active {
  background: #ffffff;
  color: #14BCED;
  box-shadow: 0 1px 2px rgba(0,0,0,0.05);
}

.tab-inactive {
  background: transparent;
  color: #9ca3af;
}

.tab-inactive:hover {
  color: #4b5563;
}

.form-area {
  padding: 20px 20px 28px;
}

/* ═══════════════════════════════════════
   태블릿 (≥ 768px): 스플릿 스크린
═══════════════════════════════════════ */
@media (min-width: 768px) {
  .auth-content {
    --background: #f0f9fd;
  }

  .auth-layout {
    flex-direction: row;
    min-height: 100vh;
  }

  /* 브랜드 패널 표시 */
  .brand-panel {
    display: flex;
    flex-direction: column;
    justify-content: center;
    width: 45%;
    flex-shrink: 0;
    background: linear-gradient(145deg, #14BCED, #0a9ab8);
    padding: 48px 40px;
    box-sizing: border-box;
    position: relative;
    overflow: hidden;
  }

  /* 배경 구름 */
  .bp-cloud {
    position: absolute;
    pointer-events: none;
  }
  .bp-c1 { width: 200px; top: -20px; right: -30px; }
  .bp-c2 { width: 160px; bottom: 60px; left: -20px; }
  .bp-c3 { width: 120px; top: 40%;  right: 10px; }

  .bp-content {
    position: relative;
    z-index: 1;
  }

  .bp-logo {
    margin-bottom: 16px;
  }

  .bp-title {
    font-size: 36px;
    font-weight: 900;
    color: #fff;
    margin: 0 0 8px;
    letter-spacing: -0.8px;
  }

  .bp-subtitle {
    font-size: 15px;
    color: rgba(255,255,255,0.85);
    margin: 0 0 40px;
  }

  .bp-features {
    display: flex;
    flex-direction: column;
    gap: 20px;
  }

  .bp-feature {
    display: flex;
    align-items: flex-start;
    gap: 14px;
  }

  .bp-feature-icon {
    font-size: 26px;
    flex-shrink: 0;
    margin-top: 1px;
  }

  .bp-feature-title {
    font-size: 14px;
    font-weight: 700;
    color: #fff;
    margin: 0 0 2px;
  }

  .bp-feature-desc {
    font-size: 12px;
    color: rgba(255,255,255,0.75);
    margin: 0;
  }

  /* 폼 패널 */
  .form-panel {
    flex: 1;
    padding: 48px 40px;
    background: #ffffff;
    justify-content: center;
  }

  /* 모바일 로고 숨김 */
  .mobile-brand-header {
    display: none;
  }

  .auth-card {
    max-width: 380px;
    box-shadow: 0 4px 24px rgba(0,0,0,0.06);
  }
}

/* ═══════════════════════════════════════
   데스크탑 (≥ 1024px)
═══════════════════════════════════════ */
@media (min-width: 1024px) {
  .brand-panel {
    width: 50%;
    padding: 64px 56px;
  }

  .bp-title    { font-size: 44px; }
  .bp-subtitle { font-size: 16px; margin-bottom: 48px; }

  .form-panel {
    padding: 64px 56px;
  }

  .auth-card {
    max-width: 420px;
  }

  .bp-c1 { width: 260px; }
  .bp-c2 { width: 200px; }
  .bp-c3 { width: 150px; }
}
</style>
