<template>
  <div class="page-enter">
    <div class="stats-bar">
      <div class="stat-item">
        <span class="stat-num">{{ families.length }}</span>
        <span class="stat-label">家庭记录</span>
      </div>
      <div class="stat-item warning">
        <span class="stat-num">{{ families.filter(f => f.difficulty).length }}</span>
        <span class="stat-label">困难家庭</span>
      </div>
    </div>

    <el-card shadow="never" class="table-card">
      <template #header>
        <div class="card-header">
          <div class="card-title">
            <el-icon :size="20" color="var(--el-color-primary)"><HomeFilled /></el-icon>
            <span>家庭情况</span>
          </div>
          <el-button type="primary" icon="Plus" @click="openAdd">添加记录</el-button>
        </div>
      </template>

      <div class="filter-bar">
        <el-input v-model="searchKey" placeholder="搜索学生姓名 / 学号..." prefix-icon="Search" clearable class="filter-search" />
        <el-select v-model="filterDifficulty" placeholder="困难类型" clearable style="width:150px">
          <el-option label="一般困难" value="一般困难" />
          <el-option label="特别困难" value="特别困难" />
        </el-select>
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
        <el-table-column prop="parentName" label="家长姓名" width="100" />
        <el-table-column prop="relation" label="关系" width="80" align="center" />
        <el-table-column prop="parentPhone" label="联系电话" width="140" />
        <el-table-column prop="address" label="家庭住址" min-width="180" show-overflow-tooltip />
        <el-table-column prop="income" label="年收入(万)" width="110" align="center">
          <template #default="{ row }">
            <span :class="row.income < 3 ? 'income-low' : ''">{{ row.income }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="difficulty" label="困难认定" width="110" align="center">
          <template #default="{ row }">
            <el-tag v-if="row.difficulty" :type="row.difficulty === '特别困难' ? 'danger' : 'warning'" effect="light" size="small" round>{{ row.difficulty }}</el-tag>
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

    <el-dialog v-model="showModal" :title="isEdit ? '编辑家庭信息' : '添加家庭信息'" width="560px" destroy-on-close>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="90px">
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="学号" prop="studentCode"><el-input v-model="form.studentCode" placeholder="请输入学号" /></el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="学生姓名" prop="studentName"><el-input v-model="form.studentName" placeholder="请输入姓名" /></el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="家长姓名" prop="parentName"><el-input v-model="form.parentName" placeholder="请输入家长姓名" /></el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="关系"><el-input v-model="form.relation" placeholder="如：父亲" /></el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="年收入(万)"><el-input-number v-model="form.income" :min="0" :step="0.5" style="width:100%" /></el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="联系电话" prop="parentPhone"><el-input v-model="form.parentPhone" placeholder="请输入电话" /></el-form-item>
        <el-form-item label="家庭住址"><el-input v-model="form.address" placeholder="请输入地址" /></el-form-item>
        <el-form-item label="困难认定">
          <el-select v-model="form.difficulty" clearable placeholder="无" style="width:100%">
            <el-option label="一般困难" value="一般困难" />
            <el-option label="特别困难" value="特别困难" />
          </el-select>
        </el-form-item>
        <el-form-item label="备注"><el-input v-model="form.remark" type="textarea" :rows="2" placeholder="备注信息" /></el-form-item>
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
const filterDifficulty = ref('')
const showModal = ref(false)
const isEdit = ref(false)
const editId = ref(null)
const formRef = ref()

const form = ref({ studentCode: '', studentName: '', parentName: '', relation: '父亲', parentPhone: '', address: '', income: 5, difficulty: '', remark: '' })
const rules = {
  studentCode: [{ required: true, message: '请输入学号', trigger: 'blur' }],
  studentName: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
  parentName: [{ required: true, message: '请输入家长姓名', trigger: 'blur' }],
  parentPhone: [{ required: true, message: '请输入电话', trigger: 'blur' }],
}

const families = ref([
  { id: 1, studentCode: '2024001', studentName: '张三', parentName: '张父', relation: '父亲', parentPhone: '13900001111', address: '北京市海淀区xx路xx号', income: 12, difficulty: '', remark: '' },
  { id: 2, studentCode: '2024002', studentName: '李四', parentName: '李母', relation: '母亲', parentPhone: '13900002222', address: '河北省石家庄市xx街xx号', income: 2.5, difficulty: '一般困难', remark: '单亲家庭' },
  { id: 3, studentCode: '2024003', studentName: '王五', parentName: '王父', relation: '父亲', parentPhone: '13900003333', address: '山东省济南市xx路xx号', income: 8, difficulty: '', remark: '' },
  { id: 4, studentCode: '2024004', studentName: '赵六', parentName: '赵母', relation: '母亲', parentPhone: '13900004444', address: '河南省郑州市xx村', income: 1.2, difficulty: '特别困难', remark: '低保户，家中多人患病' },
])

let nextId = 5
const filteredList = computed(() => {
  return families.value.filter(f => {
    const matchKey = !searchKey.value || f.studentName.includes(searchKey.value) || f.studentCode.includes(searchKey.value)
    const matchDiff = !filterDifficulty.value || f.difficulty === filterDifficulty.value
    return matchKey && matchDiff
  })
})

function resetFilters() { searchKey.value = ''; filterDifficulty.value = '' }
function openAdd() {
  isEdit.value = false
  form.value = { studentCode: '', studentName: '', parentName: '', relation: '父亲', parentPhone: '', address: '', income: 5, difficulty: '', remark: '' }
  showModal.value = true
}
function openEdit(row) {
  isEdit.value = true; editId.value = row.id; form.value = { ...row }; showModal.value = true
}
function onSave() {
  formRef.value.validate(valid => {
    if (!valid) return
    if (isEdit.value) {
      const idx = families.value.findIndex(f => f.id === editId.value)
      if (idx !== -1) families.value[idx] = { ...form.value, id: editId.value }
    } else {
      families.value.push({ ...form.value, id: nextId++ })
    }
    showModal.value = false; ElMessage.success(isEdit.value ? '编辑成功' : '添加成功')
  })
}
function onDelete(row) { families.value = families.value.filter(f => f.id !== row.id); ElMessage.success('删除成功') }
</script>

<style scoped>
.stats-bar { display: flex; gap: 16px; margin-bottom: 20px; }
.stat-item { flex: 1; background: #fff; border-radius: 10px; padding: 18px 22px; border: 1px solid var(--border-color); display: flex; align-items: baseline; gap: 10px; transition: box-shadow 0.2s; }
.stat-item:hover { box-shadow: var(--shadow-md); }
.stat-item.warning .stat-num { color: #d97706; }
.stat-num { font-size: 28px; font-weight: 800; color: var(--el-color-primary); }
.stat-label { font-size: 13px; color: var(--text-tertiary); }
.table-card { min-height: 0; }
.card-header { display: flex; justify-content: space-between; align-items: center; }
.card-title { display: flex; align-items: center; gap: 8px; font-size: 16px; font-weight: 700; color: var(--text-primary); }
.filter-bar { display: flex; gap: 12px; margin-bottom: 18px; align-items: center; }
.filter-search { width: 260px; }
.cell-code { font-family: 'SF Mono','Menlo',monospace; font-size: 13px; color: var(--text-secondary); }
.cell-name { display: flex; align-items: center; gap: 8px; }
.name-avatar { background: linear-gradient(135deg,#4f6ef7,#6366f1); color: #fff; font-size: 12px; font-weight: 600; flex-shrink: 0; }
.income-low { color: #ff3b30; font-weight: 600; }
.text-muted { color: var(--text-tertiary); }
.pagination-wrap { display: flex; justify-content: flex-end; margin-top: 18px; }
</style>
