
import { createApp } from 'vue'
import { createPinia } from 'pinia'
import ElementPlus from 'element-plus'  // 合并了这句

import App from './App.vue'
import router from './router'

const app = createApp(App)
const pinia = createPinia()

app.use(pinia)
app.use(createPinia())
app.use(router)
app.use(ElementPlus)  // 加了这一行

app.mount('#app')
