<template>
  <ion-page>
    <ion-content :fullscreen="true" class="detail-content">
      <div class="page-layout">
        <Sidebar active="sns"/>
        <div class="main-wrap">
          <TopBar title="게시글" @back="router.back()"/>
          <PostDetailContent
            :post-idx="Number(route.params.idx)"
            :feed-posts="feedPosts"
            @go-back="router.back()"
            @go-post="(idx) => router.push({ name: 'PostDetail', params: { idx } })"
            @go-place="(placeIdx) => router.push({ name: 'Map', query: { placeId: placeIdx } })"
          />
        </div>
      </div>
    </ion-content>
  </ion-page>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { IonPage, IonContent, useIonRouter } from '@ionic/vue';
import { useRoute } from 'vue-router';
import Sidebar from '@/components/common/Sidebar.vue';
import TopBar from '@/components/common/TopBar.vue';
import PostDetailContent from '@/components/post/PostDetailContent.vue';
import { postApi } from '@/api/postApi';

const router = useIonRouter();
const route = useRoute();
const feedPosts = ref<any[]>([]);

onMounted(async () => {
  if (window.innerWidth < 1024) {
    try { feedPosts.value = await postApi.getFeed(); } catch {}
  }
});
</script>

<style scoped>
.detail-content { --background: #f1f5f9; }
.page-layout { display: flex; min-height: 100%; background: #f1f5f9; }
.main-wrap { flex: 1; min-width: 0; display: flex; flex-direction: column; }
</style>
