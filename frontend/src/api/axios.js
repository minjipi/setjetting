import axios from 'axios';

const $axios = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL ?? '',
  withCredentials: true,
  headers: { 'Content-Type': 'application/json' },
});

$axios.interceptors.response.use(
  (response) => {
    const data = response.data;
    // HTTP 200이지만 백엔드 응답 isSuccess가 false인 경우
    if (data !== null && typeof data === 'object' && 'isSuccess' in data && !data.isSuccess) {
      const error = new Error(data.message || '오류가 발생했습니다.');
      error.code = data.code;
      error.data = { ...data, success: false };
      return Promise.reject(error);
    }
    return response;
  },
  (error) => {
    const data = {};
    if (error.response) {
      // 요청 전송 성공, 응답 수신 성공(2xx 외 상태 코드)
      Object.assign(data, error.response.data);
      data.errorMessage = error.response.data?.message;
    } else {
      // 요청 전송 성공, 응답 수신 실패 또는 요청 설정 중 문제 발생
      data.code = error.code;
      data.errorMessage = error.message;
    }
    error.data = data;
    error.data.success = false;
    return Promise.reject(error);
  }
);

export default $axios;
