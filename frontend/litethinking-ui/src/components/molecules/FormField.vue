<template>
  <app-input
    :model-value="modelValue"
    :label="label"
    :rules="rules"
    :disable="disable"
    :type="type"
    :placeholder="placeholder"
    @update:model-value="$emit('update:modelValue', $event)"
  />
</template>

<script setup>
import AppInput from '../atoms/AppInput.vue'

defineProps({
  modelValue: { type: [String, Number], default: '' },
  label: { type: String, required: true },
  rules: { type: Array, default: () => [] },
  disable: { type: Boolean, default: false },
  type: { type: String, default: 'text' },
  placeholder: { type: String, default: '' }
})

defineEmits(['update:modelValue'])
</script>
