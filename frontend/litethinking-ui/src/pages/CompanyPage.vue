<template>
  <q-page class="q-pa-md">
    <!-- Page Header -->
    <div class="row justify-between items-center q-mb-md">
      <div>
        <h1 class="text-h4 text-weight-bold text-primary q-my-none">Empresas</h1>
        <p class="text-subtitle2 text-grey-7 q-my-none">
          Directorio de empresas registradas en el sistema.
        </p>
      </div>
      <q-btn
        v-if="isAdmin"
        color="primary"
        icon="add"
        label="Crear Empresa"
        unevaluated
        class="q-px-md"
        @click="openCreateDialog"
      />
    </div>

    <!-- Error State Alert -->
    <q-banner v-if="error" inline-actions class="text-white bg-red-6 q-mb-md rounded-borders">
      {{ error }}
      <template v-slot:action>
        <q-btn flat color="white" label="Reintentar" @click="loadCompanies" />
      </template>
    </q-banner>

    <!-- Company List Table -->
    <CompanyList
      :companies="companies"
      :loading="loading"
      @edit="openEditDialog"
      @delete="confirmDelete"
    />

    <!-- Create/Edit Form Modal -->
    <q-dialog v-model="formDialogOpen" persistent>
      <CompanyForm
        :company="selectedCompany"
        :loading="formLoading"
        @save="handleSave"
      />
    </q-dialog>

    <!-- Delete Confirmation Modal -->
    <ConfirmDialog
      v-model="deleteDialogOpen"
      title="Eliminar Empresa"
      :message="`¿Está seguro de que desea eliminar la empresa '${companyToDelete?.name}'? Esta acción inactivará sus registros en el sistema.`"
      @confirm="handleDelete"
    />
  </q-page>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useCompany } from '../composables/useCompany.js'
import { useAuthStore } from '../stores/auth.store.js'
import { useQuasar } from 'quasar'
import CompanyList from '../components/company/CompanyList.vue'
import CompanyForm from '../components/company/CompanyForm.vue'
import ConfirmDialog from '../components/common/ConfirmDialog.vue'

const $q = useQuasar()
const authStore = useAuthStore()
const { companies, loading, error, fetchCompanies, createCompany, updateCompany, deleteCompany } = useCompany()

const isAdmin = computed(() => authStore.isAdmin)

const formDialogOpen = ref(false)
const deleteDialogOpen = ref(false)

const selectedCompany = ref(null)
const companyToDelete = ref(null)
const formLoading = ref(false)

async function loadCompanies() {
  await fetchCompanies()
}

onMounted(() => {
  loadCompanies()
})

function openCreateDialog() {
  selectedCompany.value = null
  formDialogOpen.value = true
}

function openEditDialog(company) {
  selectedCompany.value = company
  formDialogOpen.value = true
}

async function handleSave(formData) {
  formLoading.value = true
  try {
    if (selectedCompany.value) {
      await updateCompany(selectedCompany.value.id, formData)
      $q.notify({
        type: 'positive',
        message: 'Empresa actualizada correctamente'
      })
    } else {
      await createCompany(formData)
      $q.notify({
        type: 'positive',
        message: 'Empresa creada correctamente'
      })
    }
    formDialogOpen.value = false
  } catch (err) {
    $q.notify({
      type: 'negative',
      message: error.value || 'Error al procesar la solicitud'
    })
  } finally {
    formLoading.value = false
  }
}

function confirmDelete(company) {
  companyToDelete.value = company
  deleteDialogOpen.value = true
}

async function handleDelete() {
  if (!companyToDelete.value) return
  try {
    await deleteCompany(companyToDelete.value.id)
    $q.notify({
      type: 'positive',
      message: 'Empresa eliminada correctamente'
    })
  } catch (err) {
    $q.notify({
      type: 'negative',
      message: error.value || 'Error al eliminar la empresa'
    })
  } finally {
    companyToDelete.value = null
  }
}
</script>

<style scoped>
h1 {
  margin: 0;
}
</style>
