<template>
  <div class="page-enter">
    <!-- Stats bar -->
    <div class="stats-bar">
      <div class="stat-item">
        <span class="stat-num">{{ scores.length }}</span>
        <span class="stat-label">成绩记录</span>
      </div>
      <div class="stat-item">
        <span class="stat-num">{{ avgScore }}</span>
        <span class="stat-label">平均分</span>
      </div>
      <div class="stat-item">
        <span class="stat-num">{{ passRate }}%</span>
        <span class="stat-label">及格率</span>
      </div>
      <div class="stat-item">
        <span class="stat-num">{{ courseList.length }}</span>
        <span class="stat-label">涉及课程</span>
      </div>
    </div>

    <el-card shadow="never" class="table-card">
      <template #header>
        <div class="card-header">
          <div class="card-title">
            <el-icon :size="20" color="var(--el-color-primary)"><Document /></el-icon>
            <span>成绩列表</span>
          </div>
          <el-button v-if="userRole !== '学生'" type="primary" icon="Plus" @click="openAdd">录入成绩</el-button>
        </div>
      </template>

      <div class="filter-bar">
        <el-input v-model="searchKey" placeholder="搜索学号 / 姓名..." prefix-icon="Search" clearable class="filter-search" @input="filterList" />
        <el-select v-model="filterCourse" placeholder="选择课程" clearable style="width:200px" @change="filterList">
          <el-option v-for="c in courseList" :key="c" :label="c" :value="c" />
        </el-select>
        <el-button icon="RefreshRight" @click="resetFilters">重置</el-button>
      </div>

      <el-table :data="filteredList" stripe style="width:100%" class="data-table">
        <el-table-column prop="studentCode" label="学号" width="130">
          <template #default="{ row }">
            <span class="cell-code">{{ row.studentCode }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="studentName" label="姓名" width="100">
          <template #default="{ row }">
            <div class="cell-name">
              <el-avatar :size="30" class="name-avatar">{{ row.studentName.charAt(0) }}</el-avatar>
              <span>{{ row.studentName }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="courseName" label="课程" min-width="150">
          <template #default="{ row }">
            <span class="cell-bold">{{ row.courseName }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="score" label="成绩" width="120" align="center">
          <template #default="{ row }">
            <div class="score-cell">
              <span class="score-value" :class="getScoreClass(row.score)">{{ row.score }}</span>
              <span class="score-level">{{ getScoreLevel(row.score) }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="semester" label="学期" width="160" />
        <el-table-column v-if="userRole !== '学生'" label="操作" width="140" align="center" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link icon="Edit" @click="openEdit(row)">编辑</el-button>
            <el-popconfirm title="确认删除?" @confirm="onDelete(row)" width="200">
              <template #reference>
                <el-button type="danger" link icon="Delete">删除</el-button>
              </template>
            </el-popconfirm>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-wrap">
        <el-pagination background layout="total, prev, pager, next" :total="filteredList.length" :page-size="10" />
      </div>
    </el-card>

    <el-dialog v-model="showModal" :title="isEdit ? '编辑成绩' : '录入成绩'" width="500px" destroy-on-close>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="80px">
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="学号" prop="studentCode">
              <el-input v-model="form.studentCode" placeholder="请输入学号" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="姓名" prop="studentName">
              <el-input v-model="form.studentName" placeholder="请输入姓名" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="课程" prop="courseName">
          <el-input v-model="form.courseName" placeholder="请输入课程名" />
        </el-form-item>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="成绩" prop="score">
              <el-input-number v-model="form.score" :min="0" :max="100" style="width:100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="学期" prop="semester">
              <el-select v-model="form.semester" placeholder="选择学期" style="width:100%">
                <el-option label="2024-2025 第一学期" value="2024-2025-1" />
                <el-option label="2024-2025 第二学期" value="2024-2025-2" />
                <el-option label="2025-2026 第一学期" value="2025-2026-1" />
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
const filterCourse = ref('')
const showModal = ref(false)
const isEdit = ref(false)
const editId = ref(null)
const formRef = ref()

const form = ref({ studentCode: '', studentName: '', courseName: '', score: 80, semester: '' })
const rules = {
  studentCode: [{ required: true, message: '请输入学号', trigger: 'blur' }],
  studentName: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
  courseName: [{ required: true, message: '请输入课程名', trigger: 'blur' }],
  score: [{ required: true, message: '请输入成绩', trigger: 'blur' }],
}

const scores = ref([
  { id: 1, studentCode: '2024001', studentName: '张三', courseName: '计算机导论', score: 92, semester: '2024-2025-1' },
  { id: 2, studentCode: '2024001', studentName: '张三', courseName: '数据结构', score: 88, semester: '2024-2025-1' },
  { id: 3, studentCode: '2024002', studentName: '李四', courseName: '计算机导论', score: 95, semester: '2024-2025-1' },
  { id: 4, studentCode: '2024003', studentName: '王五', courseName: '数据结构', score: 58, semester: '2024-2025-1' },
  { id: 5, studentCode: '2024002', studentName: '李四', courseName: '音乐欣赏', score: 85, semester: '2024-2025-2' },
])

let nextId = 6
const courseList = computed(() => [...new Set(scores.value.map(s => s.courseName))])
const avgScore = computed(() => {
  if (!scores.value.length) return 0
  return Math.round(scores.value.reduce((sum, s) => sum + s.score, 0) / scores.value.length)
})
const passRate = computed(() => {
  if (!scores.value.length) return 0
  return Math.round(scores.value.filter(s => s.score >= 60).length / scores.value.length * 100)
})
const filteredList = computed(() => {
  return scores.value.filter(s => {
    const matchKey = !searchKey.value || s.studentName.includes(searchKey.value) || s.studentCode.includes(searchKey.value)
    const matchCourse = !filterCourse.value || s.courseName === filterCourse.value
    return matchKey && matchCourse
  })
})

function getScoreClass(score) {
  if (score >= 90) return 'excellent'
  if (score >= 80) return 'good'
  if (score >= 60) return 'pass'
  return 'fail'
}
function getScoreLevel(score) {
  if (score >= 90) return '优秀'
  if (score >= 80) return '良好'
  if (score >= 60) return '及格'
  return '不及格'
}
function filterList() {}
function resetFilters() {
  searchKey.value = ''
  filterCourse.value = ''
}
function openAdd() {
  isEdit.value = false
  form.value = { studentCode: '', studentName: '', courseName: '', score: 80, semester: '' }
  showModal.value = true
}
function openEdit(row) {
  isEdit.value = true
  editId.value = row.id
  form.value = { ...row }
  showModal.value = true
}
function onSave() {
  formRef.value.validate(valid => {
    if (!valid) return
    if (isEdit.value) {
      const idx = scores.value.findIndex(s => s.id === editId.value)
      if (idx !== -1) scores.value[idx] = { ...form.value, id: editId.value }
    } else {
      scores.value.push({ ...form.value, id: nextId++ })
    }
    showModal.value = false
    ElMessage.success(isEdit.value ? '编辑成功' : '录入成功')
  })
}
function onDelete(row) {
  scores.value = scores.value.filter(s => s.id !== row.id)
  ElMessage.success('删除成功')
}
</script>

<style scoped>
.stats-bar {
  display: flex;
  gap: 16px;
  margin-bottom: 20px;
}
.stat-item {
  flex: 1;
  background: #fff;
  border-radius: 10px;
  padding: 18px 22px;
  border: 1px solid var(--border-color);
  display: flex;
  align-items: baseline;
  gap: 10px;
  transition: box-shadow 0.2s;
}
.stat-item:hover { box-shadow: var(--shadow-md); }
.stat-num {
  font-size: 28px;
  font-weight: 800;
  color: var(--el-color-primary);
}
.stat-label {
  font-size: 13px;
  color: var(--text-tertiary);
}

.table-card { min-height: 0; }
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.card-title {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 16px;
  font-weight: 700;
  color: var(--text-primary);
}

.filter-bar {
  display: flex;
  gap: 12px;
  margin-bottom: 18px;
  align-items: center;
}
.filter-search { width: 260px; }

.data-table { border-radius: 10px; overflow: hidden; }

.cell-code {
  font-family: 'SF Mono', 'Menlo', monospace;
  font-size: 13px;
  color: var(--text-secondary);
}
.cell-name {
  display: flex;
  align-items: center;
  gap: 8px;
}
.name-avatar {
  background: linear-gradient(135deg, #4f6ef7 0%, #6366f1 100%);
  color: #fff;
  font-size: 12px;
  font-weight: 600;
  flex-shrink: 0;
}
.cell-bold { font-weight: 500; }

.score-cell {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 2px;
}
.score-value {
  font-size: 18px;
  font-weight: 800;
}
.score-value.excellent { color: #059669; }
.score-value.good { color: #4f6ef7; }
.score-value.pass { color: #f5a623; }
.score-value.fail { color: #ff3b30; }
.score-level {
  font-size: 11px;
  color: var(--text-tertiary);
}

.pagination-wrap {
  display: flex;
  justify-content: flex-end;
  margin-top: 18px;
}
</style>
