<template>
  <CustomerDetailsHeader :customer="customer" :loading="loading">
    <el-button
      v-if="permissionListRef?.validateWrite"
      v-hasPermi="['crm:customer:update']"
      type="primary"
      @click="openForm"
    >
      {{ t('customer.edit') }}
    </el-button>
    <el-button
      v-if="permissionListRef?.validateOwnerUser"
      type="primary"
      @click="handleTransfer"
    >
      {{ t('customer.transfer') }}
    </el-button>
    <el-button
      v-if="permissionListRef?.validateOwnerUser && !customer.lockStatus"
      type="warning"
      @click="handleLock(true)"
    >
      {{ t('customer.lock') }}
    </el-button>
    <el-button
      v-if="permissionListRef?.validateOwnerUser && customer.lockStatus"
      type="success"
      @click="handleLock(false)"
    >
      {{ t('customer.unlock') }}
    </el-button>
    <el-button
      v-if="permissionListRef?.validateOwnerUser"
      type="primary"
      @click="handleChangeDealStatus"
    >
      {{ t('customer.changeDealStatus') }}
    </el-button>
    <el-button
      v-if="permissionListRef?.validateOwnerUser"
      type="danger"
      @click="handlePutPool"
    >
      {{ t('customer.putPool') }}
    </el-button>
  </CustomerDetailsHeader>
  <el-col>
    <el-tabs v-model="activeTab">
      <el-tab-pane :label="t('customer.followUpTab')" name="followUp">
        <FollowUpList :biz-id="customerId" :biz-type="BizTypeEnum.CRM_CUSTOMER" />
      </el-tab-pane>
      <el-tab-pane :label="t('customer.basicInfoTab')" name="basicInfo">
        <CustomerDetailsInfo :customer="customer" />
      </el-tab-pane>
      <el-tab-pane :label="t('customer.teamMemberTab')" name="teamMember">
        <PermissionList
          ref="permissionListRef"
          :biz-id="customerId"
          :biz-type="BizTypeEnum.CRM_CUSTOMER"
          :show-action="true"
          @quit-team="close"
        />
      </el-tab-pane>
      <el-tab-pane :label="t('customer.contactTab')" name="contact">
        <CustomerContactList :customer-id="customerId" />
      </el-tab-pane>
      <el-tab-pane :label="t('customer.businessTab')" name="business">
        <el-empty v-if="businessList.length === 0" :description="t('common.noData')" />
        <el-table v-else :data="businessList" :show-overflow-tooltip="true" :stripe="true">
          <el-table-column align="center" :label="t('business.name')" prop="name" min-width="160">
            <template #default="scope">
              <el-link :underline="false" type="primary" @click="openBusinessDetail(scope.row.id)">
                {{ scope.row.name }}
              </el-link>
            </template>
          </el-table-column>
          <el-table-column align="center" :label="t('business.statusType')" prop="statusTypeName" min-width="120" />
          <el-table-column align="center" :label="t('business.totalPrice')" prop="totalPrice" min-width="120">
            <template #default="scope">
              {{ scope.row.totalPrice }}
            </template>
          </el-table-column>
          <el-table-column align="center" :label="t('common.createTime')" min-width="160">
            <template #default="scope">
              {{ formatDate(scope.row.createTime) }}
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>
      <el-tab-pane :label="t('customer.contractTab')" name="contract">
        <el-empty v-if="contractList.length === 0" :description="t('common.noData')" />
        <el-table v-else :data="contractList" :show-overflow-tooltip="true" :stripe="true">
          <el-table-column align="center" :label="t('contract.name')" prop="name" min-width="160">
            <template #default="scope">
              <el-link :underline="false" type="primary" @click="openContractDetail(scope.row.id)">
                {{ scope.row.name }}
              </el-link>
            </template>
          </el-table-column>
          <el-table-column align="center" :label="t('contract.totalPrice')" prop="totalPrice" min-width="120">
            <template #default="scope">
              {{ scope.row.totalPrice }}
            </template>
          </el-table-column>
          <el-table-column align="center" :label="t('contract.auditStatus')" prop="auditStatus" min-width="100">
            <template #default="scope">
              <dict-tag :type="DICT_TYPE.CRM_AUDIT_STATUS" :value="scope.row.auditStatus" />
            </template>
          </el-table-column>
          <el-table-column align="center" :label="t('common.createTime')" min-width="160">
            <template #default="scope">
              {{ formatDate(scope.row.createTime) }}
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>
      <el-tab-pane :label="t('customer.receivableTab')" name="receivable">
        <el-empty v-if="receivableList.length === 0" :description="t('common.noData')" />
        <el-table v-else :data="receivableList" :show-overflow-tooltip="true" :stripe="true">
          <el-table-column align="center" :label="t('receivable.planName')" prop="planName" min-width="160">
            <template #default="scope">
              <el-link :underline="false" type="primary" @click="openReceivableDetail(scope.row.id)">
                {{ scope.row.planName }}
              </el-link>
            </template>
          </el-table-column>
          <el-table-column align="center" :label="t('receivable.price')" prop="price" min-width="120">
            <template #default="scope">
              {{ scope.row.price }}
            </template>
          </el-table-column>
          <el-table-column align="center" :label="t('receivable.auditStatus')" prop="auditStatus" min-width="100" />
          <el-table-column align="center" :label="t('common.createTime')" min-width="160">
            <template #default="scope">
              {{ formatDate(scope.row.createTime) }}
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>
      <el-tab-pane :label="t('customer.operateLogTab')" name="operateLog">
        <OperateLogV2 :log-list="logList" />
      </el-tab-pane>
    </el-tabs>
  </el-col>

  <!-- 表单弹窗：编辑 -->
  <CustomerForm ref="formRef" @success="getCustomer" />
  <!-- 转移弹窗 -->
  <CrmTransferForm ref="transferFormRef" :biz-type="BizTypeEnum.CRM_CUSTOMER" @success="close" />
