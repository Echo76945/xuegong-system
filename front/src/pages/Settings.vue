<template>
  <div class="page-enter">
    <el-row :gutter="20">
      <el-col :span="16">
        <!-- Site settings -->
        <el-card shadow="never" class="settings-card">
          <template #header>
            <span class="card-title">站点设置</span>
          </template>
          <el-form :model="siteSettings" label-width="120px">
            <el-form-item label="系统名称">
              <el-input v-model="siteSettings.siteName" />
            </el-form-item>
            <el-form-item label="系统描述">
              <el-input v-model="siteSettings.siteDesc" type="textarea" :rows="2" />
            </el-form-item>
            <el-form-item label="版权信息">
              <el-input v-model="siteSettings.copyright" />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="saveSite">保存设置</el-button>
            </el-form-item>
          </el-form>
        </el-card>

        <!-- Notification settings -->
        <el-card shadow="never" class="settings-card">
          <template #header>
            <span class="card-title">通知设置</span>
          </template>
          <div class="setting-list">
            <div class="setting-item" v-for="item in notifSettings" :key="item.key">
              <div class="setting-info">
                <span class="setting-label">{{ item.label }}</span>
                <span class="setting-desc">{{ item.desc }}</span>
              </div>
              <el-switch v-model="item.enabled" @change="saveNotif" />
            </div>
          </div>
        </el-card>

        <!-- Display settings -->
        <el-card shadow="never" class="settings-card">
          <template #header>
            <span class="card-title">显示设置</span>
          </template>
          <el-form label-width="120px">
            <el-form-item label="侧边栏默认">
              <el-radio-group v-model="displaySettings.sidebarCollapsed">
                <el-radio :label="false">展开</el-radio>
                <el-radio :label="true">收起</el-radio>
              </el-radio-group>
            </el-form-item>
            <el-form-item label="表格每页条数">
              <el-select v-model="displaySettings.pageSize" style="width:120px">
                <el-option :label="10" :value="10" />
                <el-option :label="20" :value="20" />
                <el-option :label="50" :value="50" />
                <el-option :label="100" :value="100" />
              </el-select>
            </el-form-item>
            <el-form-item label="动画效果">
              <el-switch v-model="displaySettings.animations" active-text="开启" inactive-text="关闭" />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="saveDisplay">保存设置</el-button>
            </el-form-item>
          </el-form>
        </el-card>
      </el-col>

      <!-- Right: system info -->
      <el-col :span="8">
        <el-card shadow="never" class="settings-card">
          <template #header>
            <span class="card-title">系统信息</span>
          </template>
          <div class="info-list">
            <div class="info-item" v-for="info in sysInfo" :key="info.label">
              <span class="info-label">{{ info.label }}</span>
              <span class="info-value">{{ info.value }}</span>
            </div>
          </div>
        </el-card>

        <el-card shadow="never" class="settings-card">
          <template #header>
            <span class="card-title">数据管理</span>
          </template>
          <div class="data-actions">
            <el-button icon="Download" class="data-btn" @click="exportData">导出数据</el-button>
            <el-button icon="Upload" class="data-btn" @click="triggerImport">导入数据</el-button>
            <input ref="fileInput" type="file" accept=".xlsx,.xls" style="display:none" @change="importData" />
            <el-button icon="RefreshRight" type="warning" class="data-btn" @click="clearCache">清除缓存</el-button>
            <el-popconfirm title="确认重置所有设置为默认值?" width="240" @confirm="resetDefaults">
              <template #reference>
                <el-button icon="RefreshLeft" type="danger" class="data-btn">恢复默认</el-button>
              </template>
            </el-popconfirm>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import * as XLSX from 'xlsx'
import request from '../api/request'

const fileInput = ref(null)

const siteSettings = reactive({
  siteName: '学工管理系统',
  siteDesc: '学生工作信息化管理平台',
  copyright: '© 2026 学工管理系统',
})

const notifSettings = reactive([
  { key: 'email', label: '邮件通知', desc: '重要事件通过邮件发送通知', enabled: true },
  { key: 'sms', label: '短信通知', desc: '紧急事件发送短信提醒', enabled: false },
  { key: 'system', label: '系统通知', desc: '在系统内显示通知消息', enabled: true },
  { key: 'approval', label: '审批提醒', desc: '有待审批事项时提醒', enabled: true },
  { key: 'deadline', label: '截止提醒', desc: '任务截止前发送提醒', enabled: true },
])

const defaultNotifSettings = [
  { key: 'email', label: '邮件通知', desc: '重要事件通过邮件发送通知', enabled: true },
  { key: 'sms', label: '短信通知', desc: '紧急事件发送短信提醒', enabled: false },
  { key: 'system', label: '系统通知', desc: '在系统内显示通知消息', enabled: true },
  { key: 'approval', label: '审批提醒', desc: '有待审批事项时提醒', enabled: true },
  { key: 'deadline', label: '截止提醒', desc: '任务截止前发送提醒', enabled: true },
]

const displaySettings = reactive({
  sidebarCollapsed: false,
  pageSize: 10,
  animations: true,
})

