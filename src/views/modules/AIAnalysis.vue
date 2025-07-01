<!--
 * 文件: AIAnalysis.vue - AI文件分析组件
 * 作者: 项目小组1组
 * 版本: 1.0.0
 * 版权: 2025 东莞佰和生物科技有限公司 保留所有权利
 * 许可证: MIT
 * 创建时间: 2025-06-20
 * 最后修改: 2025-06-29
 * 功能:
 *   - 支持拖放和选择多种文件格式(txt,csv,pdf等)
 *   - 显示已选文件列表和大小
 *   - 发送文件内容到AI进行分析
 *   - 显示分析结果并支持复制
 * 数据:
 *   - files: 已选文件列表
 *   - userMessage: 用户输入的分析要求
 * 状态:
 *   - loading: 加载状态
 *   - error: 错误信息
 *   - result: AI分析结果
 * 注意事项:
 *   - 文本文件直接发送内容给AI
 *   - 大文件(>1MB)可能导致处理缓慢
 -->
<template>
  <div class="chat-uploader">
    <button class="back-button" @click="goBack">← 返回</button>
    <h2>AI 文件分析</h2>

    <div class="file-upload-section">
      <div
        class="drop-zone"
        @dragover.prevent="dragOver = true"
        @dragleave="dragOver = false"
        @drop.prevent="handleDrop"
        :class="{ 'drag-active': dragOver }"
      >
        <div class="drop-content">
          <input
            type="file"
            multiple
            ref="fileInput"
            @change="handleFileChange"
            accept=".txt,.csv,.log,.json,.xml,.pdf,.doc,.docx,.xls,.xlsx,.jpg,.jpeg,.png"
          />
          <p>拖放文件到此处或点击选择</p>
          <p class="hint-text">
            支持文本(.txt,.csv)、文档(.pdf,.docx)、图片(.jpg,.png)等格式
          </p>
          <p class="hint-text text-danger" v-if="hasTextFiles">
            * 文本文件将直接发送内容给AI分析，请确认使用UTF-8编码
          </p>
        </div>
      </div>

      <div v-if="files.length > 0" class="file-preview">
        <h3>已选文件 <span class="file-count">({{ files.length }}个)</span></h3>

        <div class="file-list">
          <div v-for="(file, index) in files" :key="index" class="file-item">
            <div class="file-info">
              <span class="file-name">{{ file.name }}</span>
              <span class="file-size">({{ formatFileSize(file.size) }})</span>
              <span v-if="isTextFile(file)" class="file-type">文本文件</span>
            </div>
            <button @click="removeFile(index)" class="remove-btn">×</button>
          </div>
        </div>

        <p class="warning" v-if="hasLargeFile">
          <span class="warning-icon">⚠</span> 文件超过1MB可能导致处理缓慢
        </p>
      </div>
    </div>

    <div class="message-section">
      <textarea
        v-model="userMessage"
        placeholder="输入你的问题或分析要求"
        rows="5"
      ></textarea>

      <div class="action-bar">
        <button
          @click="sendToAI"
          :disabled="loading || files.length === 0 && !userMessage.trim()"
        >
          {{ loading ? '分析中...' : '发送分析请求' }}
        </button>
      </div>
    </div>

    <div v-if="loading" class="loading-indicator">
      <div class="spinner"></div>
      <p>AI分析中，请稍候...</p>
    </div>

    <div v-if="error" class="error">
      <h3>错误信息</h3>
      <p>{{ error }}</p>
    </div>

    <div v-if="result" class="result">
      <div class="result-header">
        <h3>AI 分析结果</h3>
        <button @click="copyResult" class="copy-btn">复制结果</button>
      </div>
      <div class="result-content">{{ result }}</div>
    </div>
  </div>
</template>

