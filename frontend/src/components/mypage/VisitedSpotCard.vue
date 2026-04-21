<template>
  <div class="spot-card" @click="$emit('goPost', spot)">
    <div class="spot-thumb" :style="spot.imageUrl ? {} : { background: spot.gradient }">
      <img v-if="spot.imageUrl" :src="spot.imageUrl" class="spot-img" :alt="spot.spotName"/>
      <span v-else class="spot-emoji">{{ spot.emoji }}</span>
      <span v-if="spot.mediaType" class="spot-badge">{{ spot.mediaType }}</span>
    </div>
    <div class="spot-info">
      <p class="spot-media">{{ spot.mediaTitle }}</p>
      <div class="spot-name-row">
        <p class="spot-name">{{ spot.spotName }}</p>
        <button
          v-if="spot.id"
          class="pin-btn"
          @click.stop="$emit('goPlace', spot)"
          aria-label="성지 지도에서 보기"
        >
          <ion-icon name="location"/>
        </button>
      </div>
      <div class="spot-meta">
        <span class="spot-date">{{ spot.visitedAt }}</span>
        <span class="spot-photo-count" v-if="spot.photoCount">
          <ion-icon name="camera-outline"/> {{ spot.photoCount }}
        </span>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { IonIcon } from '@ionic/vue';
import { addIcons } from 'ionicons';
import { cameraOutline, location } from 'ionicons/icons';

addIcons({ 'camera-outline': cameraOutline, 'location': location });

defineProps<{
  spot: {
    id: number;
    postIdx?: number;
    mediaTitle: string;
    mediaType: string;
    spotName: string;
    visitedAt: string;
    emoji?: string;
    gradient?: string;
    imageUrl?: string;
    photoCount?: number;
  };
}>();
defineEmits(['goPlace', 'goPost']);
</script>

<style scoped>
.spot-card {
  background: #fff;
  border-radius: 14px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  transition: transform 0.15s, box-shadow 0.15s;
  display: flex;
  flex-direction: column;
  cursor: pointer;
}

.spot-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(20, 188, 237, 0.15);
}

.spot-thumb {
  position: relative;
  aspect-ratio: 3 / 2;
  display: flex;
  align-items: center;
  justify-content: center;
}

.spot-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  position: absolute;
  inset: 0;
}

.spot-emoji {
  font-size: 32px;
}

.spot-badge {
  position: absolute;
  top: 8px;
  left: 8px;
  background: #14BCED;
  color: #fff;
  font-size: 10px;
  font-weight: 700;
  padding: 2px 7px;
  border-radius: 20px;
}

.spot-info {
  padding: 10px 12px 12px;
  flex: 1;
}

.spot-media {
  font-size: 11px;
  color: #14BCED;
  font-weight: 600;
  margin: 0 0 2px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.spot-name-row {
  display: flex;
  align-items: center;
  gap: 4px;
  margin: 0 0 6px;
}

.spot-name {
  font-size: 13px;
  font-weight: 700;
  color: #111827;
  margin: 0;
  flex: 1;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.pin-btn {
  flex-shrink: 0;
  width: 24px;
  height: 24px;
  border-radius: 50%;
  border: none;
  background: rgba(20, 188, 237, 0.12);
  color: #14BCED;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 13px;
  cursor: pointer;
  transition: background 0.15s;
}

.pin-btn:hover {
  background: rgba(20, 188, 237, 0.25);
}

.spot-meta {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.spot-date {
  font-size: 11px;
  color: #9ca3af;
}

.spot-photo-count {
  font-size: 11px;
  color: #9ca3af;
  display: flex;
  align-items: center;
  gap: 3px;
}
</style>
