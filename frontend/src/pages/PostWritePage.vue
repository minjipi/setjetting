<template>
    <ion-page>
        <!-- Header -->
        <div class="page-header">
            <button class="back-btn" @click="router.back()">
                <ion-icon name="arrow-back-outline"/>
            </button>
            <span class="page-title">새 게시글</span>
            <button class="submit-btn" :disabled="!canSubmit || submitting" @click="submit">
                {{ submitting ? '저장 중...' : '게시' }}
            </button>
        </div>

        <ion-content class="page-content">
            <div class="form-body">
                <!-- Main Photo (camera only) -->
                <div class="section">
                    <p class="section-label">대표 사진 <span class="required">*</span></p>
                    <div class="main-photo-wrap" @click="triggerMainCamera">
                        <img v-if="mainImageUrl" :src="resolveImageUrl(mainImageUrl)" class="main-preview" alt="대표 사진"/>
                        <div v-else class="main-placeholder">
                            <ion-icon name="camera-outline" class="placeholder-icon"/>
                            <span>카메라로 촬영</span>
                        </div>
                        <div v-if="mainUploading" class="upload-overlay">
                            <div class="spinner"/>
                        </div>
                    </div>
                </div>

                <!-- Additional Photos -->
                <div class="section">
                    <p class="section-label">추가 사진 <span class="optional">(선택)</span></p>
                    <div class="extra-photos-row">
                        <div v-for="(img, i) in extraImages" :key="i" class="extra-thumb-wrap">
                            <img :src="resolveImageUrl(img.url)" class="extra-thumb" alt="추가 사진"/>
                            <div v-if="img.uploading" class="thumb-overlay"><div class="spinner-sm"/></div>
                            <button class="thumb-remove" @click="removeExtra(i)">
                                <ion-icon name="close-circle"/>
                            </button>
                        </div>
                        <button class="add-extra-btn" @click="triggerExtraInput">
                            <ion-icon name="add-outline"/>
                        </button>
                    </div>
                    <input ref="extraInput" type="file" accept="image/*" multiple
                           class="hidden-input" @change="onExtraImagesSelected"/>
                </div>

                <!-- Place Search -->
                <div class="section">
                    <p class="section-label">장소 <span class="required">*</span></p>
                    <div class="place-search-wrap">
                        <ion-icon name="location-outline" class="place-icon"/>
                        <input
                            v-model="placeQuery"
                            class="place-input"
                            placeholder="촬영 장소 검색..."
                            @input="onPlaceSearch"
                            @focus="showPlaceResults = true"
                        />
                        <button v-if="selectedPlace" class="place-clear" @click="clearPlace">
                            <ion-icon name="close-circle-outline"/>
                        </button>
                    </div>
                    <div v-if="showPlaceResults && placeResults.length" class="place-results">
                        <button
                            v-for="p in placeResults"
                            :key="p.placeIdx"
                            class="place-result-item"
                            @click="selectPlace(p)"
                        >
                            <span class="place-result-name">{{ p.name }}</span>
                            <span class="place-result-addr">{{ p.address }}</span>
                        </button>
                    </div>
                </div>

                <!-- Title -->
                <div class="section">
                    <p class="section-label">제목 <span class="required">*</span></p>
                    <input v-model="title" class="text-input" placeholder="게시글 제목을 입력하세요" maxlength="100"/>
                </div>

                <!-- Description -->
                <div class="section">
                    <p class="section-label">내용</p>
                    <textarea v-model="description" class="text-area" placeholder="촬영지 방문 후기를 남겨주세요..." rows="5"/>
                </div>
            </div>
        </ion-content>
    </ion-page>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue';
import { IonPage, IonContent, IonIcon, useIonRouter, onIonViewDidEnter } from '@ionic/vue';
import { addIcons } from 'ionicons';
import {
    arrowBackOutline, cameraOutline, addOutline,
    locationOutline, closeCircleOutline, closeCircle,
} from 'ionicons/icons';
import { postApi } from '@/api/postApi';
import { placeApi } from '@/api/placeApi';
import { useAuth } from '@/composables/useAuth';
import { useCaptureBuffer } from '@/composables/useCaptureBuffer';
import { resolveImageUrl } from '@/utils/imageUrl';

addIcons({
    'arrow-back-outline': arrowBackOutline,
    'camera-outline': cameraOutline,
    'add-outline': addOutline,
    'location-outline': locationOutline,
    'close-circle-outline': closeCircleOutline,
    'close-circle': closeCircle,
});

const router = useIonRouter();
const { user } = useAuth();
const { consume: consumePhoto } = useCaptureBuffer();

const extraInput = ref<HTMLInputElement | null>(null);

const mainImageUrl = ref('');
const mainUploading = ref(false);

interface ExtraImage { url: string; uploading: boolean }
const extraImages = ref<ExtraImage[]>([]);