<script>
export default {
  data() {
    return {
      files: [],          // 已选文件列表
      userMessage: '',    // 用户输入的分析要求
      loading: false,    // 加载状态
      error: null,       // 错误信息
      result: null,      // AI分析结果
      dragOver: false    // 拖放区域激活状态
    };
  },
  computed: {
    // 检查是否有文本文件
    hasTextFiles() {
      return this.files.some(file => this.isTextFile(file));
    },
    // 检查是否有大文件(>1MB)
    hasLargeFile() {
      return this.files.some(file => file.size > 1024 * 1024);
    }
  },
  created() {
    this.loadDiagnosisFile();
  },
  methods: {
    // 判断文件是否为文本文件(通过类型或扩展名)
    isTextFile(file) {
      if (!file) return false;
      if (file.type.startsWith('text/')) return true;

      const name = file.name.toLowerCase();
      return name.endsWith('.txt') ||
             name.endsWith('.csv') ||
             name.endsWith('.log') ||
             name.endsWith('.json') ||
             name.endsWith('.xml');
    },
    // 格式化文件大小显示(如 1024 => "1 KB")
    formatFileSize(bytes) {
      if (bytes === 0) return '0 Bytes';
      const k = 1024;
      const sizes = ['Bytes', 'KB', 'MB', 'GB'];
      const i = Math.floor(Math.log(bytes) / Math.log(k));
      return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i];
    },
    // 处理文件选择事件
    handleFileChange(event) {
      const newFiles = Array.from(event.target.files);
      if (newFiles.length > 0) {
        this.files = [...this.files, ...newFiles];
      }
      this.dragOver = false;
    },
    // 处理文件拖放事件
    handleDrop(event) {
      this.dragOver = false;
      const newFiles = Array.from(event.dataTransfer.files);
      if (newFiles.length > 0) {
        this.files = [...this.files, ...newFiles];
      }
    },
    // 从文件列表中移除指定文件
    removeFile(index) {
      this.files.splice(index, 1);
    },
    // 从localStorage加载诊断文件(如果有)
    loadDiagnosisFile() {
      const fileData = localStorage.getItem('diagnosisFile');
      if (fileData) {
        const fileName = this.$route.query.fileName || 'diagnosis.txt';
        const file = this.dataURLtoFile(fileData, fileName);
        this.files = [file];
        localStorage.removeItem('diagnosisFile');
      }
    },
    // 将dataURL转换为File对象
    dataURLtoFile(dataurl, filename) {
      const arr = dataurl.split(',');
      const mime = arr[0].match(/:(.*?);/)[1];
      const bstr = atob(arr[1]);
      let n = bstr.length;
      const u8arr = new Uint8Array(n);
      while (n--) {
        u8arr[n] = bstr.charCodeAt(n);
      }
      return new File([u8arr], filename, { type: mime });
    },
    // 发送文件和分析请求到AI服务
    async sendToAI() {
      // 验证输入
      if (this.files.length === 0 && !this.userMessage.trim()) {
        this.error = '请上传文件或输入分析要求';
        setTimeout(() => { this.error = null; }, 3000);
        return;
      }

      // 显示编码提示
      if (this.hasTextFiles) {
        const hasMessage = this.userMessage.trim().length > 0;
        this.userMessage = `${hasMessage ? this.userMessage + '\n\n' : ''}` +
                           `[上传文件提示] 检测到文本文件，已直接发送文件内容进行分析`;
      }

      this.loading = true;
      this.error = null;
      this.result = null;

      const formData = new FormData();

      // 添加文件
      this.files.forEach(file => {
        formData.append('files', file);
      });

      // 添加消息
      const messages = [];
      if (this.userMessage.trim()) {
        messages.push({
          role: "user",
          content: this.userMessage
        });
      }
      formData.append('messages', JSON.stringify(messages));

      try {
        const response = await fetch('http://localhost:8080/api/deepseek-chat', {
          method: 'POST',
          body: formData
        });

        if (!response.ok) {
          const errText = await response.text();
          throw new Error(errText || `请求失败: ${response.status}`);
        }

        const data = await response.json();
        if (data.choices && data.choices.length > 0) {
          this.result = data.choices[0].message.content;
        } else {
          this.result = 'AI未返回有效结果，请重试';
        }
      } catch (err) {
        console.error('Analysis error:', err);
        this.error = err.message || '分析请求失败，请检查网络连接或文件格式';
      } finally {
        this.loading = false;
      }
    },
    // 返回上一页
    goBack() {
      this.$router.go(-1);
    },
    // 复制分析结果到剪贴板
    copyResult() {
      navigator.clipboard.writeText(this.result)
        .then(() => {
          const copyBtn = document.querySelector('.copy-btn');
          if (copyBtn) {
            const originalText = copyBtn.textContent;
            copyBtn.textContent = '已复制!';
            setTimeout(() => {
              copyBtn.textContent = originalText;
            }, 2000);
          }
        })
        .catch(err => {
          console.error('Failed to copy:', err);
          this.error = '复制失败，请手动复制结果';
        });
    }
  }
};
</script>

<style scoped>
.chat-uploader {
  max-width: 800px;
  margin: 0 auto;
  padding: 30px;
  font-family: 'PingFang SC', 'Microsoft YaHei', sans-serif;
  background: white;
  border-radius: 12px;
  box-shadow: 0 4px 12px rgba(0,0,0,0.1);
}

.back-button {
  background: none;
  border: none;
  color: #667eea;
  font-size: 16px;
  cursor: pointer;
  padding: 0;
  margin-bottom: 15px;
  transition: color 0.3s;
}

.back-button:hover {
  color: #4a90e2;
}

h2 {
  color: #2c3e50;
  text-align: center;
  margin-bottom: 30px;
  font-size: 24px;
  font-weight: 600;
}

