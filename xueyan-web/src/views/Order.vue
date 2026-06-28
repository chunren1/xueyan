<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { orderApi, type OrderInfo } from '@/api/order'
import { ElMessage } from 'element-plus'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const order = ref<OrderInfo | null>(null)
const loading = ref(false)

const courseId = Number(route.query.courseId) || 0
const courseName = (route.query.courseName as string) || ''
const price = Number(route.query.price) || 0

const quantity = ref(1)

async function handleCreate() {
  if (!userStore.user) {
    router.push('/login')
    return
  }
  loading.value = true
  try {
    const res = await orderApi.create({
      userId: userStore.user.userId,
      courseId,
      quantity: quantity.value,
    })
    order.value = res.data
    ElMessage.success('下单成功！请尽快支付')
  } catch {
    // 错误已拦截
  } finally {
    loading.value = false
  }
}

async function loadOrder(orderNo: string) {
  const res = await orderApi.getByNo(orderNo)
  order.value = res.data
}

// 如果 URL 带 orderNo，查询订单
const queryOrderNo = route.query.orderNo as string
onMounted(() => {
  if (queryOrderNo) loadOrder(queryOrderNo)
})
</script>

<template>
  <div class="order-container">
    <!-- 下单表单 -->
    <el-card v-if="!order && courseId" class="order-form">
      <h2>确认订单</h2>
      <el-descriptions :column="1" border>
        <el-descriptions-item label="课程">{{ courseName }}</el-descriptions-item>
        <el-descriptions-item label="单价">¥{{ price }}</el-descriptions-item>
        <el-descriptions-item label="数量">
          <el-input-number v-model="quantity" :min="1" :max="10" />
        </el-descriptions-item>
        <el-descriptions-item label="合计">
          <span class="total-price">¥{{ (price * quantity).toFixed(2) }}</span>
        </el-descriptions-item>
      </el-descriptions>
      <el-button
        type="primary"
        size="large"
        class="submit-btn"
        :loading="loading"
        @click="handleCreate"
      >
        提交订单
      </el-button>
    </el-card>

    <!-- 订单详情 -->
    <el-card v-if="order">
      <h2>订单详情</h2>
      <el-descriptions :column="1" border>
        <el-descriptions-item label="订单号">{{ order.orderNo }}</el-descriptions-item>
        <el-descriptions-item label="课程">{{ order.courseName }}</el-descriptions-item>
        <el-descriptions-item label="金额">¥{{ order.amount }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="order.status === 'PAID' ? 'success' : order.status === 'CANCELLED' ? 'danger' : 'warning'">
            {{ order.status === 'WAIT_PAY' ? '待支付' : order.status === 'PAID' ? '已支付' : order.status === 'CANCELLED' ? '已取消' : '已完成' }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ order.createdAt }}</el-descriptions-item>
      </el-descriptions>
      <div class="actions">
        <el-button @click="router.push('/')">返回首页</el-button>
      </div>
    </el-card>

    <!-- 无订单参数 -->
    <el-empty v-if="!order && !courseId" description="请从课程页面下单" />
  </div>
</template>

<style scoped>
.order-container {
  max-width: 700px;
  margin: 0 auto;
}
.order-form h2 {
  margin-bottom: 16px;
}
.total-price {
  font-size: 18px;
  font-weight: 700;
  color: #f56c6c;
}
.submit-btn {
  margin-top: 20px;
  width: 100%;
}
.actions {
  margin-top: 20px;
  text-align: center;
}
</style>
