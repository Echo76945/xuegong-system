<template>
  <div class="page-enter">
    <div class="stats-bar">
      <div class="stat-item">
        <span class="stat-num">{{ incidents.length }}</span>
        <span class="stat-label">上报总数</span>
      </div>
      <div class="stat-item danger">
        <span class="stat-num">{{ incidents.filter(i => i.severity === '紧急').length }}</span>
        <span class="stat-label">紧急</span>
      </div>
      <div class="stat-item warning">
        <span class="stat-num">{{ incidents.filter(i => i.status === '处理中').length }}</span>
        <span class="stat-label">处理中</span>
      </div>
      <div class="stat-item success">
        <span class="stat-num">{{ incidents.filter(i => i.status === '已处理').length }}</span>
        <span class="stat-label">已处理</span>
      </div>
    </div>

    <el-card shadow="never" class="table-card">
      <template #header>
        <div class="card-header">
          <div class="card-title">
            <el-icon :size="20" color="var(--el-color-primary)"><WarningFilled /></el-icon>
            <span>异常上报</span>
          </div>
          <el-button type="primary" icon="Plus" @click="openAdd">新建上报</el-button>
        </div>
      </template>

      <div class="filter-bar">
        <el-input v-model="searchKey" placeholder="搜索标题 / 上报人..." prefix-icon="Search" clearable class="filter-search" />
        <el-select v-model="filterSeverity" placeholder="严重程度" clearable style="width:130px">
          <el-option label="一般" value="一般" />
          <el-option label="重要" value="重要" />
          <el-option label="紧急" value="紧急" />
        </el-select>
        <el-select v-model="filterStatus" placeholder="处理状态" clearable style="width:130px">
          <el-option label="待处理" value="待处理" />
          <el-option label="处理中" value="处理中" />
          <el-option label="已处理" value="已处理" />
        </el-select>
        <el-button icon="RefreshRight" @click="resetFilters">重置</el-button>
      </div>

      <el-table :data="filteredList" stripe style="width:100%">
        <el-table-column prop="title" label="标题" min-width="200">
          <template #default="{ row }"><span class="cell-bold">{{ row.title }}</span></template>
        </el-table-column>
        <el-table-column prop="reporter" label="上报人" width="100" />
        <el-table-column prop="severity" label="严重程度" width="110" align="center">
          <template #default="{ row }">
            <el-tag :type="severityType(row.severity)" effect="dark" size="small" round>{{ row.severity }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="category" label="类别" width="100" align="center">
          <template #default="{ row }">
            <el-tag effect="light" size="small" round>{{ row.category }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="reportTime" label="上报时间" width="170" />
        <el-table-column prop="status" label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="statusType(row.status)" effect="light" size="small" round>{{ row.status }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="handler" label="处理人" width="100" />
        <el-table-column label="操作" width="180" align="center" fixed="right">
          <template #default="{ row }">
            <template v-if="row.status === '待处理'">
              <el-button type="primary" link icon="Refresh" @click="onProcess(row)">开始处理</el-button>
            </template>
            <template v-else-if="row.status === '处理中'">
              <el-button type="success" link icon="Check" @click="onResolve(row)">标记完成</el-button>
            </template>
            <el-button type="primary" link icon="View" @click="onView(row)">详情</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-wrap">
        <el-pagination background layout="total, prev, pager, next" :total="filteredList.length" :page-size="10" />
      </div>
    </el-card>

    <el-dialog v-model="showModal" title="新建异常上报" width="560px" destroy-on-close>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="90px">
        <el-form-item label="标题" prop="title"><el-input v-model="form.title" placeholder="请输入标题" /></el-form-item>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="上报人" prop="reporter"><el-input v-model="form.reporter" placeholder="请输入" /></el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="类别" prop="category">
              <el-select v-model="form.category" style="width:100%">
                <el-option label="安全" value="安全" />
                <el-option label="健康" value="健康" />
                <el-option label="心理" value="心理" />
                <el-option label="设备" value="设备" />
                <el-option label="其他" value="其他" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="严重程度" prop="severity">
          <el-radio-group v-model="form.severity">
            <el-radio label="一般" />
            <el-radio label="重要" />
            <el-radio label="紧急" />
          </el-radio-group>
        </el-form-item>
        <el-form-item label="详细描述" prop="description">
          <el-input v-model="form.description" type="textarea" :rows="4" placeholder="请详细描述异常情况" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showModal=false">取消</el-button>
        <el-button type="primary" @click="onSave">提交上报</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="showDetail" title="异常详情" width="520px">
      <el-descriptions :column="1" border>
        <el-descriptions-item label="标题">{{ detail.title }}</el-descriptions-item>
        <el-descriptions-item label="上报人">{{ detail.reporter }}</el-descriptions-item>
        <el-descriptions-item label="类别">{{ detail.category }}</el-descriptions-item>
        <el-descriptions-item label="严重程度">
          <el-tag :type="severityType(detail.severity)" effect="dark" size="small" round>{{ detail.severity }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="上报时间">{{ detail.reportTime }}</el-descriptions-item>
        <el-descriptions-item label="描述">{{ detail.description }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="statusType(detail.status)" effect="light" size="small" round>{{ detail.status }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="处理人">{{ detail.handler || '-' }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { ElMessage } from 'element-plus'

const searchKey = ref('')
const filterSeverity = ref('')
const filterStatus = ref('')
const showModal = ref(false)
const showDetail = ref(false)
const detail = ref({})
const formRef = ref()

const form = ref({ title: '', reporter: '', category: '安全', severity: '一般', description: '' })
const rules = {
  title: [{ required: true, message: '请输入标题', trigger: 'blur' }],
  reporter: [{ required: true, message: '请输入上报人', trigger: 'blur' }],
  description: [{ required: true, message: '请输入描述', trigger: 'blur' }],
}

const incidents = ref([
  { id: 1, title: '3号宿舍楼消防通道被杂物堵塞', reporter: '张三', category: '安全', severity: '紧急', description: '3号楼2层消防通道堆放大量杂物，存在严重安全隐患。', reportTime: '2026-05-15 08:30', status: '处理中', handler: '后勤处' },
  { id: 2, title: '教学楼A区饮水机故障', reporter: '李四', category: '设备', severity: '一般', description: 'A区3楼饮水机不出水，已持续2天。', reportTime: '2026-05-14 10:00', status: '待处理', handler: '' },
  { id: 3, title: '学生心理异常预警', reporter: '辅导员王老师', category: '心理', severity: '重要', description: '计算机2401班赵六近期情绪低落，多次缺课，需关注。', reportTime: '2026-05-13 14:20', status: '已处理', handler: '心理中心' },
  { id: 4, title: '运动场地面破损', reporter: '体育部', category: '安全', severity: '一般', description: '篮球场3号场地地面有裂缝，容易导致扭伤。', reportTime: '2026-05-12 09:15', status: '已处理', handler: '后勤处' },
])

let nextId = 5
const filteredList = computed(() => {
  return incidents.value.filter(i => {
    const matchKey = !searchKey.value || i.title.includes(searchKey.value) || i.reporter.includes(searchKey.value)
    const matchSev = !filterSeverity.value || i.severity === filterSeverity.value
    const matchStatus = !filterStatus.value || i.status === filterStatus.value
    return matchKey && matchSev && matchStatus
  })
})

function severityType(s) { return { '一般': 'info', '重要': 'warning', '紧急': 'danger' }[s] || 'info' }
function statusType(s) { return { '待处理': 'warning', '处理中': 'primary', '已处理': 'success' }[s] || 'info' }
function resetFilters() { searchKey.value = ''; filterSeverity.value = ''; filterStatus.value = '' }
function onProcess(row) { row.status = '处理中'; row.handler = '管理员'; ElMessage.success('已开始处理') }
function onResolve(row) { row.status = '已处理'; ElMessage.success('已标记完成') }
function onView(row) { detail.value = row; showDetail.value = true }
function openAdd() {
  form.value = { title: '', reporter: '', category: '安全', severity: '一般', description: '' }
  showModal.value = true
}
function onSave() {
  formRef.value.validate(valid => {
    if (!valid) return
    incidents.value.push({ ...form.value, id: nextId++, reportTime: new Date().toLocaleString('zh-CN'), status: '待处理', handler: '' })
    showModal.value = false; ElMessage.success('上报成功')
  })
}
</script>

<style scoped>
.stats-bar { display: flex; gap: 16px; margin-bottom: 20px; }
.stat-item { flex: 1; background: #fff; border-radius: 10px; padding: 18px 22px; border: 1px solid var(--border-color); display: flex; align-items: baseline; gap: 10px; transition: box-shadow 0.2s; }
.stat-item:hover { box-shadow: var(--shadow-md); }
.stat-item.danger .stat-num { color: #ff3b30; }
.stat-item.warning .stat-num { color: #d97706; }
.stat-item.success .stat-num { color: #059669; }
.stat-num { font-size: 28px; font-weight: 800; color: var(--el-color-primary); }
.stat-label { font-size: 13px; color: var(--text-tertiary); }
.table-card { min-height: 0; }
.card-header { display: flex; justify-content: space-between; align-items: center; }
.card-title { display: flex; align-items: center; gap: 8px; font-size: 16px; font-weight: 700; color: var(--text-primary); }
.filter-bar { display: flex; gap: 12px; margin-bottom: 18px; align-items: center; }
.filter-search { width: 260px; }
.cell-bold { font-weight: 500; }
.pagination-wrap { display: flex; justify-content: flex-end; margin-top: 18px; }
</style>
