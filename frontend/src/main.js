import './style.css'
import 'tailwindcss/tailwind.css';
import router from './router';
import { createApp } from 'vue'
import App from './App.vue'
import store from './store'
import './assets/css/fontFamily.css'
const app = createApp(App);

app.use(router).use(store).mount('#app'); 

