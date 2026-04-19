<template>
    <aside class="sidebar">
        <div class="sidebar-logo">
            <svg width="28" height="28" viewBox="0 0 30 30" xmlns="http://www.w3.org/2000/svg">
                <circle cx="15" cy="15" r="15" fill="#14BCED"/>
                <path d="M15 7 L12 15 L18 15 L11 23 L17 16 L13 16 Z" fill="#fff"/>
            </svg>
        </div>
        <nav class="sidebar-nav">
            <button class="nav-icon-btn" @click="goHome">
                <ion-icon name="home"/>
            </button>
            <button class="nav-icon-btn active" @click="goMap">
                <ion-icon name="map-outline"/>
            </button>
            <button class="nav-icon-btn">
                <ion-icon name="bookmark-outline"/>
            </button>
            <button class="nav-icon-btn" @click="goAuth">
                <ion-icon name="person-outline"/>
            </button>
        </nav>
    </aside>
</template>

<script setup lang="ts">
import { IonIcon, useIonRouter } from '@ionic/vue';
import { addIcons } from 'ionicons';
import { arrowBackOutline, mapOutline, bookmarkOutline, personOutline } from 'ionicons/icons';
import { useAuth } from '@/composables/useAuth';

addIcons({ 'arrow-back-outline': arrowBackOutline, 'map-outline': mapOutline, 'bookmark-outline': bookmarkOutline, 'person-outline': personOutline });

const router = useIonRouter();
const { isLoggedIn } = useAuth();

function goHome() { router.push('/home'); }
function goMap() { router.push('/map'); }
function goAuth() { router.push(isLoggedIn.value ? '/mypage' : '/auth'); }
</script>

<style scoped>
.sidebar {
    display: none;
}

@media (min-width: 1024px) {
    .sidebar {
        display: flex;
        flex-direction: column;
        align-items: center;
        width: 64px;
        flex-shrink: 0;
        background: #fff;
        border-right: 1px solid rgba(20, 188, 237, 0.12);
        padding: 16px 0;
        gap: 4px;
        box-shadow: 2px 0 12px rgba(20, 188, 237, 0.06);
        z-index: 10;
    }

    .sidebar-logo {
        padding-bottom: 16px;
        margin-bottom: 8px;
        border-bottom: 1px solid rgba(20, 188, 237, 0.1);
        width: 100%;
        display: flex;
        justify-content: center;
    }

    .sidebar-nav {
        display: flex;
        flex-direction: column;
        align-items: center;
        gap: 4px;
        width: 100%;
    }

    .nav-icon-btn {
        width: 44px;
        height: 44px;
        border: none;
        border-radius: 12px;
        background: transparent;
        color: var(--text-secondary, #6b7280);
        font-size: 20px;
        display: flex;
        align-items: center;
        justify-content: center;
        cursor: pointer;
        transition: background 0.15s, color 0.15s;
    }

    .nav-icon-btn:hover,
    .nav-icon-btn.active {
        background: var(--brand-light, #e0f7fd);
        color: var(--brand, #14BCED);
    }
}
</style>
