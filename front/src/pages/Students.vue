<template>
  <div class="page-enter">
    <!-- Stats bar -->
    <div class="stats-bar">
      <div class="stat-item">
        <span class="stat-num">{{ students.length }}</span>
        <span class="stat-label">学生总数</span>
      </div>
      <div class="stat-item">
        <span class="stat-num">{{ students.filter(s => s.status === '在校').length }}</span>
        <span class="stat-label">在校学生</span>
      </div>
      <div class="stat-item">
        <span class="stat-num">{{ classList.length }}</span>
        <span class="stat-label">班级数量</span>
      </div>
    </div>

    <el-card shadow="never" class="table-card">
      <template #header>
        <div class="card-header">
          <div class="card-title">
            <el-icon :size="20" color="var(--el-color-primary)"><User /></el-icon>
            <span>学生列表</span>
          </div>
          <el-button type="primary" icon="Plus" @click="openAdd">添加学生</el-button>
        </div>
      </template>

      <!-- Filters -->
      <div class="filter-bar">
        <el-input v-model="searchKey" placeholder="搜索姓名 / 学号..." prefix-icon="Search" clearable class="filter-search" @input="fetchStudents" />
        <el-select v-model="filterClass" placeholder="选择班级" clearable style="width:180px" @change="fetchStudents">
          <el-option v-for="c in classList" :key="c" :label="c" :value="c" />
        </el-select>
        <el-button icon="RefreshRight" @click="resetFilters">重置</el-button>
      </div>

      <!-- Table -->
      <el-table :data="students" stripe style="width:100%" class="data-table" v-loading="loading">
        <el-table-column prop="code" label="学号" width="130">
          <template #default="{ row }">
            <span class="cell-code">{{ row.code }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="name" label="姓名" width="100">
          <template #default="{ row }">
            <div class="cell-name">
              <el-avatar :size="30" class="name-avatar">{{ row.name.charAt(0) }}</el-avatar>
              <span>{{ row.name }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="gender" label="性别" width="70" align="center">
          <template #default="{ row }">
            <el-icon :color="row.gender === '男' ? '#4f6ef7' : '#f472b6'">
              <Male v-if="row.gender === '男'" /><Female v-else />
            </el-icon>
          </template>
        </el-table-column>
        <el-table-column prop="className" label="班级" min-width="140" />
        <el-table-column prop="phone" label="联系电话" width="140" />
        <el-table-column prop="status" label="状态" width="90" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status === '在校' ? 'success' : row.status === '休学' ? 'warning' : 'info'" effect="light" size="small" round>{{ row.status }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="140" align="center" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link icon="Edit" @click="openEdit(row)">编辑</el-button>
            <el-popconfirm title="确认删除该学生?" @confirm="onDelete(row)" width="220">
              <template #reference>
                <el-button type="danger" link icon="Delete">删除</el-button>
              </template>
            </el-popconfirm>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-wrap">
        <el-pagination background layout="total, prev, pager, next" :total="total" :page-size="pageSize" @current-change="handlePageChange" />
      </div>
    </el-card>

    <!-- Dialog -->
    <el-dialog v-model="showModal" :title="isEdit ? '编辑学生' : '添加学生'" width="540px" destroy-on-close>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="80px">
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="学号" prop="code">
              <el-input v-model="form.code" placeholder="请输入学号" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="姓名" prop="name">
              <el-input v-model="form.name" placeholder="请输入姓名" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="性别">
              <el-radio-group v-model="form.gender">
                <el-radio label="男" />
                <el-radio label="女" />
              </el-radio-group>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="状态">
              <el-select v-model="form.status" style="width:100%">
                <el-option label="在校" value="在校" />
                <el-option label="离校" value="离校" />
                <el-option label="休学" value="休学" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="班级" prop="className">
          <el-input v-model="form.className" placeholder="请输入班级" />
        </el-form-item>
        <el-form-item label="联系电话">
          <el-input v-model="form.phone" placeholder="请输入电话" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showModal=false">取消</el-button>
        <el-button type="primary" @click="onSave" :loading="saving">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import request from '../api/request'

const searchKey = ref('')
const filterClass = ref('')
const showModal = ref(false)
const isEdit = ref(false)
const editId = ref(null)
const formRef = ref()
const loading = ref(false)
const saving = ref(false)
const students = ref([])
const total = ref(0)
const pageSize = ref(10)

const form = ref({ code: '', name: '', gender: '男', className: '', phone: '', status: '在校' })
const rules = {
  code: [{ required: true, message: '请输入学号', trigger: 'blur' }],
  name: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
  className: [{ required: true, message: '请输入班级', trigger: 'blur' }],
}

const classList = computed(() => [...new Set(students.value.map(s => s.className))])

onMounted(() => {
  fetchStudents()
})

async function fetchStudents() {
  loading.value = true
  try {
    const params = {}
    if (searchKey.value) params.keyword = searchKey.value
    if (filterClass.value) params.className = filterClass.value
    
    const res = await request.get('/students', { params })
    students.value = res.data
    total.value = res.data.length
  } catch (err) {
    console.error('获取学生列表失败:', err)
  } finally {
    loading.value = false
  }
}

function resetFilters() {
  searchKey.value = ''
  filterClass.value = ''
  fetchStudents()
}

function handlePageChange(page) {
  // 简单分页，实际项目中应传page参数给后端
  console.log('Page:', page)
}

function openAdd() {
  isEdit.value = false
  form.value = { code: '', name: '', gender: '男', className: '', phone: '', status: '在校' }
  showModal.value = true
}

function openEdit(row) {
  isEdit.value = true
  editId.value = row.id
  form.value = { ...row }
  showModal.value = true
}

async function onSave() {
  formRef.value.validate(async valid => {
    if (!valid) return
    saving.value = true
    
    try {
      if (isEdit.value) {
        await request.put(`/students/${editId.value}`, form.value)
        ElMessage.success('编辑成功')
      } else {
        await request.post('/students', form.value)
        ElMessage.success('添加成功')
      }
      showModal.value = false
      fetchStudents()
    } catch (err) {
      console.error('保存失败:', err)
    } finally {
      saving.value = false
    }
  })
}

async function onDelete(row) {
  try {
    await request.delete(`/students/${row.id}`)
    ElMessage.success('删除成功')
    fetchStudents()
  } catch (err) {
    console.error('删除失败:', err)
  }
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

.table-card {
  min-height: 0;
}
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
.filter-search {
  width: 260px;
}

.data-table {
  border-radius: 10px;
  overflow: hidden;
}

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

.pagination-wrap {
  display: flex;
  justify-content: flex-end;
  margin-top: 18px;
}
</style>
