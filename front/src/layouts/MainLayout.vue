<template>
  <el-container class="layout-container">
    <!-- Sidebar -->
    <el-aside class="sidebar" :width="isCollapsed ? '72px' : '240px'">
      <div class="sidebar-logo" @click="$router.push('/home')">
        <el-icon :size="28" color="#fff"><School /></el-icon>
        <span v-show="!isCollapsed" class="logo-text">学工管理系统</span>
      </div>
      <div class="sidebar-menu-wrap">
        <el-menu
          :default-active="currentRoute"
          :default-openeds="defaultOpeneds"
          :collapse="isCollapsed"
          router
          class="sidebar-menu"
        >
          <!-- 仪表盘 -->
          <el-menu-item index="/home">
            <el-icon><Odometer /></el-icon>
            <template #title>仪表盘</template>
          </el-menu-item>

          <!-- 学生信息 -->
          <el-sub-menu v-if="hasAny('student-info')" index="student-info">
            <template #title>
              <el-icon><User /></el-icon>
              <span>学生信息</span>
            </template>
            <el-menu-item v-if="has('/students')" index="/students">学生管理</el-menu-item>
            <el-menu-item v-if="has('/family')" index="/family">家庭情况</el-menu-item>
            <el-menu-item v-if="has('/awards')" index="/awards">评奖评优</el-menu-item>
          </el-sub-menu>

          <!-- 学业发展 -->
          <el-sub-menu v-if="hasAny('academic')" index="academic">
            <template #title>
              <el-icon><Reading /></el-icon>
              <span>学业发展</span>
            </template>
            <el-menu-item v-if="has('/courses')" index="/courses">课程管理</el-menu-item>
            <el-menu-item v-if="has('/scores')" index="/scores">成绩管理</el-menu-item>
            <el-menu-item v-if="has('/extracurricular')" index="/extracurricular">第二课堂</el-menu-item>
          </el-sub-menu>

          <!-- 奖助服务 -->
          <el-menu-item v-if="has('/scholarship')" index="/scholarship">
            <el-icon><Trophy /></el-icon>
            <template #title>奖助申请</template>
          </el-menu-item>

          <!-- AI 助理 -->
          <el-menu-item v-if="has('/ai-assistant')" index="/ai-assistant">
            <el-icon><MagicStick /></el-icon>
            <template #title>
              <span class="ai-badge">AI</span>AI 助理
            </template>
          </el-menu-item>

          <!-- 沟通互动 -->
          <el-sub-menu v-if="hasAny('communication')" index="communication">
            <template #title>
              <el-icon><ChatDotRound /></el-icon>
              <span>沟通互动</span>
            </template>
            <el-menu-item v-if="has('/announcements')" index="/announcements">班级公告</el-menu-item>
            <el-menu-item v-if="has('/activities')" index="/activities">活动报名</el-menu-item>
            <el-menu-item v-if="has('/messages')" index="/messages">私信系统</el-menu-item>
          </el-sub-menu>

          <!-- 安全管理 -->
          <el-sub-menu v-if="hasAny('safety')" index="safety">
            <template #title>
              <el-icon><Shield /></el-icon>
              <span>安全管理</span>
            </template>
            <el-menu-item v-if="has('/late-records')" index="/late-records">晚归登记</el-menu-item>
            <el-menu-item v-if="has('/leave-requests')" index="/leave-requests">外出报备</el-menu-item>
            <el-menu-item v-if="has('/incidents')" index="/incidents">异常上报</el-menu-item>
          </el-sub-menu>

          <!-- 系统 -->
          <el-sub-menu v-if="hasAny('system')" index="system">
            <template #title>
              <el-icon><Setting /></el-icon>
              <span>系统</span>
            </template>
            <el-menu-item v-if="has('/users')" index="/users">用户管理</el-menu-item>
            <el-menu-item v-if="has('/profile')" index="/profile">个人中心</el-menu-item>
            <el-menu-item v-if="has('/settings')" index="/settings">系统设置</el-menu-item>
          </el-sub-menu>
        </el-menu>
      </div>
      <div class="sidebar-footer">
        <div class="collapse-btn" @click="isCollapsed = !isCollapsed">
          <el-icon :size="18"><Fold v-if="!isCollapsed" /><Expand v-else /></el-icon>
          <span v-show="!isCollapsed">收起菜单</span>
        </div>
      </div>
    </el-aside>

    <!-- Main area -->
    <el-container class="main-container">
      <!-- Header -->
      <el-header class="header">
        <div class="header-left">
          <div v-if="currentRoute !== '/home'" class="back-btn" @click="goBack">
            <el-icon :size="18"><ArrowLeft /></el-icon>
            <span>返回</span>
          </div>
          <div class="breadcrumb-area">
            <span class="page-title">{{ currentTitle }}</span>
            <span class="page-desc">{{ currentDesc }}</span>
          </div>
        </div>
        <div class="header-right">
          <el-button type="danger" plain size="small" icon="SwitchButton" @click="logout">退出登录</el-button>
          <el-dropdown @command="handleCommand" trigger="click">
            <div class="user-area">
              <el-avatar :size="36" class="user-avatar" :style="{ background: avatarBg }">
                {{ userName.charAt(0) }}
              </el-avatar>
              <div class="user-info">
                <span class="user-name">{{ userName }}</span>
                <span class="user-role">{{ userRoleDisplay }}</span>
              </div>
              <el-icon class="user-arrow"><ArrowDown /></el-icon>
            </div>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item icon="User" command="profile">个人中心</el-dropdown-item>
                <el-dropdown-item v-if="userRole === '管理员'" icon="Setting" command="settings">系统设置</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>

      <!-- Content -->
      <el-main class="main-content">
        <router-view v-slot="{ Component }">
          <transition name="fade-slide" mode="out-in">
            <component :is="Component" />
          </transition>
        </router-view>
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'

