<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { courseApi, type CourseInfo } from '@/api/course'

const route = useRoute()
const router = useRouter()
const course = ref<CourseInfo | null>(null)

function goBuy() {
  if (!localStorage.getItem('token')) {
    router.push('/login')
    return
  }
  if (!course.value) return
  router.push({
    path: '/order',
    query: { courseId: course.value.id, courseName: course.value.name, price: course.value.price },
  })
}

onMounted(async () => {
  const id = Number(route.params.id)
  const res = await courseApi.detail(id)
  course.value = res.data
})
</script>

<template>
  <div v-if="course" class="detail-container">
    <el-card>
      <div class="detail-header">
        <div>
          <el-tag size="small">{{ course.category }}</el-tag>
          <h1>{{ course.name }}</h1>
          <p class="teacher">👨‍🏫 讲师：{{ course.teacherName }}</p>
          <p class="stock">📦 剩余名额：{{ course.stock }}</p>
        </div>
        <div class="price-box">
          <span class="price">¥{{ course.price }}</span>
          <el-button type="primary" size="large" @click="goBuy">
            立即购买
          </el-button>
        </div>
      </div>
      <el-divider />
      <div class="description">
        <h3>课程介绍</h3>
        <p>{{ course.description }}</p>
      </div>
    </el-card>
  </div>
  <div v-else class="loading-box">
    <el-skeleton :rows="8" animated />
  </div>
</template>

<style scoped>
.detail-container {
  max-width: 900px;
  margin: 0 auto;
}
.detail-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 24px;
}
.detail-header h1 {
  margin: 12px 0;
  font-size: 24px;
}
.teacher,
.stock {
  color: #909399;
  margin: 4px 0;
}
.price-box {
  text-align: right;
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  gap: 12px;
}
.price {
  font-size: 32px;
  font-weight: 700;
  color: #f56c6c;
}
.description {
  line-height: 1.8;
  color: #333;
}
.description h3 {
  margin-bottom: 12px;
}
.loading-box {
  max-width: 900px;
  margin: 0 auto;
}
</style>
