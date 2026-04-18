import $axios from './axios';

export const placeApi = {
  /** 목록 조회 */
  getList: () =>
    $axios.get('/place/all').then(res => res.data.results).catch((error) => {error.data}),
};
