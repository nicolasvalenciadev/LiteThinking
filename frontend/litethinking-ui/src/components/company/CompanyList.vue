<template>
  <q-table
    :rows="companies"
    :columns="columns"
    row-key="id"
    :loading="loading"
    flat
    bordered
    class="lt-card"
    no-data-label="No hay empresas registradas"
    no-results-label="No se encontraron registros coincidentes"
    :pagination="pagination"
  >
    <!-- Action Cell Custom Slot -->
    <template v-slot:body-cell-actions="props">
      <q-td :props="props" class="q-gutter-sm text-center">
        <q-btn
          dense
          round
          flat
          color="primary"
          icon="edit"
          @click="emit('edit', props.row)"
        >
          <q-tooltip>Editar Empresa</q-tooltip>
        </q-btn>
        <q-btn
          dense
          round
          flat
          color="negative"
          icon="delete"
          @click="emit('delete', props.row)"
        >
          <q-tooltip>Eliminar Empresa</q-tooltip>
        </q-btn>
      </q-td>
    </template>

    <!-- Custom Loading Spinner -->
    <template v-slot:loading>
      <q-inner-loading showing color="primary">
        <q-spinner-dots size="40px" />
      </q-inner-loading>
    </template>
  </q-table>
</template>

<script setup>
import { computed, ref } from 'vue'
import { useAuthStore } from '../../stores/auth.store.js'

const props = defineProps({
  companies: {
    type: Array,
    required: true
  },
  loading: {
    type: Boolean,
    default: false
  }
})

const emit = defineEmits(['edit', 'delete'])

const authStore = useAuthStore()
const isAdmin = computed(() => authStore.isAdmin)

const pagination = ref({
  rowsPerPage: 10,
  sortBy: 'name',
  descending: false
})

const columns = computed(() => {
  const baseCols = [
    { name: 'nit', align: 'left', label: 'NIT', field: 'nit', sortable: true },
    { name: 'name', align: 'left', label: 'Nombre de la Empresa', field: 'name', sortable: true },
    { name: 'address', align: 'left', label: 'Dirección', field: 'address', sortable: true },
    { name: 'telephone', align: 'left', label: 'Teléfono', field: 'telephone', sortable: true }
  ]

  if (isAdmin.value) {
    baseCols.push({
      name: 'actions',
      align: 'center',
      label: 'Acciones',
      field: 'actions'
    })
  }

  return baseCols
})
</script>
