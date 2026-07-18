<template>
  <Dialog :title="dialogTitle" v-model="dialogVisible" width="600">
    <el-form
      ref="formRef"
      :model="formData"
      :rules="formRules"
      label-width="auto"
      v-loading="formLoading"
    >
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="工单标题" prop="title">
            <el-input v-model="formData.title" placeholder="请输入工单标题" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="工单类型" prop="type">
            <el-select v-model="formData.type" class="w-1/1" placeholder="请选择工单类型">
              <el-option v-for="item in WORK_ORDER_TYPE_OPTIONS" :key="item.value" :label="item.label" :value="item.value" />
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="优先级" prop="priority">
            <el-select v-model="formData.priority" class="w-1/1" placeholder="请选择优先级">
              <el-option v-for="item in WORK_ORDER_PRIORITY_OPTIONS" :key="item.value" :label="item.label" :value="item.value" />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="客户" prop="customerId">
            <el-select v-model="formData.customerId" class="w-1/1" clearable placeholder="请选择客户">
              <el-option v-for="item in customerOptions" :key="item.id" :label="item.name" :value="item.id" />
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="处理人" prop="ownerUserId">
            <el-select v-model="formData.ownerUserId" class="w-1/1" clearable placeholder="请选择处理人">
              <el-option v-for="item in userOptions" :key="item.id" :label="item.nickname" :value="item.id" />
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="24">
          <el-form-item label="工单内容" prop="content">
            <el-input type="textarea" v-model="formData.content" placeholder="请输入工单内容" :rows="4" />
          </el-form-item>
        </el-col>
      </el-row>
    </el-form>
    <template #footer>
      <el-button @click="submitForm" type="primary" :disabled="formLoading">{{ t('common.confirm') }}</el-button>
      <el-button @click="dialogVisible = false">{{ t('common.cancel') }}</el-button>
    </template>
  </Dialog>
</template>

<script lang="ts" setup>
import * as WorkOrderApi from '@/api/crm/workOrder'
import * as UserApi from '@/api/system/user'
import * as CustomerApi from '@/api/crm/customer'
import { WORK_ORDER_TYPE_OPTIONS, WORK_ORDER_PRIORITY_OPTIONS } from '@/api/crm/workOrder'

const { t } = useI18n()
const message = useMessage()

const dialogVisible = ref(false)
const dialogTitle = ref('')
const formLoading = ref(false)
const formType = ref('')
const formData = ref({
  id: undefined,
  title: undefined,
  content: undefined,
  type: undefined,
  priority: undefined,
  customerId: undefined,
  ownerUserId: undefined
})
const formRules = reactive({
  title: [{ required: true, message: '工单标题不能为空', trigger: 'blur' }],
  type: [{ required: true, message: '工单类型不能为空', trigger: 'blur' }],
  priority: [{ required: true, message: '优先级不能为空', trigger: 'blur' }]
})
const formRef = ref()
const userOptions = ref<any[]>([])
const customerOptions = ref<any[]>([])

const open = async (type: string, id?: number) => {
  dialogVisible.value = true
  dialogTitle.value = t('action.' + type)
  formType.value = type
  resetForm()
  userOptions.value = await UserApi.getSimpleUserList()
  customerOptions.value = await CustomerApi.getCustomerSimpleList()
  if (id) {
    formLoading.value = true
    try {
      formData.value = await WorkOrderApi.getWorkOrder(id)
    } finally {
      formLoading.value = false
    }
  }
}
defineExpose({ open })

const emit = defineEmits(['success'])
const submitForm = async () => {
  if (!formRef) return
  const valid = await formRef.value.validate()
  if (!valid) return
  formLoading.value = true
  try {
    const data = formData.value as unknown as WorkOrderApi.WorkOrderVO
    if (formType.value === 'create') {
      await WorkOrderApi.createWorkOrder(data)
      message.success(t('common.createSuccess'))
    } else {
      await WorkOrderApi.updateWorkOrder(data)
      message.success(t('common.updateSuccess'))
    }
    dialogVisible.value = false
    emit('success')
  } finally {
    formLoading.value = false
  }
}

const resetForm = () => {
  formData.value = {
    id: undefined,
    title: undefined,
    content: undefined,
    type: undefined,
    priority: undefined,
    customerId: undefined,
    ownerUserId: undefined
  }
  formRef.value?.resetFields()
}
</script>