<template>
      <div><ThreeModelViewer @speech-recognized="handleSpeechRecognized" /></div>
      <div class="back-button-container">
        <button class="back-button" @click="goBack">← 返回</button>
      </div>
  <div class="ragflow-chat-container">
    <h1>RAGFlow 智能助手</h1>

    <div class="session-control">
      <div class="dropdown">
        <label>选择助手：</label>
        <select v-model="selectedChatId" @change="loadSessions">
          <option v-for="chat in availableChats" :value="chat.id" :key="chat.id">
            {{ chat.name }}
          </option>
        </select>
      </div>

      <div class="dropdown">
        <label>选择会话：</label>
        <select v-model="sessionId" @change="loadSessionMessages">
          <option value="">-- 新建会话 --</option>
          <option v-for="session in availableSessions" :value="session.id" :key="session.id">
            {{ session.name }} ({{ formatDate(session.create_time) }})
          </option>
        </select>
      </div>

      <div class="button-group">
        <button @click="createNewSession" :disabled="!selectedChatId || loading">
          <span v-if="loading">创建中...</span>
          <span v-else>新建会话</span>
        </button>
        <button @click="deleteSession" :disabled="!sessionId" class="danger">
          删除会话
        </button>
      </div>
    </div>

    <div class="chat-box">
      <div class="messages" ref="messagesContainer">
        <div v-for="(msg, index) in messages" :key="index"
             :class="['message', msg.role]">
          <div class="message-content">{{ msg.content }}</div>
          <div v-if="msg.reference" class="message-reference">
            <div class="reference-title">引用来源：</div>
            <div v-for="(doc, docIndex) in msg.reference.doc_aggs" :key="docIndex" class="reference-doc">
              {{ docIndex + 1 }}. {{ doc.doc_name }} (相似度: {{ doc.max_similarity?.toFixed(2) ?? "0.00" }})
            </div>
          </div>
          <div class="message-time">{{ formatTime(msg.create_time) }}</div>
        </div>
        <div v-if="streaming" class="message assistant">
          <div class="message-content">{{ streamContent }}<span class="cursor">▌</span></div>
        </div>
      </div>

      <div class="input-area">
        <textarea v-model="newMessage" @keydown.enter.exact.prevent="sendMessage"
                 placeholder="输入您的问题..." :disabled="!selectedChatId" rows="3"></textarea>
        <div class="controls">
          <label class="stream-toggle">
            <input type="checkbox" v-model="useStream"> 流式响应
          </label>
          <button @click="sendMessage" :disabled="!selectedChatId || sending">
            <span v-if="sending">发送中...</span>
            <span v-else>发送 (Enter)</span>
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, onMounted, nextTick } from 'vue';
import ThreeModelViewer from '@/components/ThreeModelViewer.vue';
export default {
   components: {
    ThreeModelViewer,
  },
  setup() {
    // 配置
    const apiKey = 'ragflow-ZjYjdiYzkyM2ZmMTExZjBhMGVjNzZhZT';
    const serverAddress = 'http://172.22.104.102:80';

    // 状态
    const availableChats = ref([]);
    const availableSessions = ref([]);
    const selectedChatId = ref('');
    const sessionId = ref('');
    const messages = ref([]);
    const newMessage = ref('');
    const loading = ref(false);
    const sending = ref(false);
    const streaming = ref(false);
    const streamContent = ref('');
    const useStream = ref(true);
    const lastReference = ref(null);
    const messagesContainer = ref(null);

    // 初始化加载
    onMounted(async () => {
      await loadChats();
    });

    // 自动滚动到底部
    const scrollToBottom = () => {
      nextTick(() => {
        if (messagesContainer.value) {
          messagesContainer.value.scrollTop = messagesContainer.value.scrollHeight;
        }
      });
    };

    // 加载聊天助手列表
    const loadChats = async () => {
      try {
        const res = await fetch(`${serverAddress}/api/v1/chats`, {
          headers: { 'Authorization': `Bearer ${apiKey}` }
        });
        const { code, data } = await res.json();
        if (code === 0 && data?.length) {
          availableChats.value = data;
          selectedChatId.value = data[0].id;
          await loadSessions();
        }
      } catch (error) {
        console.error('加载助手失败:', error);
        addErrorMessage(`加载助手失败: ${error.message}`);
      }
    };

    // 加载会话列表
    const loadSessions = async () => {
      if (!selectedChatId.value) return;

      try {
        const res = await fetch(
          `${serverAddress}/api/v1/chats/${selectedChatId.value}/sessions?page_size=50&orderby=create_time&desc=true`,
          { headers: { 'Authorization': `Bearer ${apiKey}` } }
        );
        const { code, data } = await res.json();
        if (code === 0) {
          availableSessions.value = data;
          if (data.length) {
            sessionId.value = data[0].id;
            await loadSessionMessages();
          } else {
            messages.value = [];
          }
        }
      } catch (error) {
        console.error('加载会话失败:', error);
        addErrorMessage(`加载会话失败: ${error.message}`);
      }
    };

    // 加载会话消息
    const loadSessionMessages = async () => {
      if (!sessionId.value) {
        messages.value = [];
        return;
      }

      try {
        const session = availableSessions.value.find(s => s.id === sessionId.value);
        if (session) {
          messages.value = session.messages || [];
          scrollToBottom();
        }
      } catch (error) {
        console.error('加载消息失败:', error);
        addErrorMessage(`加载消息失败: ${error.message}`);
      }
    };

    // 添加错误消息
    const addErrorMessage = (message) => {
      messages.value.push({
        content: message,
        role: 'error',
        create_time: Date.now()
      });
      scrollToBottom();
    };

    // 创建新会话
    const createNewSession = async () => {
      if (!selectedChatId.value || loading.value) return;

      loading.value = true;
      try {
        const res = await fetch(
          `${serverAddress}/api/v1/chats/${selectedChatId.value}/sessions`,
          {
            method: 'POST',
            headers: {
              'Content-Type': 'application/json',
              'Authorization': `Bearer ${apiKey}`
            },
            body: JSON.stringify({
              name: `会话-${new Date().toLocaleString()}`,
              user_id: "user-" + Math.random().toString(36).substring(2, 9)
            })
          }
        );

        const { code, data } = await res.json();
        if (code === 0) {
          sessionId.value = data.id;
          messages.value = data.messages || [];
          await loadSessions();
          addWelcomeMessage();
        } else {
          addErrorMessage(`创建会话失败: ${data?.message || '未知错误'}`);
        }
      } catch (error) {
        console.error('创建会话失败:', error);
        addErrorMessage(`创建会话失败: ${error.message}`);
      } finally {
        loading.value = false;
      }
    };

    // 添加欢迎消息
    const addWelcomeMessage = () => {
      if (messages.value.length === 0) {
        messages.value.push({
          content: `您好！我是${availableChats.value.find(c => c.id === selectedChatId.value)?.name || 'AI助手'}，请问有什么可以帮您？`,
          role: 'assistant',
          create_time: Date.now()
        });
        scrollToBottom();
      }
    };

    // 删除会话
    const deleteSession = async () => {
      if (!sessionId.value || !confirm('确定要删除此会话吗？所有对话记录将永久丢失。')) return;

      try {
        const res = await fetch(
          `${serverAddress}/api/v1/chats/${selectedChatId.value}/sessions`,
          {
            method: 'DELETE',
            headers: {
              'Content-Type': 'application/json',
              'Authorization': `Bearer ${apiKey}`
            },
            body: JSON.stringify({
              ids: [sessionId.value]
            })
          }
        );

        const { code } = await res.json();
        if (code === 0) {
          sessionId.value = '';
          messages.value = [];
          await loadSessions();
        } else {
          addErrorMessage('删除会话失败');
        }
      } catch (error) {
        console.error('删除会话失败:', error);
        addErrorMessage(`删除会话失败: ${error.message}`);
      }
    };

    // 发送消息（支持流式和非流式）
    const sendMessage = async () => {
      if (!newMessage.value.trim() || !selectedChatId.value || sending.value) return;

      sending.value = true;
      streaming.value = useStream.value;
      streamContent.value = '';
      lastReference.value = null;

      const userMessage = {
        content: newMessage.value,
        role: 'user',
        create_time: Date.now()
      };
      messages.value.push(userMessage);
      const question = newMessage.value;
      newMessage.value = '';
      scrollToBottom();

      try {
        if (useStream.value) {
          await processStreamResponse(question);
        } else {
          await processNormalResponse(question);
        }
      } catch (error) {
        console.error('发送失败:', error);
        addErrorMessage(`发送失败: ${error.message}`);
      } finally {
        sending.value = false;
        streaming.value = false;
      }
    };

    // 处理流式响应
    const processStreamResponse = async (question) => {
      const response = await fetch(
        `${serverAddress}/api/v1/chats/${selectedChatId.value}/completions`,
        {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${apiKey}`,
            'Accept': 'text/event-stream'
          },
          body: JSON.stringify({
            question,
            stream: true,
            session_id: sessionId.value
          })
        }
      );

      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }

      const reader = response.body.getReader();
      const decoder = new TextDecoder();
      let buffer = '';

      while (true) {
        const { done, value } = await reader.read();
        if (done) break;

        buffer += decoder.decode(value, { stream: true });

        // 处理可能的多条消息
        const parts = buffer.split('\n\n');
        buffer = parts.pop() || ''; // 保留不完整的部分

        for (const part of parts) {
          if (!part.startsWith('data:')) continue;

          try {
            const data = JSON.parse(part.replace('data:', '').trim());
            if (data.code === 0 && data.data !== true) {
              if (data.data.answer) {
                streamContent.value = data.data.answer;
              }
              if (data.data.reference) {
                lastReference.value = data.data.reference;
              }
              scrollToBottom();
            }
          } catch (e) {
            console.error('解析流数据失败:', e);
          }
        }
      }

      // 流结束，保存完整消息
      if (streamContent.value) {
        messages.value.push({
          content: streamContent.value,
          role: 'assistant',
          create_time: Date.now(),
          reference: lastReference.value
        });
        scrollToBottom();
      }
    };

    // 处理普通响应
    const processNormalResponse = async (question) => {
      const response = await fetch(
        `${serverAddress}/api/v1/chats/${selectedChatId.value}/completions`,
        {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${apiKey}`
          },
          body: JSON.stringify({
            question,
            stream: false,
            session_id: sessionId.value
          })
        }
      );

      const { code, data } = await response.json();
      if (code === 0) {
        messages.value.push({
          content: data.answer,
          role: 'assistant',
          create_time: Date.now(),
          reference: data.reference
        });
        scrollToBottom();
      } else {
        throw new Error(data?.message || '请求失败');
      }
    };

    // 辅助函数
    const formatTime = (timestamp) => {
      if (!timestamp) return '';
      const date = new Date(timestamp);
      return date.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' });
    };

    const formatDate = (timestamp) => {
      if (!timestamp) return '';
      const date = new Date(timestamp);
      return date.toLocaleDateString([], { month: 'short', day: 'numeric' });
    };

    const goBack = () => {
      window.history.back();
    };

    return {
      availableChats,
      availableSessions,
      selectedChatId,
      sessionId,
      messages,
      newMessage,
      loading,
      sending,
      streaming,
      streamContent,
      useStream,
      messagesContainer,
      loadSessions,
      createNewSession,
      deleteSession,
      sendMessage,
      formatTime,
      formatDate,
      goBack
    };
  }
};
</script>