const placeQuery = ref('');
const placeResults = ref<any[]>([]);
const selectedPlace = ref<any | null>(null);
const showPlaceResults = ref(false);
let allPlaces: any[] = [];
let searchTimer: ReturnType<typeof setTimeout> | null = null;

const title = ref('');
const description = ref('');
const submitting = ref(false);

const canSubmit = computed(() =>
    !!mainImageUrl.value && !!selectedPlace.value && !!title.value.trim()
);

onMounted(async () => {
    try {
        const list = await placeApi.getList();
        allPlaces = list ?? [];
    } catch { /* ignore */ }
});

function onPlaceSearch() {
    selectedPlace.value = null;
    if (searchTimer) clearTimeout(searchTimer);
    searchTimer = setTimeout(() => {
        const q = placeQuery.value.trim().toLowerCase();
        if (!q) { placeResults.value = []; return; }
        placeResults.value = allPlaces
            .filter(p => p.name?.toLowerCase().includes(q) || p.address?.toLowerCase().includes(q))
            .slice(0, 8);
    }, 200);
}

function selectPlace(p: any) {
    selectedPlace.value = p;
    placeQuery.value = p.name;
    placeResults.value = [];
    showPlaceResults.value = false;
}

function clearPlace() {
    selectedPlace.value = null;
    placeQuery.value = '';
}

function triggerMainCamera() {
    router.push('/camera?for=post');
}

async function uploadDataUrl(dataUrl: string) {
    mainUploading.value = true;
    try {
        const res = await fetch(dataUrl);
        const blob = await res.blob();
        const file = new File([blob], 'photo.jpg', { type: 'image/jpeg' });
        mainImageUrl.value = await postApi.uploadImage(file);
    } catch { alert('이미지 업로드에 실패했습니다.'); }
    finally { mainUploading.value = false; }
}

onIonViewDidEnter(() => {
    const photo = consumePhoto();
    if (photo) uploadDataUrl(photo.dataUrl);
});

function triggerExtraInput() {
    extraInput.value?.click();
}

async function onExtraImagesSelected(e: Event) {
    const files = Array.from((e.target as HTMLInputElement).files ?? []);
    if (!files.length) return;
    const startIdx = extraImages.value.length;
    const placeholders = files.map(() => ({ url: '', uploading: true }));
    extraImages.value.push(...placeholders);

    await Promise.all(files.map(async (file, i) => {
        try {
            const url = await postApi.uploadImage(file);
            extraImages.value[startIdx + i] = { url, uploading: false };
        } catch {
            extraImages.value.splice(startIdx + i, 1);
        }
    }));
    (e.target as HTMLInputElement).value = '';
}

function removeExtra(i: number) {
    extraImages.value.splice(i, 1);
}

async function submit() {
    if (!canSubmit.value || submitting.value) return;
    submitting.value = true;
    try {
        const images = [
            { imageUrl: mainImageUrl.value, description: '' },
            ...extraImages.value.filter(i => i.url).map(i => ({ imageUrl: i.url, description: '' })),
        ];
        await postApi.createPost({
            userIdx: user.value?.idx,
            title: title.value.trim(),
            description: description.value.trim(),
            placeIdx: selectedPlace.value?.placeIdx ?? null,
            images,
        });
        router.back();
    } catch { alert('게시글 저장에 실패했습니다.'); }
    finally { submitting.value = false; }
}
</script>

