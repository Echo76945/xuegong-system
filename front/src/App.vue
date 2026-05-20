<template>
  <router-view v-slot="{ Component }">
    <transition name="fade-slide" mode="out-in">
      <component :is="Component" />
    </transition>
  </router-view>
</template>

<script setup>
import { onMounted } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()

onMounted(() => {
  // 检查登录状态，如果token存在但用户信息不完整，则清除
  const token = localStorage.getItem('token')
  const userStr = localStorage.getItem('user')
  
  if (token) {
    try {
      const user = JSON.parse(userStr || '{}')
      if (!user.username || !user.role) {
        // 用户信息不完整，清除登录状态
        localStorage.removeItem('token')
        localStorage.removeItem('user')
        router.push('/login')
      }
    } catch (e) {
      // 解析失败，清除登录状态
      localStorage.removeItem('token')
      localStorage.removeItem('user')
      router.push('/login')
    }
  }
})
</script>

<style>
/* Global styles */
.fade-slide-enter-active,
.fade-slide-leave-active {
  transition: all 0.3s ease;
}

.fade-slide-enter-from {
  opacity: 0;
  transform: translateX(20px);
}

.fade-slide-leave-to {
  opacity: 0;
  transform: translateX(-20px);
}
</style>
