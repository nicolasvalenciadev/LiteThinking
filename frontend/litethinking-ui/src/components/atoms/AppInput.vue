<template>
  <q-input
    v-bind="$attrs"
    :model-value="modelValue"
    :label="label"
    :rules="rules"
    :disable="disable"
    :type="type"
    outlined
    dense
    lazy-rules
    @update:model-value="$emit('update:modelValue', $event)"
  />
</template>

<script setup>
defineOptions({ inheritAttrs: false })

defineProps({
  modelValue: { type: [String, Number], default: '' },
  label: { type: String, default: '' },
  rules: { type: Array, default: () => [] },
  disable: { type: Boolean, default: false },
  type: { type: String, default: 'text' }
})

defineEmits(['update:modelValue'])
</script>
