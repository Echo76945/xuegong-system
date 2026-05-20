<template>
  <div class="page-enter">
    <el-row :gutter="20">
      <!-- Left: avatar card -->
      <el-col :span="8">
        <el-card shadow="never" class="profile-card">
          <div class="avatar-section">
            <el-avatar :size="96" class="profile-avatar">{{ user.name.charAt(0) }}</el-avatar>
            <h3 class="profile-name">{{ user.name }}</h3>
            <el-tag effect="light" size="small" round>{{ user.role }}</el-tag>
          </div>
          <div class="profile-stats">
            <div class="profile-stat">
              <span class="stat-num">128</span>
              <span class="stat-label">管理学生</span>
            </div>
            <div class="profile-stat">
              <span class="stat-num">24</span>
              <span class="stat-label">管理课程</span>
            </div>
            <div class="profile-stat">
              <span class="stat-num">365</span>
              <span class="stat-label">使用天数</span>
            </div>
          </div>
        </el-card>
      </el-col>

      <!-- Right: info form -->
      <el-col :span="16">
        <el-card shadow="never">
          <template #header>
            <div class="card-header">
              <span class="card-title">基本信息</span>
              <el-button type="primary" icon="Edit" @click="isEditing = !isEditing">{{ isEditing ? '取消编辑' : '编辑' }}</el-button>
            </div>
          </template>
          <el-form :model="user" label-width="90px" :disabled="!isEditing">
            <el-row :gutter="16">
              <el-col :span="12">
                <el-form-item label="用户名"><el-input v-model="user.username" /></el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="姓名"><el-input v-model="user.name" /></el-form-item>
              </el-col>
            </el-row>
            <el-row :gutter="16">
              <el-col :span="12">
                <el-form-item label="角色"><el-input v-model="user.role" disabled /></el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="手机号"><el-input v-model="user.phone" /></el-form-item>
              </el-col>
            </el-row>
            <el-row :gutter="16">
              <el-col :span="12">
                <el-form-item label="邮箱"><el-input v-model="user.email" /></el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="部门"><el-input v-model="user.department" /></el-form-item>
              </el-col>
            </el-row>
            <el-form-item v-if="isEditing">
              <el-button type="primary" @click="onSave">保存修改</el-button>
            </el-form-item>
          </el-form>
        </el-card>

        <!-- Change password -->
        <el-card shadow="never" style="margin-top:20px">
          <template #header>
            <span class="card-title">修改密码</span>
          </template>
          <el-form :model="pwdForm" :rules="pwdRules" ref="pwdFormRef" label-width="90px" style="max-width:480px">
            <el-form-item label="原密码" prop="oldPwd">
              <el-input v-model="pwdForm.oldPwd" type="password" show-password placeholder="请输入原密码" />
            </el-form-item>
            <el-form-item label="新密码" prop="newPwd">
              <el-input v-model="pwdForm.newPwd" type="password" show-password placeholder="请输入新密码" />
            </el-form-item>
            <el-form-item label="确认密码" prop="confirmPwd">
              <el-input v-model="pwdForm.confirmPwd" type="password" show-password placeholder="请再次输入新密码" />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="onChangePwd">修改密码</el-button>
            </el-form-item>
          </el-form>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { ElMessage } from 'element-plus'

const isEditing = ref(false)
const pwdFormRef = ref()

const user = reactive({
  username: 'admin',
  name: '管理员',
  role: '超级管理员',
  phone: '13800000000',
  email: 'admin@school.edu.cn',
  department: '学生工作处',
})

const pwdForm = reactive({ oldPwd: '', newPwd: '', confirmPwd: '' })
const pwdRules = {
  oldPwd: [{ required: true, message: '请输入原密码', trigger: 'blur' }],
  newPwd: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, message: '密码至少6位', trigger: 'blur' },
  ],
  confirmPwd: [
    { required: true, message: '请确认密码', trigger: 'blur' },
    {
      validator: (rule, value, callback) => {
        if (value !== pwdForm.newPwd) callback(new Error('两次密码不一致'))
        else callback()
      },
      trigger: 'blur',
    },
  ],
}

function onSave() {
  isEditing.value = false
  localStorage.setItem('user', JSON.stringify({ name: user.name, role: user.role }))
  ElMessage.success('保存成功')
}
function onChangePwd() {
  pwdFormRef.value.validate(valid => {
    if (!valid) return
    ElMessage.success('密码修改成功')
    pwdForm.oldPwd = ''
    pwdForm.newPwd = ''
    pwdForm.confirmPwd = ''
  })
}
</script>

<style scoped>
.profile-card { text-align: center; }
.avatar-section { padding: 20px 0; }
.profile-avatar { background: linear-gradient(135deg,#4f6ef7,#6366f1); color: #fff; font-size: 36px; font-weight: 700; }
.profile-name { font-size: 20px; font-weight: 700; margin: 12px 0 6px; color: var(--text-primary); }
.profile-stats { display: flex; border-top: 1px solid #f2f3f5; margin-top: 16px; padding-top: 16px; }
.profile-stat { flex: 1; text-align: center; }
.profile-stat .stat-num { display: block; font-size: 22px; font-weight: 800; color: var(--el-color-primary); }
.profile-stat .stat-label { font-size: 12px; color: var(--text-tertiary); }
.card-header { display: flex; justify-content: space-between; align-items: center; }
.card-title { font-size: 16px; font-weight: 700; color: var(--text-primary); }
</style>
