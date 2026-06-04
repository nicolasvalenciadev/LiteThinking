<template>
  <q-layout view="lHh Lpr lFf" class="auth-layout">
    <q-page-container class="window-height window-width row justify-center items-center">
      <router-view />
    </q-page-container>
  </q-layout>
</template>

<script setup>
// Auth layout host page
</script>

<style scoped>
.auth-layout {
  background: linear-gradient(135deg, #eef2f3 0%, #8e9eab 100%);
  min-height: 100vh;
}
</style>
