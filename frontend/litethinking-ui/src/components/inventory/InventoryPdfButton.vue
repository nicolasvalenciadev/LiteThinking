<template>
  <q-btn
    color="primary"
    icon="picture_as_pdf"
    label="Descargar PDF"
    :loading="loading"
    unevaluated
    class="q-px-md"
    @click="handleDownload"
  />
</template>

<script setup>
import { useInventory } from '../../composables/useInventory.js'
import { useQuasar } from 'quasar'

const $q = useQuasar()
const { loading, error, downloadPdf } = useInventory()

async function handleDownload() {
  const success = await downloadPdf()
  if (success) {
    $q.notify({
      type: 'positive',
      message: 'El reporte en PDF se ha descargado correctamente'
    })
  } else {
    $q.notify({
      type: 'negative',
      message: error.value || 'Error al descargar el archivo PDF'
    })
  }
}
</script>
