import $axios from './axios';

export const trendApi = {
  getTopPlaces: () =>
    $axios.get('/trend/places/top5').then(res => res.data.results).catch(() => []),

  getTopContents: () =>
    $axios.get('/trend/contents/top5').then(res => res.data.results).catch(() => []),

  recordPlaceActivity: (placeId, type) =>
    $axios.post(`/trend/place/${placeId}/activity`, { type }).catch(() => {}),

  recordContentActivity: (contentId, type) =>
    $axios.post(`/trend/content/${contentId}/activity`, { type }).catch(() => {}),
};
