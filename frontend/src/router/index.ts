import { createRouter, createWebHistory } from '@ionic/vue-router';
import { RouteRecordRaw } from 'vue-router';
import AuthPage from '@/pages/AuthPage.vue';
import HomePage from '@/pages/HomePage.vue';
import MyPage from '@/pages/MyPage.vue';
import CameraPage from '@/pages/CameraPage.vue';

const routes: Array<RouteRecordRaw> = [
  {
    path: '/',
    redirect: '/home'
  },
  {
    path: '/auth',
    name: 'Auth',
    component: AuthPage,
  },
  {
    path: '/home',
    name: 'Home',
    component: HomePage,
  },
  {
    path: '/mypage',
    name: 'MyPage',
    component: MyPage,
    meta: { requiresAuth: true },
  },
  {
    path: '/camera/:spotId?',
    name: 'Camera',
    component: CameraPage,
    meta: { requiresAuth: true },
  },
];

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes
});

router.beforeEach((to, _from, next) => {
  const isLoggedIn = !!localStorage.getItem('sj_user');

  if (to.path === '/auth' && isLoggedIn) {
    next('/home');
  } else if (to.meta.requiresAuth && !isLoggedIn) {
    next('/auth');
  } else {
    next();
  }
});

export default router;
