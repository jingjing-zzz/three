<template>
  <ContentWrap>
    <el-form
      ref="queryFormRef"
      :model="queryParams"
      class="-mb-15px"
      label-width="auto"
    >
      <el-row :gutter="20">
        <el-col :span="8">
          <el-form-item :label="t('crm.order.no')" prop="no">
            <el-input
              v-model="queryParams.no"
              class="!w-240px"
              clearable
              :placeholder="t('crm.order.noPlaceholder')"
              @keyup.enter="handleQuery"
            />
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item :label="t('crm.order.customerId')" prop="customerId">
            <el-select
              v-model="queryParams.customerId"
              class="!w-240px"
              clearable
              lable-key="name"
              :placeholder="t('crm.order.customerIdPlaceholder')"
              value-key="id"
              @keyup.enter="handleQuery"
            >
              <el-option
                v-for="item in customerList"
                :key="item.id"
                :label="item.name"
                :value="item.id!"
              />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item :label="t('crm.order.status')" prop="status">
            <el-select
              v-model="queryParams.status"
              class="!w-240px"
              clearable
              :placeholder="t('crm.order.statusPlaceholder')"
            >
              <el-option :label="t('crm.order.statusDraft')" :value="0" />
              <el-option :label="t('crm.order.statusPendingAudit')" :value="10" />
              <el-option :label="t('crm.order.statusAuditing')" :value="20" />
              <el-option :label="t('crm.order.statusApproved')" :value="30" />
              <el-option :label="t('crm.order.statusRejected')" :value="40" />
              <el-option :label="t('crm.order.statusCompleted')" :value="50" />
              <el-option :label="t('crm.order.statusCancelled')" :value="60" />
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row>
        <el-col :span="24">
          <el-form-item>
            <el-button @click="handleQuery">
              <Icon class="mr-5px" icon="ep:search" />
              {{ t('common.search') }}
            </el-button>
            <el-button @click="resetQuery">
              <Icon class="mr-5px" icon="ep:refresh" />
              {{ t('common.reset') }}
            </el-button>
            <el-button v-hasPermi="['crm:order:create']" type="primary" @click="openForm('create')">
              <Icon class="mr-5px" icon="ep:plus" />
              {{ t('common.add') }}
            </el-button>
            <el-button
              v-hasPermi="['crm:order:export']"
              :loading="exportLoading"
              plain
              type="success"
              @click="handleExport"
            >
              <Icon class="mr-5px" icon="ep:download" />
              {{ t('common.export') }}
            </el-button>
          </el-form-item>
        </el-col>
      </el-row>
    </el-form>
  </ContentWrap>

  <ContentWrap>
    <el-tabs v-model="activeName" @tab-click="handleTabClick">
      <el-tab-pane :label="t('crm.customer.myResponsible')" name="1" />
      <el-tab-pane :label="t('crm.customer.myInvolved')" name="2" />
      <el-tab-pane :label="t('crm.customer.subordinateResponsible')" name="3" />
    </el-tabs>
    <el-table v-loading="loading" :data="list" :show-overflow-tooltip="true" :stripe="true" :table-layout="'auto'">
      <el-table-column align="center" fixed="left" :label="t('crm.order.no')" prop="no" min-width="180" />
      <el-table-column align="center" :label="t('crm.order.customerName')" prop="customerName" min-width="120">
        <template #default="scope">
          <el-link
            :underline="false"
            type="primary"
            @click="openCustomerDetail(scope.row.customerId)"
          >
            {{ scope.row.customerName }}
          </el-link>
        </template>
      </el-table-column>
      <el-table-column align="center" :label="t('crm.order.contractName')" prop="contractName" min-width="130">
        <template #default="scope">
          <el-link
            v-if="scope.row.contractId"
            :underline="false"
            type="primary"
            @click="openContractDetail(scope.row.contractId)"
          >
            {{ scope.row.contractName }}
          </el-link>
          <span v-else>-</span>
        </template>
      </el-table-column>
      <el-table-column
        align="center"
        :label="t('crm.order.totalPrice') + '（元）'"
        prop="totalPrice"
        min-width="140"
        :formatter="erpPriceTableColumnFormatter"
      />
      <el-table-column
        align="center"
        :label="t('crm.order.orderTime')"
        prop="orderTime"
        min-width="120"
        :formatter="dateFormatter2"
      />
      <el-table-column align="center" :label="t('crm.order.status')" prop="status" min-width="100">
        <template #default="scope">
          <el-tag
            v-if="scope.row.status === 0"
            type="info"
          >
            {{ t('crm.order.statusDraft') }}
          </el-tag>
          <el-tag
            v-else-if="scope.row.status === 10"
            type="warning"
          >
            {{ t('crm.order.statusPendingAudit') }}
          </el-tag>
          <el-tag
            v-else-if="scope.row.status === 20"
            type="primary"
          >
            {{ t('crm.order.statusAuditing') }}
          </el-tag>
          <el-tag
            v-else-if="scope.row.status === 30"
            type="success"
          >
            {{ t('crm.order.statusApproved') }}
          </el-tag>
          <el-tag
            v-else-if="scope.row.status === 40"
            type="danger"
          >
            {{ t('crm.order.statusRejected') }}
          </el-tag>
          <el-tag
            v-else-if="scope.row.status === 50"
            type="success"
          >
            {{ t('crm.order.statusCompleted') }}
          </el-tag>
          <el-tag
            v-else-if="scope.row.status === 60"
            type="danger"
          >
            {{ t('crm.order.statusCancelled') }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column align="center" :label="t('crm.order.ownerUserName')" prop="ownerUserName" min-width="120" />
      <el-table-column
        :formatter="dateFormatter"
        align="center"
        :label="t('crm.order.createTime')"
        prop="createTime"
        min-width="180"
      />
      <el-table-column align="center" :label="t('crm.order.creatorName')" prop="creatorName" min-width="120" />
      <el-table-column fixed="right" :label="t('common.action')" width="200">
        <template #default="scope">
          <el-button
            v-hasPermi="['crm:order:query']"
            link
            type="primary"
            @click="openDetail(scope.row.id)"
          >
            {{ t('common.detail') }}
          </el-button>
          <el-button
            v-if="scope.row.status === 0 || scope.row.status === 40"
            v-hasPermi="['crm:order:update']"
            link
            type="primary"
            @click="openForm('update', scope.row.id)"
          >
            {{ t('common.edit') }}
          </el-button>
          <el-dropdown
            @command="(command) => handleCommand(command, scope.row)"
            v-hasPermi="[
              'crm:order:approval',
              'crm:order:delete'
            ]"
          >
            <el-button type="primary" link><Icon icon="ep:d-arrow-right" /> {{ t('common.more') }}</el-button>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item
                  command="handleSubmit"
                  v-if="scope.row.status === 0 && checkPermi(['crm:order:approval'])"
                >
                  {{ t('crm.order.submit') }}
                </el-dropdown-item>
                <el-dropdown-item
                  command="handleApproval"
                  v-if="(scope.row.status === 10 || scope.row.status === 20) && checkPermi(['crm:order:approval'])"
                >
                  {{ t('common.approve') }}
                </el-dropdown-item>
                <el-dropdown-item
                  command="handleComplete"
                  v-if="scope.row.status === 30 && checkPermi(['crm:order:update'])"
                >
                  {{ t('crm.order.complete') }}
                </el-dropdown-item>
                <el-dropdown-item
                  command="handleDelete"
                  v-if="scope.row.status !== 50 && scope.row.status !== 60 && checkPermi(['crm:order:delete'])"
                >
                  <Icon icon="ep:delete" />{{ t('common.del') }}
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
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

  <OrderForm ref="formRef" @success="getList" />