<style scoped>
.page-header {
    display: flex;
    align-items: center;
    padding: 12px 16px;
    background: #fff;
    border-bottom: 1px solid rgba(20, 188, 237, 0.1);
    gap: 8px;
    position: sticky;
    top: 0;
    z-index: 10;
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

.page-title {
    flex: 1;
    font-size: 16px;
    font-weight: 700;
    color: #1f2937;
}

.submit-btn {
    background: var(--brand, #14BCED);
    color: #fff;
    border: none;
    border-radius: 20px;
    padding: 8px 20px;
    font-size: 14px;
    font-weight: 700;
    cursor: pointer;
    transition: background 0.15s;
    white-space: nowrap;
}

.submit-btn:disabled {
    background: #d1d5db;
    cursor: not-allowed;
}

.submit-btn:not(:disabled):hover { background: #0fa8d4; }

.page-content {
    --background: #f9fafb;
}

.form-body {
    padding: 20px 16px 40px;
    display: flex;
    flex-direction: column;
    gap: 24px;
    max-width: 600px;
    margin: 0 auto;
}

.section-label {
    font-size: 13px;
    font-weight: 600;
    color: #374151;
    margin: 0 0 8px;
}

.required { color: #ef4444; }
.optional { font-weight: 400; color: #9ca3af; }

/* Main photo */
.main-photo-wrap {
    position: relative;
    width: 100%;
    aspect-ratio: 4/3;
    border-radius: 14px;
    overflow: hidden;
    background: #f3f4f6;
    cursor: pointer;
    border: 2px dashed #d1d5db;
    display: flex;
    align-items: center;
    justify-content: center;
    transition: border-color 0.15s;
}

.main-photo-wrap:has(img) {
    border-style: solid;
    border-color: transparent;
}

.main-photo-wrap:not(:has(img)):hover {
    border-color: var(--brand, #14BCED);
}

.main-preview {
    width: 100%;
    height: 100%;
    object-fit: cover;
}

.main-placeholder {
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: 10px;
    color: #9ca3af;
    font-size: 13px;
}

.placeholder-icon { font-size: 40px; }

.upload-overlay {
    position: absolute;
    inset: 0;
    background: rgba(0,0,0,0.45);
    display: flex;
    align-items: center;
    justify-content: center;
}

/* Extra photos */
.extra-photos-row {
    display: flex;
    gap: 10px;
    flex-wrap: wrap;
}

.extra-thumb-wrap {
    position: relative;
    width: 84px;
    height: 84px;
    border-radius: 10px;
    overflow: visible;
}

.extra-thumb {
    width: 84px;
    height: 84px;
    object-fit: cover;
    border-radius: 10px;
}

.thumb-overlay {
    position: absolute;
    inset: 0;
    background: rgba(0,0,0,0.45);
    border-radius: 10px;
    display: flex;
    align-items: center;
    justify-content: center;
}

.thumb-remove {
    position: absolute;
    top: -7px;
    right: -7px;
    background: none;
    border: none;
    font-size: 22px;
    color: #ef4444;
    cursor: pointer;
    padding: 0;
    line-height: 1;
}

.add-extra-btn {
    width: 84px;
    height: 84px;
    border-radius: 10px;
    border: 2px dashed #d1d5db;
    background: #fff;
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 28px;
    color: #9ca3af;
    cursor: pointer;
    flex-shrink: 0;
    transition: border-color 0.15s, color 0.15s;
}

.add-extra-btn:hover {
    border-color: var(--brand, #14BCED);
    color: var(--brand, #14BCED);
}

/* Place search */
.place-search-wrap {
    display: flex;
    align-items: center;
    gap: 8px;
    border: 1px solid #e5e7eb;
    border-radius: 10px;
    padding: 11px 14px;
    background: #fff;
    transition: border-color 0.15s;
}

.place-search-wrap:focus-within {
    border-color: var(--brand, #14BCED);
}

.place-icon {
    font-size: 18px;
    color: var(--brand, #14BCED);
    flex-shrink: 0;
}

.place-input {
    flex: 1;
    background: transparent;
    border: none;
    outline: none;
    font-size: 14px;
    color: #1f2937;
}

.place-input::placeholder { color: #9ca3af; }

.place-clear {
    background: none;
    border: none;
    font-size: 18px;
    color: #9ca3af;
    cursor: pointer;
    display: flex;
    align-items: center;
    padding: 0;
}

.place-results {
    border: 1px solid #e5e7eb;
    border-radius: 10px;
    margin-top: 4px;
    overflow: hidden;
    background: #fff;
    box-shadow: 0 4px 16px rgba(0,0,0,0.08);
}

.place-result-item {
    width: 100%;
    display: flex;
    flex-direction: column;
    align-items: flex-start;
    padding: 11px 14px;
    border: none;
    background: none;
    cursor: pointer;
    text-align: left;
    border-bottom: 1px solid #f3f4f6;
    transition: background 0.1s;
}

.place-result-item:last-child { border-bottom: none; }
.place-result-item:hover { background: #f0fbff; }

.place-result-name {
    font-size: 14px;
    font-weight: 600;
    color: #1f2937;
}

.place-result-addr {
    font-size: 12px;
    color: #6b7280;
    margin-top: 2px;
}

/* Text inputs */
.text-input {
    width: 100%;
    border: 1px solid #e5e7eb;
    border-radius: 10px;
    padding: 11px 14px;
    font-size: 14px;
    color: #1f2937;
    background: #fff;
    outline: none;
    box-sizing: border-box;
    transition: border-color 0.15s;
}

.text-input:focus { border-color: var(--brand, #14BCED); }

.text-area {
    width: 100%;
    border: 1px solid #e5e7eb;
    border-radius: 10px;
    padding: 11px 14px;
    font-size: 14px;
    color: #1f2937;
    background: #fff;
    outline: none;
    resize: none;
    box-sizing: border-box;
    font-family: inherit;
    transition: border-color 0.15s;
}

.text-area:focus { border-color: var(--brand, #14BCED); }

/* Spinners */
.spinner {
    width: 30px;
    height: 30px;
    border: 3px solid rgba(255,255,255,0.4);
    border-top-color: #fff;
    border-radius: 50%;
    animation: spin 0.7s linear infinite;
}

.spinner-sm {
    width: 18px;
    height: 18px;
    border: 2px solid rgba(255,255,255,0.4);
    border-top-color: #fff;
    border-radius: 50%;
    animation: spin 0.7s linear infinite;
}

@keyframes spin { to { transform: rotate(360deg); } }

.hidden-input { display: none; }
</style>
