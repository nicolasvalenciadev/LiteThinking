<template>
  <q-card style="width: 500px; max-width: 95vw; border-radius: 12px;">
    <q-card-section class="bg-primary text-white row items-center q-pb-md">
      <div class="text-h6 text-weight-bold">
        {{ isEdit ? 'Editar Empresa' : 'Crear Empresa' }}
      </div>
      <q-space />
      <q-btn icon="close" flat round dense aria-label="Cerrar" v-close-popup />
    </q-card-section>

    <q-form @submit.prevent="onSubmit">
      <q-card-section class="q-gutter-sm q-pt-lg">
        <form-field
          v-model="form.nit"
          label="NIT *"
          placeholder="Ej: 900123456-1"
          :disable="isEdit"
          :rules="[val => (val && val.trim().length > 0) || 'El NIT es obligatorio',
                   val => !val || val.trim().length <= 20 || 'El NIT no puede superar 20 caracteres']"
        />
        <form-field
          v-model="form.name"
          label="Nombre de la empresa *"
          placeholder="Ej: LiteThinking S.A.S"
          :rules="[val => (val && val.trim().length > 0) || 'El nombre de la empresa es obligatorio']"
        />
        <form-field
          v-model="form.address"
          label="Dirección"
          placeholder="Ej: Calle 100 # 15-20"
        />
        <form-field
          v-model="form.telephone"
          label="Teléfono"
          placeholder="Ej: +57 601 123 4567"
        />
      </q-card-section>

      <q-card-actions align="right" class="q-pb-md q-px-md">
        <q-btn flat label="Cancelar" color="grey-7" v-close-popup />
        <q-btn
          unelevated
          label="Guardar"
          color="primary"
          type="submit"
          :loading="loading"
          class="q-px-md"
        />
      </q-card-actions>
    </q-form>
  </q-card>
</template>

<script setup>
import { ref, watch, computed } from 'vue'
import FormField from '../molecules/FormField.vue'

const props = defineProps({
  company: { type: Object, default: null },
  loading: { type: Boolean, default: false }
})

const emit = defineEmits(['save'])

const isEdit = computed(() => !!props.company)

const form = ref({ nit: '', name: '', address: '', telephone: '' })

watch(
  () => props.company,
  (newVal) => {
    form.value = newVal
      ? { id: newVal.id, nit: newVal.nit || '', name: newVal.name || '', address: newVal.address || '', telephone: newVal.telephone || '' }
      : { nit: '', name: '', address: '', telephone: '' }
  },
  { immediate: true }
)

function onSubmit() {
  emit('save', { ...form.value })
}
</script>
