<template>
  <div class="analysis-page">
    <div class="navbar">
      <div class="logo" @click="goHome">
        <img src="../assets/logo.svg" alt="logo" class="logo-img" />
        <span class="title">AI 学习分析</span>
      </div>
      <div class="nav-actions">
        <el-button @click="goHome" icon="ArrowLeft">返回首页</el-button>
        <el-button type="primary" @click="goToExam" icon="Document">考试入口</el-button>
        <el-button @click="goToPractice" icon="Edit">刷题练习</el-button>
      </div>
    </div>

    <div class="main-container">
      <div class="hero-card">
        <h1>智能学习报告</h1>
        <p>基于系统数据的学习概览，后续可接入个人考试记录，生成专属能力雷达图和学习建议。</p>
      </div>

      <div class="stats-section" v-loading="loading">
        <div class="stats-grid">
          <div class="stat-card">
            <div class="stat-label">题目总数</div>
            <div class="stat-number">{{ stats.questionCount }}</div>
          </div>
          <div class="stat-card">
            <div class="stat-label">累计考试次数</div>
            <div class="stat-number">{{ stats.examCount }}</div>
          </div>
          <div class="stat-card">
            <div class="stat-label">注册用户数</div>
            <div class="stat-number">{{ stats.userCount }}</div>
          </div>
          <div class="stat-card">
            <div class="stat-label">今日考试次数</div>
            <div class="stat-number">{{ stats.todayExamCount }}</div>
          </div>
        </div>
      </div>

      <div class="tips-section">
        <el-card shadow="never">
          <h2>下一步规划</h2>
          <ul class="tips-list">
            <li>接入个人考试记录和练习记录，生成专属能力雷达图。</li>
            <li>根据错题分布，自动推荐薄弱知识点的视频和练习题。</li>
            <li>按时间维度展示成绩变化趋势，辅助长期学习规划。</li>
          </ul>
          <p class="tips-footer">当前页面已提供系统级统计数据，后续可以在此基础上继续扩展个性化 AI 分析能力。</p>
        </el-card>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { ArrowLeft, Document, Edit } from '@element-plus/icons-vue'
import request from '../utils/request'

const router = useRouter()

const loading = ref(false)
const stats = ref({
  questionCount: 0,
  userCount: 0,
  examCount: 0,
  todayExamCount: 0
})

const fetchStats = async () => {
  loading.value = true
  try {
    const res = await request.get('/api/stats/overview')
    stats.value = {
      questionCount: res.data.questionCount || 0,
      userCount: res.data.userCount || 0,
      examCount: res.data.examCount || 0,
      todayExamCount: res.data.todayExamCount || 0
    }
  } catch (error) {
    ElMessage.error('获取统计数据失败')
  } finally {
    loading.value = false
  }
}

const goHome = () => {
  router.push('/home')
}

const goToExam = () => {
  router.push('/exam/list')
}

const goToPractice = () => {
  router.push('/practice')
}

onMounted(() => {
  fetchStats()
})
</script>

<style scoped>
.analysis-page {
  min-height: 100vh;
  background: linear-gradient(135deg, #eef2ff 0%, #fdfbfb 50%, #e6fffa 100%);
}

.navbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 32px;
  background: white;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.logo {
  display: flex;
  align-items: center;
  cursor: pointer;
}

.logo-img {
  width: 40px;
  height: 40px;
  margin-right: 12px;
}

.title {
  font-size: 18px;
  font-weight: 600;
  color: #303133;
}

.nav-actions {
  display: flex;
  align-items: center;
  gap: 12px;
}

.main-container {
  max-width: 1200px;
  margin: 24px auto 40px;
  padding: 0 16px;
}

.hero-card {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 50%, #78ffd6 100%);
  border-radius: 16px;
  padding: 32px 28px;
  color: white;
  box-shadow: 0 12px 40px rgba(102, 126, 234, 0.4);
  margin-bottom: 24px;
}

.hero-card h1 {
  font-size: 26px;
  margin-bottom: 12px;
}

.hero-card p {
  font-size: 14px;
  line-height: 1.8;
  max-width: 720px;
}

.stats-section {
  margin-bottom: 24px;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(220px, 1fr));
  gap: 20px;
}

.stat-card {
  background: white;
  border-radius: 12px;
  padding: 20px 18px;
  box-shadow: 0 6px 20px rgba(0, 0, 0, 0.06);
}

.stat-label {
  font-size: 14px;
  color: #909399;
  margin-bottom: 8px;
}

.stat-number {
  font-size: 26px;
  font-weight: 600;
  color: #409eff;
}

.tips-section {
  margin-top: 8px;
}

.tips-list {
  padding-left: 18px;
  margin: 8px 0 12px;
  color: #606266;
  line-height: 1.8;
}

.tips-footer {
  font-size: 13px;
  color: #909399;
}

@media (max-width: 768px) {
  .navbar {
    flex-direction: column;
    align-items: flex-start;
    gap: 12px;
  }

  .nav-actions {
    width: 100%;
    justify-content: flex-end;
    flex-wrap: wrap;
  }
}
</style>

