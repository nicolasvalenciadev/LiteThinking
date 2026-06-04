<template>
  <q-layout view="hHh Lpr lFf">
    <q-header elevated class="bg-primary text-white">
      <q-toolbar>
        <q-btn
          flat
          dense
          round
          icon="menu"
          aria-label="Abrir menú"
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

          <app-badge :color="isAdmin ? 'deep-orange' : 'teal'">
            {{ isAdmin ? 'ADMINISTRADOR' : 'EXTERNO' }}
          </app-badge>

          <q-btn
            flat
            round
            dense
            icon="logout"
            class="q-ml-md"
            aria-label="Cerrar sesión"
            @click="handleLogout"
          >
            <q-tooltip>Cerrar Sesión</q-tooltip>
          </q-btn>
        </div>
      </q-toolbar>
    </q-header>

    <app-sidebar v-model="leftDrawerOpen" />

    <q-page-container>
      <main role="main">
        <router-view />
      </main>
    </q-page-container>
  </q-layout>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import { useQuasar } from 'quasar'
import { useAuthStore } from '../stores/auth.store.js'
import AppSidebar from '../components/organisms/AppSidebar.vue'
import AppBadge from '../components/atoms/AppBadge.vue'

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
    cancel: { label: 'Cancelar', color: 'grey-7', flat: true },
    ok: { label: 'Salir', color: 'primary', flat: true },
    persistent: true
  }).onOk(() => {
    authStore.clearAuth()
    $q.notify({ type: 'info', message: 'Sesión cerrada correctamente' })
    router.push('/login')
  })
}
</script>
