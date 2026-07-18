<template>
  <Dialog :title="dialogTitle" v-model="dialogVisible" width="1280">
    <el-form
      ref="formRef"
      :model="formData"
      :rules="formRules"
      label-width="auto"
      v-loading="formLoading"
    >
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item :label="t('crm.business.name')" prop="name">
            <el-input v-model="formData.name" :placeholder="t('crm.business.namePlaceholder')" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item :label="t('crm.business.ownerUserId')" prop="ownerUserId">
            <el-select
              v-model="formData.ownerUserId"
              :disabled="formType !== 'create'"
              class="w-1/1"
            >
              <el-option
                v-for="item in userOptions"
                :key="item.id"
                :label="item.nickname"
                :value="item.id"
              />
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item :label="t('crm.business.customerName')" prop="customerId">
            <el-select
              :disabled="formData.customerDefault"
              v-model="formData.customerId"
              :placeholder="t('crm.business.customerIdPlaceholder')"
              class="w-1/1"
            >
              <el-option
                v-for="item in customerList"
                :key="item.id"
                :label="item.name"
                :value="item.id"
              />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item :label="t('crm.business.statusTypeId')" prop="statusTypeId">
            <el-select
              v-model="formData.statusTypeId"
              :placeholder="t('crm.business.statusTypePlaceholder')"
              clearable
              class="w-1/1"
              :disabled="formType !== 'create'"
            >
              <el-option
                v-for="item in statusTypeList"
                :key="item.id"
                :label="item.name"
                :value="item.id"
              />
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item :label="t('crm.business.dealTime')" prop="dealTime">
            <el-date-picker
              v-model="formData.dealTime"
              type="date"
              value-format="x"
              :placeholder="t('crm.business.dealTimePlaceholder')"
              class="!w-1/1"
            />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item :label="t('crm.business.remark')" prop="remark">
            <el-input type="textarea" v-model="formData.remark" :placeholder="t('crm.business.remarkPlaceholder')" />
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="商机来源" prop="source">
            <el-select
              v-model="formData.source"
              :placeholder="t('crm.business.sourcePlaceholder')"
              clearable
              class="w-1/1"
            >
              <el-option
                v-for="item in sourceOptions"
                :key="item.value"
                :label="item.label"
                :value="item.value"
              />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="竞争对手" prop="competitor">
            <el-input v-model="formData.competitor" placeholder="请输入竞争对手" maxlength="100" show-word-limit />
          </el-form-item>
        </el-col>
      </el-row>
      <ContentWrap>
        <el-tabs v-model="subTabsName" class="-mt-15px -mb-10px">
          <el-tab-pane :label="t('crm.business.productList')" name="product">
            <BusinessProductForm
              ref="productFormRef"
              :products="formData.products"
              :disabled="disabled"
            />
          </el-tab-pane>
        </el-tabs>
      </ContentWrap>
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item :label="t('crm.business.totalProductPrice')" prop="totalProductPrice">
            <el-input
              disabled
              v-model="formData.totalProductPrice"
              :formatter="erpPriceInputFormatter"
            />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item :label="t('crm.business.discountPercent')" prop="discountPercent">
            <el-input-number
              v-model="formData.discountPercent"
              :placeholder="t('crm.business.discountPercent')"
              controls-position="right"
              :min="0"
              :precision="2"
              class="!w-1/1"
            />
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item :label="t('crm.business.discountAmount')" prop="price">
            <el-input
              disabled
              v-model="formData.totalPrice"
              :placeholder="t('crm.business.namePlaceholder')"
              :formatter="erpPriceInputFormatter"
            />
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
<script setup lang="ts">
import * as BusinessApi from '@/api/crm/business'
import * as BusinessStatusApi from '@/api/crm/business/status'
import * as CustomerApi from '@/api/crm/customer'
import * as UserApi from '@/api/system/user'
import * as DictDataApi from '@/api/system/dict/dict.data'
import { useUserStore } from '@/store/modules/user'
import BusinessProductForm from './components/BusinessProductForm.vue'
import { erpPriceMultiply, erpPriceInputFormatter } from '@/utils'

