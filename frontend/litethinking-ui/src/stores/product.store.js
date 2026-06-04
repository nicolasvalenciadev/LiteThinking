import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useProductStore = defineStore('product', () => {
  const products = ref([])
  const filters = ref({
    search: '',
    companyId: null
  })

  function setProducts(newProducts) {
    products.value = newProducts
  }

  function setFilters(newFilters) {
    filters.value = { ...filters.value, ...newFilters }
  }

  return {
    products,
    filters,
    setProducts,
    setFilters
  }
})
