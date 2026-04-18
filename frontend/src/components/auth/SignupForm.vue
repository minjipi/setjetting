<template>
  <div class="signup-wrap">

    <!-- ── STEP 1: 정보 입력 ── -->
    <form v-if="step === 1" @submit.prevent="onSignup" class="form-body" novalidate>

      <div v-if="errorMsg" class="error-banner" role="alert">
        <span class="error-icon">⚠</span>{{ errorMsg }}
      </div>

      <div class="field">
        <label class="field-label">이름</label>
        <input
          v-model="name"
          type="text"
          placeholder="이름 또는 닉네임"
          class="field-input"
          :class="{ 'input-error': nameError }"
          @blur="validateName"
        />
        <span v-if="nameError" class="field-error">{{ nameError }}</span>
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
            placeholder="영문+숫자+특수문자 8~15자"
            class="field-input"
            :class="{ 'input-error': passwordError }"
            @blur="validatePassword"
            autocomplete="new-password"
          />
          <button type="button" class="toggle-pw" @click="showPw = !showPw" tabindex="-1">
            {{ showPw ? '숨기기' : '보기' }}
          </button>
        </div>
        <span v-if="passwordError" class="field-error">{{ passwordError }}</span>
        <div class="pw-strength" v-if="password">
          <div
            v-for="i in 4" :key="i"
            class="pw-bar"
            :class="{ active: pwStrength >= i }"
          />
          <span class="pw-strength-label">{{ pwStrengthLabel }}</span>
        </div>
      </div>

      <div class="field">
        <label class="field-label">비밀번호 확인</label>
        <input
          v-model="passwordConfirm"
          type="password"
          placeholder="비밀번호를 다시 입력하세요"
          class="field-input"
          :class="{ 'input-error': confirmError }"
          @blur="validateConfirm"
          autocomplete="new-password"
        />
        <span v-if="confirmError" class="field-error">{{ confirmError }}</span>
      </div>

      <label class="terms-row">
        <input v-model="agreed" type="checkbox" class="terms-check" />
        <span class="terms-text">
          <span class="terms-link">이용약관</span> 및
          <span class="terms-link">개인정보처리방침</span>에 동의합니다
        </span>
      </label>

      <button type="submit" class="submit-btn" :disabled="loading || !agreed">
        <span v-if="loading" class="spinner" />
        {{ loading ? '처리 중...' : '인증 메일 발송' }}
      </button>

    </form>

    <!-- ── STEP 2: 이메일 UUID 인증 ── -->
    <form v-else @submit.prevent="onVerify" class="form-body" novalidate>

      <div class="verify-guide">
        <div class="verify-guide-icon">📬</div>
        <p class="verify-guide-title">이메일을 확인하세요</p>
        <p class="verify-guide-desc">
          <strong>{{ email }}</strong>으로 발송된<br />
          인증 코드를 아래에 입력해주세요.
        </p>
      </div>

      <div v-if="errorMsg" class="error-banner" role="alert">
        <span class="error-icon">⚠</span>{{ errorMsg }}
      </div>

      <div class="field">
        <label class="field-label">인증 코드</label>
        <input
          v-model="uuid"
          type="text"
          placeholder="이메일에서 인증 코드를 복사해 붙여넣으세요"
          class="field-input"
          :class="{ 'input-error': uuidError }"
          @blur="validateUuid"
          autocomplete="off"
        />
        <span v-if="uuidError" class="field-error">{{ uuidError }}</span>
      </div>

      <button type="submit" class="submit-btn" :disabled="loading">
        <span v-if="loading" class="spinner" />
        {{ loading ? '인증 중...' : '인증 완료' }}
      </button>

      <div class="resend-row">
        <span class="resend-hint">인증 메일을 받지 못하셨나요?</span>
        <button type="button" class="resend-btn" :disabled="resendLoading" @click="onResend">
          {{ resendLoading ? '발송 중...' : '재발송' }}
        </button>
      </div>

      <button type="button" class="back-btn" @click="step = 1; errorMsg = ''">
        ← 이전으로
      </button>

    </form>

  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue';
