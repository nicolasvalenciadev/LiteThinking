<template>
  <q-table
    :rows="products"
    :columns="columns"
    row-key="id"
    :loading="loading"
    flat
    bordered
    class="lt-card"
    no-data-label="No hay productos registrados"
    no-results-label="No se encontraron registros coincidentes"
    :pagination="pagination"
  >
    <!-- Custom Company Cell -->
    <template v-slot:body-cell-company="props">
      <q-td :props="props">
        {{ props.row.company?.name || getCompanyName(props.row.companyId) || 'N/A' }}
      </q-td>
    </template>

    <!-- Custom Categories Cell -->
    <template v-slot:body-cell-categories="props">
      <q-td :props="props">
        <div class="row q-gutter-xs">
          <q-badge
            v-for="cat in props.row.categories"
            :key="cat"
            color="blue-2"
            text-color="primary"
            class="text-weight-bold"
          >
            {{ cat }}
          </q-badge>
          <span v-if="!props.row.categories || props.row.categories.length === 0" class="text-grey-5 text-italic text-caption">
            Sin categorías
          </span>
        </div>
      </q-td>
    </template>

    <!-- Custom Prices Cell -->
    <template v-slot:body-cell-prices="props">
      <q-td :props="props">
        <div class="column q-gutter-xs">
          <div
            v-for="price in props.row.prices"
            :key="price.currency"
            class="row items-center justify-between"
            style="min-width: 140px;"
          >
            <q-badge color="grey-3" text-color="grey-9" class="q-mr-xs text-weight-bold">
              {{ price.currency }}
            </q-badge>
            <span class="text-weight-bold text-grey-8">{{ formatPrice(price.price, price.currency) }}</span>
          </div>
          <span v-if="!props.row.prices || props.row.prices.length === 0" class="text-grey-5 text-italic text-caption">
            Sin precios
          </span>
        </div>
      </q-td>
    </template>

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
          <q-tooltip>Editar Producto</q-tooltip>
        </q-btn>
        <q-btn
          dense
          round
          flat
          color="negative"
          icon="delete"
          @click="emit('delete', props.row)"
        >
          <q-tooltip>Eliminar Producto</q-tooltip>
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
  products: {
    type: Array,
    required: true
  },
  companies: {
    type: Array,
    default: () => []
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
    { name: 'code', align: 'left', label: 'Código', field: 'code', sortable: true },
    { name: 'name', align: 'left', label: 'Nombre del Producto', field: 'name', sortable: true },
    { name: 'description', align: 'left', label: 'Descripción', field: 'description', sortable: true },
    { name: 'company', align: 'left', label: 'Empresa', field: 'companyId', sortable: true },
    { name: 'prices', align: 'left', label: 'Precios', field: 'prices' },
    { name: 'categories', align: 'left', label: 'Categorías', field: 'categories' }
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

function getCompanyName(companyId) {
  const company = props.companies.find(c => c.id === companyId)
  return company ? company.name : ''
}

function formatPrice(val, currency) {
  if (val === null || val === undefined) return '-'
  const formatted = new Intl.NumberFormat('es-CO', {
    minimumFractionDigits: currency === 'COP' ? 0 : 2,
    maximumFractionDigits: 2
  }).format(val)

  if (currency === 'EUR') return `€ ${formatted}`
  return `$ ${formatted}`
}
</script>
