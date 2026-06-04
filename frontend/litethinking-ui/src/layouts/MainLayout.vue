<template>
  <q-layout view="hHh Lpr lFf">
    <q-header elevated class="bg-primary text-white">
      <q-toolbar>
        <q-btn
          flat
          dense
          round
          icon="menu"
          aria-label="Menu"
          @click="toggleLeftDrawer"
        />

        <q-toolbar-title class="text-weight-bold">
          LiteThinking
        </q-toolbar-title>

        <div class="row items-center q-gutter-sm">
          <div class="column items-end text-weight-medium gt-xs q-mr-sm">
            <span class="text-caption text-blue-2" style="font-size: 0.75rem;">Usuario</span>
            <span class="text-subtitle2" style="line-height: 1;">{{ authStore.username }}</span>
          </div>

          <q-badge :color="isAdmin ? 'deep-orange' : 'teal'" class="text-weight-bold q-py-xs q-px-sm">
            {{ isAdmin ? 'ADMINISTRADOR' : 'EXTERNO' }}
          </q-badge>

          <q-btn
            flat
            round
            dense
            icon="logout"
            class="q-ml-md"
            @click="handleLogout"
          >
            <q-tooltip>Cerrar Sesión</q-tooltip>
          </q-btn>
        </div>
      </q-toolbar>
    </q-header>

    <q-drawer
      v-model="leftDrawerOpen"
      show-if-above
      bordered
      class="bg-grey-1"
    >
      <q-list>
        <q-item-label
          header
          class="text-weight-bold text-uppercase text-grey-7 q-pt-md"
        >
          Menú de Navegación
        </q-item-label>

        <q-item
          clickable
          v-ripple
          to="/companies"
          active-class="bg-blue-1 text-primary text-weight-bold"
        >
          <q-item-section avatar>
            <q-icon name="corporate_fare" />
          </q-item-section>
          <q-item-section>
            <q-item-label>Empresas</q-item-label>
            <q-item-label caption>Gestión de empresas</q-item-label>
          </q-item-section>
        </q-item>

        <q-item
          clickable
          v-ripple
          to="/products"
          active-class="bg-blue-1 text-primary text-weight-bold"
        >
          <q-item-section avatar>
            <q-icon name="inventory_2" />
          </q-item-section>
          <q-item-section>
            <q-item-label>Productos</q-item-label>
            <q-item-label caption>Catálogo de artículos</q-item-label>
          </q-item-section>
        </q-item>

        <q-item
          clickable
          v-ripple
          to="/inventory"
          active-class="bg-blue-1 text-primary text-weight-bold"
        >
          <q-item-section avatar>
            <q-icon name="assessment" />
          </q-item-section>
          <q-item-section>
            <q-item-label>Inventario</q-item-label>
            <q-item-label caption>Reportes y Exportaciones</q-item-label>
          </q-item-section>
        </q-item>
      </q-list>
    </q-drawer>

    <q-page-container>
      <q-page class="q-pa-md bg-grey-2">
        <router-view />
      </q-page>
    </q-page-container>
  </q-layout>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../stores/auth.store.js'
import { useQuasar } from 'quasar'

const $q = useQuasar()
const router = useRouter()
const authStore = useAuthStore()

const leftDrawerOpen = ref(false)

const isAdmin = computed(() => authStore.isAdmin)

function toggleLeftDrawer() {
  leftDrawerOpen.value = !leftDrawerOpen.value
}

function handleLogout() {
  $q.dialog({
    title: 'Cerrar Sesión',
    message: '¿Está seguro de que desea salir del sistema?',
    cancel: {
      label: 'Cancelar',
      color: 'grey-7',
      flat: true
    },
    ok: {
      label: 'Salir',
      color: 'primary',
      flat: true
    },
    persistent: true
  }).onOk(() => {
    authStore.clearAuth()
    $q.notify({
      type: 'info',
      message: 'Sesión cerrada correctamente'
    })
    router.push('/login')
  })
}
</script>

<style scoped>
.q-drawer {
  background-color: #fafafa;
}
</style>
