<template>
    <div class="top-bar">
        <button v-if="title" class="back-btn" @click="$emit('back')" aria-label="뒤로">
            <ion-icon name="arrow-back-outline"/>
        </button>
        <div v-else class="logo-wrap">
            <svg width="30" height="30" viewBox="0 0 30 30" xmlns="http://www.w3.org/2000/svg">
                <circle cx="15" cy="15" r="15" fill="#14BCED"/>
                <path d="M15 7 L12 15 L18 15 L11 23 L17 16 L13 16 Z" fill="#fff"/>
            </svg>
        </div>

        <h2 v-if="title" class="detail-title">{{ title }}</h2>
        <div v-else class="search-bar" @click="inputEl?.focus()">
            <ion-icon name="search-outline" class="search-icon"/>
            <input
                ref="inputEl"
                :value="modelValue"
                type="text"
                :placeholder="placeholder"
                class="search-input"
                @input="$emit('update:modelValue', ($event.target as HTMLInputElement).value)"
            />
        </div>

        <button v-if="!title && isLoggedIn" class="post-btn" aria-label="게시글 작성" @click="router.push('/post/write')">
            <ion-icon name="add-outline"/>
        </button>

        <div class="top-bar-actions">
            <template v-if="isLoggedIn">
                <button class="top-action-btn" aria-label="알림">
                    <ion-icon name="notifications-outline"/>
                </button>
                <div class="top-avatar" @click="router.push('/mypage')">
                    {{ userInitial }}
                </div>
            </template>
            <button v-else class="login-btn" @click="router.push('/auth')">
                로그인
            </button>
        </div>
    </div>

</template>

<script setup lang="ts">
import { ref, computed } from 'vue';
import { IonIcon, useIonRouter } from '@ionic/vue';
import { addIcons } from 'ionicons';
import { searchOutline, notificationsOutline, arrowBackOutline, addOutline } from 'ionicons/icons';
import { useAuth } from '@/composables/useAuth';

addIcons({ 'search-outline': searchOutline, 'notifications-outline': notificationsOutline, 'arrow-back-outline': arrowBackOutline, 'add-outline': addOutline });

withDefaults(defineProps<{
    modelValue?: string;
    placeholder?: string;
    title?: string;
}>(), {
    modelValue: '',
    placeholder: '작품명, 장소 검색...',
    title: '',
});

defineEmits<{ 'update:modelValue': [value: string]; 'back': [] }>();

const inputEl = ref<HTMLInputElement | null>(null);
const router = useIonRouter();
const { isLoggedIn, user } = useAuth();
const userInitial = computed(() => user.value?.name?.charAt(0).toUpperCase() ?? 'U');
</script>

<style scoped>
.top-bar {
    position: sticky;
    top: 0;
    z-index: 100;
    display: flex;
    align-items: center;
    gap: 10px;
    padding: 0 16px;
    height: 61px;
    box-sizing: border-box;
    background: #fff;
    border-bottom: 1px solid rgba(20, 188, 237, 0.1);
}

.back-btn {
    width: 34px;
    height: 34px;
    border-radius: 50%;
    background: #f3f4f6;
    border: none;
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 18px;
    color: #374151;
    cursor: pointer;
    flex-shrink: 0;
    transition: background 0.15s;
}

.back-btn:hover { background: #e5e7eb; }

.detail-title {
    flex: 1;
    font-size: 15px;
    font-weight: 700;
    color: #1f2937;
    margin: 0;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
}

.logo-wrap {
    display: flex;
    align-items: center;
    gap: 6px;
    flex-shrink: 0;
}

.logo-text {
    font-size: 15px;
    font-weight: 800;
    color: var(--brand);
    letter-spacing: -0.3px;
}

.search-bar {
    flex: 1;
    display: flex;
    align-items: center;
    gap: 8px;
    background: #fff;
    border-radius: 24px;
    padding: 9px 16px;
    box-shadow: 0 1px 6px rgba(20, 188, 237, 0.12);
    border: 1px solid rgba(20, 188, 237, 0.15);
    cursor: text;
}

.search-icon {
    font-size: 16px;
    color: var(--brand);
    flex-shrink: 0;
}

.search-input {
    flex: 1;
    background: transparent;
    border: none;
    outline: none;
    font-size: 14px;
    color: var(--text-primary);
}

.search-input::placeholder {
    color: var(--text-muted);
}

.post-btn {
    width: 34px;
    height: 34px;
    border-radius: 50%;
    background: var(--brand, #14BCED);
    border: none;
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 20px;
    color: #fff;
    cursor: pointer;
    flex-shrink: 0;
    transition: background 0.15s;
}

.post-btn:hover { background: #0fa8d4; }

.top-bar-actions {
    display: none;
}

@media (min-width: 768px) {
    .top-bar {
        padding: 0 28px;
        height: 65px;
    }

    .search-bar {
        max-width: 480px;
    }
}

@media (min-width: 1024px) {
    .logo-wrap {
        display: none;
    }

    .top-bar {
        padding: 0 32px;
        height: 69px;
        background: #fff;
        border-bottom: 1px solid rgba(20, 188, 237, 0.1);
    }

    .search-bar {
        max-width: 560px;
    }

    .top-bar-actions {
        display: flex;
        align-items: center;
        gap: 8px;
        flex-shrink: 0;
        margin-left: auto;
    }

    .top-action-btn {
        background: none;
        border: none;
        font-size: 22px;
        color: var(--text-secondary, #666);
        cursor: pointer;
        padding: 6px;
        border-radius: 8px;
        transition: color 0.15s;
        display: flex;
        align-items: center;
    }

    .top-action-btn:hover {
        color: var(--brand);
    }

    .top-avatar {
        width: 36px;
        height: 36px;
        border-radius: 50%;
        background: var(--brand);
        color: #fff;
        font-size: 14px;
        font-weight: 700;
        display: flex;
        align-items: center;
        justify-content: center;
        cursor: pointer;
    }

    .login-btn {
        padding: 8px 18px;
        border-radius: 20px;
        background: var(--brand);
        color: #fff;
        font-size: 13px;
        font-weight: 700;
        border: none;
        cursor: pointer;
        transition: background 0.15s;
        white-space: nowrap;
    }

    .login-btn:hover {
        background: #0fa8d4;
    }
}
</style>
