<template>
  <div class="page-enter">
    <div class="stats-bar">
      <div class="stat-item">
        <span class="stat-num">{{ activities.length }}</span>
        <span class="stat-label">活动总数</span>
      </div>
      <div class="stat-item">
        <span class="stat-num">{{ activities.filter(a => a.status === '报名中').length }}</span>
        <span class="stat-label">报名中</span>
      </div>
      <div class="stat-item">
        <span class="stat-num">{{ activities.filter(a => a.status === '进行中').length }}</span>
        <span class="stat-label">进行中</span>
      </div>
    </div>

    <el-card shadow="never" class="table-card">
      <template #header>
        <div class="card-header">
          <div class="card-title">
            <el-icon :size="20" color="var(--el-color-primary)"><Calendar /></el-icon>
            <span>活动报名</span>
          </div>
          <el-button v-if="userRole !== '学生'" type="primary" icon="Plus" @click="openAdd">发布活动</el-button>
        </div>
      </template>

      <div class="filter-bar">
        <el-input v-model="searchKey" placeholder="搜索活动名称..." prefix-icon="Search" clearable class="filter-search" />
        <el-select v-model="filterStatus" placeholder="状态" clearable style="width:130px">
          <el-option label="报名中" value="报名中" />
          <el-option label="进行中" value="进行中" />
          <el-option label="已结束" value="已结束" />
        </el-select>
        <el-button icon="RefreshRight" @click="resetFilters">重置</el-button>
      </div>

      <div class="activity-grid">
        <div v-for="item in filteredList" :key="item.id" class="activity-card">
          <div class="activity-cover" :style="{ background: item.coverBg }">
            <el-tag :type="statusTagType(item.status)" effect="dark" size="small" round class="activity-status-tag">{{ item.status }}</el-tag>
          </div>
          <div class="activity-body">
            <h3 class="activity-name">{{ item.name }}</h3>
            <p class="activity-desc">{{ item.description }}</p>
            <div class="activity-info">
              <span><el-icon><Clock /></el-icon> {{ item.date }}</span>
              <span><el-icon><Location /></el-icon> {{ item.location }}</span>
            </div>
            <div class="activity-progress">
              <el-progress :percentage="Math.round(item.enrolled / item.capacity * 100)" :stroke-width="6" :color="item.enrolled / item.capacity > 0.9 ? '#ff3b30' : '#4f6ef7'" />
              <span class="progress-text">{{ item.enrolled }}/{{ item.capacity }} 人</span>
            </div>
            <div class="activity-actions">
              <el-button v-if="item.status === '报名中' && userRole === '学生'" type="primary" size="small" @click="onEnroll(item)">立即报名</el-button>
              <template v-if="userRole !== '学生'">
                <el-button v-if="item.status === '报名中'" type="primary" link icon="Edit" size="small" @click="openEdit(item)">编辑</el-button>
                <el-popconfirm title="确认删除?" @confirm="onDelete(item)" width="200">
                  <template #reference><el-button type="danger" link icon="Delete" size="small">删除</el-button></template>
                </el-popconfirm>
              </template>
            </div>
          </div>
        </div>
      </div>
    </el-card>

    <el-dialog v-model="showModal" :title="isEdit ? '编辑活动' : '发布活动'" width="540px" destroy-on-close>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="90px">
        <el-form-item label="活动名称" prop="name"><el-input v-model="form.name" placeholder="请输入" /></el-form-item>
        <el-form-item label="活动描述" prop="description"><el-input v-model="form.description" type="textarea" :rows="3" placeholder="请输入" /></el-form-item>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="活动日期" prop="date"><el-date-picker v-model="form.date" type="date" value-format="YYYY-MM-DD" style="width:100%" /></el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="活动地点" prop="location"><el-input v-model="form.location" placeholder="请输入" /></el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="容量" prop="capacity"><el-input-number v-model="form.capacity" :min="1" style="width:100%" /></el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="状态">
              <el-select v-model="form.status" style="width:100%">
                <el-option label="报名中" value="报名中" />
                <el-option label="进行中" value="进行中" />
                <el-option label="已结束" value="已结束" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <template #footer>
        <el-button @click="showModal=false">取消</el-button>
        <el-button type="primary" @click="onSave">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { ElMessage } from 'element-plus'

const userRole = computed(() => {
  try { return JSON.parse(localStorage.getItem('user') || '{}').role || '管理员' } catch { return '管理员' }
})

const searchKey = ref('')
const filterStatus = ref('')
const showModal = ref(false)
const isEdit = ref(false)
const editId = ref(null)
const formRef = ref()

const form = ref({ name: '', description: '', date: '', location: '', capacity: 50, status: '报名中' })
const rules = {
  name: [{ required: true, message: '请输入名称', trigger: 'blur' }],
  description: [{ required: true, message: '请输入描述', trigger: 'blur' }],
  date: [{ required: true, message: '请选择日期', trigger: 'change' }],
  location: [{ required: true, message: '请输入地点', trigger: 'blur' }],
}

