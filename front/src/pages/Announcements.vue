<template>
  <div class="page-enter">
    <el-card shadow="never" class="table-card">
      <template #header>
        <div class="card-header">
          <div class="card-title">
            <el-icon :size="20" color="var(--el-color-primary)"><Promotion /></el-icon>
            <span>班级公告</span>
          </div>
          <el-button v-if="userRole !== '学生'" type="primary" icon="Plus" @click="openAdd">发布公告</el-button>
        </div>
      </template>

      <div class="filter-bar">
        <el-input v-model="searchKey" placeholder="搜索公告标题..." prefix-icon="Search" clearable class="filter-search" />
        <el-select v-model="filterScope" placeholder="发布范围" clearable style="width:140px">
          <el-option label="全校" value="全校" />
          <el-option label="院系" value="院系" />
          <el-option label="班级" value="班级" />
        </el-select>
        <el-button icon="RefreshRight" @click="resetFilters">重置</el-button>
      </div>

      <div class="announcement-list">
        <div v-for="item in filteredList" :key="item.id" class="announcement-item" :class="{ pinned: item.pinned }">
          <div class="announcement-left">
            <div class="announcement-pin" v-if="item.pinned">
              <el-icon color="#ff3b30"><Top /></el-icon>
            </div>
            <div class="announcement-meta">
              <el-tag :type="scopeType(item.scope)" effect="light" size="small" round>{{ item.scope }}</el-tag>
              <el-tag v-if="item.pinned" type="danger" effect="light" size="small" round>置顶</el-tag>
            </div>
            <h3 class="announcement-title">{{ item.title }}</h3>
            <p class="announcement-content">{{ item.content }}</p>
            <div class="announcement-footer">
              <span><el-icon><User /></el-icon> {{ item.author }}</span>
              <span><el-icon><Clock /></el-icon> {{ item.publishTime }}</span>
              <span><el-icon><View /></el-icon> {{ item.views }} 次阅读</span>
            </div>
          </div>
          <div v-if="userRole !== '学生'" class="announcement-actions">
            <el-button type="primary" link icon="Edit" @click="openEdit(item)">编辑</el-button>
            <el-button :type="item.pinned ? 'warning' : 'success'" link :icon="item.pinned ? 'Bottom' : 'Top'" @click="togglePin(item)">
              {{ item.pinned ? '取消置顶' : '置顶' }}
            </el-button>
            <el-popconfirm title="确认删除?" @confirm="onDelete(item)" width="200">
              <template #reference><el-button type="danger" link icon="Delete">删除</el-button></template>
            </el-popconfirm>
          </div>
        </div>
      </div>

      <div class="pagination-wrap">
        <el-pagination background layout="total, prev, pager, next" :total="filteredList.length" :page-size="10" />
      </div>
    </el-card>

    <el-dialog v-model="showModal" :title="isEdit ? '编辑公告' : '发布公告'" width="600px" destroy-on-close>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="90px">
        <el-form-item label="公告标题" prop="title"><el-input v-model="form.title" placeholder="请输入标题" /></el-form-item>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="发布范围" prop="scope">
              <el-select v-model="form.scope" style="width:100%">
                <el-option label="全校" value="全校" />
                <el-option label="院系" value="院系" />
                <el-option label="班级" value="班级" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="置顶">
              <el-switch v-model="form.pinned" active-text="是" inactive-text="否" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="公告内容" prop="content">
          <el-input v-model="form.content" type="textarea" :rows="6" placeholder="请输入公告内容" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showModal=false">取消</el-button>
        <el-button type="primary" @click="onSave">{{ isEdit ? '保存' : '发布' }}</el-button>
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
const filterScope = ref('')
const showModal = ref(false)
const isEdit = ref(false)
const editId = ref(null)
const formRef = ref()

const form = ref({ title: '', content: '', scope: '全校', pinned: false })
const rules = {
  title: [{ required: true, message: '请输入标题', trigger: 'blur' }],
  content: [{ required: true, message: '请输入内容', trigger: 'blur' }],
  scope: [{ required: true, message: '请选择范围', trigger: 'change' }],
}

