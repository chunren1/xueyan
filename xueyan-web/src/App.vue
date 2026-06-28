<script setup lang="ts">
import NavBar from '@/components/NavBar.vue'
</script>

<template>
  <NavBar />
  <main class="main-container">
    <router-view />
  </main>
</template>

<style>
body {
  margin: 0;
  font-family: 'Helvetica Neue', Arial, sans-serif;
  background: #f5f7fa;
}
.main-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 24px 16px;
  min-height: calc(100vh - 60px);
}
</style>
