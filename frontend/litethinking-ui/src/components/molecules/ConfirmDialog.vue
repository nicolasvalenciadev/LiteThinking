<template>
  <q-dialog v-model="isOpen" persistent>
    <q-card style="min-width: 380px; border-radius: 12px;">
      <q-card-section class="row items-center q-pb-none">
        <q-avatar icon="warning" color="amber-8" text-color="white" />
        <span class="q-ml-md text-weight-bold text-subtitle1">{{ title }}</span>
      </q-card-section>

      <q-card-section class="q-py-md text-body2 text-grey-8">
        {{ message }}
      </q-card-section>

      <q-card-actions align="right" class="q-px-md q-pb-md">
        <q-btn flat label="Cancelar" color="grey-7" @click="onCancel" />
        <q-btn
          unelevated
          label="Confirmar"
          color="negative"
          class="q-px-sm"
          @click="onConfirm"
        />
      </q-card-actions>
    </q-card>
  </q-dialog>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  modelValue: { type: Boolean, required: true },
  title: { type: String, default: 'Confirmar acción' },
  message: { type: String, default: '¿Está seguro de realizar esta operación?' }
})

const emit = defineEmits(['update:modelValue', 'confirm', 'cancel'])

const isOpen = computed({
  get: () => props.modelValue,
  set: (val) => emit('update:modelValue', val)
})

function onConfirm() {
  isOpen.value = false
  emit('confirm')
}

function onCancel() {
  isOpen.value = false
  emit('cancel')
}
</script>
