<template>
    <ion-page>
        <ion-content :fullscreen="true" class="detail-content">
            <div class="detail-wrap">

                <!-- 헤더 -->
                <div class="detail-header">
                    <button class="back-btn" @click="goBack">
                        <ion-icon name="arrow-back-outline"/>
                    </button>
                    <h2 class="detail-title">
                        <span class="brand-text">'{{ content?.title }}'</span> 성지 정보
                    </h2>
                </div>

                <!-- 카카오 지도 -->
                <MapSection/>

                <!-- 본문 -->
                <div class="detail-body">

                    <!-- 작품 정보 (좌측) -->
                    <section class="left-section">
                        <h3 class="section-heading">
                            <ion-icon name="film-outline" class="section-icon"/>
                            작품 정보
                        </h3>
                        <div class="content-card">
                            <div class="content-poster">
                                <img v-if="content?.posterImageUrl" :src="content.posterImageUrl" :alt="content.title" class="poster-img"/>
                                <div v-else class="poster-placeholder">🎬</div>
                            </div>
                            <div class="content-meta">
                                <p class="content-english">{{ content?.englishTitle }}</p>
                                <p class="content-desc">{{ content?.description }}</p>
                                <span class="place-count">
                                    <ion-icon name="location-outline"/>
                                    성지 {{ content?.placeCount }}곳
                                </span>
                            </div>
                        </div>
                    </section>

                    <!-- 관련 장소 (우측) -->
                    <section class="right-section">
                        <h3 class="section-heading">
                            <ion-icon name="location-outline" class="section-icon"/>
                            관련 장소
                            <span class="count-badge">{{ places.length }}</span>
                        </h3>
                        <div class="place-list">
                            <div
                                v-for="place in places"
                                :key="place.placeIdx"
                                class="place-item"
                                @click="router.push({ name: 'PlaceDetail', params: { id: place.placeIdx }, state: { place } })"
                            >
                                <div class="place-thumb">
                                    <img v-if="place.placeImageUrl" :src="place.placeImageUrl" :alt="place.name"/>
                                    <div v-else class="thumb-placeholder"/>
                                </div>
                                <div class="place-info">
                                    <h4 class="place-name">{{ place.name }}</h4>
                                    <p class="place-address">{{ place.address }}</p>
                                    <div class="place-tags">
                                        <span class="tag-content">{{ content?.title }}</span>
                                        <span class="tag-match">
                                            <ion-icon name="checkmark-circle-outline"/>
                                            성지 매칭
                                        </span>
                                    </div>
                                </div>
                                <button class="nav-btn" @click.stop>
                                    <ion-icon name="navigate-outline"/>
                                </button>
                            </div>
                        </div>
                    </section>

                </div>
            </div>
        </ion-content>
    </ion-page>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue';
import { IonPage, IonContent, IonIcon, useIonRouter } from '@ionic/vue';
import { useRoute } from 'vue-router';
import { addIcons } from 'ionicons';
import {
    arrowBackOutline, filmOutline, locationOutline,
    navigateOutline, checkmarkCircleOutline
} from 'ionicons/icons';
import { contentApi } from '@/api/contentApi';
import MapSection from "@/components/home/MapSection.vue";

addIcons({
    'arrow-back-outline': arrowBackOutline,
    'film-outline': filmOutline,
    'location-outline': locationOutline,
    'navigate-outline': navigateOutline,
    'checkmark-circle-outline': checkmarkCircleOutline,
});

const router = useIonRouter();
const route = useRoute();

const contentId = computed(() => Number(route.params.id));
const content = ref<any>(null);
const places = ref<any[]>([]);

function goBack() {
    router.back();
}

async function loadData() {
    const detail = await contentApi.getDetail(contentId.value);
    if (detail) {
        content.value = detail;
        places.value = detail.places ?? [];
    }
}

onMounted(() => {
    loadData();
});
</script>

<style scoped>
.detail-content {
    --background: #fff;
}

.detail-wrap {
    min-height: 100%;
    display: flex;
    flex-direction: column;
}

/* ── 헤더 ── */
.detail-header {
    position: sticky;
    top: 0;
    z-index: 100;
    display: flex;
    align-items: center;
    gap: 12px;
    padding: 14px 16px;
    background: #fff;
    border-bottom: 1px solid rgba(20, 188, 237, 0.1);
}

.back-btn {
    flex-shrink: 0;
    width: 36px;
    height: 36px;
    border-radius: 50%;
    background: #f3f4f6;
    border: none;
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 18px;
    color: #4b5563;
    cursor: pointer;
    transition: background 0.15s;
}

.back-btn:hover {
    background: #e5e7eb;
}

