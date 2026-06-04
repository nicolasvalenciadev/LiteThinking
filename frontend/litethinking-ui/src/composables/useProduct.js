import { ref, computed } from 'vue'
import { api } from '../boot/axios.js'
import { useProductStore } from '../stores/product.store.js'

export function useProduct() {
  const productStore = useProductStore()
  const loading = ref(false)
  const error = ref(null)

  const products = computed(() => productStore.products)
  const filters = computed(() => productStore.filters)

  const fetchProducts = async () => {
    loading.value = true
    error.value = null
    try {
      const { data } = await api.get('/api/products')
      productStore.setProducts(data)
    } catch (err) {
      error.value = 'Error al cargar la lista de productos'
    } finally {
      loading.value = false
    }
  }

  const fetchProductById = async (id) => {
    loading.value = true
    error.value = null
    try {
      const { data } = await api.get(`/api/products/${id}`)
      return data
    } catch (err) {
      error.value = 'Error al obtener los detalles del producto'
      throw err
    } finally {
      loading.value = false
    }
  }

  const fetchProductsByCompany = async (companyId) => {
    loading.value = true
    error.value = null
    try {
      const { data } = await api.get(`/api/products/company/${companyId}`)
      productStore.setProducts(data)
      return data
    } catch (err) {
      error.value = 'Error al cargar los productos por empresa'
    } finally {
      loading.value = false
    }
  }

  const createProduct = async (productData) => {
    loading.value = true
    error.value = null
    try {
      const { data } = await api.post('/api/products', productData)
      await fetchProducts()
      return data
    } catch (err) {
      if (err.response && err.response.data && err.response.data.message) {
        error.value = err.response.data.message
      } else {
        error.value = 'Error al crear el producto'
      }
      throw err
    } finally {
      loading.value = false
    }
  }

  const updateProduct = async (id, productData) => {
    loading.value = true
    error.value = null
    try {
      const { data } = await api.put(`/api/products/${id}`, productData)
      await fetchProducts()
      return data
    } catch (err) {
      if (err.response && err.response.data && err.response.data.message) {
        error.value = err.response.data.message
      } else {
        error.value = 'Error al actualizar el producto'
      }
      throw err
    } finally {
      loading.value = false
    }
  }

  const deleteProduct = async (id) => {
    loading.value = true
    error.value = null
    try {
      await api.delete(`/api/products/${id}`)
      await fetchProducts()
      return true
    } catch (err) {
      error.value = 'Error al eliminar el producto'
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
