<template>
  <div class="page-enter">
    <!-- Welcome banner -->
    <div class="welcome-banner">
      <div class="welcome-text">
        <h2>你好，{{ userName }}（{{ userRoleDisplay }}）👋</h2>
        <p>{{ today }} · 欢迎回到学工管理系统</p>
      </div>
      <div class="welcome-art">
        <svg width="200" height="80" viewBox="0 0 200 80" fill="none">
          <ellipse cx="100" cy="60" rx="90" ry="18" fill="rgba(255,255,255,0.1)" />
          <circle cx="40" cy="35" r="18" fill="rgba(255,255,255,0.15)" />
          <circle cx="100" cy="25" r="25" fill="rgba(255,255,255,0.12)" />
          <circle cx="155" cy="40" r="14" fill="rgba(255,255,255,0.1)" />
        </svg>
      </div>
    </div>

    <!-- Metric cards -->
    <el-row :gutter="20" class="metric-row">
      <el-col :span="6" v-for="m in metrics" :key="m.label">
        <div class="metric-card" :style="{ background: m.bg }">
          <div class="metric-icon-wrap" :style="{ background: m.iconBg }">
            <el-icon :size="24" color="#fff"><component :is="m.icon" /></el-icon>
          </div>
          <div class="metric-info">
            <div class="metric-value">{{ m.value }}</div>
            <div class="metric-label">{{ m.label }}</div>
          </div>
          <div class="metric-trend" :class="m.trendUp ? 'up' : 'down'">
            <el-icon><Top v-if="m.trendUp" /><Bottom v-else /></el-icon>
            <span>{{ m.trend }}</span>
          </div>
        </div>
      </el-col>
    </el-row>

    <!-- Main content -->
    <el-row :gutter="20">
      <!-- Recent activities -->
      <el-col :span="15">
        <el-card class="section-card" shadow="never">
          <template #header>
            <div class="section-header">
              <div>
                <span class="section-title">最近活动</span>
                <span class="section-sub">系统操作记录</span>
              </div>
              <el-button text type="primary" size="small">查看全部</el-button>
            </div>
          </template>
          <div class="activity-list">
            <div v-for="(a, i) in activities" :key="i" class="activity-item">
              <div class="activity-dot" :style="{ background: a.color }" />
              <div class="activity-content">
                <div class="activity-text">{{ a.text }}</div>
                <div class="activity-time">{{ a.time }}</div>
              </div>
            </div>
          </div>
        </el-card>
      </el-col>

      <!-- Right column -->
      <el-col :span="9">
        <!-- Quick actions -->
        <el-card class="section-card" shadow="never">
          <template #header>
            <div class="section-header">
              <div>
                <span class="section-title">快捷操作</span>
                <span class="section-sub">常用功能入口</span>
              </div>
            </div>
          </template>
          <div class="quick-grid">
            <div
              v-for="q in quickActions"
              :key="q.label"
              class="quick-item"
              @click="$router.push(q.route)"
            >
              <div class="quick-icon" :style="{ background: q.bg }">
                <el-icon :size="22" color="#fff"><component :is="q.icon" /></el-icon>
              </div>
              <span class="quick-label">{{ q.label }}</span>
            </div>
          </div>
        </el-card>

        <!-- System info -->
        <el-card class="section-card" shadow="never" style="margin-top:20px">
          <template #header>
            <div class="section-header">
              <div>
                <span class="section-title">系统信息</span>
              </div>
            </div>
          </template>
          <div class="info-list">
            <div class="info-item" v-for="info in sysInfo" :key="info.label">
              <span class="info-label">{{ info.label }}</span>
              <span class="info-value">{{ info.value }}</span>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import request from '../api/request'

const userObj = computed(() => {
  try { return JSON.parse(localStorage.getItem('user') || '{}') } catch { return {} }
})
const userName = computed(() => userObj.value.name || '管理员')
const userRole = computed(() => userObj.value.role || '管理员')
const userRoleDisplay = computed(() => ({ '管理员': '超级管理员', '教师': '教师', '学生': '学生' }[userRole.value] || '超级管理员'))
const today = new Date().toLocaleDateString('zh-CN', { year: 'numeric', month: 'long', day: 'numeric', weekday: 'long' })

// 统计数据
const studentCount = ref(0)
const courseCount = ref(0)
const pendingCount = ref(0)

onMounted(async () => {
  try {
    // 获取学生数量
    const studentRes = await request.get('/students/count', { params: { status: '在校' } })
    studentCount.value = studentRes.data || 0
    
    // 获取课程数量
    const courseRes = await request.get('/courses')
    courseCount.value = courseRes.data?.length || 0
    
    // 获取待处理事项数量
    const scholarshipRes = await request.get('/scholarships', { params: { status: '待审核' } })
    pendingCount.value = scholarshipRes.data?.length || 0
  } catch (err) {
    console.error('获取统计数据失败:', err)
    // 使用默认值
    studentCount.value = 1286
    courseCount.value = 56
    pendingCount.value = 8
  }
})