</template>
<script lang="ts" setup>
import { dateFormatter, dateFormatter2 } from '@/utils/formatTime'
import download from '@/utils/download'
import * as OrderApi from '@/api/crm/order'
import OrderForm from './OrderForm.vue'
import { erpPriceTableColumnFormatter } from '@/utils'
import * as CustomerApi from '@/api/crm/customer'
import { TabsPaneContext } from 'element-plus'
import { checkPermi } from '@/utils/permission'

defineOptions({ name: 'CrmOrder' })

const message = useMessage()
const { t } = useI18n()
const loading = ref(true)
const total = ref(0)
const list = ref([])
const queryParams = reactive({
  pageNo: 1,
  pageSize: 10,
  sceneType: '1',
  no: null,
  customerId: null,
  status: null,
  contractId: null,
  orderTimeRange: []
})
const queryFormRef = ref()
const exportLoading = ref(false)
const activeName = ref('1')
const customerList = ref<CustomerApi.CustomerVO[]>([])

const handleTabClick = (tab: TabsPaneContext) => {
  queryParams.sceneType = tab.paneName
  handleQuery()
}

const getList = async () => {
  loading.value = true
  try {
    const data = await OrderApi.getOrderPage(queryParams)
    list.value = data.list
    total.value = data.total
  } finally {
    loading.value = false
  }
}

const handleQuery = () => {
  queryParams.pageNo = 1
  getList()
}

const resetQuery = () => {
  queryFormRef.value.resetFields()
  handleQuery()
}

const formRef = ref()
const openForm = (type: string, id?: number) => {
  formRef.value.open(type, id)
}

const handleDelete = async (id: number) => {
  try {
    await message.delConfirm()
    await OrderApi.deleteOrder(id)
    message.success(t('common.delSuccess'))
    await getList()
  } catch {}
}

const handleSubmit = async (id: number) => {
  try {
    await message.confirm(t('crm.order.submitConfirm'))
    await OrderApi.submitOrder(id)
    message.success(t('crm.order.submitSuccess'))
    await getList()
  } catch {}
}

const handleApproval = async (id: number) => {
  try {
    await message.confirm(t('common.approveConfirm'))
    await OrderApi.startApproval(id)
    message.success(t('common.approveSuccess'))
    await getList()
  } catch {}
}

const handleComplete = async (id: number) => {
  try {
    await message.confirm(t('crm.order.completeConfirm'))
    await OrderApi.completeOrder(id)
    message.success(t('crm.order.completeSuccess'))
    await getList()
  } catch {}
}

const handleCommand = (command: string, row: any) => {
  switch (command) {
    case 'handleSubmit':
      handleSubmit(row.id)
      break
    case 'handleApproval':
      handleApproval(row.id)
      break
    case 'handleComplete':
      handleComplete(row.id)
      break
    case 'handleDelete':
      handleDelete(row.id)
      break
  }
}

const handleExport = async () => {
  try {
    await message.exportConfirm()
    exportLoading.value = true
    const data = await OrderApi.exportOrder(queryParams)
    download.excel(data, '订单.xls')
  } catch {
  } finally {
    exportLoading.value = false
  }
}

const { push } = useRouter()
const openDetail = (id: number) => {
  push({ name: 'CrmOrderDetail', params: { id } })
}

const openCustomerDetail = (id: number) => {
  push({ name: 'CrmCustomerDetail', params: { id } })
}

const openContractDetail = (id: number) => {
  push({ name: 'CrmContractDetail', params: { id } })
}

onMounted(async () => {
  await getList()
  customerList.value = await CustomerApi.getCustomerSimpleList()
})
</script>