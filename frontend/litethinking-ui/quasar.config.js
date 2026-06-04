import { defineConfig } from '#q-app/wrappers'

export default defineConfig((ctx) => {
  return {
    boot: [
      'axios'
    ],

    css: [
      'app.scss'
    ],

    extras: [
      'roboto-font',
      'material-icons'
    ],

    build: {
      target: {
        browser: ['es2020', 'edge88', 'firefox78', 'chrome87', 'safari13.1'],
        node: 'node22'
      },
      vueRouterMode: 'hash',
      env: {
        API_URL: process.env.API_URL || 'http://localhost:8080'
      },
      extendViteConf(viteConf) {
        viteConf.build = viteConf.build || {}
        viteConf.build.rollupOptions = {
          output: {
            manualChunks: (id) => {
              if (!id.includes('node_modules')) return
              if (id.includes('/vue/') || id.includes('/pinia/') || id.includes('/vue-router/')) return 'vendor'
              if (id.includes('/quasar/')) return 'quasar'
              if (id.includes('/axios/')) return 'axios'
            }
          }
        }
      }
    },

    devServer: {
      port: 9000,
      open: false
    },

    framework: {
      config: {
        notify: {
          position: 'top-right',
          timeout: 2500,
          textColor: 'white',
          actions: [{ icon: 'close', color: 'white' }]
        }
      },
      plugins: [
        'Notify',
        'Dialog'
      ]
    }
  }
})
