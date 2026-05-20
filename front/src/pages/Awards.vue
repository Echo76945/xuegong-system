<template>
  <div class="page-enter">
    <div class="stats-bar">
      <div class="stat-item">
        <span class="stat-num">{{ awards.length }}</span>
        <span class="stat-label">奖项总数</span>
      </div>
      <div class="stat-item">
        <span class="stat-num">{{ awards.filter(a => a.level === '国家级').length }}</span>
        <span class="stat-label">国家级</span>
      </div>
      <div class="stat-item">
        <span class="stat-num">{{ awards.filter(a => a.level === '省级').length }}</span>
        <span class="stat-label">省级</span>
      </div>
      <div class="stat-item">
        <span class="stat-num">{{ awards.filter(a => a.level === '校级').length }}</span>
        <span class="stat-label">校级</span>
      </div>
    </div>

    <el-card shadow="never" class="table-card">
      <template #header>
        <div class="card-header">
          <div class="card-title">
            <el-icon :size="20" color="var(--el-color-primary)"><Medal /></el-icon>
            <span>评奖评优</span>
          </div>
          <el-button type="primary" icon="Plus" @click="openAdd">添加奖项</el-button>
        </div>
      </template>

      <div class="filter-bar">
        <el-input v-model="searchKey" placeholder="搜索学生 / 奖项名称..." prefix-icon="Search" clearable class="filter-search" />
        <el-select v-model="filterLevel" placeholder="奖项级别" clearable style="width:130px">
          <el-option label="国家级" value="国家级" />
          <el-option label="省级" value="省级" />
          <el-option label="校级" value="校级" />
        </el-select>
        <el-select v-model="filterType" placeholder="奖项类型" clearable style="width:130px">
          <el-option label="奖学金" value="奖学金" />
          <el-option label="竞赛" value="竞赛" />
          <el-option label="荣誉" value="荣誉" />
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
        <el-table-column prop="awardName" label="奖项名称" min-width="200">
          <template #default="{ row }"><span class="cell-bold">{{ row.awardName }}</span></template>
        </el-table-column>
        <el-table-column prop="type" label="类型" width="90" align="center">
          <template #default="{ row }">
            <el-tag :type="row.type === '奖学金' ? 'warning' : row.type === '竞赛' ? 'primary' : 'success'" effect="light" size="small" round>{{ row.type }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="level" label="级别" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="levelType(row.level)" effect="dark" size="small" round>{{ row.level }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="grantUnit" label="颁奖单位" width="140" />
        <el-table-column prop="awardDate" label="获奖日期" width="120" />
        <el-table-column prop="bonus" label="奖金(元)" width="100" align="center">
          <template #default="{ row }">
            <span v-if="row.bonus" class="bonus-text">¥{{ row.bonus.toLocaleString() }}</span>
            <span v-else class="text-muted">-</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="140" align="center" fixed="right">
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

    <el-dialog v-model="showModal" :title="isEdit ? '编辑奖项' : '添加奖项'" width="540px" destroy-on-close>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="90px">
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="学生姓名" prop="studentName"><el-input v-model="form.studentName" placeholder="请输入" /></el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="奖项类型" prop="type">
              <el-select v-model="form.type" style="width:100%">
                <el-option label="奖学金" value="奖学金" />
                <el-option label="竞赛" value="竞赛" />
                <el-option label="荣誉" value="荣誉" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="奖项名称" prop="awardName"><el-input v-model="form.awardName" placeholder="请输入奖项名称" /></el-form-item>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="级别" prop="level">
              <el-select v-model="form.level" style="width:100%">
                <el-option label="国家级" value="国家级" />
                <el-option label="省级" value="省级" />
                <el-option label="校级" value="校级" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="奖金(元)"><el-input-number v-model="form.bonus" :min="0" :step="500" style="width:100%" /></el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="颁奖单位"><el-input v-model="form.grantUnit" placeholder="请输入" /></el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="获奖日期"><el-date-picker v-model="form.awardDate" type="date" value-format="YYYY-MM-DD" style="width:100%" /></el-form-item>
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

const searchKey = ref('')
const filterLevel = ref('')
const filterType = ref('')
const showModal = ref(false)
const isEdit = ref(false)
const editId = ref(null)
const formRef = ref()

const form = ref({ studentName: '', awardName: '', type: '奖学金', level: '校级', grantUnit: '', awardDate: '', bonus: 0 })
const rules = {
  studentName: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
  awardName: [{ required: true, message: '请输入奖项名称', trigger: 'blur' }],
  type: [{ required: true, message: '请选择类型', trigger: 'change' }],
  level: [{ required: true, message: '请选择级别', trigger: 'change' }],
}

const awards = ref([
  { id: 1, studentName: '张三', awardName: '国家奖学金', type: '奖学金', level: '国家级', grantUnit: '教育部', awardDate: '2025-12-01', bonus: 8000 },
  { id: 2, studentName: '李四', awardName: 'ACM程序设计竞赛金奖', type: '竞赛', level: '国家级', grantUnit: 'ACM组委会', awardDate: '2025-11-15', bonus: 5000 },
  { id: 3, studentName: '王五', awardName: '校级三好学生', type: '荣誉', level: '校级', grantUnit: '学校学工处', awardDate: '2025-10-20', bonus: 0 },
  { id: 4, studentName: '张三', awardName: '省级数学建模二等奖', type: '竞赛', level: '省级', grantUnit: '省教育厅', awardDate: '2025-09-10', bonus: 2000 },
  { id: 5, studentName: '赵六', awardName: '校级一等奖学金', type: '奖学金', level: '校级', grantUnit: '学校财务处', awardDate: '2025-12-10', bonus: 3000 },
])

let nextId = 6
const filteredList = computed(() => {
  return awards.value.filter(a => {
    const matchKey = !searchKey.value || a.studentName.includes(searchKey.value) || a.awardName.includes(searchKey.value)
    const matchLevel = !filterLevel.value || a.level === filterLevel.value
    const matchType = !filterType.value || a.type === filterType.value
    return matchKey && matchLevel && matchType
  })
})

function levelType(level) {
  return { '国家级': 'danger', '省级': 'warning', '校级': 'primary' }[level] || 'info'
}
function resetFilters() { searchKey.value = ''; filterLevel.value = ''; filterType.value = '' }
function openAdd() {
  isEdit.value = false
  form.value = { studentName: '', awardName: '', type: '奖学金', level: '校级', grantUnit: '', awardDate: '', bonus: 0 }
  showModal.value = true
}
function openEdit(row) { isEdit.value = true; editId.value = row.id; form.value = { ...row }; showModal.value = true }
function onSave() {
  formRef.value.validate(valid => {
    if (!valid) return
    if (isEdit.value) {
      const idx = awards.value.findIndex(a => a.id === editId.value)
      if (idx !== -1) awards.value[idx] = { ...form.value, id: editId.value }
    } else {
      awards.value.push({ ...form.value, id: nextId++ })
    }
    showModal.value = false; ElMessage.success(isEdit.value ? '编辑成功' : '添加成功')
  })
}
function onDelete(row) { awards.value = awards.value.filter(a => a.id !== row.id); ElMessage.success('删除成功') }
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
.name-avatar { background: linear-gradient(135deg,#d97706,#f59e0b); color: #fff; font-size: 12px; font-weight: 600; flex-shrink: 0; }
.cell-bold { font-weight: 500; }
.bonus-text { color: #d97706; font-weight: 600; }
.text-muted { color: var(--text-tertiary); }
.pagination-wrap { display: flex; justify-content: flex-end; margin-top: 18px; }
</style>
