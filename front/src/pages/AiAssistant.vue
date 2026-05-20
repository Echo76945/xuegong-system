<template>
  <div class="page-enter">
    <el-card shadow="never" class="ai-card">
      <div class="ai-layout">
        <!-- Sidebar: quick questions -->
        <div class="ai-sidebar">
          <div class="ai-sidebar-header">
            <el-icon :size="18" color="var(--el-color-primary)"><MagicStick /></el-icon>
            <span>快捷问题</span>
          </div>
          <div class="quick-questions">
            <div v-for="(q, i) in quickQuestions" :key="i" class="quick-q-item" @click="askQuick(q)">
              <el-icon :size="14"><ChatLineRound /></el-icon>
              <span>{{ q }}</span>
            </div>
          </div>
          <div class="ai-sidebar-footer">
            <el-button text type="danger" icon="Delete" size="small" @click="clearChat">清空对话</el-button>
          </div>
        </div>

        <!-- Chat area -->
        <div class="ai-chat">
          <div class="ai-messages" ref="msgContainer">
            <!-- Welcome message -->
            <div v-if="messages.length === 0" class="ai-welcome">
              <div class="welcome-icon">
                <el-icon :size="40" color="#fff"><ChatDotRound /></el-icon>
              </div>
              <h2>AI 智能助理</h2>
              <p>你好！我是学工管理系统AI助手，可以帮你解答学业、生活、奖助等方面的问题。</p>
              <div class="welcome-chips">
                <el-tag v-for="(q, i) in quickQuestions.slice(0, 4)" :key="i" effect="plain" class="welcome-chip" @click="askQuick(q)">{{ q }}</el-tag>
              </div>
            </div>

            <!-- Messages -->
            <div v-for="msg in messages" :key="msg.id" class="ai-msg" :class="msg.role">
              <div class="ai-msg-avatar">
                <el-avatar v-if="msg.role === 'user'" :size="34" class="user-msg-avatar">{{ userName.charAt(0) }}</el-avatar>
                <el-avatar v-else :size="34" class="ai-msg-icon">
                  <el-icon :size="18" color="#fff"><ChatDotRound /></el-icon>
                </el-avatar>
              </div>
              <div class="ai-msg-content">
                <div class="ai-msg-name">{{ msg.role === 'user' ? userName : 'AI 助理' }}</div>
                <div class="ai-msg-bubble" :class="{ loading: msg.loading }">
                  <span v-if="msg.loading" class="typing-dots"><span>.</span><span>.</span><span>.</span></span>
                  <template v-else>{{ msg.text }}</template>
                </div>
                <div class="ai-msg-time">{{ msg.time }}</div>
              </div>
            </div>
          </div>

          <!-- Input -->
          <div class="ai-input">
            <el-input
              v-model="inputText"
              placeholder="输入你的问题..."
              size="large"
              @keyup.enter="sendMsg"
              clearable
            >
              <template #prefix>
                <el-icon><ChatDotRound /></el-icon>
              </template>
              <template #append>
                <el-button type="primary" icon="Promotion" @click="sendMsg" :loading="isTyping">发送</el-button>
              </template>
            </el-input>
          </div>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, computed, nextTick } from 'vue'
import { ElMessage } from 'element-plus'
import request from '../api/request'

const inputText = ref('')
const isTyping = ref(false)
const msgContainer = ref(null)
const userName = computed(() => {
  try { return JSON.parse(localStorage.getItem('user') || '{}').name || '管理员' } catch { return '管理员' }
})

const quickQuestions = [
  '如何申请国家奖学金？',
  '本学期成绩什么时候出？',
  '如何办理外出请假？',
  '图书馆开放时间是什么？',
  '第二课堂积分怎么获取？',
  '如何报名参加社团活动？',
]

const messages = ref([])
let nextId = 1

function sendMsg() {
  if (!inputText.value.trim() || isTyping.value) return
  const text = inputText.value.trim()
  addMessage('user', text)
  inputText.value = ''
  callAiApi(text)
}

function askQuick(q) {
  addMessage('user', q)
  callAiApi(q)
}

function addMessage(role, text) {
  messages.value.push({
    id: nextId++,
    role,
    text,
    time: new Date().toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' }),
    loading: false,
  })
  nextTick(() => scrollToBottom())
}

async function callAiApi(userText) {
  isTyping.value = true
  const loadingMsg = { id: nextId++, role: 'assistant', text: '', time: '', loading: true }
  messages.value.push(loadingMsg)
  nextTick(() => scrollToBottom())

  try {
    // Build conversation history for context
    const history = messages.value
      .filter(m => !m.loading)
      .map(m => ({ role: m.role === 'user' ? 'user' : 'assistant', content: m.text }))

    const res = await request.post('/ai/chat', { message: userText, history }, { timeout: 60000 })

    loadingMsg.text = res.data?.reply || res.reply || '暂时无法回答，请稍后再试。'
  } catch (err) {
    loadingMsg.text = '请求失败，请检查网络或稍后再试。'
  } finally {
    loadingMsg.loading = false
    loadingMsg.time = new Date().toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' })
    isTyping.value = false
    nextTick(() => scrollToBottom())
  }
}

