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

                    <!-- 관련 작품 (좌측) -->
                    <section class="left-section">
                        <h3 class="section-heading">
                            <ion-icon name="film-outline" class="section-icon"/>
                            관련 작품
                            <span class="count-badge">{{ allContents.length }}</span>
                        </h3>
                        <div class="content-grid">
                            <div
                                v-for="item in allContents"
                                :key="item.contentIdx"
                                class="content-card"
                                :class="{ active: item.contentIdx === contentId }"
                                @click="router.push({ name: 'ContentDetail', params: { id: item.contentIdx }, state: { content: item } })"
                            >
                                <div class="content-poster">
                                    <img v-if="item.posterImageUrl" :src="item.posterImageUrl" :alt="item.title" class="poster-img"/>
                                    <div v-else class="poster-placeholder">🎬</div>
                                </div>
                                <p class="content-name">{{ item.title }}</p>
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
import { contentsApi } from '@/api/contentsApi';
import { placeApi } from '@/api/placeApi';
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
const content = ref<any>(history.state?.content ?? null);
const allContents = ref<any[]>([]);
const places = ref<any[]>([]);

function goBack() {
    router.back();
}

async function loadData() {
    const [contents, placeList] = await Promise.all([
        contentsApi.getList(),
        placeApi.getList(),
    ]);
    if (Array.isArray(contents)) {
        allContents.value = contents;
        if (!content.value) {
            content.value = contents.find((c: any) => c.contentIdx === contentId.value) ?? null;
        }
    }
    if (Array.isArray(placeList)) {
        places.value = placeList;
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

/* ── 관련 작품 ── */
.left-section {
    padding: 20px 16px;
    border-bottom: 1px solid #f3f4f6;
}

.content-grid {
    display: grid;
    grid-template-columns: repeat(3, 1fr);
    gap: 10px;
}

.content-card {
    cursor: pointer;
    transition: transform 0.2s;
}

.content-card:hover {
    transform: translateY(-2px);
}

.content-card.active .content-poster {
    box-shadow: 0 0 0 2px var(--brand, #14BCED);
}

.content-poster {
    aspect-ratio: 2 / 3;
    border-radius: 10px;
    overflow: hidden;
    background: #f3f4f6;
    border: 1px solid #e5e7eb;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
    transition: box-shadow 0.2s, transform 0.3s;
}

.content-card:hover .content-poster {
    box-shadow: 0 6px 16px rgba(0, 0, 0, 0.14);
}

.content-poster .poster-img {
    width: 100%;
    height: 100%;
    object-fit: cover;
    transition: transform 0.5s;
}

.content-card:hover .poster-img {
    transform: scale(1.06);
}

.poster-placeholder {
    width: 100%;
    height: 100%;
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 28px;
}

.content-name {
    margin: 6px 0 0;
    font-size: 11px;
    font-weight: 600;
    color: #1f2937;
    line-height: 1.4;
    overflow: hidden;
    display: -webkit-box;
    -webkit-line-clamp: 2;
    -webkit-box-orient: vertical;
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
