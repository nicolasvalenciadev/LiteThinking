import { ref } from 'vue'
import { api } from '../boot/axios.js'

export function useInventory() {
  const inventory = ref([])
  const loading = ref(false)
  const error = ref(null)

  const fetchInventory = async () => {
    loading.value = true
    error.value = null
    try {
      const { data } = await api.get('/api/inventory')
      inventory.value = data
    } catch (err) {
      error.value = 'Error al cargar el inventario'
    } finally {
      loading.value = false
    }
  }

  const downloadPdf = async () => {
    loading.value = true
    error.value = null
    try {
      const response = await api.get('/api/inventory/pdf', {
        responseType: 'blob'
      })
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
      error.value = 'Error al descargar el PDF del inventario'
      return false
    } finally {
      loading.value = false
    }
  }

  const sendEmail = async (email) => {
    loading.value = true
    error.value = null
    try {
      await api.post('/api/inventory/send-email', { email })
      return true
    } catch (err) {
      if (err.response && err.response.data && err.response.data.message) {
        error.value = err.response.data.message
      } else {
        error.value = 'Error al enviar el correo electrónico'
      }
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
