<template>
  <q-page class="q-pa-md">
    <div class="row justify-between items-center q-mb-md">
      <div>
        <h1 class="text-h4 text-weight-bold text-primary q-my-none">{{ title }}</h1>
        <p class="text-subtitle2 text-grey-7 q-my-none">{{ subtitle }}</p>
      </div>
      <slot name="actions" />
    </div>

    <q-banner
      v-if="error"
      inline-actions
      class="text-white bg-red-6 q-mb-md rounded-borders"
    >
      {{ error }}
      <template #action>
        <q-btn flat color="white" label="Reintentar" @click="$emit('retry')" />
      </template>
    </q-banner>

    <slot />
  </q-page>
</template>

<script setup>
defineProps({
  title: { type: String, required: true },
  subtitle: { type: String, default: '' },
  error: { type: String, default: null }
})

defineEmits(['retry'])
</script>

<style scoped>
h1 { margin: 0; }
</style>
