<template>
    <div class="bottom-nav">
        <button class="nav-btn" :class="{ active: active === 'home' }" @click="goHome()">
            <ion-icon name="home"/>
            <span>홈</span>
        </button>
        <button class="nav-btn" :class="{ active: active === 'map' }" @click="goMap()">
            <ion-icon name="map-outline"/>
            <span>지도</span>
        </button>
        <button class="nav-btn" :class="{ active: active === 'bookmark' }">
            <ion-icon name="bookmark-outline"/>
            <span>저장</span>
        </button>
        <button class="nav-btn" :class="{ active: active === 'profile' }" @click="goAuth">
            <ion-icon name="person-outline"/>
            <span>{{ isLoggedIn ? '프로필' : '로그인' }}</span>
        </button>
    </div>
</template>

<script setup lang="ts">
import { IonIcon, useIonRouter } from '@ionic/vue';
import { addIcons } from 'ionicons';
import { home, mapOutline, bookmarkOutline, personOutline } from 'ionicons/icons';
import { useAuth } from '@/composables/useAuth';

addIcons({ 'home': home, 'map-outline': mapOutline, 'bookmark-outline': bookmarkOutline, 'person-outline': personOutline });

defineProps<{ active?: 'home' | 'map' | 'bookmark' | 'profile' }>();

const router = useIonRouter();
const { isLoggedIn } = useAuth();

function goHome() { router.push('/home'); }
function goMap() { router.push('/map'); }
function goAuth() { router.push(isLoggedIn.value ? '/mypage' : '/auth'); }
</script>

<style scoped>
.bottom-nav {
    position: fixed;
    bottom: 0;
    left: 0;
    right: 0;
    height: 60px;
    background: #fff;
    border-top: 1px solid rgba(20, 188, 237, 0.15);
    box-shadow: 0 -2px 16px rgba(20, 188, 237, 0.10);
    display: flex;
    align-items: center;
    justify-content: space-around;
    padding-bottom: env(safe-area-inset-bottom, 0);
    z-index: 200;
}

.nav-btn {
    flex: 1;
    background: none;
    border: none;
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: 3px;
    color: var(--text-muted);
    font-size: 20px;
    cursor: pointer;
    padding: 6px 0;
    transition: color 0.2s;
}

.nav-btn span {
    font-size: 10px;
    font-weight: 600;
}

.nav-btn.active {
    color: var(--brand);
}

@media (min-width: 1024px) {
    .bottom-nav {
        display: none;
    }
}
</style>
