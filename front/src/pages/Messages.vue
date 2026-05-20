<template>
  <div class="page-enter">
    <el-card shadow="never" class="msg-card">
      <div class="msg-layout">
        <!-- Conversation list -->
        <div class="conv-list">
          <div class="conv-header">
            <el-input v-model="searchKey" placeholder="搜索联系人..." prefix-icon="Search" clearable size="small" />
          </div>
          <div class="conv-items">
            <div
              v-for="conv in filteredConvs"
              :key="conv.id"
              class="conv-item"
              :class="{ active: activeConv?.id === conv.id }"
              @click="selectConv(conv)"
            >
              <el-avatar :size="40" class="conv-avatar" :style="{ background: conv.avatarBg }">{{ conv.name.charAt(0) }}</el-avatar>
              <div class="conv-info">
                <div class="conv-name">{{ conv.name }}</div>
                <div class="conv-last">{{ conv.lastMsg }}</div>
              </div>
              <div class="conv-meta">
                <span class="conv-time">{{ conv.time }}</span>
                <el-badge v-if="conv.unread" :value="conv.unread" :max="9" class="conv-badge" />
              </div>
            </div>
          </div>
        </div>

        <!-- Chat area -->
        <div class="chat-area">
          <template v-if="activeConv">
            <div class="chat-header">
              <el-avatar :size="34" class="conv-avatar" :style="{ background: activeConv.avatarBg }">{{ activeConv.name.charAt(0) }}</el-avatar>
              <span class="chat-name">{{ activeConv.name }}</span>
            </div>
            <div class="chat-messages" ref="msgContainer">
              <div v-for="msg in activeMessages" :key="msg.id" class="chat-msg" :class="msg.isMine ? 'mine' : 'theirs'">
                <div class="msg-bubble">
                  <div class="msg-text">{{ msg.text }}</div>
                  <div class="msg-time">{{ msg.time }}</div>
                </div>
              </div>
            </div>
            <div class="chat-input">
              <el-input v-model="newMsg" placeholder="输入消息..." @keyup.enter="sendMsg" clearable>
                <template #append>
                  <el-button type="primary" icon="Promotion" @click="sendMsg">发送</el-button>
                </template>
              </el-input>
            </div>
          </template>
          <template v-else>
            <div class="chat-empty">
              <el-icon :size="48" color="#c0c4cc"><ChatDotRound /></el-icon>
              <p>选择一个对话开始聊天</p>
            </div>
          </template>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, computed, nextTick } from 'vue'
import { ElMessage } from 'element-plus'

const searchKey = ref('')
const newMsg = ref('')
const activeConv = ref(null)
const msgContainer = ref(null)

const conversations = ref([
  { id: 1, name: '王教授', lastMsg: '好的，下周三下午来我办公室', time: '14:30', unread: 2, avatarBg: 'linear-gradient(135deg,#d97706,#f59e0b)' },
  { id: 2, name: '张三', lastMsg: '老师，我想咨询一下选课的事', time: '13:15', unread: 0, avatarBg: 'linear-gradient(135deg,#4f6ef7,#6366f1)' },
  { id: 3, name: '辅导员李老师', lastMsg: '班会改到周五下午3点', time: '昨天', unread: 1, avatarBg: 'linear-gradient(135deg,#059669,#10b981)' },
  { id: 4, name: '教务处', newMsg: '您的选课已确认', time: '昨天', unread: 0, avatarBg: 'linear-gradient(135deg,#7c3aed,#8b5cf6)' },
])

const messagesMap = ref({
  1: [
    { id: 1, text: '王教授您好，我想问一下关于毕业设计选题的事', time: '14:20', isMine: true },
    { id: 2, text: '你好，你对哪个方向比较感兴趣？', time: '14:25', isMine: false },
    { id: 3, text: '我对Web开发和人工智能方向都比较感兴趣', time: '14:28', isMine: true },
    { id: 4, text: '好的，下周三下午来我办公室', time: '14:30', isMine: false },
  ],
  2: [
    { id: 1, text: '老师，我想咨询一下选课的事', time: '13:15', isMine: false },
  ],
  3: [
    { id: 1, text: '通知：班会改到周五下午3点，地点不变', time: '昨天 16:00', isMine: false },
  ],
  4: [
    { id: 1, text: '您的2025-2026学年第一学期选课已确认，请查看课表', time: '昨天 10:00', isMine: false },
  ],
})

