import axios from 'axios';

const $axios = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL ?? '',
  withCredentials: true,
  headers: { 'Content-Type': 'application/json' },
});

let isRefreshing = false;
let pendingQueue = [];

function flushQueue(error) {
  pendingQueue.forEach(({ resolve, reject }) => error ? reject(error) : resolve());
  pendingQueue = [];
}

$axios.interceptors.response.use(
  (response) => {
    const data = response.data;
    if (data !== null && typeof data === 'object' && 'isSuccess' in data && !data.isSuccess) {
      const error = new Error(data.message || '오류가 발생했습니다.');
      error.code = data.code;
      error.data = { ...data, success: false };
      return Promise.reject(error);
    }
    return response;
  },
  async (error) => {
    const original = error.config;

    // 401이고, 재시도가 아니고, refresh 엔드포인트 자체가 아닐 때만 갱신 시도
    if (
      error.response?.status === 401 &&
      !original._retry &&
      !original.url?.includes('/user/token/refresh')
    ) {
      if (isRefreshing) {
        // 이미 갱신 중이면 완료를 기다렸다가 원본 요청 재시도
        return new Promise((resolve, reject) => {
          pendingQueue.push({ resolve, reject });
        })
          .then(() => $axios(original))
          .catch((e) => Promise.reject(e));
      }

      original._retry = true;
      isRefreshing = true;

      try {
        await $axios.get('/user/token/refresh');
        flushQueue(null);
        return $axios(original);
      } catch (refreshError) {
        flushQueue(refreshError);
        // 리프레시 토큰도 만료 → 로그아웃 처리
        localStorage.removeItem('sj_user');
        window.location.href = '/auth';
        return Promise.reject(refreshError);
      } finally {
        isRefreshing = false;
      }
    }

    const data = {};
    if (error.response) {
      Object.assign(data, error.response.data);
      data.errorMessage = error.response.data?.message;
    } else {
      data.code = error.code;
      data.errorMessage = error.message;
    }
    error.data = data;
    error.data.success = false;
    return Promise.reject(error);
  }
);

export default $axios;
