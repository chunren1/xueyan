<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { courseApi, type CourseInfo, type CourseListParams } from '@/api/course'
import { ElMessage } from 'element-plus'

const router = useRouter()
const list = ref<CourseInfo[]>([])
const loading = ref(false)
const error = ref(false)
const keyword = ref('')
const activeCategory = ref('')

const categories = ['全部', '后端开发', '前端开发', '数据库']

// 高亮分类
const categoryColors: Record<string, string> = {
  '后端开发': '#409eff',
  '前端开发': '#67c23a',
  '数据库': '#e6a23c',
}

function goDetail(id: number) {
  router.push(`/course/${id}`)
}

function goOrder(course: CourseInfo) {
  if (!localStorage.getItem('token')) {
    router.push('/login')
    return
  }
  router.push({
    path: '/order',
    query: { courseId: course.id, courseName: course.name, price: course.price },
  })
}

async function fetchList() {
  loading.value = true
  error.value = false
  try {
    const params: CourseListParams = {}
    if (keyword.value) params.keyword = keyword.value
    if (activeCategory.value && activeCategory.value !== '全部') {
      params.category = activeCategory.value
    }
    const res = await courseApi.list(params)
    list.value = res.data || []
  } catch {
    error.value = true
    ElMessage.error('加载课程失败')
  } finally {
    loading.value = false
  }
}

function onSearch() {
  fetchList()
}

function onClickCategory(cat: string) {
  activeCategory.value = cat === '全部' ? '' : cat
  fetchList()
}

onMounted(fetchList)
</script>

<template>
  <div>
    <!-- 搜索/分类 -->
    <div class="search-bar">
      <el-input
        v-model="keyword"
        placeholder="搜索课程..."
        class="search-input"
        clearable
        @keyup.enter="onSearch"
      >
        <template #append>
          <el-button @click="onSearch">搜索</el-button>
        </template>
      </el-input>
      <div class="category-tabs">
        <el-tag
          v-for="cat in categories"
          :key="cat"
          :type="(activeCategory === cat || (cat === '全部' && !activeCategory)) ? '' : 'info'"
          :effect="(activeCategory === cat || (cat === '全部' && !activeCategory)) ? 'dark' : 'plain'"
          class="tag-item"
          @click="onClickCategory(cat)"
        >
          {{ cat }}
        </el-tag>
      </div>
    </div>

    <!-- 骨架屏 -->
    <el-row :gutter="20" v-if="loading">
      <el-col v-for="i in 6" :key="i" :xs="24" :sm="12" :md="8" class="mb-4">
        <el-card shadow="hover">
          <el-skeleton :rows="4" animated />
        </el-card>
      </el-col>
    </el-row>

    <!-- 错误重试 -->
    <div v-if="error" class="error-box">
      <el-empty description="加载失败">
        <el-button type="primary" @click="fetchList">重新加载</el-button>
      </el-empty>
    </div>

    <!-- 课程列表 -->
    <el-row :gutter="20" v-if="!loading && !error">
      <el-col v-for="item in list" :key="item.id" :xs="24" :sm="12" :md="8" class="mb-4">
        <el-card shadow="hover" class="course-card" @click="goDetail(item.id)">
          <div class="course-header">
            <span class="course-category" :style="{ background: categoryColors[item.category] + '18', color: categoryColors[item.category] }">
              {{ item.category }}
            </span>
            <span class="course-price">¥{{ item.price }}</span>
          </div>
          <h3 class="course-name">{{ item.name }}</h3>
          <p class="course-desc">{{ item.description?.slice(0, 60) }}...</p>
          <div class="course-footer">
            <span>👨‍🏫 {{ item.teacherName }}</span>
            <span class="stock" :class="{ low: item.stock < 20 }">
              {{ item.stock < 20 ? '仅剩 ' + item.stock + ' 名额' : '剩余 ' + item.stock + ' 名额' }}
            </span>
          </div>
          <el-button
            type="primary"
            class="buy-btn"
            @click.stop="goOrder(item)"
          >
            立即购买
          </el-button>
        </el-card>
      </el-col>
    </el-row>

    <!-- 空状态 -->
    <el-empty v-if="!loading && !error && list.length === 0" description="暂无课程">
      <template v-if="keyword || activeCategory">
        <el-button @click="keyword='';activeCategory='';fetchList()">清除筛选</el-button>
      </template>
    </el-empty>
  </div>
</template>

<style scoped>
.search-bar { margin-bottom: 24px; }
.search-input { max-width: 480px; margin-bottom: 12px; }
.category-tabs { display: flex; gap: 8px; flex-wrap: wrap; }
.tag-item { cursor: pointer; }
.course-card { cursor: pointer; transition: transform .2s; }
.course-card:hover { transform: translateY(-2px); }
.course-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 8px; }
.course-category { font-size: 12px; padding: 2px 8px; border-radius: 4px; }
.course-price { font-size: 18px; font-weight: 700; color: #f56c6c; }
.course-name { font-size: 16px; margin: 8px 0; }
.course-desc { font-size: 13px; color: #909399; line-height: 1.5; margin-bottom: 12px; }
.course-footer { display: flex; justify-content: space-between; font-size: 12px; color: #909399; margin-bottom: 12px; }
.stock { color: #67c23a; }
.stock.low { color: #f56c6c; font-weight: 600; }
.buy-btn { width: 100%; }
.mb-4 { margin-bottom: 20px; }
.error-box { text-align: center; padding: 40px 0; }
</style>
