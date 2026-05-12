import { createRouter, createWebHistory } from 'vue-router'
import { useStore } from 'vuex'
import HomePage from '../pages/homePage.vue';
import LoginPage from '../pages/login.vue'
import konwPage from '../pages/konwPage.vue'
import UserService from '../api/user.js';
import { getAuthToken } from '../api/auth.js';
const routes = [
  {
    path: '/',
    name: 'loginPage',
    component: LoginPage
  },
  {
    path: '/home_page',
    name: 'homePage',
    component: HomePage
  },
  {
    path: '/konw_page',
    name: 'konwPage',
    component: konwPage
  },
]

const router = createRouter({
  //history: createWebHistory('/'),
  history: createWebHistory('/static/'),
  
  routes,
});


router.beforeEach(async (to, from, next) => {
  const store = useStore()
  const user = store.getters.getUser
  const isLoginPage = to.name === 'loginPage'

  if (user && user.id) {
    next()
    return
  }

  const token = getAuthToken()
  if (!token) {
    store.commit('clearUser')
    if (isLoginPage) {
      next()
    } else {
      next({ name: 'loginPage' })
    }
    return
  }

  const currentUser = UserService.getCurrentUserFromToken()
  if (currentUser && currentUser.id) {
    store.commit('setUser', { id: currentUser.id, name: currentUser.name })
    next()
  } else {
    store.commit('clearUser')
    next({ name: 'loginPage' })
  }
})


export default router;  