const router = useRouter()
const route = useRoute()
const q = ref('')
const isCollapsed = ref(false)

// ===== 角色权限配置 =====
// 管理员：全部功能
// 教师：学生信息、学业发展、奖助、沟通互动、安全管理、个人中心、AI助理
// 学生：成绩查询、课程查看、第二课堂、奖助申请(查看)、公告、活动、私信、外出报备、个人中心、AI助理
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
    '/home', '/family',
    '/courses', '/scores', '/extracurricular',
    '/scholarship',
    '/announcements', '/activities', '/messages',
    '/leave-requests',
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

const groupMap = {
  'student-info': ['/students', '/family', '/awards'],
  'academic': ['/courses', '/scores', '/extracurricular'],
  'communication': ['/announcements', '/activities', '/messages'],
  'safety': ['/late-records', '/leave-requests', '/incidents'],
  'system': ['/users', '/profile', '/settings'],
}

function has(path) {
  const perms = rolePermissions[userRole.value] || rolePermissions['管理员']
  return perms.includes(path)
}

function hasAny(groupId) {
  const paths = groupMap[groupId] || []
  return paths.some(p => has(p))
}

const defaultOpeneds = ['student-info', 'academic', 'communication', 'safety', 'system']

const userName = computed(() => {
  try { return JSON.parse(localStorage.getItem('user') || '{}').name || '管理员' } catch { return '管理员' }
})
const userRole = computed(() => {
  try { return JSON.parse(localStorage.getItem('user') || '{}').role || '管理员' } catch { return '管理员' }
})
const userRoleDisplay = computed(() => {
  return { '管理员': '超级管理员', '教师': '教师', '学生': '学生' }[userRole.value] || '超级管理员'
})
const avatarBg = computed(() => {
  const map = {
    '管理员': 'linear-gradient(135deg,#ff3b30,#ff6b6b)',
    '教师': 'linear-gradient(135deg,#d97706,#f59e0b)',
    '学生': 'linear-gradient(135deg,#4f6ef7,#6366f1)',
  }
  return map[userRole.value] || map['管理员']
})

