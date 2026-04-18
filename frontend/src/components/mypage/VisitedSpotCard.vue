<template>
  <div class="spot-card" @click="$emit('click', spot)">
    <div class="spot-thumb" :style="{ background: spot.gradient }">
      <span class="spot-emoji">{{ spot.emoji }}</span>
      <span class="spot-badge">{{ spot.mediaType }}</span>
    </div>
    <div class="spot-info">
      <p class="spot-media">{{ spot.mediaTitle }}</p>
      <p class="spot-name">{{ spot.spotName }}</p>
      <div class="spot-meta">
        <span class="spot-date">{{ spot.visitedAt }}</span>
        <span class="spot-photo-count" v-if="spot.photoCount">
          <ion-icon name="camera-outline" /> {{ spot.photoCount }}
        </span>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { IonIcon } from '@ionic/vue';
import { addIcons } from 'ionicons';
import { cameraOutline } from 'ionicons/icons';

addIcons({ 'camera-outline': cameraOutline });

defineProps<{
  spot: {
    id: number;
    mediaTitle: string;
    mediaType: string;
    spotName: string;
    visitedAt: string;
    emoji: string;
    gradient: string;
    photoCount?: number;
  };
}>();
defineEmits(['click']);
</script>

<style scoped>
.spot-card {
  background: #fff;
  border-radius: 14px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  cursor: pointer;
  transition: transform 0.15s, box-shadow 0.15s;
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

.spot-name {
  font-size: 13px;
  font-weight: 700;
  color: #111827;
  margin: 0 0 6px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
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
