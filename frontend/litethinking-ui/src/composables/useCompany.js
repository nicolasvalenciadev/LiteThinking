import { ref, computed } from 'vue'
import { Notify } from 'quasar'
import { api } from '../boot/axios.js'
import { useCompanyStore } from '../stores/company.store.js'
import { API } from '../constants/index.js'

export function useCompany() {
  const companyStore = useCompanyStore()
  const loading = ref(false)
  const error = ref(null)

  const companies = computed(() => companyStore.companies)
  const selectedCompany = computed(() => companyStore.selectedCompany)

  function resolveError(err) {
    if (!err.response) return 'Sin conexión al servidor'
    if (err.response.status === 403) return 'No tiene permisos para realizar esta acción'
    if (err.response.status === 404) return 'El recurso solicitado no fue encontrado'
    if (err.response.status >= 500) return 'Error interno del servidor. Intente nuevamente'
    if (err.response.data?.message) return err.response.data.message
    return 'Ha ocurrido un error inesperado'
  }

  const fetchCompanies = async () => {
    loading.value = true
    error.value = null
    try {
      const { data } = await api.get(API.COMPANIES)
      companyStore.setCompanies(data)
    } catch (err) {
      error.value = resolveError(err)
      Notify.create({ type: 'negative', message: error.value })
    } finally {
      loading.value = false
    }
  }

  const fetchCompanyById = async (id) => {
    loading.value = true
    error.value = null
    try {
      const { data } = await api.get(`${API.COMPANIES}/${id}`)
      companyStore.setSelectedCompany(data)
      return data
    } catch (err) {
      error.value = resolveError(err)
      Notify.create({ type: 'negative', message: error.value })
    } finally {
      loading.value = false
    }
  }

  const createCompany = async (companyData) => {
    loading.value = true
    error.value = null
    try {
      const { data } = await api.post(API.COMPANIES, companyData)
      await fetchCompanies()
      return data
    } catch (err) {
      error.value = resolveError(err)
      Notify.create({ type: 'negative', message: error.value })
      throw err
    } finally {
      loading.value = false
    }
  }

  const updateCompany = async (id, companyData) => {
    loading.value = true
    error.value = null
    try {
      const { data } = await api.put(`${API.COMPANIES}/${id}`, companyData)
      await fetchCompanies()
      return data
    } catch (err) {
      error.value = resolveError(err)
      Notify.create({ type: 'negative', message: error.value })
      throw err
    } finally {
      loading.value = false
    }
  }

  const deleteCompany = async (id) => {
    loading.value = true
    error.value = null
    try {
      await api.delete(`${API.COMPANIES}/${id}`)
      await fetchCompanies()
      return true
    } catch (err) {
      error.value = resolveError(err)
      Notify.create({ type: 'negative', message: error.value })
      throw err
    } finally {
      loading.value = false
    }
  }

  return {
    companies,
    selectedCompany,
    loading,
    error,
    fetchCompanies,
    fetchCompanyById,
    createCompany,
    updateCompany,
    deleteCompany
  }
}
