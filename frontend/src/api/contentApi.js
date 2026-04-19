import $axios from './axios';

export const contentApi = {
  /** 목록 조회 */
  getList: () =>
    $axios.get('/content/all').then(res => res.data.results).catch((error) => {error.data}),

  /** 상세 조회 */
  getDetail: (id) =>
    $axios.get(`/content/${id}`).then(res => res.data.results).catch((error) => {error.data}),
};
