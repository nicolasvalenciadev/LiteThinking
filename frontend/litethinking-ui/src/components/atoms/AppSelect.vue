<template>
  <q-select
    v-bind="$attrs"
    :model-value="modelValue"
    :options="options"
    :label="label"
    :rules="rules"
    outlined
    dense
    emit-value
    map-options
    lazy-rules
    @update:model-value="$emit('update:modelValue', $event)"
  />
</template>

<script setup>
defineOptions({ inheritAttrs: false })

defineProps({
  modelValue: { type: [String, Number, Object], default: null },
  options: { type: Array, default: () => [] },
  label: { type: String, default: '' },
  rules: { type: Array, default: () => [] }
})

defineEmits(['update:modelValue'])
</script>
