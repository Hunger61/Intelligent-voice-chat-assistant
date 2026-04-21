<template>

    <div class="body">
        <canvas class="photobox" width="1280" height="800"></canvas>
        <div v-if="!issuccessLogin" class="absolute top-8 left-0 right-0 flex justify-center z-10">
            <div
                class="bg-white/90 backdrop-blur-sm px-6 py-3 rounded-full shadow-lg border border-gray-200 text-gray-800 font-medium text-lg flex items-center gap-2 animate-pulse">
                <svg class="w-5 h-5 text-indigo-500" fill="none" stroke="currentColor" viewBox="0 0 24 24"
                    xmlns="http://www.w3.org/2000/svg">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                        d="M13 10V3L4 14h7v7l9-11h-7z"></path>
                </svg>
                请双击选择你的语音助手
            </div>
        </div>

        <div v-if="!issuccessLogin" @click="skip()" class="skip-btn absolute top-8 right-8 z-30 bg-blue-600
         text-white px-4 py-2 rounded-lg font-medium
         transition-all duration-200 ease-in-out
         shadow-md hover:shadow-lg hover:bg-blue-700
         active:scale-95 active:bg-blue-800
         focus:outline-none focus:ring-2 focus:ring-blue-500 focus:ring-opacity-50
         cursor-pointer select-none
         touch-manipulation" 
            >
            跳过 >>
        </div>


        <!-- 蒙版容器 - 全屏覆盖并添加半透明背景 -->
        <div v-if="issuccessLogin"
            class="fixed inset-0 bg-black bg-opacity-30 flex items-center justify-center p-4 z-50">
            <!-- 原表单内容 -->
            <div :class="{ 'shake': isShaking }"
                class="min-h-[50vh] w-full  max-w-md bg-gray-50 flex flex-col justify-center rounded-lg shadow-2xl">
                <div class="sm:mx-auto sm:w-full sm:max-w-md">
                    <h2 class="mt-6 text-center text-3xl font-extrabold text-gray-900">
                        {{ isLogin ? '登录您的账户' : '创建新账户' }}
                    </h2>
                    <p class="mt-2 text-center text-sm text-gray-600">
                        或
                        <a href="#" @click.prevent="isLogin = !isLogin"
                            class="font-medium text-indigo-600 hover:text-indigo-500">
                            {{ isLogin ? '注册新账户' : '登录已有账户' }}
                        </a>
                    </p>
                </div>

                <div class="mt-8 sm:mx-auto sm:w-full sm:max-w-md">
                    <!-- 错误提示信息 -->
                    <div v-if="errorMessage" class="mb-4 text-center text-red-600 text-sm font-medium">
                        {{ errorMessage }}
                    </div>

                    <!-- 表单容器，绑定震动动画类 -->
                    <div class="bg-white py-8 px-4 shadow sm:rounded-lg sm:px-10 transition-all duration-300">
                        <form class="space-y-6" @submit.prevent="submitForm">
                            <!-- 用户名字段（仅在注册时显示） -->
                            <div v-if="!isLogin">
                                <label for="username" class="block text-sm font-medium text-gray-700">
                                    用户名
                                </label>
                                <div class="mt-1">
                                    <input id="username" name="username" type="text" v-model="form.username" required
                                        :class="{ 'border-red-500': errorFields.includes('username') }"
                                        class="appearance-none block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm placeholder-gray-400 focus:outline-none focus:ring-indigo-500 focus:border-indigo-500 sm:text-sm transition-colors">
                                </div>
                            </div>

                            <!-- 邮箱字段 -->
                            <div>
                                <label for="email" class="block text-sm font-medium text-gray-700">
                                    邮箱地址
                                </label>
                                <div class="mt-1">
                                    <input id="email" name="email" type="email" v-model="form.email"
                                        autocomplete="email" required
                                        :class="{ 'border-red-500': errorFields.includes('email') }"
                                        class="appearance-none block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm placeholder-gray-400 focus:outline-none focus:ring-indigo-500 focus:border-indigo-500 sm:text-sm transition-colors">
                                </div>
                            </div>

                            <!-- 密码字段 -->
                            <div>
                                <label for="password" class="block text-sm font-medium text-gray-700">
                                    密码
                                </label>
                                <div class="mt-1">
                                    <input id="password" name="password" type="password" v-model="form.password"
                                        autocomplete="current-password" required
                                        :class="{ 'border-red-500': errorFields.includes('password') }"
                                        class="appearance-none block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm placeholder-gray-400 focus:outline-none focus:ring-indigo-500 focus:border-indigo-500 sm:text-sm transition-colors">
                                </div>
                            </div>

                            <!-- 确认密码字段（仅在注册时显示） -->
                            <div v-if="!isLogin">
                                <label for="confirmPassword" class="block text-sm font-medium text-gray-700">
                                    确认密码
                                </label>
                                <div class="mt-1">
                                    <input id="confirmPassword" name="confirmPassword" type="password"
                                        v-model="form.confirmPassword" required
                                        :class="{ 'border-red-500': errorFields.includes('confirmPassword') }"
                                        class="appearance-none block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm placeholder-gray-400 focus:outline-none focus:ring-indigo-500 focus:border-indigo-500 sm:text-sm transition-colors">
                                </div>
                            </div>

                            <div v-if="!isLogin">
                                <label for="verificationCode" class="block text-sm font-medium text-gray-700">
                                    邮箱验证码
                                </label>
                                <div class="mt-1 flex gap-2">
                                    <input id="verificationCode" name="verificationCode" type="text"
                                        v-model="form.verificationCode" required
                                        class="appearance-none block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm placeholder-gray-400 focus:outline-none focus:ring-indigo-500 focus:border-indigo-500 sm:text-sm transition-colors">
                                    <button type="button" @click="sendCode"
                                        class="px-3 py-2 text-sm rounded-md bg-indigo-100 text-indigo-700 hover:bg-indigo-200">
                                        获取验证码
                                    </button>
                                </div>
                            </div>

                            <!-- 记住我（仅在登录时显示） -->
                            <div v-if="isLogin" class="flex items-center justify-between">
                                <div class="flex items-center">
                                    <input id="remember-me" name="remember-me" type="checkbox" v-model="form.rememberMe"
                                        class="h-4 w-4 text-indigo-600 focus:ring-indigo-500 border-gray-300 rounded">
                                    <label for="remember-me" class="ml-2 block text-sm text-gray-900">
                                        记住我
                                    </label>
                                </div>

                                <div class="text-sm">
                                    <a href="#" class="font-medium text-indigo-600 hover:text-indigo-500">
                                        忘记密码?
                                    </a>
                                </div>
                            </div>

                            <div>
                                <button type="submit"
                                    class="w-full flex justify-center py-2 px-4 border border-transparent rounded-md shadow-sm text-sm font-medium text-white bg-indigo-600 hover:bg-indigo-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500">
                                    {{ isLogin ? '登录' : '注册' }}
                                </button>
                            </div>
                        </form>
                    </div>
                </div>
            </div>
        </div>
    </div>

