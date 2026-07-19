<template>
  <Dialog :title="dialogTitle" v-model="dialogVisible">
    <el-form ref="formRef" :model="formData" :rules="formRules" label-width="100px" v-loading="formLoading">
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="主题" prop="subject">
            <el-input v-model="formData.subject" placeholder="请输入主题" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="业务时间" prop="recordTime">
            <el-date-picker
              v-model="formData.recordTime"
              type="datetime"
              value-format="x"
              placeholder="请选择业务时间"
              class="!w-full"
            />
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="往来单位" prop="counterparty">
            <el-input v-model="formData.counterparty" placeholder="请输入往来单位" />
          </el-form-item>
        </el-col>
        <el-col :span="12" v-if="props.type === 10">
          <el-form-item label="发票号码" prop="invoiceNo">
            <el-input v-model="formData.invoiceNo" placeholder="请输入发票号码" />
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="申请人" prop="applicantUserId">
            <el-select v-model="formData.applicantUserId" clearable filterable placeholder="请选择申请人" class="!w-full">
              <el-option v-for="item in userList" :key="item.id" :label="item.nickname" :value="item.id" />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="财务人员" prop="financeUserId">
            <el-select v-model="formData.financeUserId" clearable filterable placeholder="请选择财务人员" class="!w-full">
              <el-option v-for="item in userList" :key="item.id" :label="item.nickname" :value="item.id" />
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="结算账户" prop="accountId">
            <el-select v-model="formData.accountId" clearable filterable placeholder="请选择结算账户" class="!w-full">
              <el-option v-for="item in accountList" :key="item.id" :label="item.name" :value="item.id" />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="到期时间" prop="dueTime">
            <el-date-picker
              v-model="formData.dueTime"
              type="datetime"
              value-format="x"
              placeholder="请选择到期时间"
              class="!w-full"
            />
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="不含税金额" prop="amount">
            <el-input-number v-model="formData.amount" :precision="2" :min="0" class="!w-full" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="税额" prop="taxAmount">
            <el-input-number v-model="formData.taxAmount" :precision="2" :min="0" class="!w-full" />
          </el-form-item>
        </el-col>
      </el-row>
      <el-form-item label="备注" prop="remark">
        <el-input v-model="formData.remark" type="textarea" placeholder="请输入备注" />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="submitForm" type="primary" :disabled="formLoading">确定</el-button>
      <el-button @click="dialogVisible = false">取消</el-button>
    </template>
  </Dialog>
</template>

<script setup lang="ts">
import { AccountApi } from '@/api/erp/finance/account'
import { FinanceRecordApi, FinanceRecordVO } from '@/api/erp/finance/record'
import { getSimpleUserList, UserVO } from '@/api/system/user'

defineOptions({ name: 'ErpFinanceRecordForm' })

const props = defineProps<{ type: number; title: string }>()
const message = useMessage()

const dialogVisible = ref(false)
const dialogTitle = ref('')
const formLoading = ref(false)
const formType = ref('')
const userList = ref<UserVO[]>([])
const accountList = ref<any[]>([])
const formData = ref<FinanceRecordVO>({
  type: props.type,
  recordTime: '',
  subject: '',
  amount: 0,
  taxAmount: 0
})
const isBpmType = computed(() => [20, 30].includes(props.type))
const formRules = computed(() => ({
  subject: [{ required: true, message: '主题不能为空', trigger: 'blur' }],
  recordTime: [{ required: true, message: '业务时间不能为空', trigger: 'change' }],
  amount: [{ required: true, message: '不含税金额不能为空', trigger: 'blur' }],
  applicantUserId: props.type === 20
    ? [{ required: true, message: '报销必须选择申请人', trigger: 'change' }]
    : [],
  financeUserId: isBpmType.value
    ? [{ required: true, message: '请选择财务审批人', trigger: 'change' }]
    : []
}))
const formRef = ref()

const open = async (type: string, id?: number) => {
  dialogVisible.value = true
  dialogTitle.value = `${type === 'create' ? '新增' : '编辑'}${props.title}`
  formType.value = type
  resetForm()
  await loadOptions()
  if (id) {
    formLoading.value = true
    try {
      formData.value = await FinanceRecordApi.get(id)
    } finally {
      formLoading.value = false
    }
  }
}
defineExpose({ open })

const loadOptions = async () => {
  if (!userList.value.length) {
    userList.value = await getSimpleUserList()
  }
  if (!accountList.value.length) {
    accountList.value = await AccountApi.getAccountSimpleList()
  }
}

const emit = defineEmits(['success'])
const submitForm = async () => {
  await formRef.value.validate()
  formLoading.value = true
  try {
    const data = { ...formData.value, type: props.type } as FinanceRecordVO
    if (formType.value === 'create') {
      await FinanceRecordApi.create(data)
      message.success('创建成功')
    } else {
      await FinanceRecordApi.update(data)
      message.success('更新成功')
    }
    dialogVisible.value = false
    emit('success')
  } finally {
    formLoading.value = false
  }
}

const resetForm = () => {
  formData.value = {
    type: props.type,
    recordTime: Date.now(),
    subject: '',
    amount: 0,
    taxAmount: 0
  }
  formRef.value?.resetFields()
}
</script>