const { t } = useI18n()
const message = useMessage()

const dialogVisible = ref(false)
const dialogTitle = ref('')
const formLoading = ref(false)
const formType = ref('')
const formData = ref({
  id: undefined,
  name: undefined,
  customerId: undefined,
  ownerUserId: undefined,
  statusTypeId: undefined,
  dealTime: undefined,
  discountPercent: 0,
  totalProductPrice: undefined,
  totalPrice: undefined,
  remark: undefined,
  source: undefined,
  competitor: undefined,
  products: [],
  contactId: undefined,
  customerDefault: false
})
const formRules = reactive({
  name: [{ required: true, message: t('crm.business.nameRequired'), trigger: 'blur' }],
  customerId: [{ required: true, message: t('crm.business.customerIdRequired'), trigger: 'blur' }],
  ownerUserId: [{ required: true, message: t('crm.business.ownerUserRequired'), trigger: 'blur' }],
  statusTypeId: [{ required: true, message: t('crm.business.statusTypeRequired'), trigger: 'blur' }]
})
const formRef = ref()
const userOptions = ref<UserApi.UserVO[]>([])
const statusTypeList = ref([])
const customerList = ref([])
const sourceOptions = ref<DictDataApi.DictDataVO[]>([])

const subTabsName = ref('product')
const productFormRef = ref()

watch(
  () => formData.value,
  (val) => {
    if (!val) {
      return
    }
    const totalProductPrice = val.products.reduce((prev, curr) => prev + curr.totalPrice, 0)
    const discountPrice =
      val.discountPercent != null
        ? erpPriceMultiply(totalProductPrice, val.discountPercent / 100.0)
        : 0
    const totalPrice = totalProductPrice - discountPrice
    formData.value.totalProductPrice = totalProductPrice
    formData.value.totalPrice = totalPrice
  },
  { deep: true }
)

const open = async (type: string, id?: number, customerId?: number, contactId?: number) => {
  dialogVisible.value = true
  dialogTitle.value = t('action.' + type)
  formType.value = type
  resetForm()
  if (id) {
    formLoading.value = true
    try {
      formData.value = await BusinessApi.getBusiness(id)
    } finally {
      formLoading.value = false
    }
  } else {
    if (customerId) {
      formData.value.customerId = customerId
      formData.value.customerDefault = true
    }
    if (contactId) {
      formData.value.contactId = contactId
    }
  }
  customerList.value = await CustomerApi.getCustomerSimpleList()
  const statusPage = await BusinessStatusApi.getBusinessStatusPage({ pageNo: 1, pageSize: 100 })
  const statusLists = await Promise.all(
    statusPage.list.map((statusType) => BusinessStatusApi.getBusinessStatusSimpleList(statusType.id))
  )
  statusTypeList.value = statusPage.list.filter((_, index) => statusLists[index].length > 0)
  userOptions.value = await UserApi.getSimpleUserList()
  sourceOptions.value = await DictDataApi.getDictDataByType('crm_business_source')
  if (formType.value === 'create') {
    formData.value.ownerUserId = useUserStore().getUser.id
  }
}
defineExpose({ open })

const emit = defineEmits(['success'])
const submitForm = async () => {
  if (!formRef) return
  const valid = await formRef.value.validate()
  if (!valid) return
  await productFormRef.value.validate()
  formLoading.value = true
  try {
    const data = formData.value as unknown as BusinessApi.BusinessVO
    if (formType.value === 'create') {
      await BusinessApi.createBusiness(data)
      message.success(t('common.createSuccess'))
    } else {
      await BusinessApi.updateBusiness(data)
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
    name: undefined,
    customerId: undefined,
    ownerUserId: undefined,
    statusTypeId: undefined,
    dealTime: undefined,
    discountPercent: 0,
    totalProductPrice: undefined,
    totalPrice: undefined,
    remark: undefined,
    source: undefined,
    competitor: undefined,
    products: [],
    contactId: undefined,
    customerDefault: false
  }
  formRef.value?.resetFields()
}
</script>
