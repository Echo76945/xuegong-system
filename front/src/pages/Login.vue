<template>
  <div class="login-wrap">
    <!-- Decorative shapes -->
    <div class="shape shape-1" />
    <div class="shape shape-2" />
    <div class="shape shape-3" />

    <div class="login-card">
      <div class="login-header">
        <div class="logo-circle">
          <el-icon :size="32" color="#fff"><School /></el-icon>
        </div>
        <h2>学工管理系统</h2>
        <p class="login-subtitle">学生工作信息化平台</p>
      </div>

      <el-form ref="formRef" :model="form" :rules="rules" label-width="0" size="large" class="login-form">
        <el-form-item prop="username">
          <el-input
            v-model="form.username"
            placeholder="请输入用户名"
            prefix-icon="User"
            class="login-input"
          />
        </el-form-item>
        <el-form-item prop="password">
          <el-input
            v-model="form.password"
            type="password"
            placeholder="请输入密码"
            prefix-icon="Lock"
            show-password
            class="login-input"
            @keyup.enter="onSubmit"
          />
        </el-form-item>
        <el-form-item>
          <div class="login-options">
            <el-checkbox v-model="form.remember">记住我</el-checkbox>
            <el-link type="primary" underline="never">忘记密码?</el-link>
          </div>
        </el-form-item>
        <el-form-item>
          <el-button
            type="primary"
            class="login-btn"
            :loading="loading"
            @click="onSubmit"
          >
            登 录
          </el-button>
        </el-form-item>
      </el-form>

      <div class="login-footer">
        <div class="test-accounts">
          <span class="footer-label">测试账号</span>
          <div class="account-list">
            <div class="account-item" v-for="acc in testAccounts" :key="acc.username" @click="quickLogin(acc)">
              <span class="acc-role">{{ acc.role }}</span>
              <span class="acc-info">{{ acc.username }} / {{ acc.password }}</span>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import request from '../api/request'

const router = useRouter()
const formRef = ref()
const loading = ref(false)
const form = reactive({ username: '', password: '', remember: false })
const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
}

const testAccounts = [
  { username: 'admin', password: '123456', name: '张管理', role: '管理员' },
  { username: 'teacher', password: '123456', name: '王教授', role: '教师' },
  { username: 'student', password: '123456', name: '李同学', role: '学生' },
]

function quickLogin(acc) {
  form.username = acc.username
  form.password = acc.password
  onSubmit()
}

async function onSubmit() {
  formRef.value.validate(async valid => {
    if (!valid) return
    loading.value = true
    
    try {
      const res = await request.post('/users/login', {
        username: form.username,
        password: form.password
      })
      
      localStorage.setItem('token', res.data.token)
      localStorage.setItem('user', JSON.stringify(res.data.user))
      ElMessage.success(`登录成功，欢迎 ${res.data.user.name}`)
      router.push('/home')
    } catch (err) {
      // 错误已在request拦截器中处理
    } finally {
      loading.value = false
    }
  })
}
</script>

<style scoped>
.login-wrap {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #1e1b4b 0%, #312e81 30%, #4338ca 60%, #4f46e5 100%);
  position: relative;
  overflow: hidden;
}

/* Decorative shapes */
.shape {
  position: absolute;
  border-radius: 50%;
  opacity: 0.08;
  background: #fff;
}
.shape-1 {
  width: 500px;
  height: 500px;
  top: -180px;
  right: -100px;
  animation: float 8s ease-in-out infinite;
}
.shape-2 {
  width: 350px;
  height: 350px;
  bottom: -120px;
  left: -80px;
  animation: float 10s ease-in-out infinite reverse;
}
.shape-3 {
  width: 200px;
  height: 200px;
  top: 40%;
  left: 10%;
  animation: float 12s ease-in-out infinite;
}

@keyframes float {
  0%, 100% { transform: translateY(0px) rotate(0deg); }
  50% { transform: translateY(-20px) rotate(5deg); }
}

.login-card {
  width: 440px;
  padding: 44px 40px 36px;
  border-radius: 20px;
  background: rgba(255,255,255,0.97);
  backdrop-filter: blur(24px);
  box-shadow:
    0 24px 80px rgba(0,0,0,0.25),
    0 8px 24px rgba(0,0,0,0.12),
    inset 0 1px 0 rgba(255,255,255,0.6);
  position: relative;
  z-index: 1;
  animation: cardEnter 0.6s ease both;
}

@keyframes cardEnter {
  from {
    opacity: 0;
    transform: translateY(20px) scale(0.97);
  }
  to {
    opacity: 1;
    transform: translateY(0) scale(1);
  }
}

.login-header {
  text-align: center;
  margin-bottom: 36px;
}
.logo-circle {
  width: 64px;
  height: 64px;
  border-radius: 18px;
  background: linear-gradient(135deg, #4f46e5 0%, #6366f1 100%);
  display: inline-flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 16px;
  box-shadow: 0 8px 24px rgba(79, 70, 229, 0.35);
}
.login-header h2 {
  margin: 0 0 6px;
  font-size: 24px;
  font-weight: 800;
  color: #1e1b4b;
  letter-spacing: 0.5px;
}
.login-subtitle {
  color: #8e8e93;
  font-size: 14px;
  margin: 0;
}

.login-form {
  margin-bottom: 0;
}
.login-form :deep(.el-input__wrapper) {
  border-radius: 10px;
  padding: 4px 12px;
  box-shadow: 0 0 0 1px #e5e7eb inset;
  transition: all 0.2s ease;
}
.login-form :deep(.el-input__wrapper:hover) {
  box-shadow: 0 0 0 1px #c7d2fe inset;
}
.login-form :deep(.el-input__wrapper:focus-within) {
  box-shadow: 0 0 0 2px #4f46e5 inset;
}

.login-options {
  display: flex;
  justify-content: space-between;
  width: 100%;
  align-items: center;
}

.login-btn {
  width: 100%;
  height: 46px;
  font-size: 16px;
  font-weight: 600;
  border-radius: 10px !important;
  letter-spacing: 2px;
  background: linear-gradient(135deg, #4f46e5 0%, #6366f1 100%) !important;
  border: none !important;
  box-shadow: 0 4px 16px rgba(79, 70, 229, 0.35) !important;
  transition: all 0.2s ease !important;
}
.login-btn:hover {
  box-shadow: 0 6px 24px rgba(79, 70, 229, 0.45) !important;
  transform: translateY(-1px);
}
.login-btn:active {
  transform: translateY(0);
}

.login-footer {
  margin-top: 20px;
  padding-top: 18px;
  border-top: 1px solid #f0f0f0;
}
.test-accounts {
  text-align: center;
}
.footer-label {
  font-size: 12px;
  color: #b0b0b0;
  display: block;
  margin-bottom: 10px;
}
.account-list {
  display: flex;
  gap: 8px;
  justify-content: center;
}
.account-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 3px;
  padding: 8px 12px;
  border-radius: 8px;
  background: #f7f8fa;
  cursor: pointer;
  transition: all 0.2s;
  border: 1px solid #eee;
  flex: 1;
}
.account-item:hover {
  background: #f0f5ff;
  border-color: var(--el-color-primary-light-5);
  box-shadow: 0 2px 8px rgba(79,110,247,0.1);
}
.acc-role {
  font-size: 11px;
  font-weight: 600;
  color: var(--el-color-primary);
}
.acc-info {
  font-size: 11px;
  color: #909399;
  font-family: 'SF Mono','Menlo',monospace;
}
</style>
