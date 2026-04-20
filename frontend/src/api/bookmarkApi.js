import $axios from './axios';

export const bookmarkApi = {
    toggle: (placeId) =>
        $axios.post(`/bookmark/place/${placeId}`).then(res => res.data.results).catch(() => null),

    getMyPlaces: () =>
        $axios.get('/bookmark/places').then(res => res.data.results).catch(() => []),

    getStatus: (placeId) =>
        $axios.get(`/bookmark/place/${placeId}/status`).then(res => res.data.results).catch(() => ({ bookmarked: false })),
};
