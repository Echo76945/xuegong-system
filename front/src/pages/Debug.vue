<template>
  <div class="debug-container">
    <el-card title="调试信息">
      <h3>LocalStorage 用户信息</h3>
      <pre>{{ userInfo }}</pre>
      
      <h3>权限检查结果</h3>
      <p>AI助理权限: {{ has('/ai-assistant') }}</p>
      <p>当前路径: {{ currentRoute }}</p>
      <p>用户角色: {{ userRole }}</p>
      
      <h3>测试不同角色权限</h3>
      <div v-for="role in ['管理员', '教师', '学生']" :key="role">
        <p>{{ role }}: AI助理权限 = {{ checkRolePermission(role, '/ai-assistant') }}</p>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'

const userInfo = ref({})
const currentRoute = ref('')
const userRole = ref('')

// 模拟权限检查函数
function has(path) {
  const userStr = localStorage.getItem('user')
  if (!userStr) return false
  
  try {
    const user = JSON.parse(userStr)
    const perms = rolePermissions[user.role] || rolePermissions['管理员']
    return perms.includes(path)
  } catch (e) {
    return false
  }
}

function checkRolePermission(role, path) {
  const perms = rolePermissions[role] || rolePermissions['管理员']
  return perms.includes(path)
}

const rolePermissions = {
  '管理员': [
    '/home', '/students', '/family', '/awards',
    '/courses', '/scores', '/extracurricular',
    '/scholarship',
    '/announcements', '/activities', '/messages',
    '/late-records', '/leave-requests', '/incidents',
    '/users', '/ai-assistant', '/profile', '/settings',
  ],
  '教师': [
    '/home', '/students', '/family', '/awards',
    '/courses', '/scores', '/extracurricular',
    '/scholarship',
    '/announcements', '/activities', '/messages',
    '/late-records', '/leave-requests', '/incidents',
    '/ai-assistant', '/profile',
  ],
  '学生': [
    '/home',
    '/courses', '/scores', '/extracurricular',
    '/scholarship',
    '/announcements', '/activities', '/messages',
    '/leave-requests',
    '/ai-assistant', '/profile',
  ],
}

// 获取用户信息
function getUserInfo() {
  const userStr = localStorage.getItem('user')
  const token = localStorage.getItem('token')
  
  userInfo.value = {
    token: token || '无',
    user: userStr ? JSON.parse(userStr) : '无',
    timestamp: new Date().toLocaleString()
  }
  
  if (userStr) {
    try {
      const user = JSON.parse(userStr)
      userRole.value = user.role || '未知'
    } catch (e) {
      userRole.value = '解析失败'
    }
  }
  
  currentRoute.value = window.location.hash || '/'
}

// 页面加载时获取信息
getUserInfo()
</script>

<style scoped>
.debug-container {
  padding: 20px;
}
pre {
  background: #f5f5f5;
  padding: 10px;
  border-radius: 4px;
  overflow-x: auto;
}
</style>