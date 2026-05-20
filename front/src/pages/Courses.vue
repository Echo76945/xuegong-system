<template>
  <div class="page-enter">
    <!-- Stats bar -->
    <div class="stats-bar">
      <div class="stat-item">
        <span class="stat-num">{{ courses.length }}</span>
        <span class="stat-label">课程总数</span>
      </div>
      <div class="stat-item">
        <span class="stat-num">{{ courses.filter(c => c.type === '必修').length }}</span>
        <span class="stat-label">必修课程</span>
      </div>
      <div class="stat-item">
        <span class="stat-num">{{ courses.filter(c => c.type === '选修').length }}</span>
        <span class="stat-label">选修课程</span>
      </div>
      <div class="stat-item">
        <span class="stat-num">{{ totalEnrolled }}</span>
        <span class="stat-label">选课人次</span>
      </div>
    </div>

    <el-card shadow="never" class="table-card">
      <template #header>
        <div class="card-header">
          <div class="card-title">
            <el-icon :size="20" color="var(--el-color-primary)"><Reading /></el-icon>
            <span>课程列表</span>
          </div>
          <el-button v-if="userRole !== '学生'" type="primary" icon="Plus" @click="openAdd">添加课程</el-button>
        </div>
      </template>

      <div class="filter-bar">
        <el-input v-model="searchKey" placeholder="搜索课程名 / 课程号..." prefix-icon="Search" clearable class="filter-search" @input="filterList" />
        <el-select v-model="filterType" placeholder="课程类型" clearable style="width:140px" @change="filterList">
          <el-option label="必修" value="必修" />
          <el-option label="选修" value="选修" />
        </el-select>
        <el-button icon="RefreshRight" @click="resetFilters">重置</el-button>
      </div>

      <el-table :data="filteredList" stripe style="width:100%" class="data-table">
        <el-table-column prop="code" label="课程号" width="110">
          <template #default="{ row }">
            <span class="cell-code">{{ row.code }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="name" label="课程名" min-width="150">
          <template #default="{ row }">
            <span class="cell-bold">{{ row.name }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="type" label="类型" width="90" align="center">
          <template #default="{ row }">
            <el-tag :type="row.type === '必修' ? 'primary' : 'warning'" effect="light" size="small" round>{{ row.type }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="credit" label="学分" width="80" align="center">
          <template #default="{ row }">
            <span class="cell-highlight">{{ row.credit }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="teacher" label="授课教师" width="120" />
        <el-table-column prop="schedule" label="上课时间" width="140" />
        <el-table-column label="选课容量" width="200">
          <template #default="{ row }">
            <div class="capacity-cell">
              <el-progress
                :percentage="Math.round(row.enrolled / row.capacity * 100)"
                :stroke-width="8"
                :color="getProgressColor(row.enrolled / row.capacity)"
                style="flex:1"
              />
              <span class="capacity-text">{{ row.enrolled }}/{{ row.capacity }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column v-if="userRole !== '学生'" label="操作" width="140" align="center" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link icon="Edit" @click="openEdit(row)">编辑</el-button>
            <el-popconfirm title="确认删除该课程?" @confirm="onDelete(row)" width="220">
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

    <el-dialog v-model="showModal" :title="isEdit ? '编辑课程' : '添加课程'" width="540px" destroy-on-close>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="90px">
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="课程号" prop="code">
              <el-input v-model="form.code" placeholder="请输入课程号" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="课程名" prop="name">
              <el-input v-model="form.name" placeholder="请输入课程名" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="类型">
              <el-radio-group v-model="form.type">
                <el-radio label="必修" />
                <el-radio label="选修" />
              </el-radio-group>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="学分" prop="credit">
              <el-input-number v-model="form.credit" :min="0.5" :max="10" :step="0.5" style="width:100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="授课教师" prop="teacher">
          <el-input v-model="form.teacher" placeholder="请输入教师姓名" />
        </el-form-item>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="上课时间">
              <el-input v-model="form.schedule" placeholder="如：周一 3-4节" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="容量" prop="capacity">
              <el-input-number v-model="form.capacity" :min="1" :max="200" style="width:100%" />
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
const filterType = ref('')
const showModal = ref(false)
const isEdit = ref(false)
const editId = ref(null)
const formRef = ref()

const form = ref({ code: '', name: '', type: '必修', credit: 3, teacher: '', schedule: '', capacity: 60, enrolled: 0 })
const rules = {
  code: [{ required: true, message: '请输入课程号', trigger: 'blur' }],
  name: [{ required: true, message: '请输入课程名', trigger: 'blur' }],
  teacher: [{ required: true, message: '请输入教师', trigger: 'blur' }],
}

const courses = ref([
  { id: 1, code: 'CS101', name: '计算机导论', type: '必修', credit: 3, teacher: '王教授', schedule: '周一 3-4节', capacity: 60, enrolled: 45 },
  { id: 2, code: 'CS201', name: '数据结构', type: '必修', credit: 4, teacher: '李教授', schedule: '周二 1-2节', capacity: 50, enrolled: 48 },
  { id: 3, code: 'CS301', name: '操作系统', type: '必修', credit: 4, teacher: '张教授', schedule: '周三 5-6节', capacity: 45, enrolled: 40 },
  { id: 4, code: 'EL101', name: '音乐欣赏', type: '选修', credit: 2, teacher: '陈老师', schedule: '周四 7-8节', capacity: 80, enrolled: 62 },
  { id: 5, code: 'EL201', name: '摄影入门', type: '选修', credit: 2, teacher: '刘老师', schedule: '周五 3-4节', capacity: 40, enrolled: 38 },
])

let nextId = 6
const totalEnrolled = computed(() => courses.value.reduce((sum, c) => sum + c.enrolled, 0))
const filteredList = computed(() => {
  return courses.value.filter(c => {
    const matchKey = !searchKey.value || c.name.includes(searchKey.value) || c.code.includes(searchKey.value)
    const matchType = !filterType.value || c.type === filterType.value
    return matchKey && matchType
  })
})

function getProgressColor(ratio) {
  if (ratio > 0.9) return '#ff3b30'
  if (ratio > 0.7) return '#f5a623'
  return '#4f6ef7'
}
function filterList() {}
function resetFilters() {
  searchKey.value = ''
  filterType.value = ''
}
function openAdd() {
  isEdit.value = false
  form.value = { code: '', name: '', type: '必修', credit: 3, teacher: '', schedule: '', capacity: 60, enrolled: 0 }
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
      const idx = courses.value.findIndex(c => c.id === editId.value)
      if (idx !== -1) courses.value[idx] = { ...form.value, id: editId.value }
    } else {
      courses.value.push({ ...form.value, id: nextId++ })
    }
    showModal.value = false
    ElMessage.success(isEdit.value ? '编辑成功' : '添加成功')
  })
}
function onDelete(row) {
  courses.value = courses.value.filter(c => c.id !== row.id)
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
.stat-item:hover {
  box-shadow: var(--shadow-md);
}
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
.cell-bold { font-weight: 500; }
.cell-highlight {
  font-weight: 700;
  color: var(--el-color-primary);
}

.capacity-cell {
  display: flex;
  align-items: center;
  gap: 10px;
}
.capacity-text {
  font-size: 12px;
  color: var(--text-tertiary);
  white-space: nowrap;
  min-width: 50px;
}

.pagination-wrap {
  display: flex;
  justify-content: flex-end;
  margin-top: 18px;
}
</style>
