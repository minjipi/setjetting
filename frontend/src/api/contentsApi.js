import $axios from './axios';

export const contentApi = {
  /** 목록 조회 */
  getList: () =>
    $axios.get('/content/all').then(res => res.data.results).catch((error) => {error.data}),
};