// 管理员/教师仪表盘
const adminMetrics = computed(() => [
  { label: '在校学生', value: studentCount.value.toLocaleString(), bg: 'linear-gradient(135deg, #eef2ff 0%, #e0e7ff 100%)', iconBg: 'linear-gradient(135deg, #4f6ef7 0%, #6366f1 100%)', icon: 'User', trend: '12%', trendUp: true },
  { label: '课程总数', value: courseCount.value.toString(), bg: 'linear-gradient(135deg, #f5f3ff 0%, #ede9fe 100%)', iconBg: 'linear-gradient(135deg, #7c3aed 0%, #8b5cf6 100%)', icon: 'Reading', trend: '3', trendUp: true },
  { label: '今日签到', value: '842', bg: 'linear-gradient(135deg, #ecfdf5 0%, #d1fae5 100%)', iconBg: 'linear-gradient(135deg, #059669 0%, #10b981 100%)', icon: 'Check', trend: '8%', trendUp: true },
  { label: '待处理事项', value: pendingCount.value.toString(), bg: 'linear-gradient(135deg, #fffbeb 0%, #fef3c7 100%)', iconBg: 'linear-gradient(135deg, #d97706 0%, #f59e0b 100%)', icon: 'Bell', trend: '2', trendUp: false },
])

// 学生仪表盘
const studentMetrics = [
  { label: '我的成绩', value: '92', bg: 'linear-gradient(135deg, #ecfdf5 0%, #d1fae5 100%)', iconBg: 'linear-gradient(135deg, #059669 0%, #10b981 100%)', icon: 'Document', trend: '优秀', trendUp: true },
  { label: '已修学分', value: '48', bg: 'linear-gradient(135deg, #eef2ff 0%, #e0e7ff 100%)', iconBg: 'linear-gradient(135deg, #4f6ef7 0%, #6366f1 100%)', icon: 'Reading', trend: '6分', trendUp: true },
  { label: '第二课堂', value: '62', bg: 'linear-gradient(135deg, #f5f3ff 0%, #ede9fe 100%)', iconBg: 'linear-gradient(135deg, #7c3aed 0%, #8b5cf6 100%)', icon: 'Star', trend: '18分', trendUp: true },
  { label: '待办事项', value: '3', bg: 'linear-gradient(135deg, #fffbeb 0%, #fef3c7 100%)', iconBg: 'linear-gradient(135deg, #d97706 0%, #f59e0b 100%)', icon: 'Bell', trend: '1项', trendUp: false },
]

const metrics = computed(() => userRole.value === '学生' ? studentMetrics : adminMetrics.value)

// 管理员/教师活动
const adminActivities = [
  { text: '新增学生：张三（计算机2401）', time: '10 分钟前', color: '#4f6ef7' },
  { text: '课程「数据结构」考试安排已发布', time: '1 小时前', color: '#7c3aed' },
  { text: '学生信息批量导入完成（50 条）', time: '2 小时前', color: '#059669' },
  { text: '系统主题更新', time: '1 天前', color: '#8e8e93' },
  { text: '学期成绩录入截止提醒', time: '2 天前', color: '#d97706' },
]

// 学生活动
const studentActivities = [
  { text: '课程「数据结构」期末考试安排已发布', time: '1 小时前', color: '#4f6ef7' },
  { text: '你的国家奖学金申请已通过审批', time: '3 小时前', color: '#059669' },
  { text: '班级公告：期末复习规划班会通知', time: '1 天前', color: '#7c3aed' },
  { text: '第二课堂积分已更新（+10）', time: '2 天前', color: '#d97706' },
  { text: '外出报备申请已通过', time: '3 天前', color: '#059669' },
]

const activities = computed(() => userRole.value === '学生' ? studentActivities : adminActivities)

// 管理员快捷操作
const adminQuickActions = [
  { icon: 'User', label: '学生管理', route: '/students', bg: 'linear-gradient(135deg, #4f6ef7 0%, #6366f1 100%)' },
  { icon: 'Reading', label: '课程管理', route: '/courses', bg: 'linear-gradient(135deg, #7c3aed 0%, #8b5cf6 100%)' },
  { icon: 'Document', label: '成绩录入', route: '/scores', bg: 'linear-gradient(135deg, #059669 0%, #10b981 100%)' },
  { icon: 'Trophy', label: '奖助申请', route: '/scholarship', bg: 'linear-gradient(135deg, #d97706 0%, #f59e0b 100%)' },
]

// 学生快捷操作
const studentQuickActions = [
  { icon: 'Document', label: '我的成绩', route: '/scores', bg: 'linear-gradient(135deg, #059669 0%, #10b981 100%)' },
  { icon: 'Trophy', label: '奖助申请', route: '/scholarship', bg: 'linear-gradient(135deg, #d97706 0%, #f59e0b 100%)' },
  { icon: 'Calendar', label: '活动报名', route: '/activities', bg: 'linear-gradient(135deg, #4f6ef7 0%, #6366f1 100%)' },
  { icon: 'Position', label: '外出报备', route: '/leave-requests', bg: 'linear-gradient(135deg, #7c3aed 0%, #8b5cf6 100%)' },
]