const sysInfo = [
  { label: '系统版本', value: 'v2.4.0' },
  { label: 'Vue 版本', value: '3.4+' },
  { label: 'Element Plus', value: '2.14+' },
  { label: 'Node 版本', value: '18.x' },
  { label: '构建工具', value: 'Vite 5' },
  { label: '服务器时间', value: new Date().toLocaleString('zh-CN') },
]

function saveSite() { ElMessage.success('站点设置已保存') }
function saveNotif() { ElMessage.success('通知设置已保存') }
function saveDisplay() { ElMessage.success('显示设置已保存') }

// ===== 数据管理 =====

async function exportData() {
  try {
    const res = await request.get('/students')
    const students = res.data || []
    const exportList = students.map(s => ({
      '学号': s.code,
      '姓名': s.name,
      '性别': s.gender,
      '班级': s.className,
      '手机': s.phone,
      '状态': s.status,
    }))
    const ws = XLSX.utils.json_to_sheet(exportList)
    ws['!cols'] = [
      { wch: 12 }, { wch: 10 }, { wch: 6 },
      { wch: 16 }, { wch: 14 }, { wch: 8 },
    ]
    const wb = XLSX.utils.book_new()
    XLSX.utils.book_append_sheet(wb, ws, '学生数据')
    XLSX.writeFile(wb, `学生数据_${new Date().toLocaleDateString('zh-CN').replace(/\//g, '-')}.xlsx`)
    ElMessage.success(`导出成功，共 ${exportList.length} 条数据`)
  } catch (err) {
    ElMessage.error('导出失败：' + (err.message || '网络错误'))
  }
}

function triggerImport() {
  fileInput.value?.click()
}

async function importData(e) {
  const file = e.target.files[0]
  if (!file) return
  e.target.value = ''

  try {
    const data = await file.arrayBuffer()
    const wb = XLSX.read(data, { type: 'array' })
    const ws = wb.Sheets[wb.SheetNames[0]]
    const rows = XLSX.utils.sheet_to_json(ws)

    if (rows.length === 0) {
      ElMessage.warning('文件中没有数据')
      return
    }

    const students = rows.map(row => ({
      code: row['学号'] || row['code'] || '',
      name: row['姓名'] || row['name'] || '',
      gender: row['性别'] || row['gender'] || '',
      className: row['班级'] || row['className'] || '',
      phone: row['手机'] || row['phone'] || '',
      status: row['状态'] || row['status'] || '在校',
    })).filter(s => s.code && s.name)

    if (students.length === 0) {
      ElMessage.warning('未识别到有效数据，请确保 Excel 包含"学号"和"姓名"列')
      return
    }

    await request.post('/students/import', students)
    ElMessage.success(`导入成功，共 ${students.length} 条数据`)
  } catch (err) {
    ElMessage.error('导入失败：' + (err.message || '文件格式错误'))
  }
}

async function clearCache() {
  try {
    await ElMessageBox.confirm('将清除缓存数据（保留登录状态），是否继续？', '清除缓存', { type: 'warning' })
    const keysToKeep = ['token', 'user']
    const allKeys = Object.keys(localStorage)
    let cleared = 0
    allKeys.forEach(key => {
      if (!keysToKeep.includes(key)) {
        localStorage.removeItem(key)
        cleared++
      }
    })
    ElMessage.success(`缓存已清除（${cleared} 项）`)
  } catch {}
}

function resetDefaults() {
  siteSettings.siteName = '学工管理系统'
  siteSettings.siteDesc = '学生工作信息化管理平台'
  siteSettings.copyright = '© 2026 学工管理系统'
  notifSettings.splice(0, notifSettings.length, ...defaultNotifSettings.map(n => ({ ...n })))
  displaySettings.sidebarCollapsed = false
  displaySettings.pageSize = 10
  displaySettings.animations = true
  ElMessage.success('已恢复默认设置')
}
</script>

<style scoped>
.settings-card { margin-bottom: 20px; }
.card-title { font-size: 16px; font-weight: 700; color: var(--text-primary); }
.setting-list { display: flex; flex-direction: column; }
.setting-item { display: flex; justify-content: space-between; align-items: center; padding: 14px 0; border-bottom: 1px solid #f5f5f5; }
.setting-item:last-child { border-bottom: none; }
.setting-info { display: flex; flex-direction: column; }
.setting-label { font-size: 14px; font-weight: 500; color: var(--text-primary); }
.setting-desc { font-size: 12px; color: var(--text-tertiary); margin-top: 2px; }
.info-list { display: flex; flex-direction: column; }
.info-item { display: flex; justify-content: space-between; padding: 10px 0; border-bottom: 1px solid #f5f5f5; font-size: 13px; }
.info-item:last-child { border-bottom: none; }
.info-label { color: var(--text-tertiary); }
.info-value { color: var(--text-primary); font-weight: 500; }
.data-actions { display: flex; flex-direction: column; gap: 10px; }
.data-btn { width: 100%; margin-left: 0 !important; }
</style>
