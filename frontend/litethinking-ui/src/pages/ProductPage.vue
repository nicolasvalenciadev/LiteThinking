<template>
  <q-page class="q-pa-md">
    <!-- Page Title -->
    <div class="row justify-between items-center q-mb-md">
      <div>
        <h1 class="text-h4 text-weight-bold text-primary q-my-none">Productos</h1>
        <p class="text-subtitle2 text-grey-7 q-my-none">
          Catálogo general de productos e insumos por empresa.
        </p>
      </div>
      <q-btn
        v-if="isAdmin"
        color="primary"
        icon="add"
        label="Crear Producto"
        unevaluated
        class="q-px-md"
        @click="openCreateDialog"
      />
    </div>

    <!-- Filters Section -->
    <div class="row q-col-gutter-sm q-mb-md items-center">
      <div class="col-12 col-sm-4">
        <q-select
          v-slot:selected-item="scope"
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

    <!-- Error Banner -->
    <q-banner v-if="error" inline-actions class="text-white bg-red-6 q-mb-md rounded-borders">
      {{ error }}
      <template v-slot:action>
        <q-btn flat color="white" label="Reintentar" @click="loadData" />
      </template>
    </q-banner>

    <!-- Product List Table -->
    <ProductList
      :products="products"
      :companies="companies"
      :loading="loadingProducts"
      @edit="openEditDialog"
      @delete="confirmDelete"
    />

    <!-- Create/Edit Dialog Form -->
    <q-dialog v-model="formDialogOpen" persistent>
      <ProductForm
        :product="selectedProduct"
        :companies="companies"
        :loading="formLoading"
        @save="handleSave"
      />
    </q-dialog>

    <!-- Delete Confirmation Modal -->
    <ConfirmDialog
      v-model="deleteDialogOpen"
      title="Eliminar Producto"
      :message="`¿Está seguro de que desea eliminar el producto '${productToDelete?.name}'? Esta acción inactivará su registro en el sistema.`"
      @confirm="handleDelete"
    />
  </q-page>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useProduct } from '../composables/useProduct.js'
import { useCompany } from '../composables/useCompany.js'
import { useAuthStore } from '../stores/auth.store.js'
import { useQuasar } from 'quasar'
import ProductList from '../components/product/ProductList.vue'
import ProductForm from '../components/product/ProductForm.vue'
import ConfirmDialog from '../components/common/ConfirmDialog.vue'

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

const {
  companies,
  fetchCompanies,
  error: errorCompanies
} = useCompany()

const selectedFilterCompanyId = ref(null)
const formDialogOpen = ref(false)
const deleteDialogOpen = ref(false)
const selectedProduct = ref(null)
const productToDelete = ref(null)
const formLoading = ref(false)

const error = computed(() => errorProducts.value || errorCompanies.value)

const filterCompanyOptions = computed(() => {
  return companies.value.map(c => ({
    label: c.name,
    value: c.id
  }))
})

async function loadData() {
  await fetchCompanies()
  await fetchProducts()
}

onMounted(() => {
  loadData()
})

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
      $q.notify({
        type: 'positive',
        message: 'Producto actualizado correctamente'
      })
    } else {
      await createProduct(formData)
      $q.notify({
        type: 'positive',
        message: 'Producto creado correctamente'
      })
    }
    formDialogOpen.value = false
  } catch (err) {
    $q.notify({
      type: 'negative',
      message: errorProducts.value || 'Error al guardar el producto'
    })
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
    $q.notify({
      type: 'positive',
      message: 'Producto eliminado correctamente'
    })
  } catch (err) {
    $q.notify({
      type: 'negative',
      message: errorProducts.value || 'Error al eliminar el producto'
    })
  } finally {
    productToDelete.value = null
  }
}
</script>

<style scoped>
h1 {
  margin: 0;
}
</style>
