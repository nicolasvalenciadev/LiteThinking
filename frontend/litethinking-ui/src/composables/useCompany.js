import { ref, computed } from 'vue'
import { api } from '../boot/axios.js'
import { useCompanyStore } from '../stores/company.store.js'

export function useCompany() {
  const companyStore = useCompanyStore()
  const loading = ref(false)
  const error = ref(null)

  const companies = computed(() => companyStore.companies)
  const selectedCompany = computed(() => companyStore.selectedCompany)

  const fetchCompanies = async () => {
    loading.value = true
    error.value = null
    try {
      const { data } = await api.get('/api/companies')
      companyStore.setCompanies(data)
    } catch (err) {
      error.value = 'Error al cargar la lista de empresas'
    } finally {
      loading.value = false
    }
  }

  const fetchCompanyById = async (id) => {
    loading.value = true
    error.value = null
    try {
      const { data } = await api.get(`/api/companies/${id}`)
      companyStore.setSelectedCompany(data)
      return data
    } catch (err) {
      error.value = 'Error al obtener los detalles de la empresa'
    } finally {
      loading.value = false
    }
  }

  const createCompany = async (companyData) => {
    loading.value = true
    error.value = null
    try {
      const { data } = await api.post('/api/companies', companyData)
      await fetchCompanies()
      return data
    } catch (err) {
      if (err.response && err.response.data && err.response.data.message) {
        error.value = err.response.data.message
      } else {
        error.value = 'Error al crear la empresa. Verifique que el NIT sea único'
      }
      throw err
    } finally {
      loading.value = false
    }
  }

  const updateCompany = async (id, companyData) => {
    loading.value = true
    error.value = null
    try {
      const { data } = await api.put(`/api/companies/${id}`, companyData)
      await fetchCompanies()
      return data
    } catch (err) {
      if (err.response && err.response.data && err.response.data.message) {
        error.value = err.response.data.message
      } else {
        error.value = 'Error al actualizar la empresa'
      }
      throw err
    } finally {
      loading.value = false
    }
  }

  const deleteCompany = async (id) => {
    loading.value = true
    error.value = null
    try {
      await api.delete(`/api/companies/${id}`)
      await fetchCompanies()
      return true
    } catch (err) {
      error.value = 'Error al eliminar la empresa'
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
