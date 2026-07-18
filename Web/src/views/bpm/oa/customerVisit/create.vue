<template>
  <el-row :gutter="20">
    <el-col :span="16">
      <ContentWrap :title="t('oa.customerVisit.title') + t('common.info')">
        <el-form
          ref="formRef"
          v-loading="formLoading"
          :model="formData"
          :rules="formRules"
          label-width="80px"
        >
          <el-form-item :label="t('oa.customerVisit.customerName')" prop="customerName">
            <el-input v-model="formData.customerName" :placeholder="t('oa.customerVisit.customerNamePlaceholder')" />
          </el-form-item>
          <el-form-item :label="t('oa.customerVisit.customerContact')" prop="customerContact">
            <el-input v-model="formData.customerContact" :placeholder="t('oa.customerVisit.customerContactPlaceholder')" />
          </el-form-item>
          <el-form-item :label="t('oa.customerVisit.contactPhone')" prop="contactPhone">
            <el-input v-model="formData.contactPhone" :placeholder="t('oa.customerVisit.contactPhonePlaceholder')" />
          </el-form-item>
          <el-form-item :label="t('oa.customerVisit.visitTime')" prop="visitTime">
            <el-date-picker
              v-model="formData.visitTime"
              clearable
              :placeholder="t('common.selectTime')"
              type="datetime"
              value-format="x"
              :disabled-date="disabledPastDate"
            />
          </el-form-item>
          <el-form-item :label="t('oa.customerVisit.visitLocation')" prop="visitLocation">
            <el-input v-model="formData.visitLocation" :placeholder="t('oa.customerVisit.visitLocationPlaceholder')" />
          </el-form-item>
          <el-form-item :label="t('oa.customerVisit.purpose')" prop="purpose">
            <el-input v-model="formData.purpose" :placeholder="t('oa.customerVisit.purposePlaceholder')" type="textarea" />
          </el-form-item>
          <el-form-item>
            <el-button :disabled="formLoading" type="primary" @click="submitForm">
              {{ t('common.ok') }}
            </el-button>
          </el-form-item>
        </el-form>
      </ContentWrap>
    </el-col>
    <el-col :span="8">
      <ContentWrap :title="t('process.instance.flowDiagram')" :bodyStyle="{ padding: '0 20px 0' }">
        <ProcessInstanceTimeline
          ref="timelineRef"
          :activity-nodes="activityNodes"
          :show-status-icon="false"
          @select-user-confirm="selectUserConfirm"
        />
      </ContentWrap>
    </el-col>
  </el-row>
</template>
<script lang="ts" setup>
import * as CustomerVisitApi from '@/api/bpm/customerVisit'
import { useTagsViewStore } from '@/store/modules/tagsView'
import * as DefinitionApi from '@/api/bpm/definition'
import ProcessInstanceTimeline from '@/views/bpm/processInstance/detail/ProcessInstanceTimeline.vue'
import * as ProcessInstanceApi from '@/api/bpm/processInstance'
import { CandidateStrategy, NodeId } from '@/components/SimpleProcessDesignerV2/src/consts'
import { ApprovalNodeInfo } from '@/api/bpm/processInstance'

defineOptions({ name: 'BpmOACustomerVisitCreate' })

const message = useMessage()
const { t } = useI18n('bpm')
const { delView } = useTagsViewStore()
const { push, currentRoute } = useRouter()
const { query } = useRoute()

const formLoading = ref(false)
const formData = ref({
  customerName: undefined,
  customerContact: undefined,
  contactPhone: undefined,
  visitTime: undefined,
  visitLocation: undefined,
  purpose: undefined
})
const formRules = reactive({
  customerName: [{ required: true, message: t('oa.customerVisit.customerName') + t('common.notEmpty'), trigger: 'blur' }],
  visitTime: [{ required: true, message: t('oa.customerVisit.visitTime') + t('common.notEmpty'), trigger: 'change' }],
  purpose: [{ required: true, message: t('oa.customerVisit.purpose') + t('common.notEmpty'), trigger: 'change' }]
})
const formRef = ref()

