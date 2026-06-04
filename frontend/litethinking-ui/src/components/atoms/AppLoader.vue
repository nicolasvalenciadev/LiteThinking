<template>
  <q-inner-loading :showing="showing" :color="color">
    <q-spinner-dots :size="size" />
  </q-inner-loading>
</template>

<script setup>
defineProps({
  showing: { type: Boolean, default: false },
  color: { type: String, default: 'primary' },
  size: { type: String, default: '40px' }
})
</script>