function clearChat() {
  messages.value = []
}

function scrollToBottom() {
  if (msgContainer.value) {
    msgContainer.value.scrollTop = msgContainer.value.scrollHeight
  }
}
</script>

<style scoped>
.page-enter { height: calc(100vh - 64px - 48px); display: flex; flex-direction: column; }
.ai-card { padding: 0 !important; flex: 1; display: flex; flex-direction: column; min-height: 0; }
.ai-card :deep(.el-card__body) { padding: 0 !important; flex: 1; display: flex; min-height: 0; }
.ai-layout { display: flex; flex: 1; min-height: 0; }

.ai-sidebar { width: 240px; border-right: 1px solid var(--border-color); display: flex; flex-direction: column; background: #fafbfc; }
.ai-sidebar-header { display: flex; align-items: center; gap: 8px; padding: 16px; font-weight: 600; font-size: 14px; border-bottom: 1px solid #f0f0f0; }
.quick-questions { flex: 1; overflow-y: auto; padding: 10px; }
.quick-q-item { display: flex; align-items: flex-start; gap: 8px; padding: 10px 12px; border-radius: 8px; cursor: pointer; font-size: 13px; color: var(--text-secondary); transition: all 0.15s; line-height: 1.4; }
.quick-q-item:hover { background: #f0f5ff; color: var(--el-color-primary); }
.ai-sidebar-footer { padding: 12px; border-top: 1px solid #f0f0f0; text-align: center; }

.ai-chat { flex: 1; display: flex; flex-direction: column; min-height: 0; }
.ai-messages { flex: 1; overflow-y: auto; padding: 24px; min-height: 0; }

/* Welcome */
.ai-welcome { display: flex; flex-direction: column; align-items: center; justify-content: center; height: 100%; text-align: center; }
.welcome-icon { width: 72px; height: 72px; border-radius: 20px; background: linear-gradient(135deg,#4f6ef7,#6366f1); display: flex; align-items: center; justify-content: center; margin-bottom: 16px; box-shadow: 0 8px 24px rgba(79,110,247,0.3); }
.ai-welcome h2 { font-size: 22px; margin: 0 0 8px; color: var(--text-primary); }
.ai-welcome p { font-size: 14px; color: var(--text-tertiary); margin: 0 0 20px; max-width: 400px; }
.welcome-chips { display: flex; flex-wrap: wrap; gap: 8px; justify-content: center; max-width: 500px; }
.welcome-chip { cursor: pointer; transition: all 0.15s; }
.welcome-chip:hover { color: var(--el-color-primary); border-color: var(--el-color-primary); }

/* Messages */
.ai-msg { display: flex; gap: 12px; margin-bottom: 20px; }
.ai-msg.user { flex-direction: row-reverse; }
.ai-msg-avatar { flex-shrink: 0; }
.user-msg-avatar { background: linear-gradient(135deg,#4f6ef7,#6366f1); color: #fff; font-weight: 600; font-size: 14px; }
.ai-msg-icon { background: linear-gradient(135deg,#7c3aed,#8b5cf6); }
.ai-msg-content { max-width: 70%; }
.ai-msg.user .ai-msg-content { text-align: right; }
.ai-msg-name { font-size: 12px; color: var(--text-tertiary); margin-bottom: 4px; font-weight: 500; }
.ai-msg-bubble { padding: 12px 16px; border-radius: 14px; font-size: 14px; line-height: 1.6; white-space: pre-line; }
.ai-msg.assistant .ai-msg-bubble { background: #fff; color: var(--text-primary); border: 1px solid var(--border-color); border-bottom-left-radius: 4px; }
.ai-msg.user .ai-msg-bubble { background: linear-gradient(135deg,#4f6ef7,#6366f1); color: #fff; border-bottom-right-radius: 4px; }
.ai-msg-bubble.loading { color: var(--text-tertiary); }
.ai-msg-time { font-size: 11px; color: var(--text-tertiary); margin-top: 4px; }

.typing-dots span { animation: dot-blink 1.4s infinite; font-size: 20px; font-weight: 700; }
.typing-dots span:nth-child(2) { animation-delay: 0.2s; }
.typing-dots span:nth-child(3) { animation-delay: 0.4s; }
@keyframes dot-blink {
  0%, 60%, 100% { opacity: 0.2; }
  30% { opacity: 1; }
}

.ai-input { padding: 16px 24px; border-top: 1px solid var(--border-color); background: #fff; }
.ai-input :deep(.el-input-group__append) { background: var(--el-color-primary); color: #fff; }
</style>
