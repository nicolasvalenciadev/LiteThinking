<template>
  <q-input
    :model-value="modelValue"
    :placeholder="placeholder"
    outlined
    dense
    clearable
    @update:model-value="$emit('update:modelValue', $event)"
  >
    <template #prepend>
      <q-icon name="search" />
    </template>
  </q-input>
</template>

<script setup>
defineProps({
  modelValue: { type: String, default: '' },
  placeholder: { type: String, default: 'Buscar...' }
})

defineEmits(['update:modelValue'])
</script>