</template>


<script setup>
import { onBeforeMount, ref, onMounted, watch, computed } from 'vue'
import { useStore } from 'vuex'
import { useRouter, useRoute } from 'vue-router'
import UserService from '../api/user.js';
import AssistantService from '../api/assistant.js'; //ai助手增删改查的api调用封装
import { photobox } from '../assets/js/move.js'; // 导入photobox对象
const store = useStore()
const router = useRouter()
const route = useRoute()
const isLogin = ref(true)  //注册和登入表单切换
const issuccessLogin = ref(true) //是否成功注册
const emit = defineEmits(['page-loaded']);

watch(
    () => route.fullPath, // 监听路由完整路径变化
    emit('page-loaded') // 路由变化时执行加载完成逻辑
);

const form = ref({
    username: '',
    email: '',
    password: '',
    confirmPassword: '',
    verificationCode: '',
    rememberMe: false
})

// 响应式变量：存储要添加的助手信息
const newAssistant = ref({
    name: '',
    description: '',
    character: '',
    knowledge_base_id:0
})

// 初始化画廊（在组件挂载后执行）
onMounted(() => {
    photobox.init();
    emit('page-loaded')
});


// 错误信息相关变量
const errorMessage = ref('')  //错误信息
const isShaking = ref(false)  //是否触发震动效果
const errorFields = ref([])   //错误发生的区域

// 清除错误状态
const clearErrors = () => {
    errorMessage.value = ''
    errorFields.value = []
    isShaking.value = false
}

// 触发错误动画
const triggerError = (message, fields = []) => {
    errorMessage.value = message
    errorFields.value = fields

    // 触发震动动画
    isShaking.value = true

    // 3秒后自动清除错误状态
    setTimeout(() => {
        clearErrors()
    }, 3000)
}