const quickActions = computed(() => userRole.value === '学生' ? studentQuickActions : adminQuickActions)

const sysInfo = ref([
  { label: '系统版本', value: 'v2.4.0' },
  { label: '最后更新', value: '2026-05-14' },
  { label: '在线用户', value: '23 人' },
  { label: '服务器状态', value: '正常运行' },
])
</script>

<style scoped>
.welcome-banner {
  background: linear-gradient(135deg, #4f6ef7 0%, #6366f1 50%, #7c3aed 100%);
  border-radius: 14px;
  padding: 28px 32px;
  margin-bottom: 24px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  position: relative;
  overflow: hidden;
}
.welcome-banner::before {
  content: '';
  position: absolute;
  width: 300px;
  height: 300px;
  border-radius: 50%;
  background: rgba(255,255,255,0.05);
  top: -120px;
  right: -60px;
}
.welcome-text h2 {
  color: #fff;
  font-size: 22px;
  font-weight: 700;
  margin-bottom: 6px;
}
.welcome-text p {
  color: rgba(255,255,255,0.75);
  font-size: 14px;
}

.metric-row {
  margin-bottom: 24px;
}
.metric-card {
  border-radius: 14px;
  padding: 22px;
  display: flex;
  align-items: center;
  gap: 16px;
  position: relative;
  transition: transform 0.2s ease, box-shadow 0.2s ease;
  border: 1px solid rgba(0,0,0,0.04);
}
.metric-card:hover {
  transform: translateY(-3px);
  box-shadow: 0 8px 24px rgba(0,0,0,0.08);
}
.metric-icon-wrap {
  width: 50px;
  height: 50px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  box-shadow: 0 4px 12px rgba(0,0,0,0.15);
}
.metric-info {
  flex: 1;
}
.metric-value {
  font-size: 26px;
  font-weight: 800;
  color: var(--text-primary);
  line-height: 1.2;
}
.metric-label {
  font-size: 13px;
  color: var(--text-tertiary);
  margin-top: 2px;
}
.metric-trend {
  display: flex;
  align-items: center;
  gap: 3px;
  font-size: 12px;
  font-weight: 600;
  padding: 3px 8px;
  border-radius: 20px;
  position: absolute;
  top: 14px;
  right: 14px;
}
.metric-trend.up {
  background: rgba(5,150,105,0.1);
  color: #059669;
}
.metric-trend.down {
  background: rgba(220,38,38,0.1);
  color: #dc2626;
}

.section-card {
  margin-bottom: 0;
}
.section-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-end;
}
.section-title {
  font-size: 16px;
  font-weight: 700;
  color: var(--text-primary);
}
.section-sub {
  display: block;
  font-size: 12px;
  color: var(--text-tertiary);
  margin-top: 2px;
}

.activity-list {
  display: flex;
  flex-direction: column;
}
.activity-item {
  display: flex;
  gap: 14px;
  padding: 14px 0;
  border-bottom: 1px solid #f5f5f5;
  align-items: flex-start;
}
.activity-item:last-child {
  border-bottom: none;
  padding-bottom: 0;
}
.activity-item:first-child {
  padding-top: 0;
}
.activity-dot {
  width: 10px;
  height: 10px;
  border-radius: 50%;
  flex-shrink: 0;
  margin-top: 5px;
  box-shadow: 0 0 0 3px rgba(0,0,0,0.04);
}
.activity-text {
  font-size: 14px;
  color: var(--text-primary);
  line-height: 1.5;
}
.activity-time {
  font-size: 12px;
  color: var(--text-tertiary);
  margin-top: 2px;
}

.quick-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 14px;
}
.quick-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 10px;
  padding: 20px 12px;
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.2s ease;
  border: 1px solid #f2f3f5;
  background: #fafbfc;
}
.quick-item:hover {
  background: #fff;
  border-color: var(--el-color-primary-light-7);
  box-shadow: 0 4px 16px rgba(79,110,247,0.12);
  transform: translateY(-2px);
}
.quick-icon {
  width: 46px;
  height: 46px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 4px 10px rgba(0,0,0,0.15);
}
.quick-label {
  font-size: 13px;
  font-weight: 500;
  color: var(--text-secondary);
}

.info-list {
  display: flex;
  flex-direction: column;
}
.info-item {
  display: flex;
  justify-content: space-between;
  padding: 10px 0;
  border-bottom: 1px solid #f5f5f5;
  font-size: 13px;
}
.info-item:last-child {
  border-bottom: none;
}
.info-label {
  color: var(--text-tertiary);
}
.info-value {
  color: var(--text-primary);
  font-weight: 500;
}
</style>
