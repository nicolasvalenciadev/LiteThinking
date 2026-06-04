<template>
  <crud-template
    title="Productos"
    subtitle="Catálogo general de productos e insumos por empresa."
    :error="error"
    @retry="loadData"
  >
    <template #actions>
      <app-button
        v-if="isAdmin"
        color="primary"
        icon="add"
        label="Crear Producto"
        aria-label="Crear producto"
        class="q-px-md"
        @click="openCreateDialog"
      />
    </template>

    <div class="row q-col-gutter-sm q-mb-md items-center">
      <div class="col-12 col-sm-4">
        <q-select
          v-model="selectedFilterCompanyId"
          :options="filterCompanyOptions"
          label="Filtrar por Empresa"
          outlined
          dense
          emit-value
          map-options
          clearable
          @update:model-value="onFilterChange"
        />
      </div>
    </div>

    <product-table
      :products="products"
      :companies="companies"
      :loading="loadingProducts"
      @edit="openEditDialog"
      @delete="confirmDelete"
    />
  </crud-template>

  <q-dialog v-model="formDialogOpen" persistent>
    <product-form
      :product="selectedProduct"
      :companies="companies"
      :loading="formLoading"
      @save="handleSave"
    />
  </q-dialog>

  <confirm-dialog
    v-model="deleteDialogOpen"
    title="Eliminar Producto"
    :message="`¿Está seguro de que desea eliminar el producto '${productToDelete?.name}'? Esta acción inactivará su registro en el sistema.`"
    @confirm="handleDelete"
  />
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useQuasar } from 'quasar'
import { useProduct } from '../composables/useProduct.js'
import { useCompany } from '../composables/useCompany.js'
import { useAuthStore } from '../stores/auth.store.js'
import CrudTemplate from '../components/templates/CrudTemplate.vue'
import ProductTable from '../components/organisms/ProductTable.vue'
import ProductForm from '../components/organisms/ProductForm.vue'
import ConfirmDialog from '../components/molecules/ConfirmDialog.vue'
import AppButton from '../components/atoms/AppButton.vue'

const $q = useQuasar()
const authStore = useAuthStore()
const isAdmin = computed(() => authStore.isAdmin)

const {
  products,
  loading: loadingProducts,
  error: errorProducts,
  fetchProducts,
  fetchProductsByCompany,
  createProduct,
  updateProduct,
  deleteProduct
} = useProduct()

const { companies, fetchCompanies, error: errorCompanies } = useCompany()

const selectedFilterCompanyId = ref(null)
const formDialogOpen = ref(false)
const deleteDialogOpen = ref(false)
const selectedProduct = ref(null)
const productToDelete = ref(null)
const formLoading = ref(false)

const error = computed(() => errorProducts.value || errorCompanies.value)

const filterCompanyOptions = computed(() =>
  companies.value.map(c => ({ label: c.name, value: c.id }))
)

async function loadData() {
  await fetchCompanies()
  await fetchProducts()
}

onMounted(loadData)

async function onFilterChange(val) {
  if (val) {
    await fetchProductsByCompany(val)
  } else {
    await fetchProducts()
  }
}

function openCreateDialog() {
  selectedProduct.value = null
  formDialogOpen.value = true
}

function openEditDialog(product) {
  selectedProduct.value = product
  formDialogOpen.value = true
}

async function handleSave(formData) {
  formLoading.value = true
  try {
    if (selectedProduct.value) {
      await updateProduct(selectedProduct.value.id, formData)
      $q.notify({ type: 'positive', message: 'Producto actualizado correctamente' })
    } else {
      await createProduct(formData)
      $q.notify({ type: 'positive', message: 'Producto creado correctamente' })
    }
    formDialogOpen.value = false
  } catch {
    // Error already shown by composable Notify
  } finally {
    formLoading.value = false
  }
}

function confirmDelete(product) {
  productToDelete.value = product
  deleteDialogOpen.value = true
}

async function handleDelete() {
  if (!productToDelete.value) return
  try {
    await deleteProduct(productToDelete.value.id)
    $q.notify({ type: 'positive', message: 'Producto eliminado correctamente' })
  } catch {
    // Error already shown by composable Notify
  } finally {
    productToDelete.value = null
  }
}
</script>