const currentRoute = computed(() => route.path)
const currentTitle = computed(() => route.meta?.title || '仪表盘')
const currentDesc = computed(() => {
  const descs = {
    '/home': '系统概览与快捷入口',
    '/students': '学生信息的增删改查',
    '/family': '学生家庭信息管理',
    '/awards': '奖项查询与荣誉管理',
    '/courses': '课程信息与选课管理',
    '/scores': '学生成绩录入与查询',
    '/extracurricular': '课外活动与积分管理',
    '/scholarship': '奖学金与助学金申请审批',
    '/announcements': '班级公告发布与管理',
    '/activities': '活动发布与报名管理',
    '/messages': '用户间私信与沟通',
    '/late-records': '学生晚归记录登记',
    '/leave-requests': '外出请假申请与审批',
    '/incidents': '异常事件上报与处理',
    '/users': '系统用户与角色管理',
    '/ai-assistant': '智能AI对话助理',
    '/profile': '个人信息查看与编辑',
    '/settings': '系统参数配置',
  }
  return descs[route.path] || ''
})

function goBack() {
  if (window.history.length > 1) {
    router.back()
  } else {
    router.push('/home')
  }
}
function doSearch() {
  if (q.value.trim()) ElMessage.info(`搜索：${q.value}`)
}
function logout() {
  localStorage.removeItem('token')
  localStorage.removeItem('user')
  ElMessage.success('已退出登录')
  router.push('/login')
}

function handleCommand(cmd) {
  if (cmd === 'logout') {
    logout()
  } else if (cmd === 'profile') {
    router.push('/profile')
  } else if (cmd === 'settings') {
    router.push('/settings')
  }
}
</script>

<style scoped>
.layout-container {
  min-height: 100vh;
}

/* Sidebar */
.sidebar {
  background: #1e2233;
  display: flex;
  flex-direction: column;
  transition: width 0.3s ease;
  overflow: hidden;
  position: relative;
  z-index: 10;
  box-shadow: 2px 0 12px rgba(0,0,0,0.08);
}

