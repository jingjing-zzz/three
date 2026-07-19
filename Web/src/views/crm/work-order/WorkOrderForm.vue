<template>
  <Dialog v-model="dialogVisible" :title="dialogTitle" width="800">
    <el-form
      ref="formRef"
      v-loading="formLoading"
      :model="formData"
      :rules="formRules"
      label-width="auto"
    >
      <el-row>
        <el-col :span="12">
          <el-form-item label="工单标题" prop="title">
            <el-input v-model="formData.title" placeholder="请输入工单标题" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="工单类型" prop="type">
            <el-select v-model="formData.type" clearable placeholder="请选择工单类型">
              <el-option
                v-for="item in typeEnumList"
                :key="item.value"
                :label="item.label"
                :value="item.value"
              />
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row>
        <el-col :span="12">
          <el-form-item label="优先级" prop="priority">
            <el-select v-model="formData.priority" clearable placeholder="请选择优先级">
              <el-option
                v-for="item in priorityEnumList"
                :key="item.value"
                :label="item.label"
                :value="item.value"
              />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="客户" prop="customerId">
            <el-select v-model="formData.customerId" clearable placeholder="请选择客户" @change="handleCustomerChange">
              <el-option
                v-for="item in customerList"
                :key="item.id"
                :label="item.name"
                :value="item.id"
              />
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row>
        <el-col :span="12">
          <el-form-item label="处理人" prop="assigneeId">
            <el-input v-model="formData.assigneeId" placeholder="请输入处理人编号" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="处理人姓名" prop="assigneeName">
            <el-input v-model="formData.assigneeName" placeholder="请输入处理人姓名" />
          </el-form-item>
        </el-col>
      </el-row>
      <el-row>
        <el-col :span="24">
          <el-form-item label="工单内容" prop="content">
            <el-input v-model="formData.content" placeholder="请输入工单内容" type="textarea" :rows="4" />
          </el-form-item>
        </el-col>
      </el-row>
      <el-row>
        <el-col :span="24">
          <el-form-item label="备注" prop="remark">
            <el-input v-model="formData.remark" placeholder="请输入备注" type="textarea" :rows="2" />
          </el-form-item>
        </el-col>
      </el-row>
    </el-form>
    <template #footer>
      <el-button :disabled="formLoading" type="primary" @click="submitForm">确定</el-button>
      <el-button @click="dialogVisible = false">取消</el-button>
    </template>
  </Dialog>
</template>
<script lang="ts" setup>
import * as WorkOrderApi from '@/api/system/work-order'
import * as CustomerApi from '@/api/crm/customer'

defineOptions({ name: 'CrmWorkOrderForm' })

const { t } = useI18n()
const message = useMessage()

const dialogVisible = ref(false)
const dialogTitle = ref('')
const formLoading = ref(false)
const formType = ref('')

const typeEnumList = ref<WorkOrderApi.EnumVO[]>([])
const priorityEnumList = ref<WorkOrderApi.EnumVO[]>([])
const customerList = ref<any[]>([])

const loadEnums = async () => {
  typeEnumList.value = await WorkOrderApi.getWorkOrderTypeEnum()
  priorityEnumList.value = await WorkOrderApi.getWorkOrderPriorityEnum()
  const customerRes = await CustomerApi.getCustomerSimpleList()
  customerList.value = customerRes || []
}

const formData = ref({
  id: undefined,
  orderNo: '',
  title: '',
  type: undefined,
  priority: undefined,
  status: 1,
  content: '',
  customerId: undefined,
  customerName: '',
  assigneeId: undefined,
  assigneeName: '',
  remark: ''
})

const formRules = reactive({
  title: [{ required: true, message: '请输入工单标题', trigger: 'blur' }],
  type: [{ required: true, message: '请选择工单类型', trigger: 'change' }],
  priority: [{ required: true, message: '请选择优先级', trigger: 'change' }],
  content: [{ required: true, message: '请输入工单内容', trigger: 'blur' }]
})

const formRef = ref()

const open = async (type: string, id?: number) => {
  dialogVisible.value = true
  dialogTitle.value = type === 'create' ? '新增工单' : '修改工单'
  formType.value = type
  resetForm()
  await loadEnums()
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
  if (!formRef.value) return
  const valid = await formRef.value.validate()
  if (!valid) return
  formLoading.value = true
  try {
    const data = formData.value as unknown as WorkOrderApi.WorkOrderVO
    if (formType.value === 'create') {
      await WorkOrderApi.createWorkOrder(data)
      message.success('创建成功')
    } else {
      await WorkOrderApi.updateWorkOrder(data)
      message.success('修改成功')
    }
    dialogVisible.value = false
    emit('success')
  } finally {
    formLoading.value = false
  }
}

const handleCustomerChange = (customerId: number) => {
  if (customerId) {
    const customer = customerList.value.find(item => item.id === customerId)
    if (customer) {
      formData.value.customerName = customer.name
    }
  } else {
    formData.value.customerName = ''
  }
}

const resetForm = () => {
  formData.value = {
    id: undefined,
    orderNo: '',
    title: '',
    type: undefined,
    priority: undefined,
    status: 1,
    content: '',
    customerId: undefined,
    customerName: '',
    assigneeId: undefined,
    assigneeName: '',
    remark: ''
  }
  formRef.value?.resetFields()
}
</script>