let nextMsgId = 100
const filteredConvs = computed(() => {
  if (!searchKey.value) return conversations.value
  return conversations.value.filter(c => c.name.includes(searchKey.value))
})
const activeMessages = computed(() => {
  if (!activeConv.value) return []
  return messagesMap.value[activeConv.value.id] || []
})

function selectConv(conv) {
  activeConv.value = conv
  conv.unread = 0
  nextTick(() => scrollToBottom())
}
function sendMsg() {
  if (!newMsg.value.trim() || !activeConv.value) return
  const id = activeConv.value.id
  if (!messagesMap.value[id]) messagesMap.value[id] = []
  messagesMap.value[id].push({
    id: nextMsgId++,
    text: newMsg.value,
    time: new Date().toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' }),
    isMine: true,
  })
  activeConv.value.lastMsg = newMsg.value
  activeConv.value.time = '刚刚'
  newMsg.value = ''
  nextTick(() => scrollToBottom())
  setTimeout(() => {
    if (messagesMap.value[id]) {
      messagesMap.value[id].push({
        id: nextMsgId++,
        text: '收到，已知晓。',
        time: new Date().toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' }),
        isMine: false,
      })
      nextTick(() => scrollToBottom())
    }
  }, 1000)
}
function scrollToBottom() {
  if (msgContainer.value) {
    msgContainer.value.scrollTop = msgContainer.value.scrollHeight
  }
}
</script>

<style scoped>
.msg-card { padding: 0 !important; }
.msg-card :deep(.el-card__body) { padding: 0 !important; }
.msg-layout { display: flex; height: calc(100vh - 160px); min-height: 500px; }

.conv-list { width: 300px; border-right: 1px solid var(--border-color); display: flex; flex-direction: column; }
.conv-header { padding: 14px; border-bottom: 1px solid #f2f3f5; }
.conv-items { flex: 1; overflow-y: auto; }
.conv-item { display: flex; align-items: center; gap: 12px; padding: 14px; cursor: pointer; transition: background 0.15s; border-bottom: 1px solid #f5f5f5; }
.conv-item:hover { background: #f7f8fa; }
.conv-item.active { background: #f0f5ff; border-right: 3px solid var(--el-color-primary); }
.conv-avatar { flex-shrink: 0; color: #fff; font-weight: 600; font-size: 14px; }
.conv-info { flex: 1; min-width: 0; }
.conv-name { font-size: 14px; font-weight: 600; color: var(--text-primary); }
.conv-last { font-size: 12px; color: var(--text-tertiary); white-space: nowrap; overflow: hidden; text-overflow: ellipsis; margin-top: 2px; }
.conv-meta { display: flex; flex-direction: column; align-items: flex-end; gap: 4px; }
.conv-time { font-size: 11px; color: var(--text-tertiary); }

.chat-area { flex: 1; display: flex; flex-direction: column; }
.chat-header { display: flex; align-items: center; gap: 10px; padding: 14px 20px; border-bottom: 1px solid var(--border-color); }
.chat-name { font-size: 15px; font-weight: 600; }
.chat-messages { flex: 1; overflow-y: auto; padding: 20px; display: flex; flex-direction: column; gap: 14px; background: #f7f8fa; }
.chat-msg { display: flex; }
.chat-msg.mine { justify-content: flex-end; }
.chat-msg.theirs { justify-content: flex-start; }
.msg-bubble { max-width: 65%; padding: 10px 14px; border-radius: 12px; }
.chat-msg.mine .msg-bubble { background: linear-gradient(135deg,#4f6ef7,#6366f1); color: #fff; border-bottom-right-radius: 4px; }
.chat-msg.theirs .msg-bubble { background: #fff; color: var(--text-primary); border-bottom-left-radius: 4px; box-shadow: 0 1px 3px rgba(0,0,0,0.06); }
.msg-text { font-size: 14px; line-height: 1.5; }
.msg-time { font-size: 11px; margin-top: 4px; opacity: 0.6; text-align: right; }
.chat-input { padding: 14px 20px; border-top: 1px solid var(--border-color); background: #fff; }
.chat-input :deep(.el-input-group__append) { background: var(--el-color-primary); color: #fff; }

.chat-empty { flex: 1; display: flex; flex-direction: column; align-items: center; justify-content: center; color: var(--text-tertiary); }
.chat-empty p { margin-top: 12px; font-size: 14px; }
</style>