<style>
.back-button-container {
  display: flex;
  justify-content: center;
  margin-bottom: 20px;
}

.back-button {
  padding: 8px 16px;
  background: #4a6fa5;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 0.95em;
  transition: background-color 0.2s;
}

.back-button:hover {
  background: #166088;
}

/* 基础样式 */
:root {
  --primary-color: #4a6fa5;
  --secondary-color: #166088;
  --accent-color: #4fc3f7;
  --error-color: #f44336;
  --success-color: #4caf50;
  --text-color: #333;
  --light-text: #666;
  --border-color: #ddd;
  --bg-color: #f9f9f9;
  --user-bubble: #e3f2fd;
  --assistant-bubble: #f1f1f1;
}

* {
  box-sizing: border-box;
  margin: 0;
  padding: 0;
}

body {
  font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
  line-height: 1.6;
  color: var(--text-color);
  background-color: #f5f5f5;
}

/* 容器样式 */
.ragflow-chat-container {
  max-width: 1000px;
  margin: 20px auto;
  padding: 20px;
  background-color: white;
  border-radius: 8px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
}

h1 {
  text-align: center;
  color: var(--primary-color);
  margin-bottom: 20px;
  font-weight: 500;
}

/* 会话控制区域 */
.session-control {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 15px;
  margin-bottom: 20px;
  padding: 15px;
  background-color: var(--bg-color);
  border-radius: 6px;
}

