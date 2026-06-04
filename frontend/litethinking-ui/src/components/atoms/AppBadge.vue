<template>
  <q-badge :color="color" class="text-weight-bold q-py-xs q-px-sm">
    <slot />
  </q-badge>
</template>

<script setup>
defineProps({
  color: { type: String, default: 'primary' }
})
</script>
