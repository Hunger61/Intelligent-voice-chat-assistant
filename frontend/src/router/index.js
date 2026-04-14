import { createRouter, createWebHistory } from 'vue-router'
import { useStore } from 'vuex'
import HomePage from '../pages/homePage.vue';
import TextPage from '../pages/textPage.vue'
import LoginPage from '../pages/login.vue'
import konwPage from '../pages/konwPage.vue'
import UserService from '../api/user.js';
import xxxPage from '../pages/xxx.vue'
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
 {
    path: '/xxxPage',
    name: 'xxxPage',
    component: xxxPage
  },
  {
    path: '/robot_test',
    name: 'text',
    component: TextPage,
    // 路由独享守卫
    beforeEnter: (to, from, next) => {
      const store = useStore()
      const assistantId = store.getters.getSelectedId
      const assistant = store.getters.getAssistantById(assistantId)

      if (!assistant || Object.keys(assistant).length === 0) {
        console.log('Assistant数据不存在，重定向到首页')
        next({ name: 'homePage' })
      } else {
        next()
      }
    }
  }
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