<template>
  <q-table
    :rows="rows"
    :columns="columns"
    row-key="id"
    :loading="loading"
    flat
    bordered
    class="lt-card"
    no-data-label="No hay datos de inventario disponibles"
    :pagination="pagination"
  >
    <template #body-cell-categories="props">
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
          <span
            v-if="!props.row.categories || props.row.categories.length === 0"
            class="text-grey-5 text-italic text-caption"
          >
            Sin categorías
          </span>
        </div>
      </q-td>
    </template>

    <template #body-cell-priceCop="props">
      <q-td :props="props" class="text-right text-weight-medium">
        {{ getPriceByCurrency(props.row, 'COP') }}
      </q-td>
    </template>

    <template #body-cell-priceUsd="props">
      <q-td :props="props" class="text-right text-weight-medium text-teal-8">
        {{ getPriceByCurrency(props.row, 'USD') }}
      </q-td>
    </template>

    <template #body-cell-priceEur="props">
      <q-td :props="props" class="text-right text-weight-medium text-purple-8">
        {{ getPriceByCurrency(props.row, 'EUR') }}
      </q-td>
    </template>

    <template #loading>
      <app-loader :showing="loading" />
    </template>
  </q-table>
</template>

<script setup>
import { ref } from 'vue'
import AppLoader from '../atoms/AppLoader.vue'

defineProps({
  rows: { type: Array, required: true },
  loading: { type: Boolean, default: false }
})

const pagination = ref({ rowsPerPage: 15, sortBy: 'companyName', descending: false })

const columns = [
  {
    name: 'companyName',
    align: 'left',
    label: 'Empresa',
    field: row => row.company?.name || row.companyName || 'N/A',
    sortable: true
  },
  { name: 'code', align: 'left', label: 'Código', field: 'code', sortable: true },
  { name: 'name', align: 'left', label: 'Nombre', field: 'name', sortable: true },
  { name: 'categories', align: 'left', label: 'Categorías', field: 'categories' },
  {
    name: 'priceCop',
    align: 'right',
    label: 'Precio COP',
    field: row => getPriceValue(row, 'COP'),
    sortable: true
  },
  {
    name: 'priceUsd',
    align: 'right',
    label: 'Precio USD',
    field: row => getPriceValue(row, 'USD'),
    sortable: true
  },
  {
    name: 'priceEur',
    align: 'right',
    label: 'Precio EUR',
    field: row => getPriceValue(row, 'EUR'),
    sortable: true
  }
]

function getPriceValue(row, currency) {
  if (!row.prices) return 0
  const found = row.prices.find(p => p.currency === currency)
  return found ? found.price : 0
}

function getPriceByCurrency(row, currency) {
  if (!row.prices) return '-'
  const found = row.prices.find(p => p.currency === currency)
  if (!found) return '-'
  return formatPrice(found.price, currency)
}

function formatPrice(val, currency) {
  if (val === null || val === undefined) return '-'
  const formatted = new Intl.NumberFormat('es-CO', {
    minimumFractionDigits: currency === 'COP' ? 0 : 2,
    maximumFractionDigits: 2
  }).format(val)
  return currency === 'EUR' ? `€ ${formatted}` : `$ ${formatted}`
}
</script>
