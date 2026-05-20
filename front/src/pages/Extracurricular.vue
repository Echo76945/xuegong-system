<template>
  <div class="page-enter">
    <div class="stats-bar">
      <div class="stat-item">
        <span class="stat-num">{{ records.length }}</span>
        <span class="stat-label">活动记录</span>
      </div>
      <div class="stat-item">
        <span class="stat-num">{{ totalPoints }}</span>
        <span class="stat-label">总积分</span>
      </div>
      <div class="stat-item">
        <span class="stat-num">{{ [...new Set(records.map(r => r.studentName))].length }}</span>
        <span class="stat-label">参与学生</span>
      </div>
    </div>

    <el-card shadow="never" class="table-card">
      <template #header>
        <div class="card-header">
          <div class="card-title">
            <el-icon :size="20" color="var(--el-color-primary)"><Star /></el-icon>
            <span>第二课堂成绩单</span>
          </div>
          <el-button v-if="userRole !== '学生'" type="primary" icon="Plus" @click="openAdd">添加记录</el-button>
        </div>
      </template>

      <div class="filter-bar">
        <el-input v-model="searchKey" placeholder="搜索学生姓名 / 学号..." prefix-icon="Search" clearable class="filter-search" />
        <el-select v-model="filterCategory" placeholder="活动类别" clearable style="width:140px">
          <el-option label="志愿服务" value="志愿服务" />
          <el-option label="社团活动" value="社团活动" />
          <el-option label="学科竞赛" value="学科竞赛" />
          <el-option label="文体活动" value="文体活动" />
          <el-option label="社会实践" value="社会实践" />
        </el-select>
        <el-button icon="RefreshRight" @click="resetFilters">重置</el-button>
      </div>

      <el-table :data="filteredList" stripe style="width:100%">
        <el-table-column prop="studentName" label="学生" width="100">
          <template #default="{ row }">
            <div class="cell-name">
              <el-avatar :size="30" class="name-avatar">{{ row.studentName.charAt(0) }}</el-avatar>
              <span>{{ row.studentName }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="activityName" label="活动名称" min-width="180">
          <template #default="{ row }"><span class="cell-bold">{{ row.activityName }}</span></template>
        </el-table-column>
        <el-table-column prop="category" label="类别" width="110" align="center">
          <template #default="{ row }">
            <el-tag :type="categoryType(row.category)" effect="light" size="small" round>{{ row.category }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="points" label="积分" width="80" align="center">
          <template #default="{ row }"><span class="points-cell">+{{ row.points }}</span></template>
        </el-table-column>
        <el-table-column prop="date" label="日期" width="120" />
        <el-table-column prop="duration" label="时长(h)" width="90" align="center" />
        <el-table-column prop="status" label="状态" width="90" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status === '已认定' ? 'success' : 'warning'" effect="light" size="small" round>{{ row.status }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column v-if="userRole !== '学生'" label="操作" width="140" align="center" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link icon="Edit" @click="openEdit(row)">编辑</el-button>
            <el-popconfirm title="确认删除?" @confirm="onDelete(row)" width="200">
              <template #reference><el-button type="danger" link icon="Delete">删除</el-button></template>
            </el-popconfirm>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-wrap">
        <el-pagination background layout="total, prev, pager, next" :total="filteredList.length" :page-size="10" />
      </div>
    </el-card>

    <el-dialog v-model="showModal" :title="isEdit ? '编辑记录' : '添加记录'" width="540px" destroy-on-close>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="90px">
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="学生姓名" prop="studentName"><el-input v-model="form.studentName" placeholder="请输入" /></el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="活动类别" prop="category">
              <el-select v-model="form.category" style="width:100%">
                <el-option label="志愿服务" value="志愿服务" />
                <el-option label="社团活动" value="社团活动" />
                <el-option label="学科竞赛" value="学科竞赛" />
                <el-option label="文体活动" value="文体活动" />
                <el-option label="社会实践" value="社会实践" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="活动名称" prop="activityName"><el-input v-model="form.activityName" placeholder="请输入活动名称" /></el-form-item>
        <el-row :gutter="16">
          <el-col :span="8">
            <el-form-item label="积分" prop="points"><el-input-number v-model="form.points" :min="0" style="width:100%" /></el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="时长(h)"><el-input-number v-model="form.duration" :min="0" :step="0.5" style="width:100%" /></el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="日期"><el-date-picker v-model="form.date" type="date" value-format="YYYY-MM-DD" style="width:100%" /></el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="状态">
          <el-radio-group v-model="form.status">
            <el-radio label="已认定" />
            <el-radio label="待认定" />
          </el-radio-group>
        </el-form-item>
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
const filterCategory = ref('')
const showModal = ref(false)
const isEdit = ref(false)
const editId = ref(null)
const formRef = ref()

const form = ref({ studentName: '', activityName: '', category: '志愿服务', points: 5, duration: 2, date: '', status: '待认定' })
const rules = {
  studentName: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
  activityName: [{ required: true, message: '请输入活动名称', trigger: 'blur' }],
  category: [{ required: true, message: '请选择类别', trigger: 'change' }],
}

const records = ref([
  { id: 1, studentName: '张三', activityName: '校园马拉松志愿服务', category: '志愿服务', points: 10, duration: 8, date: '2025-11-10', status: '已认定' },
  { id: 2, studentName: '张三', activityName: '编程社团技术分享', category: '社团活动', points: 5, duration: 2, date: '2025-10-20', status: '已认定' },
  { id: 3, studentName: '李四', activityName: '暑期三下乡社会实践', category: '社会实践', points: 20, duration: 40, date: '2025-07-15', status: '已认定' },
  { id: 4, studentName: '王五', activityName: '校运动会400米跑', category: '文体活动', points: 8, duration: 3, date: '2025-10-15', status: '已认定' },
  { id: 5, studentName: '李四', activityName: '数学建模校赛', category: '学科竞赛', points: 15, duration: 24, date: '2025-09-01', status: '待认定' },
])

let nextId = 6
const totalPoints = computed(() => records.value.reduce((sum, r) => sum + r.points, 0))
const filteredList = computed(() => {
  return records.value.filter(r => {
    const matchKey = !searchKey.value || r.studentName.includes(searchKey.value) || r.activityName.includes(searchKey.value)
    const matchCat = !filterCategory.value || r.category === filterCategory.value
    return matchKey && matchCat
  })
})

function categoryType(cat) {
  return { '志愿服务': 'success', '社团活动': 'primary', '学科竞赛': 'warning', '文体活动': '', '社会实践': 'info' }[cat] || ''
}
function resetFilters() { searchKey.value = ''; filterCategory.value = '' }
function openAdd() {
  isEdit.value = false
  form.value = { studentName: '', activityName: '', category: '志愿服务', points: 5, duration: 2, date: '', status: '待认定' }
  showModal.value = true
}
function openEdit(row) { isEdit.value = true; editId.value = row.id; form.value = { ...row }; showModal.value = true }
function onSave() {
  formRef.value.validate(valid => {
    if (!valid) return
    if (isEdit.value) {
      const idx = records.value.findIndex(r => r.id === editId.value)
      if (idx !== -1) records.value[idx] = { ...form.value, id: editId.value }
    } else {
      records.value.push({ ...form.value, id: nextId++ })
    }
    showModal.value = false; ElMessage.success(isEdit.value ? '编辑成功' : '添加成功')
  })
}
function onDelete(row) { records.value = records.value.filter(r => r.id !== row.id); ElMessage.success('删除成功') }
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
.cell-name { display: flex; align-items: center; gap: 8px; }
.name-avatar { background: linear-gradient(135deg,#059669,#10b981); color: #fff; font-size: 12px; font-weight: 600; flex-shrink: 0; }
.cell-bold { font-weight: 500; }
.points-cell { color: #059669; font-weight: 700; font-size: 15px; }
.pagination-wrap { display: flex; justify-content: flex-end; margin-top: 18px; }
</style>
