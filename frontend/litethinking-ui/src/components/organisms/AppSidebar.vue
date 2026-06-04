<template>
  <q-drawer
    :model-value="modelValue"
    show-if-above
    bordered
    class="bg-grey-1"
    @update:model-value="$emit('update:modelValue', $event)"
  >
    <q-list>
      <q-item-label header class="text-weight-bold text-uppercase text-grey-7 q-pt-md">
        Menú de Navegación
      </q-item-label>

      <q-item
        clickable
        v-ripple
        to="/companies"
        active-class="bg-blue-1 text-primary text-weight-bold"
      >
        <q-item-section avatar>
          <q-icon name="corporate_fare" />
        </q-item-section>
        <q-item-section>
          <q-item-label>Empresas</q-item-label>
          <q-item-label caption>Gestión de empresas</q-item-label>
        </q-item-section>
      </q-item>

      <template v-if="isAdmin">
        <q-item
          clickable
          v-ripple
          to="/products"
          active-class="bg-blue-1 text-primary text-weight-bold"
        >
          <q-item-section avatar>
            <q-icon name="inventory_2" />
          </q-item-section>
          <q-item-section>
            <q-item-label>Productos</q-item-label>
            <q-item-label caption>Catálogo de artículos</q-item-label>
          </q-item-section>
        </q-item>

        <q-item
          clickable
          v-ripple
          to="/inventory"
          active-class="bg-blue-1 text-primary text-weight-bold"
        >
          <q-item-section avatar>
            <q-icon name="assessment" />
          </q-item-section>
          <q-item-section>
            <q-item-label>Inventario</q-item-label>
            <q-item-label caption>Reportes y Exportaciones</q-item-label>
          </q-item-section>
        </q-item>
      </template>
    </q-list>
  </q-drawer>
</template>

<script setup>
import { computed } from 'vue'
import { useAuthStore } from '../../stores/auth.store.js'

defineProps({
  modelValue: { type: Boolean, default: false }
})

defineEmits(['update:modelValue'])

const authStore = useAuthStore()
const isAdmin = computed(() => authStore.isAdmin)
</script>