const announcements = ref([
  { id: 1, title: '关于2025-2026学年第一学期选课通知', content: '各位同学，新学期选课系统将于9月1日开放，请在规定时间内完成选课。必修课已自动选入，选修课需自行选择。', scope: '全校', author: '教务处', publishTime: '2026-05-10 09:00', views: 256, pinned: true },
  { id: 2, title: '计算机学院暑期实习报名', content: '计算机学院2024级学生暑期实习报名已开始，合作企业包括华为、腾讯、阿里巴巴等，请有意向的同学在5月20日前提交申请。', scope: '院系', author: '计算机学院', publishTime: '2026-05-08 14:30', views: 128, pinned: false },
  { id: 3, title: '计算机2401班班会通知', content: '本周五下午3点在A301教室召开班会，主题为"期末复习规划"，请全体同学准时参加。', scope: '班级', author: '辅导员', publishTime: '2026-05-12 10:00', views: 45, pinned: false },
  { id: 4, title: '图书馆延长开放时间通知', content: '考试周期间图书馆开放时间延长至晚上10:30，请同学们合理安排学习时间。', scope: '全校', author: '图书馆', publishTime: '2026-05-14 08:00', views: 189, pinned: true },
])

let nextId = 5
const filteredList = computed(() => {
  let list = announcements.value.filter(a => {
    const matchKey = !searchKey.value || a.title.includes(searchKey.value)
    const matchScope = !filterScope.value || a.scope === filterScope.value
    return matchKey && matchScope
  })
  return list.sort((a, b) => (b.pinned ? 1 : 0) - (a.pinned ? 1 : 0))
})

function scopeType(scope) {
  return { '全校': 'primary', '院系': 'warning', '班级': 'success' }[scope] || 'info'
}
function resetFilters() { searchKey.value = ''; filterScope.value = '' }
function togglePin(item) {
  item.pinned = !item.pinned
  ElMessage.success(item.pinned ? '已置顶' : '已取消置顶')
}
function openAdd() {
  isEdit.value = false
  form.value = { title: '', content: '', scope: '全校', pinned: false }
  showModal.value = true
}
function openEdit(row) { isEdit.value = true; editId.value = row.id; form.value = { ...row }; showModal.value = true }
function onSave() {
  formRef.value.validate(valid => {
    if (!valid) return
    if (isEdit.value) {
      const idx = announcements.value.findIndex(a => a.id === editId.value)
      if (idx !== -1) announcements.value[idx] = { ...announcements.value[idx], ...form.value }
    } else {
      announcements.value.push({ ...form.value, id: nextId++, author: '管理员', publishTime: new Date().toLocaleString('zh-CN'), views: 0 })
    }
    showModal.value = false; ElMessage.success(isEdit.value ? '保存成功' : '发布成功')
  })
}
function onDelete(row) { announcements.value = announcements.value.filter(a => a.id !== row.id); ElMessage.success('删除成功') }
</script>

<style scoped>
.table-card { min-height: 0; }
.card-header { display: flex; justify-content: space-between; align-items: center; }
.card-title { display: flex; align-items: center; gap: 8px; font-size: 16px; font-weight: 700; color: var(--text-primary); }
.filter-bar { display: flex; gap: 12px; margin-bottom: 18px; align-items: center; }
.filter-search { width: 260px; }

.announcement-list { display: flex; flex-direction: column; gap: 12px; }
.announcement-item {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  padding: 18px 20px;
  border: 1px solid var(--border-color);
  border-radius: 10px;
  transition: all 0.2s;
  background: #fff;
}
.announcement-item:hover { box-shadow: var(--shadow-md); border-color: var(--el-color-primary-light-7); }
.announcement-item.pinned { border-left: 3px solid #ff3b30; background: #fffbfb; }
.announcement-left { flex: 1; }
.announcement-pin { margin-bottom: 4px; }
.announcement-meta { display: flex; gap: 6px; margin-bottom: 8px; }
.announcement-title { font-size: 16px; font-weight: 600; color: var(--text-primary); margin: 0 0 6px; }
.announcement-content { font-size: 13px; color: var(--text-secondary); margin: 0 0 10px; line-height: 1.6; display: -webkit-box; -webkit-line-clamp: 2; -webkit-box-orient: vertical; overflow: hidden; }
.announcement-footer { display: flex; gap: 18px; font-size: 12px; color: var(--text-tertiary); }
.announcement-footer .el-icon { margin-right: 3px; vertical-align: middle; }
.announcement-actions { display: flex; flex-direction: column; gap: 4px; flex-shrink: 0; margin-left: 16px; }

.pagination-wrap { display: flex; justify-content: flex-end; margin-top: 18px; }
</style>