.file-upload-section {
  margin-bottom: 30px;
}

.drop-zone {
  border: 2px dashed #667eea;
  border-radius: 12px;
  padding: 40px;
  text-align: center;
  transition: all 0.3s ease;
  background: #f8f9ff;
  cursor: pointer;
}

.drop-zone.drag-active {
  border-color: #4a90e2;
  background-color: rgba(74, 144, 226, 0.1);
  transform: scale(1.02);
}

.drop-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 15px;
}

.drop-content input[type="file"] {
  display: none;
}

.drop-zone p {
  margin: 0;
  color: #666;
  font-size: 16px;
}

.hint-text {
  font-size: 13px;
  margin-top: 5px;
  color: #777;
}

.text-danger {
  color: #e74c3c;
  font-weight: 500;
}

.file-preview {
  margin-top: 25px;
  border: 1px solid #eee;
  border-radius: 12px;
  padding: 20px;
  background: #f9f9f9;
}

.file-preview h3 {
  margin-top: 0;
  margin-bottom: 15px;
  font-size: 18px;
  color: #333;
  font-weight: 500;
  display: flex;
  align-items: center;
}

.file-count {
  font-size: 14px;
  font-weight: normal;
  color: #777;
  margin-left: 8px;
}

.file-list {
  max-height: 200px;
  overflow-y: auto;
  padding-right: 10px;
}

.file-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 0;
  border-bottom: 1px solid #f0f0f0;
}

.file-info {
  display: flex;
  align-items: center;
  gap: 8px;
}

.file-name {
  font-size: 15px;
  color: #555;
  max-width: 250px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.file-size {
  font-size: 13px;
  color: #888;
}

.file-type {
  font-size: 12px;
  background: #eaf5ff;
  color: #4a90e2;
  padding: 2px 6px;
  border-radius: 4px;
}

.file-item:last-child {
  border-bottom: none;
}

.remove-btn {
  background: none;
  border: none;
  color: #e74c3c;
  font-size: 20px;
  cursor: pointer;
  padding: 0 8px;
  line-height: 1;
  transition: transform 0.2s;
  width: 30px;
  height: 30px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.remove-btn:hover {
  background: rgba(231, 76, 60, 0.1);
  transform: scale(1.2);
}

.warning {
  margin-top: 15px;
  padding: 8px 12px;
  background: #fff8e6;
  border-radius: 6px;
  font-size: 13px;
  color: #e67c0a;
}

.warning-icon {
  margin-right: 5px;
  font-size: 15px;
}

.message-section {
  margin: 25px 0;
}

textarea {
  width: 100%;
  min-height: 150px;
  padding: 15px;
  border: 1px solid #ddd;
  border-radius: 8px;
  font-size: 15px;
  transition: all 0.3s;
  resize: vertical;
  font-family: inherit;
}

textarea:focus {
  outline: none;
  border-color: #667eea;
  box-shadow: 0 0 0 2px rgba(102, 126, 234, 0.2);
}

.action-bar {
  margin-top: 15px;
  display: flex;
  justify-content: flex-end;
}

button {
  padding: 14px 28px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  font-size: 16px;
  font-weight: 500;
  transition: all 0.3s;
}

button:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.3);
}

button:disabled {
  background: #ccc;
  cursor: not-allowed;
  transform: none;
  box-shadow: none;
}

.loading-indicator {
  margin: 25px 0;
  text-align: center;
  color: #667eea;
  font-size: 16px;
}

.spinner {
  border: 4px solid rgba(102, 126, 234, 0.2);
  border-radius: 50%;
  border-top: 4px solid #667eea;
  width: 40px;
  height: 40px;
  margin: 0 auto 15px;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

.error {
  padding: 15px;
  background-color: #fdecea;
  border-radius: 8px;
  margin: 20px 0;
  color: #e74c3c;
}

.error h3 {
  margin-top: 0;
  margin-bottom: 10px;
  font-size: 16px;
}

.result {
  background: #f8f9fa;
  padding: 20px;
  border-radius: 12px;
  margin-top: 25px;
  border-left: 4px solid #667eea;
}

.result-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
}

.result h3 {
  margin: 0;
  color: #2c3e50;
  font-size: 18px;
  font-weight: 600;
}

.copy-btn {
  padding: 6px 12px;
  background: #eaf5ff;
  color: #4a90e2;
  font-size: 14px;
  border-radius: 4px;
}

.copy-btn:hover {
  background: #d4e8ff;
}

.result-content {
  white-space: pre-wrap;
  line-height: 1.8;
  color: #333;
  font-size: 15px;
}

@media (max-width: 768px) {
  .chat-uploader {
    padding: 20px;
  }

  .drop-zone {
    padding: 30px 20px;
  }

  .file-name {
    max-width: 150px;
  }
}
</style>
