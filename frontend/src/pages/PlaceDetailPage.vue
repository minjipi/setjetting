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
                        <span class="brand-text">'{{ place?.name }}'</span> 성지 정보
                    </h2>
                </div>

                <!-- 카카오 지도 -->
                <MapSection/>

                <!-- 본문 -->
                <div class="detail-body">

                    <!-- 장소 정보 (좌측) -->
                    <section class="left-section">
                        <h3 class="section-heading">
                            <ion-icon name="location-outline" class="section-icon"/>
                            장소 정보
                        </h3>

                        <div class="place-card">
                            <div class="place-poster">
                                <img v-if="place?.placeImageUrl" :src="place.placeImageUrl" :alt="place.name" class="poster-img"/>
                                <div v-else class="poster-placeholder">📍</div>
                            </div>
                            <div class="place-meta">
                                <h3 class="place-title">{{ place?.name }}</h3>
                                <p class="place-english">{{ place?.englishName }}</p>
                                <p class="place-address">
                                    <ion-icon name="location-outline" class="addr-icon"/>
                                    {{ place?.address }}
                                </p>
                                <button class="map-btn">
                                    <ion-icon name="navigate-outline"/>
                                    지도에서 보기
                                </button>
                            </div>
                        </div>
                    </section>

                    <!-- 관련 작품 (우측) -->
                    <section class="right-section">
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
                                @click="router.push({ name: 'ContentDetail', params: { id: item.contentIdx }, state: { content: item } })"
                            >
                                <div class="content-poster">
                                    <img v-if="item.posterImageUrl" :src="item.posterImageUrl" :alt="item.title" class="poster-img"/>
                                    <div v-else class="poster-placeholder">🎬</div>
                                    <span class="match-badge">
                                        <ion-icon name="checkmark-circle-outline"/>
                                        성지 매칭
                                    </span>
                                </div>
                                <p class="content-name">{{ item.title }}</p>
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
    arrowBackOutline, locationOutline, filmOutline,
    navigateOutline, checkmarkCircleOutline
} from 'ionicons/icons';
import { contentsApi } from '@/api/contentsApi';
import { placeApi } from '@/api/placeApi';
import MapSection from "@/components/home/MapSection.vue";

addIcons({
    'arrow-back-outline': arrowBackOutline,
    'location-outline': locationOutline,
    'film-outline': filmOutline,
    'navigate-outline': navigateOutline,
    'checkmark-circle-outline': checkmarkCircleOutline,
});

const router = useIonRouter();
const route = useRoute();

const placeId = computed(() => Number(route.params.id));
const place = ref<any>(history.state?.place ?? null);
const allContents = ref<any[]>([]);

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
    }
    if (Array.isArray(placeList) && !place.value) {
        place.value = placeList.find((p: any) => p.placeIdx === placeId.value) ?? null;
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
}

.section-heading {
    display: flex;
    align-items: center;
    gap: 6px;
    font-size: 14px;
    font-weight: 700;
    color: #1f2937;
    margin: 0 0 16px;
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

/* ── 장소 정보 ── */
.left-section {
    padding: 20px 16px;
    border-bottom: 1px solid #f3f4f6;
}

.place-card {
    display: flex;
    gap: 16px;
}

.place-poster {
    width: 120px;
    flex-shrink: 0;
    aspect-ratio: 1;
    border-radius: 14px;
    overflow: hidden;
    background: #f3f4f6;
    border: 1px solid #e5e7eb;
    box-shadow: 0 3px 10px rgba(0, 0, 0, 0.1);
}

.place-poster .poster-img {
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
    font-size: 36px;
}

.place-meta {
    flex: 1;
    min-width: 0;
    display: flex;
    flex-direction: column;
    gap: 6px;
}

.place-title {
    font-size: 18px;
    font-weight: 800;
    color: #111;
    margin: 0;
    line-height: 1.3;
}

.place-english {
    font-size: 12px;
    color: #6b7280;
    margin: 0;
}

.place-address {
    display: flex;
    align-items: flex-start;
    gap: 4px;
    font-size: 12px;
    color: #4b5563;
    margin: 0;
    line-height: 1.5;
}

.addr-icon {
    font-size: 13px;
    color: var(--brand, #14BCED);
    flex-shrink: 0;
    margin-top: 1px;
}

.map-btn {
    display: flex;
    align-items: center;
    gap: 6px;
    margin-top: 4px;
    padding: 8px 14px;
    background: var(--brand, #14BCED);
    color: #fff;
    border: none;
    border-radius: 20px;
    font-size: 12px;
    font-weight: 700;
    cursor: pointer;
    width: fit-content;
    transition: background 0.15s;
}

.map-btn:hover {
    background: #0fa8d4;
}

/* ── 관련 작품 ── */
.right-section {
    padding: 20px 16px;
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

.content-poster {
    aspect-ratio: 2 / 3;
    border-radius: 10px;
    overflow: hidden;
    background: #f3f4f6;
    border: 1px solid #e5e7eb;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
    position: relative;
    transition: box-shadow 0.2s;
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

.match-badge {
    position: absolute;
    bottom: 6px;
    left: 50%;
    transform: translateX(-50%);
    display: flex;
    align-items: center;
    gap: 3px;
    font-size: 9px;
    font-weight: 700;
    color: #fff;
    background: rgba(20, 188, 237, 0.9);
    padding: 2px 7px;
    border-radius: 99px;
    white-space: nowrap;
    backdrop-filter: blur(4px);
}

.match-badge ion-icon {
    font-size: 10px;
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

    .place-poster {
        width: 160px;
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
        grid-template-columns: repeat(4, 1fr);
    }
}
</style>
