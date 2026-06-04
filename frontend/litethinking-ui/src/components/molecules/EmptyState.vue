<template>
  <div class="column items-center q-pa-xl text-grey-6">
    <q-icon :name="icon" size="64px" class="q-mb-md" />
    <p class="text-subtitle1 text-center">{{ message }}</p>
  </div>
</template>

<script setup>
defineProps({
  icon: { type: String, default: 'inbox' },
  message: { type: String, default: 'No hay datos disponibles' }
})
</script>