const submitForm = async () => {
    try {
        // 清除之前的错误状态
        clearErrors()

        // 客户端验证
        if (!isLogin.value) {
            // 注册表单验证
            const errors = []  //搜集所有错误信息

            if (!form.value.username.trim()) {
                errors.push('username')
            }

            if (!form.value.email.trim()) {
                errors.push('email')
            }

            if (!form.value.password) {
                errors.push('password')
            }

            if (!form.value.verificationCode.trim()) {
                errors.push('verificationCode')
            }

            if (form.value.password !== form.value.confirmPassword) {
                errors.push('password', 'confirmPassword')
                throw new Error('两次输入的密码不一致')
            }

            if (errors.length > 0) {
                throw new Error('请填写所有必填字段')
            }
        } else {
            // 登录表单验证
            const errors = []

            if (!form.value.email.trim()) {
                errors.push('email')
            }

            if (!form.value.password) {
                errors.push('password')
            }

            if (errors.length > 0) {
                throw new Error('请填写邮箱和密码')
            }
        }

        if (isLogin.value) {
            console.log('登录提交:', form.value);
            const response = await UserService.login({
                email: form.value.email,
                password: form.value.password
            });

            if (!response.success) {
                throw new Error(response.message || '登录失败，邮箱或密码不正确')
            }

            store.commit('setUser', { id: response.data.id, name: response.data.name });
            console.log('登录成功', store.getters.getUser);

            photobox.interactive = true
            issuccessLogin.value = false
            // 登录成功后初始化photobox，此时canvas元素已经显示
            setTimeout(() => {
                photobox.init();
            }, 100);
            //    router.push('/home_page');
        } else {
            console.log('注册提交:', form.value);
            const response = await UserService.register({
                name: form.value.username,
                email: form.value.email,
                password: form.value.password,
                verificationCode: form.value.verificationCode
            });

            if (!response.success) {
                throw new Error(response.message || '注册失败')
            }
            const responseL = await UserService.login({
                email: form.value.email,
                password: form.value.password
            });

            store.commit('setUser', { id: responseL.data.id, name: responseL.data.name });
            console.log('登录成功', store.getters.getUser);


            photobox.interactive = true
            issuccessLogin.value = false
            // 注册成功后初始化photobox，此时canvas元素已经显示
            setTimeout(() => {
                photobox.init();
            }, 100);

            // 清空表单
            form.value.username = ''
            form.value.password = ''
            form.value.confirmPassword = ''
            form.value.verificationCode = ''
        }
    } catch (error) {
        console.error('表单提交错误:', error);

        // 确定哪些字段有错误
        let errorFields = []
        if (error.message.includes('用户名')) errorFields.push('username')
        if (error.message.includes('邮箱')) errorFields.push('email')
        if (error.message.includes('密码')) {
            errorFields.push('password')
            if (!isLogin.value) errorFields.push('confirmPassword')
        }

        // 触发错误提示和动画
        triggerError(error.message, errorFields)
    }
}

const sendCode = async () => {
    if (!form.value.email?.trim()) {
        triggerError('请先输入邮箱', ['email'])
        return
    }
    try {
        await UserService.getVerificationCode(form.value.email.trim())
        triggerError('验证码已发送，请检查邮箱')
    } catch (error) {
        triggerError(error.message || '发送验证码失败')
    }
}

const skip = () => {
    router.push('/home_page');
}


// 1. 通过 computed 获取 Vuex 中“当前点击的助手信息”
const currentClickedAssistant = computed(() => {
    return store.getters.getCurrentAssistant
})

watch(
    currentClickedAssistant,
    (clickedInfo) => {
        if (clickedInfo) {
            if (store.getters.getSelectedId == 0) {
                store.commit('clearCurrentClickedAssistant')
            }
            router.push('/home_page');
        }
    },
)
</script>

<style scoped>
.body {
    width: 100%;
    height: 100vh;
    background-color: #c8c7c7;
    overflow: hidden;
}


.photobox {
    position: absolute;
    width: 100%;
    height: 100%;
    cursor: pointer;
    touch-action: manipulation;
    /* 优化触摸体验 */
}



.skip-btn {
    /* 扩大点击区域 */
    padding: 8px 16px;
    margin: -4px -8px;
    /* 负边距进一步扩大可点击范围 */

    /* 基础样式 */
    color: #6b7280;
    font-size: 14px;
    border-radius: 6px;
    background-color: transparent;
    /* 透明背景不影响布局 */

    /* 触摸设备优化 */
    -webkit-tap-highlight-color: transparent;
}

/* 悬停状态 */
.skip-btn:hover {
    color: #374151;
    background-color: rgba(243, 244, 246, 0.8);
}

/* 点击状态 */
.skip-btn:active {
    color: #1f2937;
    background-color: rgba(229, 231, 235, 0.8);
    transform: scale(0.98);
}

/* 震动动画效果 */
.shake {
    animation: shake 0.5s cubic-bezier(.36, .07, .19, .97) both;
    transform: translate3d(0, 0, 0);
    backface-visibility: hidden;
    perspective: 1000px;
}

@keyframes shake {

    10%,
    90% {
        transform: translate3d(-1px, 0, 0);
    }

    20%,
    80% {
        transform: translate3d(2px, 0, 0);
    }

    30%,
    50%,
    70% {
        transform: translate3d(-4px, 0, 0);
    }

    40%,
    60% {
        transform: translate3d(4px, 0, 0);
    }
}
</style>