</template>
<script lang="ts" setup>
import { DICT_TYPE } from '@/utils/dict'
import { useTagsViewStore } from '@/store/modules/tagsView'
import * as CustomerApi from '@/api/crm/customer'
import CustomerForm from '@/views/crm/customer/CustomerForm.vue'
import CustomerDetailsHeader from './CustomerDetailsHeader.vue'
import CustomerDetailsInfo from './CustomerDetailsInfo.vue'
import PermissionList from '@/views/crm/permission/components/PermissionList.vue'
import CrmTransferForm from '@/views/crm/permission/components/TransferForm.vue'
import FollowUpList from '@/views/crm/followup/index.vue'
import CustomerContactList from './CustomerContactList.vue'
import { BizTypeEnum } from '@/api/crm/permission'
import type { OperateLogVO } from '@/api/system/operatelog'
import { getOperateLogPage } from '@/api/crm/operateLog'
import { formatDate } from '@/utils/formatTime'


defineOptions({ name: 'CrmCustomerDetail' })

const { t } = useI18n('crm') // 国际化

const customerId = ref(0) // 客户编号
const loading = ref(true) // 加载中
const message = useMessage() // 消息弹窗
const { delView } = useTagsViewStore() // 视图操作
const { currentRoute, push } = useRouter() // 路由

const activeTab = ref('followUp') // 当前激活的 Tab
const permissionListRef = ref<InstanceType<typeof PermissionList>>() // 团队成员列表 Ref

/** 获取详情 */
const customer = ref<CustomerApi.CustomerVO>({} as CustomerApi.CustomerVO) // 客户详情
const getCustomer = async () => {
  loading.value = true
  try {
    customer.value = await CustomerApi.getCustomer(customerId.value)
    await getOperateLog()
  } finally {
    loading.value = false
  }
}

/** 编辑客户 */
const formRef = ref<InstanceType<typeof CustomerForm>>() // 客户表单 Ref
const openForm = () => {
  formRef.value?.open('update', customerId.value)
}

/** 客户转移 */
const transferFormRef = ref<InstanceType<typeof CrmTransferForm>>() // 客户转移表单 ref
const handleTransfer = () => {
  transferFormRef.value?.open(customerId.value)
}

/** 锁定/解锁 */
const handleLock = async (lockStatus: boolean) => {
  const action = lockStatus ? t('customer.lock') : t('customer.unlock')
  const confirmMsg = lockStatus
    ? t('customer.lockConfirm', { name: customer.value.name })
    : t('customer.unlockConfirm', { name: customer.value.name })
  try {
    await message.confirm(confirmMsg)
    await CustomerApi.lockCustomer(customerId.value, lockStatus)
    message.success(t(lockStatus ? 'customer.lockSuccess' : 'customer.unlockSuccess', { name: customer.value.name }))
    await getCustomer()
  } catch {}
}

/** 更改成交状态 */
const handleChangeDealStatus = async () => {
  const newStatus = !customer.value.dealStatus
  const statusLabel = newStatus ? t('customer.dealStatusYes') : t('customer.dealStatusNo')
  try {
    await message.confirm(t('customer.updateDealStatusConfirm', { status: statusLabel }))
    await CustomerApi.updateCustomerDealStatus(customerId.value, newStatus)
    message.success(t('customer.updateDealStatusSuccess'))
    await getCustomer()
  } catch {}
}

/** 放入公海 */
const handlePutPool = async () => {
  try {
    await message.confirm(t('customer.putPoolConfirm', { name: customer.value.name }))
    await CustomerApi.putCustomerPool(customerId.value)
    message.success(t('customer.putPoolSuccess'))
    close()
  } catch {}
}

/** 打开商机详情 */
const openBusinessDetail = (id: number) => {
  push({ name: 'CrmBusinessDetail', params: { id } })
}

/** 打开合同详情 */
const openContractDetail = (id: number) => {
  push({ name: 'CrmContractDetail', params: { id } })
}

/** 打开回款详情 */
const openReceivableDetail = (id: number) => {
  push({ name: 'CrmReceivableDetail', params: { id } })
}

/** 获取操作日志 */
const logList = ref<OperateLogVO[]>([]) // 操作日志列表
const getOperateLog = async () => {
  const data = await getOperateLogPage({
    bizType: BizTypeEnum.CRM_CUSTOMER,
    bizId: customerId.value
  })
  logList.value = data.list
}

// 关联列表数据（后续对接 API）
const businessList = ref([]) // 商机列表
const contractList = ref([]) // 合同列表
const receivableList = ref([]) // 回款列表

const close = () => {
  delView(unref(currentRoute))
}

/** 初始化 */
const { params } = useRoute()
onMounted(() => {
  if (!params.id) {
    message.warning(t('customer.paramError'))
    close()
    return
  }
  customerId.value = params.id as unknown as number
  getCustomer()
})
</script>
