<template>
  <div class="page-enter">
    <div class="stats-bar">
      <div class="stat-item">
        <span class="stat-num">{{ records.length }}</span>
        <span class="stat-label">晚归记录</span>
      </div>
      <div class="stat-item warning">
        <span class="stat-num">{{ records.filter(r => r.abnormal).length }}</span>
        <span class="stat-label">异常记录</span>
      </div>
      <div class="stat-item">
        <span class="stat-num">{{ records.filter(r => r.date === today).length }}</span>
        <span class="stat-label">今日晚归</span>
      </div>
    </div>

    <el-card shadow="never" class="table-card">
      <template #header>
        <div class="card-header">
          <div class="card-title">
            <el-icon :size="20" color="var(--el-color-primary)"><Moon /></el-icon>
            <span>晚归登记</span>
          </div>
          <el-button type="primary" icon="Plus" @click="openAdd">添加记录</el-button>
        </div>
      </template>

      <div class="filter-bar">
        <el-input v-model="searchKey" placeholder="搜索学生姓名 / 学号..." prefix-icon="Search" clearable class="filter-search" />
        <el-date-picker v-model="filterDate" type="date" placeholder="选择日期" value-format="YYYY-MM-DD" clearable style="width:160px" />
        <el-button icon="RefreshRight" @click="resetFilters">重置</el-button>
      </div>

      <el-table :data="filteredList" stripe style="width:100%">
        <el-table-column prop="studentCode" label="学号" width="120">
          <template #default="{ row }"><span class="cell-code">{{ row.studentCode }}</span></template>
        </el-table-column>
        <el-table-column prop="studentName" label="姓名" width="100">
          <template #default="{ row }">
            <div class="cell-name">
              <el-avatar :size="30" class="name-avatar">{{ row.studentName.charAt(0) }}</el-avatar>
              <span>{{ row.studentName }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="className" label="班级" width="140" />
        <el-table-column prop="date" label="日期" width="120" />
        <el-table-column prop="returnTime" label="归寝时间" width="120">
          <template #default="{ row }"><span class="time-text">{{ row.returnTime }}</span></template>
        </el-table-column>
        <el-table-column prop="reason" label="原因" min-width="180" show-overflow-tooltip />
        <el-table-column prop="abnormal" label="异常标记" width="100" align="center">
          <template #default="{ row }">
            <el-tag v-if="row.abnormal" type="danger" effect="light" size="small" round>异常</el-tag>
            <span v-else class="text-muted">正常</span>
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

    <el-dialog v-model="showModal" :title="isEdit ? '编辑记录' : '添加晚归记录'" width="540px" destroy-on-close>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="90px">
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="学号" prop="studentCode"><el-input v-model="form.studentCode" placeholder="请输入" /></el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="姓名" prop="studentName"><el-input v-model="form.studentName" placeholder="请输入" /></el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="班级"><el-input v-model="form.className" placeholder="请输入" /></el-form-item>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="日期" prop="date"><el-date-picker v-model="form.date" type="date" value-format="YYYY-MM-DD" style="width:100%" /></el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="归寝时间" prop="returnTime"><el-time-picker v-model="form.returnTime" format="HH:mm" value-format="HH:mm" style="width:100%" /></el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="原因"><el-input v-model="form.reason" type="textarea" :rows="2" placeholder="请输入晚归原因" /></el-form-item>
        <el-form-item label="异常标记">
          <el-switch v-model="form.abnormal" active-text="异常" inactive-text="正常" />
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

const today = new Date().toISOString().slice(0, 10)
const searchKey = ref('')
const filterDate = ref('')
const showModal = ref(false)
const isEdit = ref(false)
const editId = ref(null)
const formRef = ref()

const form = ref({ studentCode: '', studentName: '', className: '', date: '', returnTime: '', reason: '', abnormal: false })
const rules = {
  studentCode: [{ required: true, message: '请输入学号', trigger: 'blur' }],
  studentName: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
  date: [{ required: true, message: '请选择日期', trigger: 'change' }],
  returnTime: [{ required: true, message: '请选择时间', trigger: 'change' }],
}

const records = ref([
  { id: 1, studentCode: '2024001', studentName: '张三', className: '计算机2401', date: '2026-05-14', returnTime: '23:45', reason: '图书馆自习', abnormal: false },
  { id: 2, studentCode: '2024003', studentName: '王五', className: '软件2401', date: '2026-05-14', returnTime: '01:20', reason: '外出未归，电话联系不上', abnormal: true },
  { id: 3, studentCode: '2024005', studentName: '孙七', className: '计算机2402', date: '2026-05-13', returnTime: '23:10', reason: '社团活动', abnormal: false },
  { id: 4, studentCode: '2024002', studentName: '李四', className: '计算机2401', date: '2026-05-12', returnTime: '00:30', reason: '回家探亲，已请假', abnormal: false },
])

let nextId = 5
const filteredList = computed(() => {
  return records.value.filter(r => {
    const matchKey = !searchKey.value || r.studentName.includes(searchKey.value) || r.studentCode.includes(searchKey.value)
    const matchDate = !filterDate.value || r.date === filterDate.value
    return matchKey && matchDate
  })
})

function resetFilters() { searchKey.value = ''; filterDate.value = '' }
function openAdd() {
  isEdit.value = false
  form.value = { studentCode: '', studentName: '', className: '', date: today, returnTime: '', reason: '', abnormal: false }
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
.stat-item.warning .stat-num { color: #ff3b30; }
.stat-num { font-size: 28px; font-weight: 800; color: var(--el-color-primary); }
.stat-label { font-size: 13px; color: var(--text-tertiary); }
.table-card { min-height: 0; }
.card-header { display: flex; justify-content: space-between; align-items: center; }
.card-title { display: flex; align-items: center; gap: 8px; font-size: 16px; font-weight: 700; color: var(--text-primary); }
.filter-bar { display: flex; gap: 12px; margin-bottom: 18px; align-items: center; }
.filter-search { width: 260px; }
.cell-code { font-family: 'SF Mono','Menlo',monospace; font-size: 13px; color: var(--text-secondary); }
.cell-name { display: flex; align-items: center; gap: 8px; }
.name-avatar { background: linear-gradient(135deg,#312e81,#4338ca); color: #fff; font-size: 12px; font-weight: 600; flex-shrink: 0; }
.time-text { font-family: 'SF Mono','Menlo',monospace; font-weight: 600; color: var(--text-primary); }
.text-muted { color: var(--text-tertiary); }
.pagination-wrap { display: flex; justify-content: flex-end; margin-top: 18px; }
</style>
