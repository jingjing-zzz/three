<template>
  <doc-alert title="【商机】商机管理、商机状态" url="https://doc.iocoder.cn/crm/business/" />
  <doc-alert title="【通用】数据权限" url="https://doc.iocoder.cn/crm/permission/" />

  <ContentWrap>
    <el-form
      ref="queryFormRef"
      :model="queryParams"
      class="-mb-15px"
      label-width="auto"
    >
      <el-row :gutter="20">
        <el-col :span="8">
          <el-form-item :label="t('crm.business.name')" prop="name">
            <el-input
              v-model="queryParams.name"
              class="!w-240px"
              clearable
              :placeholder="t('crm.business.namePlaceholder')"
              @keyup.enter="handleQuery"
            />
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
            <el-button v-hasPermi="['crm:business:create']" type="primary" @click="openForm('create')">
              <Icon class="mr-5px" icon="ep:plus" />
              {{ t('common.add') }}
            </el-button>
            <el-button
              v-hasPermi="['crm:business:export']"
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
      <el-table-column align="center" fixed="left" :label="t('crm.business.name')" prop="name" min-width="160">
        <template #default="scope">
          <el-link :underline="false" type="primary" @click="openDetail(scope.row.id)">
            {{ scope.row.name }}
          </el-link>
        </template>
      </el-table-column>
      <el-table-column align="center" fixed="left" :label="t('crm.business.customerName')" prop="customerName" min-width="120">
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
      <el-table-column
        :formatter="erpPriceTableColumnFormatter"
        align="center"
        :label="t('crm.business.price') + '（元）'"
        prop="totalPrice"
        min-width="140"
      />
      <el-table-column
        :formatter="dateFormatter"
        align="center"
        :label="t('crm.business.dealTime')"
        prop="dealTime"
        min-width="180"
      />
      <el-table-column align="center" :label="t('crm.business.remark')" prop="remark" min-width="200" />
      <el-table-column
        :formatter="dateFormatter"
        align="center"
        :label="t('crm.business.contactNextTime')"
        prop="contactNextTime"
        min-width="180"
      />
      <el-table-column align="center" :label="t('crm.business.ownerUserName')" prop="ownerUserName" min-width="100" />
      <el-table-column align="center" :label="t('crm.business.ownerUserDeptName')" prop="ownerUserDeptName" min-width="100" />
      <el-table-column
        :formatter="dateFormatter"
        align="center"
        :label="t('crm.business.contactLastTime')"
        prop="contactLastTime"
        min-width="180"
      />
      <el-table-column align="center" label="未跟进天数" prop="daysWithoutFollowUp" min-width="100">
        <template #default="scope">
          <span :style="{ color: scope.row.daysWithoutFollowUp >= 7 ? '#F56C6C' : '' }">
            {{ scope.row.daysWithoutFollowUp }}
          </span>
        </template>
      </el-table-column>
      <el-table-column align="center" label="竞争对手" prop="competitor" min-width="150" />
      <el-table-column
        :formatter="dateFormatter"
        align="center"
        :label="t('crm.business.updateTime')"
        prop="updateTime"
        min-width="180"
      />
      <el-table-column
        :formatter="dateFormatter"
        align="center"
        :label="t('crm.business.createTime')"
        prop="createTime"
        min-width="180"
      />
      <el-table-column align="center" :label="t('crm.business.creatorName')" prop="creatorName" min-width="100" />
      <el-table-column
        align="center"
        fixed="right"
        :label="t('crm.business.statusTypeId')"
        prop="statusTypeName"
        min-width="140"
      />
      <el-table-column
        align="center"
        fixed="right"
        :label="t('crm.business.statusName')"
        prop="statusName"
        min-width="120"
      />
      <el-table-column align="center" fixed="right" :label="t('common.action')" min-width="200">
        <template #default="scope">
          <el-button
            v-hasPermi="['crm:business:query']"
            link
            type="primary"
            @click="openDetail(scope.row.id)"
          >
            {{ t('common.detail') }}
          </el-button>
          <el-button
            v-hasPermi="['crm:business:update']"
            link
            type="primary"
            @click="openForm('update', scope.row.id)"
          >
            {{ t('common.edit') }}
          </el-button>
          <el-button
            v-hasPermi="['crm:business:update']"
            link
            type="success"
            @click="openDetail(scope.row.id)"
          >
            {{ t('crm.business.followUp') }}
          </el-button>
          <el-button
            v-hasPermi="['crm:business:delete']"
            link
            type="danger"
            @click="handleDelete(scope.row.id)"
          >
            {{ t('common.del') }}
          </el-button>
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

  <BusinessForm ref="formRef" @success="getList" />
</template>

<script lang="ts" setup>
import { dateFormatter } from '@/utils/formatTime'
import download from '@/utils/download'
import * as BusinessApi from '@/api/crm/business'
import BusinessForm from './BusinessForm.vue'
import { erpPriceTableColumnFormatter } from '@/utils'
import { TabsPaneContext } from 'element-plus'

defineOptions({ name: 'CrmBusiness' })

const message = useMessage()
const { t } = useI18n()
const loading = ref(true)
const total = ref(0)
const list = ref([])
const queryParams = reactive({
  pageNo: 1,
  pageSize: 10,
  sceneType: '1',
  name: null
})
const queryFormRef = ref()
const exportLoading = ref(false)
const activeName = ref('1')

const getList = async () => {
  loading.value = true
  try {
    const data = await BusinessApi.getBusinessPage(queryParams)
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

const handleTabClick = (tab: TabsPaneContext) => {
  queryParams.sceneType = tab.paneName
  handleQuery()
}

const { push } = useRouter()
const openDetail = (id: number) => {
  push({ name: 'CrmBusinessDetail', params: { id } })
}

const openCustomerDetail = (id: number) => {
  push({ name: 'CrmCustomerDetail', params: { id } })
}

const formRef = ref()
const openForm = (type: string, id?: number) => {
  formRef.value.open(type, id)
}

const handleDelete = async (id: number) => {
  try {
    await message.delConfirm()
    await BusinessApi.deleteBusiness(id)
    message.success(t('common.delSuccess'))
    await getList()
  } catch {}
}

const handleExport = async () => {
  try {
    await message.exportConfirm()
    exportLoading.value = true
    const data = await BusinessApi.exportBusiness(queryParams)
    download.excel(data, '商机.xls')
  } catch {
  } finally {
    exportLoading.value = false
  }
}

onMounted(() => {
  getList()
})
</script>