const processDefineKey = 'oa_customer_visit'
const startUserSelectTasks = ref([])
const startUserSelectAssignees = ref({})
const tempStartUserSelectAssignees = ref({})
const activityNodes = ref<ProcessInstanceApi.ApprovalNodeInfo[]>([])
const processDefinitionId = ref('')

const submitForm = async () => {
  if (!formRef) return
  const valid = await formRef.value.validate()
  if (!valid) return
  if (startUserSelectTasks.value?.length > 0) {
    for (const userTask of startUserSelectTasks.value) {
      if (
        Array.isArray(startUserSelectAssignees.value[userTask.id]) &&
        startUserSelectAssignees.value[userTask.id].length === 0
      ) {
        return message.warning(t('process.instance.selectCandidate', { name: userTask.name }))
      }
    }
  }

  formLoading.value = true
  try {
    const data = { ...formData.value } as unknown as CustomerVisitApi.CustomerVisitVO
    if (startUserSelectTasks.value?.length > 0) {
      data.startUserSelectAssignees = startUserSelectAssignees.value
    }
    await CustomerVisitApi.createCustomerVisit(data)
    message.success(t('process.instance.startSuccess'))
    delView(unref(currentRoute))
    await push({ name: 'BpmOACustomerVisit' })
  } finally {
    formLoading.value = false
  }
}

const getApprovalDetail = async () => {
  try {
    const data = await ProcessInstanceApi.getApprovalDetail({
      processDefinitionId: processDefinitionId.value,
      activityId: NodeId.START_USER_NODE_ID,
      processVariablesStr: JSON.stringify({})
    })
    if (!data) {
      message.error(t('process.instance.queryError'))
      return
    }
    activityNodes.value = data.activityNodes
    startUserSelectTasks.value = data.activityNodes?.filter(
      (node: ApprovalNodeInfo) => CandidateStrategy.START_USER_SELECT === node.candidateStrategy
    )
    if (startUserSelectTasks.value?.length > 0) {
      for (const node of startUserSelectTasks.value) {
        if (
          tempStartUserSelectAssignees.value[node.id] &&
          tempStartUserSelectAssignees.value[node.id].length > 0
        ) {
          startUserSelectAssignees.value[node.id] = tempStartUserSelectAssignees.value[node.id]
        } else {
          startUserSelectAssignees.value[node.id] = []
        }
      }
    }
  } finally {
  }
}

const selectUserConfirm = (id: string, userList: any[]) => {
  startUserSelectAssignees.value[id] = userList?.map((item: any) => item.id)
}

const disabledPastDate = (time: Date) => {
  return time.getTime() < Date.now() - 8.64e7
}

const getDetail = async (id: number) => {
  try {
    formLoading.value = true
    const data = await CustomerVisitApi.getCustomerVisit(id)
    if (!data) {
      message.error(t('oa.customerVisit.restartFailed') + '：' + t('oa.customerVisit.dataNotFound'))
      return
    }
    formData.value = {
      customerName: data.customerName,
      customerContact: data.customerContact,
      contactPhone: data.contactPhone,
      visitTime: data.visitTime,
      visitLocation: data.visitLocation,
      purpose: data.purpose
    }
  } finally {
    formLoading.value = false
  }
}

onMounted(async () => {
  const processDefinitionDetail = await DefinitionApi.getProcessDefinition(
    undefined,
    processDefineKey
  )
  if (!processDefinitionDetail) {
    message.error(t('oa.customerVisit.processModelNotFound'))
    return
  }
  processDefinitionId.value = processDefinitionDetail.id
  startUserSelectTasks.value = processDefinitionDetail.startUserSelectTasks

  if (query.id) {
    await getDetail(Number(query.id))
  }

  await getApprovalDetail()
})

watch(
  formData.value,
  (newValue, oldValue) => {
    if (!oldValue) {
      return
    }
    if (newValue && Object.keys(newValue).length > 0) {
      tempStartUserSelectAssignees.value = startUserSelectAssignees.value
      startUserSelectAssignees.value = {}
      getApprovalDetail()
    }
  },
  {
    immediate: true
  }
)
</script>