.sidebar-logo {
  height: 64px;
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 0 24px;
  cursor: pointer;
  flex-shrink: 0;
  background: linear-gradient(135deg, #252a40 0%, #1e2233 100%);
  border-bottom: 1px solid rgba(255,255,255,0.06);
}
.logo-text {
  color: #fff;
  font-size: 16px;
  font-weight: 700;
  letter-spacing: 1px;
  white-space: nowrap;
}

.sidebar-menu-wrap {
  flex: 1;
  overflow-y: auto;
  overflow-x: hidden;
  padding: 12px 10px;
}
.sidebar-menu {
  background: transparent !important;
  border: none !important;
}

/* Menu items */
.sidebar-menu :deep(.el-menu-item) {
  color: rgba(255,255,255,0.65) !important;
  margin: 2px 0;
  height: 42px !important;
  line-height: 42px !important;
  border-radius: 8px !important;
  font-size: 13px;
}
.sidebar-menu :deep(.el-menu-item:hover) {
  background: rgba(255,255,255,0.06) !important;
  color: #fff !important;
}
.sidebar-menu :deep(.el-menu-item.is-active) {
  background: linear-gradient(135deg, #4f6ef7 0%, #6366f1 100%) !important;
  color: #fff !important;
  box-shadow: 0 3px 10px rgba(79, 110, 247, 0.35);
}
.sidebar-menu :deep(.el-menu-item .el-icon) {
  color: inherit !important;
  font-size: 17px;
}

/* Sub-menu */
.sidebar-menu :deep(.el-sub-menu__title) {
  color: rgba(255,255,255,0.55) !important;
  height: 42px !important;
  line-height: 42px !important;
  border-radius: 8px !important;
  font-size: 13px;
}
.sidebar-menu :deep(.el-sub-menu__title:hover) {
  background: rgba(255,255,255,0.04) !important;
  color: rgba(255,255,255,0.85) !important;
}
.sidebar-menu :deep(.el-sub-menu__title .el-icon) {
  color: inherit !important;
  font-size: 17px;
}
.sidebar-menu :deep(.el-sub-menu .el-menu-item) {
  padding-left: 52px !important;
  min-width: auto;
  font-size: 13px;
}
.sidebar-menu :deep(.el-menu--inline) {
  background: transparent !important;
}

.ai-badge {
  display: inline-block;
  background: linear-gradient(135deg, #4f6ef7, #7c3aed);
  color: #fff;
  font-size: 10px;
  font-weight: 700;
  padding: 1px 6px;
  border-radius: 4px;
  margin-right: 6px;
  letter-spacing: 0.5px;
}

.sidebar-footer {
  padding: 10px;
  border-top: 1px solid rgba(255,255,255,0.06);
  flex-shrink: 0;
}
.collapse-btn {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px 14px;
  border-radius: 8px;
  color: rgba(255,255,255,0.45);
  cursor: pointer;
  font-size: 13px;
  transition: all 0.2s;
}
.collapse-btn:hover {
  background: rgba(255,255,255,0.06);
  color: rgba(255,255,255,0.75);
}

/* Header */
.header {
  height: 64px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 28px;
  background: #fff;
  border-bottom: 1px solid var(--border-color);
  box-shadow: 0 1px 4px rgba(0,0,0,0.03);
}

.header-left {
  display: flex;
  align-items: center;
  gap: 16px;
}
.back-btn {
  display: flex;
  align-items: center;
  gap: 5px;
  padding: 6px 14px;
  border-radius: 8px;
  cursor: pointer;
  font-size: 13px;
  font-weight: 500;
  color: var(--text-secondary);
  background: #f7f8fa;
  border: 1px solid var(--border-color);
  transition: all 0.2s ease;
  user-select: none;
}
.back-btn:hover {
  color: var(--el-color-primary);
  border-color: var(--el-color-primary-light-5);
  background: #f0f5ff;
  box-shadow: 0 2px 8px rgba(79, 110, 247, 0.12);
}
.back-btn:active {
  transform: scale(0.97);
}
.breadcrumb-area {
  display: flex;
  flex-direction: column;
}
.page-title {
  font-size: 18px;
  font-weight: 700;
  color: var(--text-primary);
  line-height: 1.3;
}
.page-desc {
  font-size: 12px;
  color: var(--text-tertiary);
  margin-top: 2px;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 20px;
}
.header-search {
  width: 220px;
}
.header-search :deep(.el-input__wrapper) {
  background: #f7f8fa;
  border-radius: 20px !important;
  box-shadow: none !important;
}
.header-search :deep(.el-input__wrapper:focus-within) {
  background: #fff;
  box-shadow: 0 0 0 1px var(--el-color-primary) inset !important;
}

.icon-btn {
  cursor: pointer;
  color: var(--text-tertiary);
  transition: color 0.2s;
  padding: 6px;
  border-radius: 8px;
}
.icon-btn:hover {
  color: var(--el-color-primary);
  background: #f0f5ff;
}
.header-icon :deep(.el-badge__content) {
  top: 4px;
  right: 8px;
}

.user-area {
  display: flex;
  align-items: center;
  gap: 10px;
  cursor: pointer;
  padding: 4px 8px;
  border-radius: 10px;
  transition: background 0.2s;
}
.user-area:hover {
  background: #f7f8fa;
}
.user-avatar {
  color: #fff;
  font-weight: 700;
  font-size: 14px;
  flex-shrink: 0;
}
.user-info {
  display: flex;
  flex-direction: column;
  line-height: 1.3;
}
.user-name {
  font-size: 14px;
  font-weight: 600;
  color: var(--text-primary);
}
.user-role {
  font-size: 11px;
  color: var(--text-tertiary);
}
.user-arrow {
  color: var(--text-tertiary);
  font-size: 12px;
}

/* Main content */
.main-container {
  overflow: hidden;
}
.main-content {
  background: var(--bg-page);
  padding: 24px;
  overflow-y: auto;
}
</style>
