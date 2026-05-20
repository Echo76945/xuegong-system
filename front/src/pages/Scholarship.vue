<template>
  <div class="page-enter">
    <!-- Stats bar -->
    <div class="stats-bar">
      <div class="stat-item">
        <span class="stat-num">{{ list.length }}</span>
        <span class="stat-label">申请总数</span>
      </div>
      <div class="stat-item warning">
        <span class="stat-num">{{ list.filter(s => s.status === '待班主任审批').length }}</span>
        <span class="stat-label">待班主任审批</span>
      </div>
      <div class="stat-item info">
        <span class="stat-num">{{ list.filter(s => s.status === '待学校审批').length }}</span>
        <span class="stat-label">待学校审批</span>
      </div>
      <div class="stat-item success">
        <span class="stat-num">{{ list.filter(s => s.status === '已通过').length }}</span>
        <span class="stat-label">已通过</span>
      </div>
      <div class="stat-item danger">
        <span class="stat-num">{{ list.filter(s => s.status === '已驳回').length }}</span>
        <span class="stat-label">已驳回</span>
      </div>
    </div>

    <el-card shadow="never" class="table-card">
      <template #header>
        <div class="card-header">
          <div class="card-title">
            <el-icon :size="20" color="var(--el-color-primary)"><Trophy /></el-icon>
            <span>申请列表</span>
          </div>
          <el-button v-if="userRole === '学生'" type="primary" icon="Plus" @click="openAdd">新建申请</el-button>
        </div>
      </template>

      <el-tabs v-model="activeTab" class="custom-tabs">
        <el-tab-pane name="all">
          <template #label>
            <span class="tab-label">全部 <el-badge :value="list.length" :max="99" class="tab-badge" /></span>
          </template>
        </el-tab-pane>
        <el-tab-pane name="待班主任审批">
          <template #label>
            <span class="tab-label">待班主任审批 <el-badge :value="list.filter(s=>s.status==='待班主任审批').length" :max="99" class="tab-badge" type="warning" /></span>
          </template>
        </el-tab-pane>
        <el-tab-pane name="待学校审批">
          <template #label>
            <span class="tab-label">待学校审批 <el-badge :value="list.filter(s=>s.status==='待学校审批').length" :max="99" class="tab-badge" type="primary" /></span>
          </template>
        </el-tab-pane>
        <el-tab-pane name="已通过">
          <template #label>
            <span class="tab-label">已通过 <el-badge :value="list.filter(s=>s.status==='已通过').length" :max="99" class="tab-badge" type="success" /></span>
          </template>
        </el-tab-pane>
        <el-tab-pane name="已驳回">
          <template #label>
            <span class="tab-label">已驳回 <el-badge :value="list.filter(s=>s.status==='已驳回').length" :max="99" class="tab-badge" type="danger" /></span>
          </template>
        </el-tab-pane>
      </el-tabs>

      <el-table :data="filteredList" stripe style="width:100%" class="data-table" v-loading="loading">
        <el-table-column prop="studentName" label="申请人" width="100">
          <template #default="{ row }">
            <div class="cell-name">
              <el-avatar :size="30" class="name-avatar">{{ row.studentName.charAt(0) }}</el-avatar>
              <span>{{ row.studentName }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="type" label="类型" width="110">
          <template #default="{ row }">
            <el-tag :type="row.type === '奖学金' ? 'warning' : 'success'" effect="light" size="small" round>{{ row.type }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="name" label="名称" min-width="160">
          <template #default="{ row }">
            <span class="cell-bold">{{ row.name }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="amount" label="金额" width="120" align="center">
          <template #default="{ row }">
            <span class="amount-cell">¥{{ row.amount?.toLocaleString() }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="reason" label="申请理由" min-width="200" show-overflow-tooltip />
        <el-table-column prop="status" label="状态" width="130" align="center">
          <template #default="{ row }">
            <el-tag :type="statusType(row.status)" effect="light" size="small" round>
              <el-icon v-if="row.status==='待班主任审批'||row.status==='待学校审批'" style="margin-right:3px"><Clock /></el-icon>
              <el-icon v-else-if="row.status==='已通过'" style="margin-right:3px"><Check /></el-icon>
              <el-icon v-else style="margin-right:3px"><Close /></el-icon>
              {{ row.status }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="applyTime" label="申请时间" width="120" />
        <el-table-column label="操作" width="180" align="center" fixed="right">
          <template #default="{ row }">
            <template v-if="canApprove(row)">
              <el-button type="success" link icon="Check" @click="onApprove(row)">通过</el-button>
              <el-button type="danger" link icon="Close" @click="openReject(row)">驳回</el-button>
            </template>
            <el-button v-else type="primary" link icon="View" @click="onView(row)">详情</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-wrap">
        <el-pagination background layout="total, prev, pager, next" :total="filteredList.length" :page-size="10" />
      </div>
    </el-card>

    <!-- New application dialog -->
    <el-dialog v-model="showModal" title="新建奖助申请" width="560px" destroy-on-close>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="90px">
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="申请人" prop="studentName">
              <el-input v-model="form.studentName" placeholder="请输入姓名" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="类型" prop="type">
              <el-select v-model="form.type" style="width:100%">
                <el-option label="奖学金" value="奖学金" />
                <el-option label="助学金" value="助学金" />
                <el-option label="勤工助学" value="勤工助学" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="14">
            <el-form-item label="名称" prop="name">
              <el-input v-model="form.name" placeholder="如：国家奖学金" />
            </el-form-item>
          </el-col>
          <el-col :span="10">
            <el-form-item label="金额(元)" prop="amount">
              <el-input-number v-model="form.amount" :min="0" :step="500" style="width:100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="申请理由" prop="reason">
          <el-input v-model="form.reason" type="textarea" :rows="4" placeholder="请输入申请理由" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showModal=false">取消</el-button>
        <el-button type="primary" @click="onSave" :loading="saving">提交申请</el-button>
      </template>
    </el-dialog>

    <!-- Reject remark dialog -->
    <el-dialog v-model="showReject" title="驳回申请" width="460px" destroy-on-close>
      <el-form label-width="80px">
        <el-form-item label="驳回原因">
          <el-input v-model="rejectRemark" type="textarea" :rows="3" placeholder="请输入驳回原因（选填）" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showReject=false">取消</el-button>
        <el-button type="danger" @click="onReject" :loading="rejecting">确认驳回</el-button>
      </template>
    </el-dialog>

    <!-- Detail dialog -->
    <el-dialog v-model="showDetail" title="申请详情" width="520px">
      <el-descriptions :column="1" border>
        <el-descriptions-item label="申请人">{{ detail.studentName }}</el-descriptions-item>
        <el-descriptions-item label="类型">
          <el-tag :type="detail.type === '奖学金' ? 'warning' : 'success'" effect="light" size="small">{{ detail.type }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="名称">{{ detail.name }}</el-descriptions-item>
        <el-descriptions-item label="金额">¥{{ detail.amount?.toLocaleString() }}</el-descriptions-item>
        <el-descriptions-item label="理由">{{ detail.reason }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="statusType(detail.status)" effect="light" size="small" round>{{ detail.status }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item v-if="detail.approverRemark" label="审批备注">{{ detail.approverRemark }}</el-descriptions-item>
        <el-descriptions-item label="申请时间">{{ detail.applyTime }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import request from '../api/request'

const userRole = computed(() => {
  try { return JSON.parse(localStorage.getItem('user') || '{}').role || '管理员' } catch { return '管理员' }
})

const activeTab = ref('all')
const showModal = ref(false)
const showDetail = ref(false)
const showReject = ref(false)
const rejectRemark = ref('')
const rejectRow = ref(null)
const rejecting = ref(false)
const detail = ref({})
const formRef = ref()
const loading = ref(false)
const saving = ref(false)
const form = ref({ studentName: '', type: '奖学金', name: '', amount: 5000, reason: '' })
const rules = {
  studentName: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
  name: [{ required: true, message: '请输入名称', trigger: 'blur' }],
  reason: [{ required: true, message: '请输入理由', trigger: 'blur' }],
}

const list = ref([])

onMounted(() => {
  fetchList()
})

async function fetchList() {
  loading.value = true
  try {
    const res = await request.get('/scholarships')
    list.value = res.data
  } catch (err) {
    console.error('获取奖助申请列表失败:', err)
  } finally {
    loading.value = false
  }
}

const filteredList = computed(() => {
  if (activeTab.value === 'all') return list.value
  return list.value.filter(s => s.status === activeTab.value)
})

function statusType(s) {
  return { '待班主任审批': 'warning', '待学校审批': '', '已通过': 'success', '已驳回': 'danger' }[s] || 'info'
}

function canApprove(row) {
  if (userRole.value === '教师' && row.status === '待班主任审批') return true
  if (userRole.value === '管理员' && row.status === '待学校审批') return true
  return false
}

function openAdd() {
  form.value = { studentName: '', type: '奖学金', name: '', amount: 5000, reason: '' }
  showModal.value = true
}

async function onSave() {
  formRef.value.validate(async valid => {
    if (!valid) return
    saving.value = true
    try {
      await request.post('/scholarships', form.value)
      showModal.value = false
      ElMessage.success('申请已提交，等待班主任审批')
      fetchList()
    } catch (err) {
      console.error('提交申请失败:', err)
    } finally {
      saving.value = false
    }
  })
}

async function onApprove(row) {
  try {
    await request.put(`/scholarships/${row.id}/approve`, {
      status: '已通过',
      remark: '',
      role: userRole.value,
    })
    ElMessage.success('审批通过')
    fetchList()
  } catch (err) {
    console.error('审批失败:', err)
  }
}

function openReject(row) {
  rejectRow.value = row
  rejectRemark.value = ''
  showReject.value = true
}

async function onReject() {
  if (!rejectRow.value) return
  rejecting.value = true
  try {
    await request.put(`/scholarships/${rejectRow.value.id}/approve`, {
      status: '已驳回',
      remark: rejectRemark.value,
      role: userRole.value,
    })
    showReject.value = false
    ElMessage.warning('已驳回')
    fetchList()
  } catch (err) {
    console.error('审批失败:', err)
  } finally {
    rejecting.value = false
  }
}

function onView(row) {
  detail.value = row
  showDetail.value = true
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
.stat-item:hover { box-shadow: var(--shadow-md); }
.stat-num {
  font-size: 28px;
  font-weight: 800;
  color: var(--el-color-primary);
}
.stat-item.warning .stat-num { color: #d97706; }
.stat-item.info .stat-num { color: var(--el-color-primary); }
.stat-item.success .stat-num { color: #059669; }
.stat-item.danger .stat-num { color: #ff3b30; }
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

.custom-tabs {
  margin-bottom: 4px;
}
.tab-label {
  display: inline-flex;
  align-items: center;
  gap: 6px;
}
.tab-badge :deep(.el-badge__content) {
  font-size: 10px;
}

.data-table { border-radius: 10px; overflow: hidden; }

.cell-name {
  display: flex;
  align-items: center;
  gap: 8px;
}
.name-avatar {
  background: linear-gradient(135deg, #7c3aed 0%, #8b5cf6 100%);
  color: #fff;
  font-size: 12px;
  font-weight: 600;
  flex-shrink: 0;
}
.cell-bold { font-weight: 500; }

.amount-cell {
  font-weight: 700;
  color: #d97706;
  font-size: 14px;
}

.pagination-wrap {
  display: flex;
  justify-content: flex-end;
  margin-top: 18px;
}
</style>
