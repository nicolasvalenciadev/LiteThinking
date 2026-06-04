import { ref } from 'vue'

export function useCategory() {
  const categories = ref([
    'Electrónica',
    'Software',
    'Hardware',
    'Servicios',
    'Accesorios'
  ])

  return { categories }
}
