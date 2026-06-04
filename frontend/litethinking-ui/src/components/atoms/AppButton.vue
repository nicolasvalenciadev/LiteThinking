<template>
  <q-btn
    v-bind="$attrs"
    :color="color"
    :label="label"
    :icon="icon"
    :loading="loading"
    :disable="disable"
    :aria-label="ariaLabel || label"
  />
</template>

<script setup>
defineOptions({ inheritAttrs: false })

defineProps({
  color: { type: String, default: 'primary' },
  label: { type: String, default: '' },
  icon: { type: String, default: undefined },
  loading: { type: Boolean, default: false },
  disable: { type: Boolean, default: false },
  ariaLabel: { type: String, default: '' }
})
</script>
