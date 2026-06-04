<template>
  <q-card class="lt-auth-card q-pa-lg">
    <q-card-section class="text-center q-pb-none">
      <div class="row justify-center q-mb-md">
        <svg width="64" height="64" viewBox="0 0 64 64" fill="none" xmlns="http://www.w3.org/2000/svg">
          <rect width="64" height="64" rx="16" fill="#1976D2"/>
          <path d="M20 44V20H28L36 34V20H44V44H36L28 30V44H20Z" fill="white"/>
        </svg>
      </div>
      <h1 class="text-h5 text-weight-bold text-primary q-my-sm">LiteThinking</h1>
      <p class="text-caption text-grey-7">Sistema de Gestión de Empresas</p>
    </q-card-section>

    <q-card-section>
      <q-banner
        v-if="error"
        inline-actions
        class="text-white bg-red-6 q-mb-md rounded-borders text-subtitle2"
      >
        {{ error }}
      </q-banner>

      <q-form @submit.prevent="onSubmit" class="q-gutter-md">
        <app-input
          v-model="username"
          label="Usuario"
          autocomplete="username"
          :rules="[val => (val && val.length > 0) || 'El usuario es obligatorio']"
        >
          <template #prepend>
            <q-icon name="person" color="primary" />
          </template>
        </app-input>

        <app-input
          v-model="password"
          label="Contraseña"
          type="password"
          autocomplete="current-password"
          :rules="[val => (val && val.length > 0) || 'La contraseña es obligatoria']"
        >
          <template #prepend>
            <q-icon name="lock" color="primary" />
          </template>
        </app-input>

        <div class="q-mt-lg">
          <app-button
            type="submit"
            color="primary"
            class="full-width q-py-sm text-subtitle1"
            label="Iniciar Sesión"
            :loading="loading"
            aria-label="Iniciar sesión"
          />
        </div>
      </q-form>
    </q-card-section>
  </q-card>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useQuasar } from 'quasar'
import { useAuth } from '../composables/useAuth.js'
import AppInput from '../components/atoms/AppInput.vue'
import AppButton from '../components/atoms/AppButton.vue'

const router = useRouter()
const $q = useQuasar()
const { loading, error, login } = useAuth()

const username = ref('')
const password = ref('')

async function onSubmit() {
  const success = await login(username.value, password.value)
  if (success) {
    $q.notify({ type: 'positive', message: 'Inicio de sesión exitoso', position: 'top-right' })
    router.push('/companies')
  }
}
</script>

<style scoped>
h1 { margin: 0; }
</style>
