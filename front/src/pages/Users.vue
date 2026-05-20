<template>
  <div class="page-enter">
    <div class="stats-bar">
      <div class="stat-item">
        <span class="stat-num">{{ users.length }}</span>
        <span class="stat-label">用户总数</span>
      </div>
      <div class="stat-item">
        <span class="stat-num">{{ users.filter(u => u.role === '管理员').length }}</span>
        <span class="stat-label">超级管理员</span>
      </div>
      <div class="stat-item">
        <span class="stat-num">{{ users.filter(u => u.role === '教师').length }}</span>
        <span class="stat-label">教师</span>
      </div>
      <div class="stat-item">
        <span class="stat-num">{{ users.filter(u => u.role === '学生').length }}</span>
        <span class="stat-label">学生</span>
      </div>
    </div>

    <el-card shadow="never" class="table-card">
      <template #header>
        <div class="card-header">
          <div class="card-title">
            <el-icon :size="20" color="var(--el-color-primary)"><UserFilled /></el-icon>
            <span>用户列表</span>
          </div>
          <el-button type="primary" icon="Plus" @click="openAdd">添加用户</el-button>
        </div>
      </template>

      <div class="filter-bar">
        <el-input v-model="searchKey" placeholder="搜索用户名 / 姓名..." prefix-icon="Search" clearable class="filter-search" />
        <el-select v-model="filterRole" placeholder="角色" clearable style="width:130px">
          <el-option label="超级管理员" value="管理员" />
          <el-option label="教师" value="教师" />
          <el-option label="学生" value="学生" />
        </el-select>
        <el-select v-model="filterStatus" placeholder="状态" clearable style="width:120px">
          <el-option label="启用" value="启用" />
          <el-option label="禁用" value="禁用" />
        </el-select>
        <el-button icon="RefreshRight" @click="resetFilters">重置</el-button>
      </div>

      <el-table :data="filteredList" stripe style="width:100%">
        <el-table-column prop="id" label="ID" width="70" align="center" />
        <el-table-column prop="username" label="用户名" width="130">
          <template #default="{ row }">
            <span class="cell-code">{{ row.username }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="name" label="姓名" width="100">
          <template #default="{ row }">
            <div class="cell-name">
              <el-avatar :size="30" class="name-avatar" :style="{ background: roleColor(row.role) }">{{ row.name.charAt(0) }}</el-avatar>
              <span>{{ row.name }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="role" label="角色" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="roleTagType(row.role)" effect="light" size="small" round>{{ roleDisplay(row.role) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="phone" label="手机号" width="140" />
        <el-table-column prop="email" label="邮箱" min-width="180" />
        <el-table-column prop="status" label="状态" width="90" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status === '启用' ? 'success' : 'danger'" effect="light" size="small" round>{{ row.status }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="lastLogin" label="最后登录" width="170" />
        <el-table-column label="操作" width="200" align="center" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link icon="Edit" @click="openEdit(row)">编辑</el-button>
            <el-button type="warning" link icon="Key" @click="onResetPwd(row)">重置密码</el-button>
            <el-popconfirm title="确认删除该用户?" @confirm="onDelete(row)" width="220">
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

    <el-dialog v-model="showModal" :title="isEdit ? '编辑用户' : '添加用户'" width="540px" destroy-on-close>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="80px">
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="用户名" prop="username">
              <el-input v-model="form.username" placeholder="请输入用户名" />
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
            <el-form-item label="角色" prop="role">
              <el-select v-model="form.role" style="width:100%">
                <el-option label="超级管理员" value="管理员" />
                <el-option label="教师" value="教师" />
                <el-option label="学生" value="学生" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="状态">
              <el-radio-group v-model="form.status">
                <el-radio label="启用" />
                <el-radio label="禁用" />
              </el-radio-group>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="手机号">
              <el-input v-model="form.phone" placeholder="请输入手机号" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="邮箱">
              <el-input v-model="form.email" placeholder="请输入邮箱" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item v-if="!isEdit" label="密码" prop="password">
          <el-input v-model="form.password" type="password" placeholder="请输入密码" show-password />
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

const searchKey = ref('')
const filterRole = ref('')
const filterStatus = ref('')
const showModal = ref(false)
const isEdit = ref(false)
const editId = ref(null)
const formRef = ref()

const form = ref({ username: '', name: '', role: '学生', phone: '', email: '', status: '启用', password: '' })
const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  name: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
  role: [{ required: true, message: '请选择角色', trigger: 'change' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
}

const users = ref([
  { id: 1, username: 'admin', name: '管理员', role: '管理员', phone: '13800000000', email: 'admin@school.edu.cn', status: '启用', lastLogin: '2026-05-15 09:30:00' },
  { id: 2, username: 'teacher01', name: '王教授', role: '教师', phone: '13800001111', email: 'wang@school.edu.cn', status: '启用', lastLogin: '2026-05-14 14:20:00' },
  { id: 3, username: 'teacher02', name: '李教授', role: '教师', phone: '13800002222', email: 'li@school.edu.cn', status: '启用', lastLogin: '2026-05-13 10:15:00' },
  { id: 4, username: '2024001', name: '张三', role: '学生', phone: '13800003333', email: 'zhangsan@stu.edu.cn', status: '启用', lastLogin: '2026-05-15 08:00:00' },
  { id: 5, username: '2024002', name: '李四', role: '学生', phone: '13800004444', email: 'lisi@stu.edu.cn', status: '禁用', lastLogin: '2026-04-20 16:45:00' },
])

let nextId = 6
const filteredList = computed(() => {
  return users.value.filter(u => {
    const matchKey = !searchKey.value || u.name.includes(searchKey.value) || u.username.includes(searchKey.value)
    const matchRole = !filterRole.value || u.role === filterRole.value
    const matchStatus = !filterStatus.value || u.status === filterStatus.value
    return matchKey && matchRole && matchStatus
  })
})

function roleDisplay(role) {
  return { '管理员': '超级管理员' }[role] || role
}
function roleTagType(role) {
  return { '管理员': 'danger', '教师': 'warning', '学生': 'primary' }[role] || 'info'
}
function roleColor(role) {
  return { '管理员': 'linear-gradient(135deg,#ff3b30,#ff6b6b)', '教师': 'linear-gradient(135deg,#d97706,#f59e0b)', '学生': 'linear-gradient(135deg,#4f6ef7,#6366f1)' }[role] || '#4f6ef7'
}
function resetFilters() {
  searchKey.value = ''
  filterRole.value = ''
  filterStatus.value = ''
}
function openAdd() {
  isEdit.value = false
  form.value = { username: '', name: '', role: '学生', phone: '', email: '', status: '启用', password: '' }
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
      const idx = users.value.findIndex(u => u.id === editId.value)
      if (idx !== -1) users.value[idx] = { ...form.value, id: editId.value }
    } else {
      users.value.push({ ...form.value, id: nextId++, lastLogin: '-' })
    }
    showModal.value = false
    ElMessage.success(isEdit.value ? '编辑成功' : '添加成功')
  })
}
function onResetPwd(row) {
  ElMessage.success(`已重置 ${row.name} 的密码为 123456`)
}
function onDelete(row) {
  users.value = users.value.filter(u => u.id !== row.id)
  ElMessage.success('删除成功')
}
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
.cell-code { font-family: 'SF Mono','Menlo',monospace; font-size: 13px; color: var(--text-secondary); }
.cell-name { display: flex; align-items: center; gap: 8px; }
.name-avatar { color: #fff; font-size: 12px; font-weight: 600; flex-shrink: 0; }
.pagination-wrap { display: flex; justify-content: flex-end; margin-top: 18px; }
</style>
