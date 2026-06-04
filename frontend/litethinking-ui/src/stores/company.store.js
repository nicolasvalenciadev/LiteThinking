import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useCompanyStore = defineStore('company', () => {
  const companies = ref([])
  const selectedCompany = ref(null)

  function setCompanies(newCompanies) {
    companies.value = newCompanies
  }

  function setSelectedCompany(company) {
    selectedCompany.value = company
  }

  return {
    companies,
    selectedCompany,
    setCompanies,
    setSelectedCompany
  }
})