const activities = ref([
  { id: 1, name: '校园歌手大赛', description: '一年一度的校园歌手大赛，展示你的音乐才华！', date: '2026-06-01', location: '大学生活动中心', capacity: 200, enrolled: 156, status: '报名中', coverBg: 'linear-gradient(135deg,#f472b6,#ec4899)' },
  { id: 2, name: '编程马拉松', description: '24小时编程挑战，组队参赛赢取丰厚奖品', date: '2026-05-25', location: '计算机学院实验室', capacity: 60, enrolled: 58, status: '报名中', coverBg: 'linear-gradient(135deg,#4f6ef7,#6366f1)' },
  { id: 3, name: '志愿者社区服务', description: '走进社区，关爱老人，传递温暖', date: '2026-05-20', location: '阳光社区', capacity: 30, enrolled: 30, status: '进行中', coverBg: 'linear-gradient(135deg,#059669,#10b981)' },
  { id: 4, name: '春季运动会', description: '强身健体，展现青春风采', date: '2026-04-15', location: '学校运动场', capacity: 500, enrolled: 420, status: '已结束', coverBg: 'linear-gradient(135deg,#d97706,#f59e0b)' },
])

let nextId = 5
const filteredList = computed(() => {
  return activities.value.filter(a => {
    const matchKey = !searchKey.value || a.name.includes(searchKey.value)
    const matchStatus = !filterStatus.value || a.status === filterStatus.value
    return matchKey && matchStatus
  })
})

function statusTagType(status) {
  return { '报名中': 'success', '进行中': 'primary', '已结束': 'info' }[status] || 'info'
}
function resetFilters() { searchKey.value = ''; filterStatus.value = '' }
function onEnroll(item) {
  if (item.enrolled < item.capacity) {
    item.enrolled++
    ElMessage.success('报名成功！')
  } else {
    ElMessage.warning('名额已满')
  }
}
function openAdd() {
  isEdit.value = false
  form.value = { name: '', description: '', date: '', location: '', capacity: 50, status: '报名中' }
  showModal.value = true
}
function openEdit(row) { isEdit.value = true; editId.value = row.id; form.value = { ...row }; showModal.value = true }
function onSave() {
  formRef.value.validate(valid => {
    if (!valid) return
    if (isEdit.value) {
      const idx = activities.value.findIndex(a => a.id === editId.value)
      if (idx !== -1) activities.value[idx] = { ...activities.value[idx], ...form.value }
    } else {
      activities.value.push({ ...form.value, id: nextId++, enrolled: 0, coverBg: 'linear-gradient(135deg,#4f6ef7,#6366f1)' })
    }
    showModal.value = false; ElMessage.success(isEdit.value ? '编辑成功' : '发布成功')
  })
}
function onDelete(row) { activities.value = activities.value.filter(a => a.id !== row.id); ElMessage.success('删除成功') }
</script>

<style scoped>
.stats-bar { display: flex; gap: 16px; margin-bottom: 20px; }
.stat-item { flex: 1; background: #fff; border-radius: 10px; padding: 18px 22px; border: 1px solid var(--border-color); display: flex; align-items: baseline; gap: 10px; transition: box-shadow 0.2s; }
.stat-item:hover { box-shadow: var(--shadow-md); }
.stat-num { font-size: 28px; font-weight: 800; color: var(--el-color-primary); }
.stat-label { font-size: 13px; color: var(--text-tertiary); }
.table-card { min-height: 0; }
.card-header { display: flex; justify-content: space-between; align-items: center; }
.card-title { display: flex; align-items: center; gap: 8px; font-size: 16px; font-weight: 700; color: var(--text-primary); }
.filter-bar { display: flex; gap: 12px; margin-bottom: 18px; align-items: center; }
.filter-search { width: 260px; }

.activity-grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(280px, 1fr)); gap: 16px; }
.activity-card { border: 1px solid var(--border-color); border-radius: 12px; overflow: hidden; transition: all 0.2s; background: #fff; }
.activity-card:hover { box-shadow: var(--shadow-md); transform: translateY(-2px); }
.activity-cover { height: 100px; position: relative; display: flex; align-items: flex-start; justify-content: flex-end; padding: 10px; }
.activity-status-tag { position: absolute; top: 10px; right: 10px; }
.activity-body { padding: 16px; }
.activity-name { font-size: 16px; font-weight: 600; margin: 0 0 6px; color: var(--text-primary); }
.activity-desc { font-size: 12px; color: var(--text-tertiary); margin: 0 0 10px; display: -webkit-box; -webkit-line-clamp: 2; -webkit-box-orient: vertical; overflow: hidden; }
.activity-info { display: flex; gap: 16px; font-size: 12px; color: var(--text-tertiary); margin-bottom: 10px; }
.activity-info .el-icon { margin-right: 3px; vertical-align: middle; }
.activity-progress { margin-bottom: 12px; }
.progress-text { font-size: 12px; color: var(--text-tertiary); }
.activity-actions { display: flex; gap: 8px; align-items: center; }
</style>
