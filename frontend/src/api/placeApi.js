import $axios from './axios';

export const placeApi = {
  /** 목록 조회 */
  getList: () =>
    $axios.get('/place/all').then(res => res.data.results).catch((error) => {error.data}),

  /** 장소 기준 상세 조회 (부모 컨텐츠 + 전체 장소 반환) */
  getDetail: (id) =>
    $axios.get(`/place/${id}`).then(res => res.data.results).catch((error) => {error.data}),
};
