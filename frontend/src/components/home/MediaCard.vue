<template>
  <div class="media-card" @click="$emit('click')">
    <div class="poster" :style="{ background: item.gradient }">
      <span class="poster-emoji">{{ item.emoji }}</span>
      <span v-if="item.badge" class="type-badge">{{ item.badge }}</span>
    </div>
    <p class="media-title">{{ item.title }}</p>
    <p v-if="item.spots" class="spot-count">
      <ion-icon name="location-outline" class="loc-icon" />
      {{ item.spots }}곳
    </p>
  </div>
</template>

<script setup lang="ts">
import { IonIcon } from '@ionic/vue';
import { addIcons } from 'ionicons';
import { locationOutline } from 'ionicons/icons';

addIcons({ 'location-outline': locationOutline });

defineProps<{
  item: {
    title: string;
    emoji: string;
    gradient: string;
    badge?: string;
    spots?: number;
  };
}>();
defineEmits(['click']);
</script>

<style scoped>
.media-card {
  cursor: pointer;
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.poster {
  width: 100%;
  aspect-ratio: 2 / 3;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  overflow: hidden;
  box-shadow: 0 3px 10px rgba(0,0,0,0.12);
}

.poster-emoji {
  font-size: 34px;
}

.type-badge {
  position: absolute;
  top: 6px;
  left: 6px;
  background: rgba(20, 188, 237, 0.9);
  color: #fff;
  font-size: 9px;
  font-weight: 700;
  padding: 2px 7px;
  border-radius: 6px;
  letter-spacing: 0.3px;
  backdrop-filter: blur(4px);
}

.media-title {
  font-size: 11.5px;
  font-weight: 600;
  color: var(--text-primary);
  margin: 0;
  line-height: 1.4;
  overflow: hidden;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.spot-count {
  display: flex;
  align-items: center;
  gap: 2px;
  font-size: 10px;
  color: var(--brand);
  font-weight: 600;
  margin: 0;
}

.loc-icon {
  font-size: 10px;
}
</style>
