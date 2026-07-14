<template>
  <ContentWrap>
    <div class="mb-10px">
      <el-button type="primary" @click="handleCreateDraft" :loading="createLoading">
        <Icon icon="ep:plus" class="mr-5px" /> 创建报价草稿
      </el-button>
    </div>
    <el-table
      v-loading="loading"
      :data="list"
      :stripe="true"
      :show-overflow-tooltip="true"
      :table-layout="'auto'"
    >
      <el-table-column type="expand">
        <template #default="scope">
          <div class="p-10px" v-if="scope.row.items && scope.row.items.length > 0">
            <el-table :data="scope.row.items" :table-layout="'auto'" border>
              <el-table-column label="产品名称" prop="productName" min-width="160" />
              <el-table-column label="产品编号" prop="productNo" min-width="120" />
              <el-table-column
                label="标准售价（元）"
                prop="standardPrice"
                min-width="140"
                :formatter="erpPriceTableColumnFormatter"
              />
              <el-table-column
                label="实际售价（元）"
                prop="actualPrice"
                min-width="140"
                :formatter="erpPriceTableColumnFormatter"
              />
              <el-table-column label="数量" prop="count" min-width="80" />
              <el-table-column label="折扣（%）" prop="discountPercent" min-width="100" />
              <el-table-column
                label="总价（元）"
                prop="totalPrice"
                min-width="140"
                :formatter="erpPriceTableColumnFormatter"
              />
              <el-table-column label="赠品" prop="gift" min-width="120" />
              <el-table-column label="备注" prop="remark" min-width="160" />
            </el-table>
          </div>
          <el-empty v-else description="暂无产品明细" :image-size="60" />
        </template>
      </el-table-column>
      <el-table-column label="报价编号" prop="quotationNo" min-width="160" />
      <el-table-column label="状态" prop="statusName" min-width="100" align="center">
        <template #default="scope">
          <el-tag :type="getStatusTagType(scope.row.status)">
            {{ scope.row.statusName }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column
        label="产品总价（元）"
        prop="totalProductPrice"
        min-width="140"
        :formatter="erpPriceTableColumnFormatter"
      />
      <el-table-column label="折扣（%）" prop="discountPercent" min-width="100" align="center" />
      <el-table-column
        label="报价总金额（元）"
        prop="totalPrice"
        min-width="140"
        :formatter="erpPriceTableColumnFormatter"
      />
      <el-table-column label="确认人" prop="confirmedByName" min-width="100" align="center" />
      <el-table-column
        label="确认时间"
        prop="confirmedTime"
        min-width="180"
        :formatter="dateFormatter"
      />
      <el-table-column
        label="创建时间"
        prop="createTime"
        min-width="180"
        :formatter="dateFormatter"
      />
      <el-table-column label="操作" fixed="right" min-width="160" align="center">
        <template #default="scope">
          <el-button
            v-if="scope.row.status === 0"
            type="primary"
            link
            @click="handleConfirm(scope.row)"
          >
            确认报价
          </el-button>
          <el-button
            v-if="scope.row.status === 0"
            type="danger"
            link
            @click="handleVoid(scope.row)"
          >
            作废
          </el-button>
        </template>
      </el-table-column>
    </el-table>
    <pagination
      v-show="total > 0"
      :total="total"
      v-model:page="queryParams.pageNo"
      v-model:limit="queryParams.pageSize"
      @pagination="getList"
    />
  </ContentWrap>
</template>
<script setup lang="ts">
import { dateFormatter } from '@/utils/formatTime'
import { erpPriceTableColumnFormatter } from '@/utils'
import * as QuotationApi from '@/api/crm/business/quotation'
import type { QuotationVO } from '@/api/crm/business/quotation'

defineOptions({ name: 'QuotationList' })

const props = defineProps<{
  businessId: number
}>()

const message = useMessage() // 消息弹窗
const loading = ref(false) // 加载中
const createLoading = ref(false) // 创建草稿加载中
const list = ref<QuotationVO[]>([]) // 报价列表
const total = ref(0) // 总条数
const queryParams = reactive({
  pageNo: 1,
  pageSize: 10,
  businessId: props.businessId
})

/** 状态标签类型 */
const getStatusTagType = (status: number): string => {
  if (status === 0) return 'warning' // 草稿
  if (status === 1) return 'success' // 已确认
  if (status === 2) return 'info' // 已作废
  return ''
}

/** 获取报价列表 */
const getList = async () => {
  loading.value = true
  try {
    const data = await QuotationApi.getQuotationPage(queryParams)
    list.value = data.list
    total.value = data.total
  } finally {
    loading.value = false
  }
}

/** 创建报价草稿 */
const handleCreateDraft = async () => {
  createLoading.value = true
  try {
    await QuotationApi.createQuotationDraft(props.businessId)
    message.success('创建报价草稿成功')
    await getList()
  } finally {
    createLoading.value = false
  }
}

/** 确认报价（二次确认） */
const handleConfirm = async (row: QuotationVO) => {
  try {
    await message.confirm('确认要确认该报价吗？确认后报价将无法修改。')
    await QuotationApi.confirmQuotation(row.id as number)
    message.success('确认报价成功')
    await getList()
  } catch (e) {
    // 用户取消，忽略
  }
}

/** 作废报价（二次确认） */
const handleVoid = async (row: QuotationVO) => {
  try {
    await message.confirm('确认要作废该报价吗？作废后将无法恢复。')
    await QuotationApi.voidQuotation(row.id as number)
    message.success('作废报价成功')
    await getList()
  } catch (e) {
    // 用户取消，忽略
  }
}

/** 初始化 */
onMounted(() => {
  getList()
})
</script>
