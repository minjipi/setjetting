<template>
    <div class="bottom-nav">
        <ion-button class="nav-btn" :class="{ active: active === 'home' }" fill="clear" router-link="/home" router-direction="root">
            <div class="btn-inner">
                <ion-icon name="home"/>
                <span>홈</span>
            </div>
        </ion-button>
        <ion-button class="nav-btn" :class="{ active: active === 'map' }" fill="clear" router-link="/map" router-direction="root">
            <div class="btn-inner">
                <ion-icon name="map-outline"/>
                <span>지도</span>
            </div>
        </ion-button>
        <ion-button class="nav-btn" :class="{ active: active === 'sns' }" fill="clear" router-link="/sns" router-direction="root">
            <div class="btn-inner">
                <ion-icon name="people-outline"/>
                <span>SNS</span>
            </div>
        </ion-button>
        <ion-button class="nav-btn" :class="{ active: active === 'profile' }" fill="clear" :router-link="isLoggedIn ? '/mypage' : '/auth'" router-direction="root">
            <div class="btn-inner">
                <ion-icon name="person-outline"/>
                <span>{{ isLoggedIn ? '프로필' : '로그인' }}</span>
            </div>
        </ion-button>
    </div>
</template>

<script setup lang="ts">
import { IonIcon, IonButton } from '@ionic/vue';
import { addIcons } from 'ionicons';
import { home, mapOutline, peopleOutline, personOutline } from 'ionicons/icons';
import { useAuth } from '@/composables/useAuth';

addIcons({ 'home': home, 'map-outline': mapOutline, 'people-outline': peopleOutline, 'person-outline': personOutline });

defineProps<{ active?: 'home' | 'map' | 'sns' | 'profile' }>();

const { isLoggedIn } = useAuth();
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
    --color: var(--text-muted);
    --padding-start: 0;
    --padding-end: 0;
    --padding-top: 0;
    --padding-bottom: 0;
    margin: 0;
    height: 100%;
}

.nav-btn.active {
    --color: var(--brand);
}

.btn-inner {
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: 3px;
    font-size: 20px;
}

.btn-inner span {
    font-size: 10px;
    font-weight: 600;
}

@media (min-width: 1024px) {
    .bottom-nav {
        display: none;
    }
}
</style>
