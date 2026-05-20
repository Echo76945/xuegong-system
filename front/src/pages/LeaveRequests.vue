<template>
  <div class="page-enter">
    <div class="stats-bar">
      <div class="stat-item">
        <span class="stat-num">{{ requests.length }}</span>
        <span class="stat-label">申请总数</span>
      </div>
      <div class="stat-item warning">
        <span class="stat-num">{{ requests.filter(r => r.status === '待审核').length }}</span>
        <span class="stat-label">待审核</span>
      </div>
      <div class="stat-item success">
        <span class="stat-num">{{ requests.filter(r => r.status === '已通过').length }}</span>
        <span class="stat-label">已通过</span>
      </div>
    </div>

    <el-card shadow="never" class="table-card">
      <template #header>
        <div class="card-header">
          <div class="card-title">
            <el-icon :size="20" color="var(--el-color-primary)"><Position /></el-icon>
            <span>外出报备</span>
          </div>
          <el-button type="primary" icon="Plus" @click="openAdd">新建申请</el-button>
        </div>
      </template>

      <el-tabs v-model="activeTab" class="custom-tabs">
        <el-tab-pane label="全部" name="all" />
        <el-tab-pane label="待审核" name="待审核" />
        <el-tab-pane label="已通过" name="已通过" />
        <el-tab-pane label="已驳回" name="已驳回" />
      </el-tabs>

      <el-table :data="filteredList" stripe style="width:100%">
        <el-table-column prop="studentName" label="申请人" width="100">
          <template #default="{ row }">
            <div class="cell-name">
              <el-avatar :size="30" class="name-avatar">{{ row.studentName.charAt(0) }}</el-avatar>
              <span>{{ row.studentName }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="className" label="班级" width="140" />
        <el-table-column prop="destination" label="外出地点" width="140" />
        <el-table-column prop="startTime" label="外出时间" width="160" />
        <el-table-column prop="endTime" label="预计返回" width="160" />
        <el-table-column prop="reason" label="外出事由" min-width="180" show-overflow-tooltip />
        <el-table-column prop="status" label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="statusType(row.status)" effect="light" size="small" round>{{ row.status }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180" align="center" fixed="right">
          <template #default="{ row }">
            <template v-if="row.status === '待审核' && userRole !== '学生'">
              <el-button type="success" link icon="Check" @click="onApprove(row)">通过</el-button>
              <el-button type="danger" link icon="Close" @click="onReject(row)">驳回</el-button>
            </template>
            <el-button v-else type="primary" link icon="View" @click="onView(row)">详情</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-wrap">
        <el-pagination background layout="total, prev, pager, next" :total="filteredList.length" :page-size="10" />
      </div>
    </el-card>

    <el-dialog v-model="showModal" title="新建外出申请" width="540px" destroy-on-close>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="90px">
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="姓名" prop="studentName"><el-input v-model="form.studentName" placeholder="请输入" /></el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="班级"><el-input v-model="form.className" placeholder="请输入" /></el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="外出地点" prop="destination"><el-input v-model="form.destination" placeholder="请输入外出地点" /></el-form-item>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="外出时间" prop="startTime"><el-date-picker v-model="form.startTime" type="datetime" value-format="YYYY-MM-DD HH:mm" style="width:100%" /></el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="预计返回" prop="endTime"><el-date-picker v-model="form.endTime" type="datetime" value-format="YYYY-MM-DD HH:mm" style="width:100%" /></el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="外出事由" prop="reason"><el-input v-model="form.reason" type="textarea" :rows="3" placeholder="请输入事由" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showModal=false">取消</el-button>
        <el-button type="primary" @click="onSave">提交申请</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="showDetail" title="申请详情" width="480px">
      <el-descriptions :column="1" border>
        <el-descriptions-item label="申请人">{{ detail.studentName }}</el-descriptions-item>
        <el-descriptions-item label="班级">{{ detail.className }}</el-descriptions-item>
        <el-descriptions-item label="外出地点">{{ detail.destination }}</el-descriptions-item>
        <el-descriptions-item label="外出时间">{{ detail.startTime }}</el-descriptions-item>
        <el-descriptions-item label="预计返回">{{ detail.endTime }}</el-descriptions-item>
        <el-descriptions-item label="事由">{{ detail.reason }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="statusType(detail.status)" effect="light" size="small" round>{{ detail.status }}</el-tag>
        </el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { ElMessage } from 'element-plus'

const userRole = computed(() => {
  try { return JSON.parse(localStorage.getItem('user') || '{}').role || '管理员' } catch { return '管理员' }
})

const activeTab = ref('all')
const showModal = ref(false)
const showDetail = ref(false)
const detail = ref({})
const formRef = ref()

const form = ref({ studentName: '', className: '', destination: '', startTime: '', endTime: '', reason: '' })
const rules = {
  studentName: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
  destination: [{ required: true, message: '请输入地点', trigger: 'blur' }],
  startTime: [{ required: true, message: '请选择时间', trigger: 'change' }],
  endTime: [{ required: true, message: '请选择时间', trigger: 'change' }],
  reason: [{ required: true, message: '请输入事由', trigger: 'blur' }],
}

const requests = ref([
  { id: 1, studentName: '张三', className: '计算机2401', destination: '市中心医院', startTime: '2026-05-16 08:00', endTime: '2026-05-16 17:00', reason: '身体不适，需前往医院就诊', status: '待审核' },
  { id: 2, studentName: '李四', className: '计算机2401', destination: '老家', startTime: '2026-05-17 12:00', endTime: '2026-05-19 18:00', reason: '家中有急事需要回家处理', status: '已通过' },
  { id: 3, studentName: '王五', className: '软件2401', destination: '科技园区', startTime: '2026-05-15 14:00', endTime: '2026-05-15 18:00', reason: '参加企业面试', status: '已通过' },
  { id: 4, studentName: '赵六', className: '软件2401', destination: '火车站', startTime: '2026-05-20 06:00', endTime: '2026-05-20 09:00', reason: '赶火车回家', status: '已驳回' },
])

let nextId = 5
const filteredList = computed(() => {
  if (activeTab.value === 'all') return requests.value
  return requests.value.filter(r => r.status === activeTab.value)
})

function statusType(s) { return { '待审核': 'warning', '已通过': 'success', '已驳回': 'danger' }[s] || 'info' }
function openAdd() {
  form.value = { studentName: '', className: '', destination: '', startTime: '', endTime: '', reason: '' }
  showModal.value = true
}
function onSave() {
  formRef.value.validate(valid => {
    if (!valid) return
    requests.value.push({ ...form.value, id: nextId++, status: '待审核' })
    showModal.value = false; ElMessage.success('申请已提交')
  })
}
function onApprove(row) { row.status = '已通过'; ElMessage.success('已通过') }
function onReject(row) { row.status = '已驳回'; ElMessage.warning('已驳回') }
function onView(row) { detail.value = row; showDetail.value = true }
</script>

<style scoped>
.stats-bar { display: flex; gap: 16px; margin-bottom: 20px; }
.stat-item { flex: 1; background: #fff; border-radius: 10px; padding: 18px 22px; border: 1px solid var(--border-color); display: flex; align-items: baseline; gap: 10px; transition: box-shadow 0.2s; }
.stat-item:hover { box-shadow: var(--shadow-md); }
.stat-item.warning .stat-num { color: #d97706; }
.stat-item.success .stat-num { color: #059669; }
.stat-num { font-size: 28px; font-weight: 800; color: var(--el-color-primary); }
.stat-label { font-size: 13px; color: var(--text-tertiary); }
.table-card { min-height: 0; }
.card-header { display: flex; justify-content: space-between; align-items: center; }
.card-title { display: flex; align-items: center; gap: 8px; font-size: 16px; font-weight: 700; color: var(--text-primary); }
.custom-tabs { margin-bottom: 4px; }
.cell-name { display: flex; align-items: center; gap: 8px; }
.name-avatar { background: linear-gradient(135deg,#4f6ef7,#6366f1); color: #fff; font-size: 12px; font-weight: 600; flex-shrink: 0; }
.pagination-wrap { display: flex; justify-content: flex-end; margin-top: 18px; }
</style>