.dropdown {
  display: flex;
  flex-direction: column;
}

.dropdown label {
  margin-bottom: 5px;
  font-size: 0.9em;
  color: var(--light-text);
}

.dropdown select {
  padding: 8px 12px;
  border: 1px solid var(--border-color);
  border-radius: 4px;
  background-color: white;
  font-size: 0.95em;
}

.button-group {
  grid-column: 1 / -1;
  display: flex;
  gap: 10px;
}

button {
  padding: 8px 16px;
  border: none;
  border-radius: 4px;
  background-color: var(--primary-color);
  color: white;
  cursor: pointer;
  font-size: 0.95em;
  transition: background-color 0.2s;
  flex: 1;
}

button:hover {
  background-color: var(--secondary-color);
}

button:disabled {
  background-color: #cccccc;
  cursor: not-allowed;
}

button.danger {
  background-color: var(--error-color);
}

button.danger:hover {
  background-color: #d32f2f;
}

/* 聊天区域 */
.chat-box {
  border: 1px solid var(--border-color);
  border-radius: 6px;
  overflow: hidden;
  height: calc(100vh - 280px);
  min-height: 500px;
  display: flex;
  flex-direction: column;
}

.messages {
  flex: 1;
  overflow-y: auto;
  padding: 15px;
  background-color: var(--bg-color);
}

