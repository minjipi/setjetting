import $axios from './axios';

export const contentsApi = {
  /** 목록 조회 */
  getList: () =>
    $axios.get('/contents/all').then(res => res.data.results).catch((error) => {error.data}),
};
