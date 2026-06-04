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
      vueRouterMode: 'hash', // Hash mode is convenient for simple deployments and local routing
    },

    devServer: {
      port: 9000,
      open: false // Avoid opening browser automatically on server launch
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