import { userApi } from '@/api/userApi';

const emit = defineEmits(['signup-success']);

/* ── 단계 ── */
const step = ref<1 | 2>(1);

/* ── 필드 ── */
const name = ref('');
const email = ref('');
const password = ref('');
const passwordConfirm = ref('');
const uuid = ref('');
const agreed = ref(false);
const showPw = ref(false);

/* ── 상태 ── */
const loading = ref(false);
const resendLoading = ref(false);
const errorMsg = ref('');

/* ── 유효성 오류 ── */
const nameError = ref('');
const emailError = ref('');
const passwordError = ref('');
const confirmError = ref('');
const uuidError = ref('');

/* ── 정규식 ── */
const EMAIL_REGEX = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/;
const PW_REGEX = /^(?=.*[a-zA-Z])(?=.*[!@#$%^&*()])(?=.*[0-9]).{8,15}$/;
const NAME_REGEX = /^[A-Za-z0-9가-힣]{1,50}$/;

/* ── 비밀번호 강도 ── */
const pwStrength = computed(() => {
  const pw = password.value;
  if (!pw) return 0;
  let score = 0;
  if (pw.length >= 8) score++;
  if (/[a-zA-Z]/.test(pw)) score++;
  if (/[0-9]/.test(pw)) score++;
  if (/[!@#$%^&*()]/.test(pw)) score++;
  return score;
});

const pwStrengthLabel = computed(() => {
  const labels = ['', '약함', '보통', '강함', '매우 강함'];
  return labels[pwStrength.value] || '';
});

/* ── 유효성 검사 ── */
function validateName(): boolean {
  if (!name.value) { nameError.value = '이름을 입력해주세요.'; return false; }
  if (!NAME_REGEX.test(name.value)) { nameError.value = '특수문자 없이 1~50자로 입력해주세요.'; return false; }
  nameError.value = '';
  return true;
}

async function validateEmail(): Promise<boolean> {
  if (!email.value) { emailError.value = '이메일을 입력해주세요.'; return false; }
  if (!EMAIL_REGEX.test(email.value)) { emailError.value = '올바른 이메일 형식이 아닙니다.'; return false; }
  try {
    await userApi.checkEmailDuplicate(email.value);
  } catch (e: any) {
    emailError.value = e.message || '이미 사용 중인 이메일입니다.';
    return false;
  }
  emailError.value = '';
  return true;
}

function validatePassword(): boolean {
  if (!password.value) { passwordError.value = '비밀번호를 입력해주세요.'; return false; }
  if (!PW_REGEX.test(password.value)) {
    passwordError.value = '영문+숫자+특수문자(!@#$%^&*()) 조합 8~15자로 입력해주세요.';
    return false;
  }
  passwordError.value = '';
  return true;
}

function validateConfirm(): boolean {
  if (!passwordConfirm.value) { confirmError.value = '비밀번호를 다시 입력해주세요.'; return false; }
  if (password.value !== passwordConfirm.value) { confirmError.value = '비밀번호가 일치하지 않습니다.'; return false; }
  confirmError.value = '';
  return true;
}

function validateUuid(): boolean {
  if (!uuid.value.trim()) { uuidError.value = '인증 코드를 입력해주세요.'; return false; }
  uuidError.value = '';
  return true;
}

/* ── 이벤트 핸들러 ── */
async function onSignup() {
  errorMsg.value = '';
  const results = await Promise.all([validateName(), validateEmail(), validatePassword(), validateConfirm()]);
  if (!results.every(Boolean)) return;

  loading.value = true;
  try {
    await userApi.signup(name.value, email.value, password.value);
    step.value = 2;
  } catch (e: any) {
    errorMsg.value = e.message || '회원가입에 실패했습니다.';
  } finally {
    loading.value = false;
  }
}

async function onVerify() {
  errorMsg.value = '';
  if (!validateUuid()) return;

  loading.value = true;
  try {
    await userApi.verifyEmail(email.value, uuid.value.trim());
    emit('signup-success');
  } catch (e: any) {
    errorMsg.value = e.message || '인증에 실패했습니다. 코드를 다시 확인해주세요.';
  } finally {
    loading.value = false;
  }
}

async function onResend() {
  resendLoading.value = true;
  try {
    await userApi.signup(name.value, email.value, password.value);
    errorMsg.value = '';
  } catch (e: any) {
    errorMsg.value = e.message || '재발송에 실패했습니다.';
  } finally {
    resendLoading.value = false;
  }
}
</script>

<style scoped>
.signup-wrap {
  width: 100%;
}

.form-body {
  display: flex;
  flex-direction: column;
  gap: 14px;
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

.error-icon { font-size: 14px; flex-shrink: 0; }

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
  transition: border-color 0.15s, background 0.15s;
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
.password-wrap { position: relative; }
.password-wrap .field-input { padding-right: 60px; }

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

/* 비밀번호 강도 표시 */
.pw-strength {
  display: flex;
  align-items: center;
  gap: 4px;
  margin-top: -2px;
}

.pw-bar {
  flex: 1;
  height: 3px;
  border-radius: 2px;
  background: #e5e7eb;
  transition: background 0.2s;
}

.pw-bar.active:nth-child(1) { background: #ef4444; }
.pw-bar.active:nth-child(2) { background: #f59e0b; }
.pw-bar.active:nth-child(3) { background: #10b981; }
.pw-bar.active:nth-child(4) { background: #14BCED; }

.pw-strength-label {
  font-size: 11px;
  color: #6b7280;
  flex-shrink: 0;
  min-width: 54px;
  text-align: right;
}

/* 약관 */
.terms-row {
  display: flex;
  align-items: flex-start;
  gap: 8px;
  cursor: pointer;
}

.terms-check {
  margin-top: 2px;
  width: 16px;
  height: 16px;
  accent-color: #14BCED;
  flex-shrink: 0;
  cursor: pointer;
}

.terms-text { font-size: 13px; color: #6b7280; line-height: 1.5; }
.terms-link { color: #14BCED; font-weight: 500; }

/* 제출 버튼 */
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
  transition: background 0.15s;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  margin-top: 2px;
}

.submit-btn:hover:not(:disabled) { background: #0fa8d4; }
.submit-btn:disabled { background: #9ca3af; cursor: not-allowed; }

.spinner {
  width: 16px;
  height: 16px;
  border: 2px solid rgba(255,255,255,0.4);
  border-top-color: #fff;
  border-radius: 50%;
  animation: spin 0.7s linear infinite;
  flex-shrink: 0;
}

@keyframes spin { to { transform: rotate(360deg); } }

/* ── Step 2: 이메일 인증 ── */
.verify-guide {
  text-align: center;
  padding: 8px 0 4px;
}

.verify-guide-icon {
  font-size: 40px;
  margin-bottom: 10px;
}

.verify-guide-title {
  font-size: 16px;
  font-weight: 800;
  color: #111827;
  margin: 0 0 8px;
}

.verify-guide-desc {
  font-size: 13px;
  color: #6b7280;
  line-height: 1.6;
  margin: 0;
}

.verify-guide-desc strong {
  color: #14BCED;
  font-weight: 600;
}

.resend-row {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
}

.resend-hint {
  font-size: 12px;
  color: #9ca3af;
}

.resend-btn {
  background: none;
  border: none;
  font-size: 12px;
  font-weight: 700;
  color: #14BCED;
  cursor: pointer;
  padding: 0;
  text-decoration: underline;
}

.resend-btn:disabled {
  color: #9ca3af;
  cursor: not-allowed;
  text-decoration: none;
}

.back-btn {
  background: none;
  border: none;
  font-size: 13px;
  color: #9ca3af;
  cursor: pointer;
  padding: 0;
  text-align: center;
  width: 100%;
  margin-top: -4px;
  transition: color 0.15s;
}

.back-btn:hover { color: #6b7280; }
</style>
