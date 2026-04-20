<template>
    <aside class="sidebar">
        <div class="sidebar-logo">
            <svg width="34" height="34" viewBox="0 0 30 30" xmlns="http://www.w3.org/2000/svg">
                <circle cx="15" cy="15" r="15" fill="#14BCED"/>
                <path d="M15 7 L12 15 L18 15 L11 23 L17 16 L13 16 Z" fill="#fff"/>
            </svg>
            <span class="sidebar-logo-text">성지맵</span>
        </div>

        <nav class="sidebar-nav">
            <button class="sidebar-nav-btn" :class="{ active: active === 'home' }" @click="router.push('/home')">
                <ion-icon :name="active === 'home' ? 'home' : 'home-outline'"/>
                <span>홈</span>
            </button>
            <button class="sidebar-nav-btn" :class="{ active: active === 'map' }" @click="router.push('/map')">
                <ion-icon :name="active === 'map' ? 'map' : 'map-outline'"/>
                <span>지도</span>
            </button>
            <button class="sidebar-nav-btn" :class="{ active: active === 'sns' }" @click="router.push('/sns')">
                <ion-icon :name="active === 'sns' ? 'people' : 'people-outline'"/>
                <span>SNS</span>
            </button>
            <button class="sidebar-nav-btn" :class="{ active: active === 'profile' }" @click="router.push(isLoggedIn ? '/mypage' : '/auth')">
                <ion-icon :name="active === 'profile' ? 'person' : 'person-outline'"/>
                <span>{{ isLoggedIn ? '프로필' : '로그인' }}</span>
            </button>
        </nav>

        <div class="sidebar-footer">
            <div class="sidebar-membership">
                <span>✨</span>
                <div>
                    <p class="sm-title">멤버십</p>
                    <p class="sm-sub">더 많은 성지 탐색</p>
                </div>
            </div>
        </div>
    </aside>
</template>

<script setup lang="ts">
import { IonIcon, useIonRouter } from '@ionic/vue';
import { addIcons } from 'ionicons';
import { home, homeOutline, map, mapOutline, people, peopleOutline, person, personOutline } from 'ionicons/icons';
import { useAuth } from '@/composables/useAuth';

addIcons({
    'home': home, 'home-outline': homeOutline,
    'map': map, 'map-outline': mapOutline,
    'people': people, 'people-outline': peopleOutline,
    'person': person, 'person-outline': personOutline,
});

defineProps<{ active: 'home' | 'map' | 'sns' | 'profile' }>();

const router = useIonRouter();
const { isLoggedIn } = useAuth();
</script>

<style scoped>
.sidebar {
    display: none;
}

@media (min-width: 1024px) {
    .sidebar {
        display: flex;
        flex-direction: column;
        width: 220px;
        flex-shrink: 0;
        background: #fff;
        border-right: 1px solid rgba(20, 188, 237, 0.12);
        height: 100vh;
        position: sticky;
        top: 0;
        padding: 24px 16px;
        box-sizing: border-box;
        box-shadow: 2px 0 12px rgba(20, 188, 237, 0.06);
    }

    .sidebar-logo {
        display: flex;
        align-items: center;
        gap: 10px;
        padding: 4px 8px 28px;
    }

    .sidebar-logo-text {
        font-size: 18px;
        font-weight: 800;
        color: var(--brand);
        letter-spacing: -0.4px;
    }

    .sidebar-nav {
        display: flex;
        flex-direction: column;
        gap: 4px;
        flex: 1;
    }

    .sidebar-nav-btn {
        display: flex;
        align-items: center;
        gap: 12px;
        width: 100%;
        padding: 12px 14px;
        border: none;
        border-radius: 12px;
        background: transparent;
        font-size: 14px;
        font-weight: 600;
        color: var(--text-secondary, #666);
        cursor: pointer;
        text-align: left;
        transition: all 0.15s ease;
    }

    .sidebar-nav-btn ion-icon {
        font-size: 20px;
        flex-shrink: 0;
    }

    .sidebar-nav-btn:hover {
        background: rgba(20, 188, 237, 0.08);
        color: var(--brand);
    }

    .sidebar-nav-btn.active {
        background: rgba(20, 188, 237, 0.12);
        color: var(--brand);
        font-weight: 700;
    }

    .sidebar-footer {
        padding-top: 16px;
        border-top: 1px solid rgba(20, 188, 237, 0.12);
        margin-top: auto;
    }

    .sidebar-membership {
        display: flex;
        align-items: center;
        gap: 10px;
        padding: 12px;
        background: linear-gradient(135deg, #e0f7fd, #cff2fc);
        border-radius: 12px;
        cursor: pointer;
    }

    .sidebar-membership span {
        font-size: 20px;
    }

    .sm-title {
        font-size: 13px;
        font-weight: 700;
        color: var(--brand);
        margin: 0 0 1px;
    }

    .sm-sub {
        font-size: 11px;
        color: var(--text-muted, #999);
        margin: 0;
    }
}
</style>
