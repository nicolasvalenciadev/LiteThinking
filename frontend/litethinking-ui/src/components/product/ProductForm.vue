<template>
  <q-card style="width: 600px; max-width: 95vw; border-radius: 12px;">
    <q-card-section class="bg-primary text-white row items-center q-pb-md">
      <div class="text-h6 text-weight-bold">
        {{ isEdit ? 'Editar Producto' : 'Crear Producto' }}
      </div>
      <q-space />
      <q-btn icon="close" flat round dense v-close-popup />
    </q-card-section>

    <q-form @submit.prevent="onSubmit">
      <q-card-section class="q-gutter-sm q-pt-lg" style="max-height: 60vh; overflow-y: auto;">
        <!-- Code & Name -->
        <div class="row q-col-gutter-sm">
          <div class="col-12 col-sm-4">
            <q-input
              v-model="form.code"
              label="Código *"
              placeholder="Ej: PROD-001"
              outlined
              dense
              :disable="isEdit"
              lazy-rules
              :rules="[ val => val && val.trim().length > 0 || 'El código es obligatorio' ]"
            />
          </div>
          <div class="col-12 col-sm-8">
            <q-input
              v-model="form.name"
              label="Nombre del Producto *"
              placeholder="Ej: Laptop Empresarial"
              outlined
              dense
              lazy-rules
              :rules="[ val => val && val.trim().length > 0 || 'El nombre es obligatorio' ]"
            />
          </div>
        </div>

        <!-- Description -->
        <q-input
          v-model="form.description"
          label="Características / Descripción"
          placeholder="Ej: Laptop de alto rendimiento con 16GB RAM..."
          type="textarea"
          outlined
          dense
          rows="3"
        />

        <!-- Company Selection -->
        <q-select
          v-model="form.companyId"
          :options="companyOptions"
          label="Empresa *"
          outlined
          dense
          emit-value
          map-options
          lazy-rules
          :rules="[ val => val || 'Debe seleccionar una empresa' ]"
        />

        <!-- Categories Selection -->
        <q-select
          v-model="form.categories"
          :options="categoryOptions"
          label="Categorías"
          multiple
          use-chips
          outlined
          dense
          placeholder="Seleccione categorías"
        />

        <!-- Prices Management Section -->
        <div class="q-mt-md">
          <div class="row justify-between items-center q-mb-xs">
            <span class="text-subtitle2 text-grey-8 font-weight-bold">Lista de Precios</span>
            <q-btn
              color="primary"
              flat
              dense
              icon="add"
              label="Agregar Precio"
              @click="addPriceRow"
              :disable="form.prices.length >= 3"
            />
          </div>

          <div v-if="form.prices.length === 0" class="text-caption text-grey-6 q-pa-sm text-center bg-grey-2 rounded-borders">
            No se han definido precios. Debe agregar al menos un precio (COP, USD o EUR).
          </div>

          <div
            v-for="(priceRow, index) in form.prices"
            :key="index"
            class="row q-col-gutter-sm items-center q-mb-sm"
          >
            <!-- Currency -->
            <div class="col-5">
              <q-select
                v-model="priceRow.currency"
                :options="availableCurrencies"
                label="Moneda *"
                outlined
                dense
                lazy-rules
                :rules="[ val => val || 'Seleccione moneda' ]"
              />
            </div>
            <!-- Price -->
            <div class="col-5">
              <q-input
                v-model.number="priceRow.price"
                type="number"
                step="0.01"
                label="Precio *"
                outlined
                dense
                lazy-rules
                :rules="[
                  val => val !== null && val !== undefined || 'Ingrese un precio',
                  val => val > 0 || 'El precio debe ser mayor a 0'
                ]"
              />
            </div>
            <!-- Remove Button -->
            <div class="col-2 text-center">
              <q-btn
                round
                flat
                color="negative"
                icon="delete"
                dense
                @click="removePriceRow(index)"
              />
            </div>
          </div>
        </div>
      </q-card-section>

      <q-card-actions align="right" class="q-pb-md q-px-md">
        <q-btn flat label="Cancelar" color="grey-7" v-close-popup />
        <q-btn unevaluated label="Guardar" color="primary" type="submit" :loading="loading" class="q-px-md" />
      </q-card-actions>
    </q-form>
  </q-card>
</template>

<script setup>
import { ref, watch, computed } from 'vue'
import { useQuasar } from 'quasar'

const $q = useQuasar()

const props = defineProps({
  product: {
    type: Object,
    default: null
  },
  companies: {
    type: Array,
    required: true
  },
  loading: {
    type: Boolean,
    default: false
  }
})

const emit = defineEmits(['save'])

const isEdit = computed(() => !!props.company)

const form = ref({
  code: '',
  name: '',
  description: '',
  companyId: null,
  categories: [],
  prices: []
})

// Options mappings
const companyOptions = computed(() => {
  return props.companies.map(c => ({
    label: c.name,
    value: c.id
  }))
})

const categoryOptions = [
  'Electrónica',
  'Software',
  'Hardware',
  'Servicios',
  'Accesorios'
]

const availableCurrencies = ['COP', 'USD', 'EUR']

// Initialize form
watch(() => props.product, (newVal) => {
  if (newVal) {
    form.value = {
      id: newVal.id,
      code: newVal.code || '',
      name: newVal.name || '',
      description: newVal.description || '',
      companyId: newVal.companyId || (newVal.company ? newVal.company.id : null),
      categories: Array.isArray(newVal.categories) ? [...newVal.categories] : [],
      prices: Array.isArray(newVal.prices) 
        ? newVal.prices.map(p => ({ currency: p.currency, price: p.price })) 
        : []
    }
  } else {
    form.value = {
      code: '',
      name: '',
      description: '',
      companyId: null,
      categories: [],
      prices: [{ currency: 'COP', price: null }]
    }
  }
}, { immediate: true })

function addPriceRow() {
  if (form.value.prices.length >= 3) return
  // Determine next default currency to help user
  const currentCurrencies = form.value.prices.map(p => p.currency)
  const nextCurrency = availableCurrencies.find(c => !currentCurrencies.includes(c)) || ''
  
  form.value.prices.push({
    currency: nextCurrency,
    price: null
  })
}

function removePriceRow(index) {
  form.value.prices.splice(index, 1)
}

function validatePrices() {
  if (form.value.prices.length === 0) {
    $q.notify({
      type: 'warning',
      message: 'Debe agregar al menos un precio al producto'
    })
    return false
  }

  // Check for duplicate currencies
  const currencies = form.value.prices.map(p => p.currency)
  const hasDuplicates = currencies.some((val, i) => currencies.indexOf(val) !== i)
  if (hasDuplicates) {
    $q.notify({
      type: 'warning',
      message: 'No puede ingresar más de un precio para la misma moneda'
    })
    return false
  }

  return true
}

function onSubmit() {
  if (!validatePrices()) return
  emit('save', { ...form.value })
}
</script>
