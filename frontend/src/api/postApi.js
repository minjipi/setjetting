import $axios from './axios';

export const postApi = {
  uploadImage: (file) => {
    const formData = new FormData();
    formData.append('file', file);
    return $axios.post('/post/upload', formData, {
      headers: { 'Content-Type': 'multipart/form-data' },
    }).then(res => res.data.results);
  },

  createPost: (req) =>
    $axios.post('/post/write', req).then(res => res.data.results),

  getMyPosts: () =>
    $axios.get('/post/my').then(res => res.data.results),

  getFeed: () =>
    $axios.get('/post/feed').then(res => res.data.results),

  getPostDetail: (idx) =>
    $axios.get(`/post/${idx}`).then(res => res.data.results),

  getPostsByUser: (userIdx) =>
    $axios.get(`/post/user/${userIdx}`).then(res => res.data.results),
};
