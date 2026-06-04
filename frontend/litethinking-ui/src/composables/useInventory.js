import { ref } from 'vue'
import { Notify } from 'quasar'
import { api } from '../boot/axios.js'
import { API } from '../constants/index.js'

export function useInventory() {
  const inventory = ref([])
  const loading = ref(false)
  const error = ref(null)

  function resolveError(err) {
    if (!err.response) return 'Sin conexión al servidor'
    if (err.response.status === 403) return 'No tiene permisos para realizar esta acción'
    if (err.response.status === 404) return 'El recurso solicitado no fue encontrado'
    if (err.response.status >= 500) return 'Error interno del servidor. Intente nuevamente'
    if (err.response.data?.message) return err.response.data.message
    return 'Ha ocurrido un error inesperado'
  }

  const fetchInventory = async () => {
    loading.value = true
    error.value = null
    try {
      const { data } = await api.get(API.INVENTORY)
      inventory.value = data
    } catch (err) {
      error.value = resolveError(err)
      Notify.create({ type: 'negative', message: error.value })
    } finally {
      loading.value = false
    }
  }

  const downloadPdf = async () => {
    loading.value = true
    error.value = null
    try {
      const response = await api.get(API.INVENTORY_PDF, { responseType: 'blob' })
      const blob = new Blob([response.data], { type: 'application/pdf' })
      const url = window.URL.createObjectURL(blob)
      const link = document.createElement('a')
      link.href = url
      link.setAttribute('download', 'Inventario_LiteThinking.pdf')
      document.body.appendChild(link)
      link.click()
      document.body.removeChild(link)
      window.URL.revokeObjectURL(url)
      return true
    } catch (err) {
      error.value = resolveError(err)
      Notify.create({ type: 'negative', message: error.value })
      return false
    } finally {
      loading.value = false
    }
  }

  const sendEmail = async (email) => {
    loading.value = true
    error.value = null
    try {
      await api.post(API.INVENTORY_EMAIL, { email })
      return true
    } catch (err) {
      error.value = resolveError(err)
      Notify.create({ type: 'negative', message: error.value })
      return false
    } finally {
      loading.value = false
    }
  }

  return {
    inventory,
    loading,
    error,
    fetchInventory,
    downloadPdf,
    sendEmail
  }
}
