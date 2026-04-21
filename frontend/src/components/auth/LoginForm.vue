<template>
  <form @submit.prevent="onSubmit" class="form-body" novalidate>

    <!-- 에러 메시지 배너 -->
    <div v-if="errorMsg" class="error-banner" role="alert">
      <span class="error-icon">⚠</span>
      {{ errorMsg }}
    </div>

    <div class="field">
      <label class="field-label">이메일</label>
      <input
        v-model="email"
        type="email"
        placeholder="이메일을 입력하세요"
        class="field-input"
        :class="{ 'input-error': emailError }"
        @blur="validateEmail"
        autocomplete="email"
      />
      <span v-if="emailError" class="field-error">{{ emailError }}</span>
    </div>

    <div class="field">
      <label class="field-label">비밀번호</label>
      <div class="password-wrap">
        <input
          v-model="password"
          :type="showPw ? 'text' : 'password'"
          placeholder="비밀번호를 입력하세요"
          class="field-input"
          :class="{ 'input-error': passwordError }"
          @blur="validatePassword"
          autocomplete="current-password"
        />
        <button type="button" class="toggle-pw" @click="showPw = !showPw" tabindex="-1">
          {{ showPw ? '숨기기' : '보기' }}
        </button>
      </div>
      <span v-if="passwordError" class="field-error">{{ passwordError }}</span>
    </div>

    <div class="forgot-wrap">
      <button type="button" class="forgot-btn" @click="$emit('forgot-password')">
        비밀번호를 잊으셨나요?
      </button>
    </div>

    <button type="submit" class="submit-btn" :disabled="loading">
      <span v-if="loading" class="spinner" />
      {{ loading ? '로그인 중...' : '로그인' }}
    </button>

    <div class="divider">
      <span class="divider-line" />
      <span class="divider-text">또는</span>
      <span class="divider-line" />
    </div>

    <button type="button" class="social-btn" @click="onGoogleLogin">
      <img
        src="https://www.gstatic.com/firebasejs/ui/2.0.0/images/auth/google.svg"
        alt="Google"
        width="18"
      />
      Google로 계속하기
    </button>

  </form>
</template>

<script setup lang="ts">
import { ref } from 'vue';
import { useIonRouter } from '@ionic/vue';
import { useAuth } from '@/composables/useAuth';

const emit = defineEmits(['forgot-password', 'go-signup']);

const { login } = useAuth();
const router = useIonRouter();

const email = ref('');
const password = ref('');
const showPw = ref(false);
const loading = ref(false);
const errorMsg = ref('');
const emailError = ref('');
const passwordError = ref('');

const EMAIL_REGEX = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/;

function validateEmail(): boolean {
  if (!email.value) {
    emailError.value = '이메일을 입력해주세요.';
    return false;
  }
  if (!EMAIL_REGEX.test(email.value)) {
    emailError.value = '올바른 이메일 형식이 아닙니다.';
    return false;
  }
  emailError.value = '';
  return true;
}

function validatePassword(): boolean {
  if (!password.value) {
    passwordError.value = '비밀번호를 입력해주세요.';
    return false;
  }
  passwordError.value = '';
  return true;
}

async function onSubmit() {
  errorMsg.value = '';
  const ok = validateEmail() && validatePassword();
  if (!ok) return;

  loading.value = true;
  try {
    const success = await login(email.value, password.value);
    if (success) {
      router.push('/home');
    } else {
      errorMsg.value = '로그인에 실패했습니다.';
    }
  } catch (e: any) {
    if (e.code === 20005) {
      errorMsg.value = '이메일 인증이 필요합니다. 회원가입 탭에서 인증을 완료해주세요.';
      emit('go-signup');
    } else {
      errorMsg.value = e.message || '로그인에 실패했습니다.';
    }
  } finally {
    loading.value = false;
  }
}

function onGoogleLogin() {
  // TODO: OAuth Google login
  window.location.href = `${import.meta.env.VITE_API_BASE_URL}/oauth2/authorization/google`;
}
</script>

<style scoped>
.form-body {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

/* 에러 배너 */
.error-banner {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px 14px;
  background: #fef2f2;
  border: 1px solid #fecaca;
  border-radius: 10px;
  font-size: 13px;
  color: #dc2626;
  font-weight: 500;
}

.error-icon {
  font-size: 14px;
  flex-shrink: 0;
}

.field {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.field-label {
  font-size: 14px;
  font-weight: 600;
  color: #374151;
}

.field-input {
  width: 100%;
  padding: 12px 16px;
  border: 1.5px solid #e5e7eb;
  border-radius: 12px;
  font-size: 14px;
  background: #f9fafb;
  outline: none;
  box-sizing: border-box;
  color: #111827;
  transition: border-color 0.15s ease, background 0.15s ease;
}

.field-input::placeholder { color: #9ca3af; }
.field-input:focus { border-color: #14BCED; background: #fff; }
.input-error { border-color: #ef4444 !important; background: #fff !important; }

.field-error {
  font-size: 12px;
  color: #ef4444;
  margin-top: -2px;
}

/* 비밀번호 보기/숨기기 */
.password-wrap {
  position: relative;
}

.password-wrap .field-input {
  padding-right: 60px;
}

.toggle-pw {
  position: absolute;
  right: 12px;
  top: 50%;
  transform: translateY(-50%);
  background: none;
  border: none;
  font-size: 12px;
  color: #9ca3af;
  cursor: pointer;
  padding: 0;
  font-weight: 500;
}

.forgot-wrap {
  text-align: right;
  margin-top: -6px;
}

.forgot-btn {
  background: none;
  border: none;
  font-size: 13px;
  font-weight: 500;
  color: #14BCED;
  cursor: pointer;
  padding: 0;
}

.submit-btn {
  width: 100%;
  padding: 12px 0;
  border-radius: 12px;
  background: #14BCED;
  color: #fff;
  font-size: 14px;
  font-weight: 700;
  border: none;
  cursor: pointer;
  transition: background 0.15s ease;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
}

.submit-btn:hover:not(:disabled) { background: #0fa8d4; }
.submit-btn:disabled { background: #9ca3af; cursor: not-allowed; }

/* 로딩 스피너 */
.spinner {
  width: 16px;
  height: 16px;
  border: 2px solid rgba(255,255,255,0.4);
  border-top-color: #fff;
  border-radius: 50%;
  animation: spin 0.7s linear infinite;
  flex-shrink: 0;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

.divider {
  display: flex;
  align-items: center;
  gap: 12px;
}

.divider-line {
  flex: 1;
  height: 1px;
  background: #e5e7eb;
}

.divider-text {
  font-size: 12px;
  color: #9ca3af;
  white-space: nowrap;
}

.social-btn {
  width: 100%;
  padding: 12px 0;
  border: 1.5px solid #e5e7eb;
  border-radius: 12px;
  background: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  font-size: 14px;
  font-weight: 600;
  color: #374151;
  cursor: pointer;
  box-sizing: border-box;
  transition: background 0.15s ease;
}

.social-btn:hover { background: #f9fafb; }
</style>
