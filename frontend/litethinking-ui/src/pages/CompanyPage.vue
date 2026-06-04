<template>
  <crud-template
    title="Empresas"
    subtitle="Directorio de empresas registradas en el sistema."
    :error="error"
    @retry="fetchCompanies"
  >
    <template #actions>
      <app-button
        v-if="isAdmin"
        color="primary"
        icon="add"
        label="Crear Empresa"
        aria-label="Crear empresa"
        class="q-px-md"
        @click="openCreateDialog"
      />
    </template>

    <company-table
      :companies="companies"
      :loading="loading"
      @edit="openEditDialog"
      @delete="confirmDelete"
    />
  </crud-template>

  <q-dialog v-model="formDialogOpen" persistent>
    <company-form
      :company="selectedCompany"
      :loading="formLoading"
      @save="handleSave"
    />
  </q-dialog>

  <confirm-dialog
    v-model="deleteDialogOpen"
    title="Eliminar Empresa"
    :message="`¿Está seguro de que desea eliminar la empresa '${companyToDelete?.name}'? Esta acción inactivará sus registros en el sistema.`"
    @confirm="handleDelete"
  />
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useQuasar } from 'quasar'
import { useCompany } from '../composables/useCompany.js'
import { useAuthStore } from '../stores/auth.store.js'
import CrudTemplate from '../components/templates/CrudTemplate.vue'
import CompanyTable from '../components/organisms/CompanyTable.vue'
import CompanyForm from '../components/organisms/CompanyForm.vue'
import ConfirmDialog from '../components/molecules/ConfirmDialog.vue'
import AppButton from '../components/atoms/AppButton.vue'

const $q = useQuasar()
const authStore = useAuthStore()
const { companies, loading, error, fetchCompanies, createCompany, updateCompany, deleteCompany } = useCompany()

const isAdmin = computed(() => authStore.isAdmin)
const formDialogOpen = ref(false)
const deleteDialogOpen = ref(false)
const selectedCompany = ref(null)
const companyToDelete = ref(null)
const formLoading = ref(false)

onMounted(fetchCompanies)

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
      $q.notify({ type: 'positive', message: 'Empresa actualizada correctamente' })
    } else {
      await createCompany(formData)
      $q.notify({ type: 'positive', message: 'Empresa creada correctamente' })
    }
    formDialogOpen.value = false
  } catch {
    // Error already shown by composable Notify
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
    $q.notify({ type: 'positive', message: 'Empresa eliminada correctamente' })
  } catch {
    // Error already shown by composable Notify
  } finally {
    companyToDelete.value = null
  }
}
</script>
