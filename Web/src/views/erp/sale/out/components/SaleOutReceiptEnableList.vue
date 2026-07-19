<!-- 可收款的销售出库单列表 -->
<template>
  <Dialog
    :title="t('selectOut')"
    v-model="dialogVisible"
    :appendToBody="true"
    :scroll="true"
    width="1080"
  >
    <ContentWrap>
      <el-form
        class="-mb-15px"
        :model="queryParams"
        ref="queryFormRef"
        :inline="true"
        label-width="68px"
      >
        <el-form-item :label="t('no')" prop="no">
          <el-input
            v-model="queryParams.no"
            :placeholder="t('noPlaceholder')"
            clearable
            @keyup.enter="handleQuery"
            class="!w-160px"
          />
        </el-form-item>
        <el-form-item :label="t('product')" prop="productId">
          <el-select
            v-model="queryParams.productId"
            clearable
            filterable
            :placeholder="t('selectProduct')"
            class="!w-160px"
          >
            <el-option
              v-for="item in productList"
              :key="item.id"
              :label="item.name"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item :label="t('outTime')" prop="outTime">
          <el-date-picker
            v-model="queryParams.outTime"
            value-format="YYYY-MM-DD HH:mm:ss"
            type="daterange"
            :start-placeholder="t('common.startTime')"
            :end-placeholder="t('common.endTime')"
            :default-time="[new Date('1 00:00:00'), new Date('1 23:59:59')]"
            class="!w-160px"
          />
        </el-form-item>
        <el-form-item>
          <el-button @click="handleQuery">
            <Icon icon="ep:search" class="mr-5px" /> {{ t('common.search') }}
          </el-button>
          <el-button @click="resetQuery">
            <Icon icon="ep:refresh" class="mr-5px" /> {{ t('common.reset') }}
          </el-button>
        </el-form-item>
      </el-form>
    </ContentWrap>

    <ContentWrap>
      <el-table
        v-loading="loading"
        :data="list"
        :show-overflow-tooltip="true"
        :stripe="true"
        @selection-change="handleSelectionChange"
        :table-layout="'auto'"
      >
        <el-table-column width="30" :label="t('common.select')" type="selection" />
        <el-table-column min-width="180" :label="t('no')" align="center" prop="no" />
        <el-table-column :label="t('customer')" align="center" prop="customerName" />
        <el-table-column :label="t('productInfo')" align="center" prop="productNames" min-width="200" />
        <el-table-column
          :label="t('outTime')"
          align="center"
          prop="outTime"
          :formatter="dateFormatter2"
          width="120px"
        />
        <el-table-column :label="t('creator')" align="center" prop="creatorName" />
        <el-table-column
          :label="t('receivableAmount')"
          align="center"
          prop="totalPrice"
          :formatter="erpPriceTableColumnFormatter"
        />
        <el-table-column
          :label="t('receivedAmount')"
          align="center"
          prop="receiptPrice"
          :formatter="erpPriceTableColumnFormatter"
        />
        <el-table-column :label="t('unreceivedAmount')" align="center">
          <template #default="scope">
            <span v-if="scope.row.receiptPrice === scope.row.totalPrice">0</span>
            <el-tag type="danger" v-else>
              {{ erpPriceInputFormatter(scope.row.totalPrice - scope.row.receiptPrice) }}
            </el-tag>
          </template>
        </el-table-column>
      </el-table>
      <Pagination
        v-model:limit="queryParams.pageSize"
        v-model:page="queryParams.pageNo"
        :total="total"
        @pagination="getList"
      />
    </ContentWrap>
    <template #footer>
      <el-button :disabled="!selectionList.length" type="primary" @click="submitForm">
        {{ t('common.ok') }}
      </el-button>
      <el-button @click="dialogVisible = false">{{ t('common.cancel') }}</el-button>
    </template>
  </Dialog>
</template>

<script lang="ts" setup>
import { dateFormatter2 } from '@/utils/formatTime'
import { erpPriceInputFormatter, erpPriceTableColumnFormatter } from '@/utils'
import { ProductApi, ProductVO } from '@/api/erp/product/product'
import { SaleOutApi, SaleOutVO } from '@/api/erp/sale/out'

defineOptions({ name: 'SaleOutReceiptEnableList' })

const { t } = useI18n('erp.finance.receipt')

const list = ref<SaleOutVO[]>([])
const total = ref(0)
const loading = ref(false)
const dialogVisible = ref(false)
const queryParams = reactive({
  pageNo: 1,
  pageSize: 10,
  no: undefined,
  productId: undefined,
  outTime: [],
  receiptEnable: true,
  customerId: undefined
})
const queryFormRef = ref()
const productList = ref<ProductVO[]>([])

const selectionList = ref<SaleOutVO[]>([])
const handleSelectionChange = (rows: SaleOutVO[]) => {
  selectionList.value = rows
}

const open = async (customerId: number) => {
  dialogVisible.value = true
  await nextTick()
  queryParams.customerId = customerId
  await resetQuery()
  productList.value = await ProductApi.getProductSimpleList()
}
defineExpose({ open })

const emits = defineEmits<{
  (e: 'success', value: SaleOutVO[]): void
}>()
const submitForm = () => {
  try {
    emits('success', selectionList.value)
  } finally {
    dialogVisible.value = false
  }
}

const getList = async () => {
  loading.value = true
  try {
    const data = await SaleOutApi.getSaleOutPage(queryParams)
    list.value = data.list
    total.value = data.total
  } finally {
    loading.value = false
  }
}

const resetQuery = () => {
  queryFormRef.value.resetFields()
  handleQuery()
}

const handleQuery = () => {
  queryParams.pageNo = 1
  selectionList.value = []
  getList()
}
</script>