.detail-title {
    font-size: 17px;
    font-weight: 700;
    color: var(--text-primary, #111);
    margin: 0;
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
}

.brand-text {
    color: var(--brand, #14BCED);
}

/* ── 본문 ── */
.detail-body {
    display: flex;
    flex-direction: column;
    gap: 0;
}

/* 섹션 공통 */
.section-heading {
    display: flex;
    align-items: center;
    gap: 6px;
    font-size: 14px;
    font-weight: 700;
    color: #1f2937;
    margin: 0 0 14px;
    padding-bottom: 10px;
    border-bottom: 1px solid #f3f4f6;
}

.section-icon {
    color: var(--brand, #14BCED);
    font-size: 16px;
}

.count-badge {
    font-size: 11px;
    font-weight: 400;
    color: #6b7280;
    background: #f3f4f6;
    padding: 1px 8px;
    border-radius: 99px;
    margin-left: 2px;
}

/* ── 작품 정보 ── */
.left-section {
    padding: 20px 16px;
    border-bottom: 1px solid #f3f4f6;
}

.content-card {
    display: flex;
    gap: 16px;
}

.content-poster {
    width: 120px;
    flex-shrink: 0;
    aspect-ratio: 2 / 3;
    border-radius: 12px;
    overflow: hidden;
    background: #f3f4f6;
    border: 1px solid #e5e7eb;
    box-shadow: 0 3px 10px rgba(0, 0, 0, 0.1);
}

.content-poster .poster-img {
    width: 100%;
    height: 100%;
    object-fit: cover;
}

.poster-placeholder {
    width: 100%;
    height: 100%;
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 28px;
}

.content-meta {
    flex: 1;
    min-width: 0;
    display: flex;
    flex-direction: column;
    gap: 6px;
    padding-top: 2px;
}

.content-english {
    font-size: 12px;
    color: #6b7280;
    margin: 0;
}

.content-desc {
    font-size: 12px;
    color: #4b5563;
    margin: 0;
    line-height: 1.6;
    display: -webkit-box;
    -webkit-line-clamp: 5;
    -webkit-box-orient: vertical;
    overflow: hidden;
}

.place-count {
    display: flex;
    align-items: center;
    gap: 4px;
    font-size: 12px;
    font-weight: 600;
    color: var(--brand, #14BCED);
    margin-top: 4px;
}

/* ── 관련 장소 ── */
.right-section {
    padding: 20px 16px;
}

.place-list {
    display: flex;
    flex-direction: column;
    gap: 8px;
}

.place-item {
    display: flex;
    align-items: center;
    gap: 12px;
    padding: 10px;
    border-radius: 14px;
    border: 1px solid transparent;
    cursor: pointer;
    transition: background 0.15s, border-color 0.15s;
}

.place-item:hover {
    background: rgba(20, 188, 237, 0.05);
    border-color: rgba(20, 188, 237, 0.2);
}

.place-thumb {
    width: 64px;
    height: 64px;
    border-radius: 10px;
    overflow: hidden;
    flex-shrink: 0;
    background: #e5e7eb;
}

.place-thumb img {
    width: 100%;
    height: 100%;
    object-fit: cover;
}

.thumb-placeholder {
    width: 100%;
    height: 100%;
    background: #e5e7eb;
}

.place-info {
    flex: 1;
    min-width: 0;
}

.place-name {
    font-size: 13px;
    font-weight: 700;
    color: #1f2937;
    margin: 0 0 2px;
    transition: color 0.15s;
}

.place-item:hover .place-name {
    color: var(--brand, #14BCED);
}

.place-address {
    font-size: 11px;
    color: #6b7280;
    margin: 0 0 6px;
    overflow: hidden;
    white-space: nowrap;
    text-overflow: ellipsis;
}

.place-tags {
    display: flex;
    align-items: center;
    gap: 6px;
}

.tag-content {
    font-size: 10px;
    background: #f3f4f6;
    color: #374151;
    padding: 2px 7px;
    border-radius: 6px;
}

.tag-match {
    display: flex;
    align-items: center;
    gap: 3px;
    font-size: 10px;
    color: var(--brand, #14BCED);
    font-weight: 600;
}

.tag-match ion-icon {
    font-size: 11px;
}

.nav-btn {
    flex-shrink: 0;
    width: 30px;
    height: 30px;
    border-radius: 50%;
    border: 1px solid #e5e7eb;
    background: #fff;
    color: #9ca3af;
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 14px;
    cursor: pointer;
    box-shadow: 0 1px 4px rgba(0, 0, 0, 0.08);
    transition: color 0.15s, border-color 0.15s;
}

.nav-btn:hover {
    color: var(--brand, #14BCED);
    border-color: var(--brand, #14BCED);
}

/* ── 데스크탑 ── */
@media (min-width: 1024px) {
    .detail-header {
        padding: 16px 32px;
    }

    .detail-body {
        flex-direction: row;
        align-items: flex-start;
    }

    .left-section {
        width: 40%;
        flex-shrink: 0;
        border-bottom: none;
        border-right: 1px solid #f3f4f6;
        min-height: calc(100vh - 70px);
        padding: 24px 28px;
    }

    .right-section {
        flex: 1;
        padding: 24px 28px;
    }

    .content-grid {
        grid-template-columns: repeat(2, 1fr);
        gap: 12px;
    }
}

@media (min-width: 768px) {
    .content-grid {
        grid-template-columns: repeat(3, 1fr);
    }
}
</style>
