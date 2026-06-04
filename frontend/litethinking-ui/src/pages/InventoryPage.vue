<template>
  <q-page class="q-pa-md">
    <!-- Header -->
    <div class="row justify-between items-center q-mb-md">
      <div>
        <h1 class="text-h4 text-weight-bold text-primary q-my-none">Inventario</h1>
        <p class="text-subtitle2 text-grey-7 q-my-none">
          Reporte consolidado de productos y precios por empresa.
        </p>
      </div>

      <!-- Action Buttons (ADMIN only) -->
      <div v-if="isAdmin" class="row q-gutter-sm">
        <InventoryPdfButton />
        <q-btn
          color="secondary"
          icon="email"
          label="Enviar por correo"
          unevaluated
          class="q-px-md"
          @click="openEmailDialog"
        />
      </div>
    </div>

    <!-- Error Banner -->
    <q-banner v-if="error" inline-actions class="text-white bg-red-6 q-mb-md rounded-borders">
      {{ error }}
      <template v-slot:action>
        <q-btn flat color="white" label="Reintentar" @click="loadInventory" />
      </template>
    </q-banner>

    <!-- Table -->
    <InventoryTable :rows="inventory" :loading="loading" />

    <!-- Email Dispatch Dialog -->
    <q-dialog v-model="emailDialogOpen" persistent>
      <q-card style="width: 400px; max-width: 90vw; border-radius: 12px;">
        <q-card-section class="bg-secondary text-white row items-center q-pb-md">
          <div class="text-h6 text-weight-bold">Enviar Reporte por Correo</div>
          <q-space />
          <q-btn icon="close" flat round dense v-close-popup />
        </q-card-section>

        <q-form @submit.prevent="handleSendEmail">
          <q-card-section class="q-pt-lg">
            <p class="text-body2 text-grey-8 q-mb-md">
              Ingrese el correo electrónico al cual se enviará el archivo PDF del inventario consolidado.
            </p>
            <q-input
              v-model="emailForm.email"
              type="email"
              label="Correo Electrónico *"
              outlined
              dense
              lazy-rules
              :rules="[
                val => val && val.trim().length > 0 || 'El correo es obligatorio',
                val => isValidEmail(val) || 'Ingrese un correo electrónico válido'
              ]"
            />
          </q-card-section>

          <q-card-actions align="right" class="q-pb-md q-px-md">
            <q-btn flat label="Cancelar" color="grey-7" v-close-popup />
            <q-btn
              unevaluated
              label="Enviar"
              color="secondary"
              type="submit"
              :loading="sendEmailLoading"
              class="q-px-md"
            />
          </q-card-actions>
        </q-form>
      </q-card>
    </q-dialog>
  </q-page>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useInventory } from '../composables/useInventory.js'
import { useAuthStore } from '../stores/auth.store.js'
import { useQuasar } from 'quasar'
import InventoryTable from '../components/inventory/InventoryTable.vue'
import InventoryPdfButton from '../components/inventory/InventoryPdfButton.vue'

const $q = useQuasar()
const authStore = useAuthStore()
const { inventory, loading, error, fetchInventory, sendEmail } = useInventory()

const isAdmin = computed(() => authStore.isAdmin)

const emailDialogOpen = ref(false)
const sendEmailLoading = ref(false)
const emailForm = ref({ email: '' })

async function loadInventory() {
  await fetchInventory()
}

onMounted(() => {
  loadInventory()
})

function openEmailDialog() {
  emailForm.value.email = ''
  emailDialogOpen.value = true
}

function isValidEmail(val) {
  const emailPattern = /^(?=[a-zA-Z0-9@._%+-]{6,254}$)[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/
  return emailPattern.test(val)
}

async function handleSendEmail() {
  sendEmailLoading.value = true
  const success = await sendEmail(emailForm.value.email)
  sendEmailLoading.value = false
  if (success) {
    $q.notify({
      type: 'positive',
      message: `El reporte se ha enviado correctamente a: ${emailForm.value.email}`
    })
    emailDialogOpen.value = false
  } else {
    $q.notify({
      type: 'negative',
      message: error.value || 'Error al enviar el correo electrónico'
    })
  }
}
</script>

<style scoped>
h1 {
  margin: 0;
}
</style>
