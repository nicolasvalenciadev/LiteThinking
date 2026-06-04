import { ref, computed } from 'vue'
import { Notify } from 'quasar'
import { api } from '../boot/axios.js'
import { useProductStore } from '../stores/product.store.js'
import { API } from '../constants/index.js'

export function useProduct() {
  const productStore = useProductStore()
  const loading = ref(false)
  const error = ref(null)

  const products = computed(() => productStore.products)
  const filters = computed(() => productStore.filters)

  function resolveError(err) {
    if (!err.response) return 'Sin conexión al servidor'
    if (err.response.status === 403) return 'No tiene permisos para realizar esta acción'
    if (err.response.status === 404) return 'El recurso solicitado no fue encontrado'
    if (err.response.status >= 500) return 'Error interno del servidor. Intente nuevamente'
    if (err.response.data?.message) return err.response.data.message
    return 'Ha ocurrido un error inesperado'
  }

  const fetchProducts = async () => {
    loading.value = true
    error.value = null
    try {
      const { data } = await api.get(API.PRODUCTS)
      productStore.setProducts(data)
    } catch (err) {
      error.value = resolveError(err)
      Notify.create({ type: 'negative', message: error.value })
    } finally {
      loading.value = false
    }
  }

  const fetchProductById = async (id) => {
    loading.value = true
    error.value = null
    try {
      const { data } = await api.get(`${API.PRODUCTS}/${id}`)
      return data
    } catch (err) {
      error.value = resolveError(err)
      Notify.create({ type: 'negative', message: error.value })
      throw err
    } finally {
      loading.value = false
    }
  }

  const fetchProductsByCompany = async (companyId) => {
    loading.value = true
    error.value = null
    try {
      const { data } = await api.get(`${API.PRODUCTS}/company/${companyId}`)
      productStore.setProducts(data)
      return data
    } catch (err) {
      error.value = resolveError(err)
      Notify.create({ type: 'negative', message: error.value })
    } finally {
      loading.value = false
    }
  }

  const createProduct = async (productData) => {
    loading.value = true
    error.value = null
    try {
      const { data } = await api.post(API.PRODUCTS, productData)
      await fetchProducts()
      return data
    } catch (err) {
      error.value = resolveError(err)
      Notify.create({ type: 'negative', message: error.value })
      throw err
    } finally {
      loading.value = false
    }
  }

  const updateProduct = async (id, productData) => {
    loading.value = true
    error.value = null
    try {
      const { data } = await api.put(`${API.PRODUCTS}/${id}`, productData)
      await fetchProducts()
      return data
    } catch (err) {
      error.value = resolveError(err)
      Notify.create({ type: 'negative', message: error.value })
      throw err
    } finally {
      loading.value = false
    }
  }

  const deleteProduct = async (id) => {
    loading.value = true
    error.value = null
    try {
      await api.delete(`${API.PRODUCTS}/${id}`)
      await fetchProducts()
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
    products,
    filters,
    loading,
    error,
    fetchProducts,
    fetchProductById,
    fetchProductsByCompany,
    createProduct,
    updateProduct,
    deleteProduct
  }
}
