import { createRouter, createWebHashHistory } from 'vue-router'
import MainLayout from '../layouts/MainLayout.vue'
import Home from '../pages/Home.vue'
import Students from '../pages/Students.vue'
import Courses from '../pages/Courses.vue'
import Scores from '../pages/Scores.vue'
import Scholarship from '../pages/Scholarship.vue'
import Login from '../pages/Login.vue'
import Users from '../pages/Users.vue'
import Family from '../pages/Family.vue'
import Awards from '../pages/Awards.vue'
import Extracurricular from '../pages/Extracurricular.vue'
import Announcements from '../pages/Announcements.vue'
import Messages from '../pages/Messages.vue'
import Activities from '../pages/Activities.vue'
import LateRecords from '../pages/LateRecords.vue'
import LeaveRequests from '../pages/LeaveRequests.vue'
import Incidents from '../pages/Incidents.vue'
import AiAssistant from '../pages/AiAssistant.vue'
import Profile from '../pages/Profile.vue'
import Settings from '../pages/Settings.vue'
import Debug from '../pages/Debug.vue'

const routes = [
  {
    path: '/',
    component: MainLayout,
    redirect: '/home',
    children: [
      { path: 'home', name: 'home', component: Home, meta: { title: '仪表盘' } },
      // 学生信息
      { path: 'students', name: 'students', component: Students, meta: { title: '学生管理' } },
      { path: 'family', name: 'family', component: Family, meta: { title: '家庭情况' } },
      { path: 'awards', name: 'awards', component: Awards, meta: { title: '评奖评优' } },
      // 学业发展
      { path: 'courses', name: 'courses', component: Courses, meta: { title: '课程管理' } },
      { path: 'scores', name: 'scores', component: Scores, meta: { title: '成绩管理' } },
      { path: 'extracurricular', name: 'extracurricular', component: Extracurricular, meta: { title: '第二课堂' } },
      // 奖助服务
      { path: 'scholarship', name: 'scholarship', component: Scholarship, meta: { title: '奖助申请' } },
      // 沟通互动
      { path: 'announcements', name: 'announcements', component: Announcements, meta: { title: '班级公告' } },
      { path: 'activities', name: 'activities', component: Activities, meta: { title: '活动报名' } },
      { path: 'messages', name: 'messages', component: Messages, meta: { title: '私信系统' } },
      // 安全管理
      { path: 'late-records', name: 'late-records', component: LateRecords, meta: { title: '晚归登记' } },
      { path: 'leave-requests', name: 'leave-requests', component: LeaveRequests, meta: { title: '外出报备' } },
      { path: 'incidents', name: 'incidents', component: Incidents, meta: { title: '异常上报' } },
      // 系统
      { path: 'users', name: 'users', component: Users, meta: { title: '用户管理' } },
      { path: 'ai-assistant', name: 'ai-assistant', component: AiAssistant, meta: { title: 'AI 助理' } },
      { path: 'profile', name: 'profile', component: Profile, meta: { title: '个人中心' } },
      { path: 'settings', name: 'settings', component: Settings, meta: { title: '系统设置' } },
      // 调试页面
      { path: 'debug', name: 'debug', component: Debug, meta: { title: '调试页面' } },
    ],
  },
  // 登录
  { path: '/login', name: 'login', component: Login, meta: { title: '登录' } },
]

const router = createRouter({
  history: createWebHashHistory(),
  routes,
})

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

router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')
  const userStr = localStorage.getItem('user')
  
  // 验证token和用户信息是否完整
  let isValidLogin = false
  if (token && userStr) {
    try {
      const user = JSON.parse(userStr)
      isValidLogin = user.username && user.role
    } catch (e) {
      isValidLogin = false
    }
  }

  // 未登录 -> 跳转登录
  if (to.path !== '/login' && !isValidLogin) {
    // 清除无效的登录状态
    localStorage.removeItem('token')
    localStorage.removeItem('user')
    next('/login')
    return
  }

  // 已登录访问登录页 -> 跳转首页
  if (to.path === '/login' && isValidLogin) {
    next('/home')
    return
  }

  // 权限校验
  if (to.path !== '/login' && isValidLogin) {
    let role = '管理员'
    try { role = JSON.parse(userStr).role || '管理员' } catch {}
    const perms = rolePermissions[role] || rolePermissions['管理员']
    if (!perms.includes(to.path)) {
      next('/home')
      return
    }
  }

  next()
})

export default router
