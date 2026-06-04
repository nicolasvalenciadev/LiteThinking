<template>
  <read-only-template
    title="Inventario"
    subtitle="Reporte consolidado de productos y precios por empresa."
    :error="error"
    @retry="fetchInventory"
  >
    <template v-if="isAdmin" #actions>
      <div class="row q-gutter-sm">
        <app-button
          color="primary"
          icon="picture_as_pdf"
          label="Descargar PDF"
          aria-label="Descargar PDF del inventario"
          :loading="loading"
          class="q-px-md"
          @click="handleDownloadPdf"
        />
        <app-button
          color="secondary"
          icon="email"
          label="Enviar por correo"
          aria-label="Enviar inventario por correo"
          class="q-px-md"
          @click="openEmailDialog"
        />
      </div>
    </template>

    <inventory-table :rows="inventory" :loading="loading" />
  </read-only-template>

  <q-dialog v-model="emailDialogOpen" persistent>
    <q-card style="width: 400px; max-width: 90vw; border-radius: 12px;">
      <q-card-section class="bg-secondary text-white row items-center q-pb-md">
        <div class="text-h6 text-weight-bold">Enviar Reporte por Correo</div>
        <q-space />
        <q-btn icon="close" flat round dense aria-label="Cerrar" v-close-popup />
      </q-card-section>

      <q-form @submit.prevent="handleSendEmail">
        <q-card-section class="q-pt-lg">
          <p class="text-body2 text-grey-8 q-mb-md">
            Ingrese el correo electrónico al cual se enviará el archivo PDF del inventario consolidado.
          </p>
          <app-input
            v-model="emailAddress"
            type="email"
            label="Correo Electrónico *"
            :rules="[
              val => (val && val.trim().length > 0) || 'El correo es obligatorio',
              val => isValidEmail(val) || 'Ingrese un correo electrónico válido'
            ]"
          />
        </q-card-section>

        <q-card-actions align="right" class="q-pb-md q-px-md">
          <q-btn flat label="Cancelar" color="grey-7" v-close-popup />
          <app-button
            color="secondary"
            label="Enviar"
            type="submit"
            :loading="sendEmailLoading"
            class="q-px-md"
          />
        </q-card-actions>
      </q-form>
    </q-card>
  </q-dialog>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useQuasar } from 'quasar'
import { useInventory } from '../composables/useInventory.js'
import { useAuthStore } from '../stores/auth.store.js'
import ReadOnlyTemplate from '../components/templates/ReadOnlyTemplate.vue'
import InventoryTable from '../components/organisms/InventoryTable.vue'
import AppButton from '../components/atoms/AppButton.vue'
import AppInput from '../components/atoms/AppInput.vue'

const $q = useQuasar()
const authStore = useAuthStore()
const { inventory, loading, error, fetchInventory, downloadPdf, sendEmail } = useInventory()

const isAdmin = computed(() => authStore.isAdmin)
const emailDialogOpen = ref(false)
const sendEmailLoading = ref(false)
const emailAddress = ref('')

const EMAIL_PATTERN = /^(?=[a-zA-Z0-9@._%+-]{6,254}$)[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/

onMounted(fetchInventory)

function openEmailDialog() {
  emailAddress.value = ''
  emailDialogOpen.value = true
}

function isValidEmail(val) {
  return EMAIL_PATTERN.test(val)
}

async function handleDownloadPdf() {
  const success = await downloadPdf()
  if (success) {
    $q.notify({ type: 'positive', message: 'El reporte en PDF se ha descargado correctamente' })
  }
}

async function handleSendEmail() {
  sendEmailLoading.value = true
  const success = await sendEmail(emailAddress.value)
  sendEmailLoading.value = false
  if (success) {
    $q.notify({ type: 'positive', message: `Reporte enviado correctamente a: ${emailAddress.value}` })
    emailDialogOpen.value = false
  }
}
</script>