.message {
  margin-bottom: 15px;
  max-width: 85%;
  padding: 12px 15px;
  border-radius: 12px;
  word-wrap: break-word;
  animation: fadeIn 0.3s ease-out;
}

@keyframes fadeIn {
  from { opacity: 0; transform: translateY(10px); }
  to { opacity: 1; transform: translateY(0); }
}

.message.user {
  margin-left: auto;
  background-color: var(--user-bubble);
  border-bottom-right-radius: 4px;
}

.message.assistant {
  margin-right: auto;
  background-color: var(--assistant-bubble);
  border-bottom-left-radius: 4px;
}

.message.error {
  margin: 10px auto;
  max-width: 90%;
  background-color: #ffebee;
  color: var(--error-color);
  text-align: center;
}

.message-content {
  line-height: 1.5;
  font-size: 0.95em;
}

.message-reference {
  margin-top: 8px;
  padding-top: 8px;
  border-top: 1px dashed rgba(0, 0, 0, 0.1);
  font-size: 0.8em;
  color: var(--light-text);
}

.reference-title {
  font-weight: bold;
  margin-bottom: 4px;
}

.reference-doc {
  margin-left: 10px;
  margin-bottom: 3px;
}

.message-time {
  margin-top: 5px;
  font-size: 0.75em;
  color: var(--light-text);
  text-align: right;
}

.cursor {
  animation: blink 1s infinite;
}

@keyframes blink {
  0%, 100% { opacity: 1; }
  50% { opacity: 0; }
}

/* 输入区域 */
.input-area {
  padding: 15px;
  background-color: white;
  border-top: 1px solid var(--border-color);
}

textarea {
  width: 100%;
  padding: 12px;
  border: 1px solid var(--border-color);
  border-radius: 4px;
  resize: none;
  font-family: inherit;
  font-size: 0.95em;
  margin-bottom: 10px;
}

textarea:focus {
  outline: none;
  border-color: var(--accent-color);
  box-shadow: 0 0 0 2px rgba(79, 195, 247, 0.2);
}

.controls {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.stream-toggle {
  display: flex;
  align-items: center;
  gap: 5px;
  font-size: 0.9em;
  color: var(--light-text);
}

/* 响应式设计 */
@media (max-width: 768px) {
  .ragflow-chat-container {
    margin: 0;
    padding: 10px;
    border-radius: 0;
    min-height: 100vh;
  }

  .session-control {
    grid-template-columns: 1fr;
    gap: 10px;
    padding: 10px;
  }

  .chat-box {
    height: calc(100vh - 200px);
    min-height: unset;
  }

  .message {
    max-width: 90%;
    padding: 8px 12px;
  }

  .input-area {
    padding: 10px;
  }

  textarea {
    padding: 8px;
    font-size: 0.9em;
  }

  button {
    padding: 6px 12px;
  }
}

@media (max-width: 480px) {
  h1 {
    font-size: 1.3em;
    margin-bottom: 10px;
  }

  .dropdown label {
    font-size: 0.8em;
  }

  .dropdown select {
    padding: 6px 8px;
    font-size: 0.85em;
  }

  .back-button {
    padding: 6px 12px;
    font-size: 0.85em;
  }

  .message-content {
    font-size: 0.9em;
  }
}
</style>
