import $axios from './axios';

export const userApi = {
  /** 로그인 */
  login: (email, password) =>
    $axios.post('/user/login', { email, password }).then(res => res.data).catch((error) => {error.data}),

  /** 회원가입 (이메일 인증 UUID 발송) */
  signup: (name, email, password) =>
    $axios.post('/user/signup', { name, email, password }).then(res => res.data),

  /** 이메일 인증 코드(UUID) 확인 → 계정 활성화 */
  verifyEmail: (email, uuid) =>
    $axios.post('/user/email/verify', { email, uuid }).then(res => res.data),

  /** 이메일 중복 체크 */
  checkEmailDuplicate: (email) =>
    $axios.get(`/user/email/duplicate?email=${encodeURIComponent(email)}`).then(res => res.data),

  /** 내 프로필 조회 */
  getProfile: () =>
    $axios.get('/user/profile').then(res => res.data.result),

  /** 토큰 유효성 확인 */
  checkAuth: () =>
    $axios.get('/user/check').then(res => res.data.result),

  /** 로그아웃 */
  logout: () =>
    $axios.get('/user/logout').then(res => res.data.result),
};
