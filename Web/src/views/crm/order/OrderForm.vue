<template>
  <Dialog v-model="dialogVisible" :title="dialogTitle" width="1280">
    <el-form
      ref="formRef"
      v-loading="formLoading"
      :model="formData"
      :rules="formRules"
      label-width="auto"
    >
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item :label="t('crm.order.no')" prop="no">
            <el-input disabled v-model="formData.no" :placeholder="t('crm.order.noAutoGenerate')" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item :label="t('crm.order.status')" prop="status">
            <el-select v-model="formData.status" class="w-1/1">
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
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item :label="t('crm.order.ownerUserId')" prop="ownerUserId">
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
        <el-col :span="12">
          <el-form-item :label="t('crm.order.customerName')" prop="customerId">
            <el-select
              v-model="formData.customerId"
              :placeholder="t('crm.order.customerIdPlaceholder')"
              class="w-1/1"
              @change="handleCustomerChange"
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
      </el-row>
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item :label="t('crm.order.contractName')" prop="contractId">
            <el-select
              v-model="formData.contractId"
              class="w-1/1"
              clearable
            >
              <el-option
                v-for="item in contractList"
                :key="item.id"
                :label="item.name"
                :value="item.id!"
              />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item :label="t('crm.order.orderTime')" prop="orderTime">
            <el-date-picker
              v-model="formData.orderTime"
              :placeholder="t('crm.order.orderTimePlaceholder')"
              type="datetime"
              value-format="x"
              class="!w-1/1"
            />
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="24">
          <el-form-item :label="t('crm.order.remark')" prop="remark">
            <el-input v-model="formData.remark" :placeholder="t('crm.order.remarkPlaceholder')" type="textarea" />
          </el-form-item>
        </el-col>
      </el-row>
      <ContentWrap>
        <el-table
          :data="formData.products"
          :show-overflow-tooltip="true"
          border
          class="-mt-15px"
        >
          <el-table-column type="index" label="#" width="50" />
          <el-table-column :label="t('crm.order.productName')" prop="productId" min-width="200">
            <template #default="scope">
              <el-select
                v-model="scope.row.productId"
                class="w-1/1"
                @change="handleProductChange(scope.row)"
              >
                <el-option
                  v-for="item in productList"
                  :key="item.id"
                  :label="item.name"
                  :value="item.id"
                />
              </el-select>
            </template>
          </el-table-column>
          <el-table-column :label="t('crm.order.productPrice')" prop="productPrice" min-width="150">
            <template #default="scope">
              <el-input-number
                v-model="scope.row.productPrice"
                :precision="2"
                controls-position="right"
                class="!w-1/1"
                @change="calcTotalPrice(scope.row)"
              />
            </template>
          </el-table-column>
          <el-table-column :label="t('crm.order.count')" prop="count" min-width="100">
            <template #default="scope">
              <el-input-number
                v-model="scope.row.count"
                :precision="2"
                :min="1"
                controls-position="right"
                class="!w-1/1"
                @change="calcTotalPrice(scope.row)"
              />
            </template>
          </el-table-column>
          <el-table-column :label="t('crm.order.totalPrice')" prop="totalPrice" min-width="150">
            <template #default="scope">
              <el-input
                disabled
                v-model="scope.row.totalPrice"
                :formatter="erpPriceInputFormatter"
              />
            </template>
          </el-table-column>
          <el-table-column :label="t('crm.order.taxPercent')" prop="taxPercent" min-width="120">
            <template #default="scope">
              <el-input-number
                v-model="scope.row.taxPercent"
                :precision="2"
                :min="0"
                :max="100"
                controls-position="right"
                class="!w-1/1"
                @change="calcTotalPrice(scope.row)"
              />
            </template>
          </el-table-column>
          <el-table-column :label="t('crm.order.taxPrice')" prop="taxPrice" min-width="150">
            <template #default="scope">
              <el-input
                disabled
                v-model="scope.row.taxPrice"
                :formatter="erpPriceInputFormatter"
              />
            </template>
          </el-table-column>
          <el-table-column :label="t('crm.order.remark')" prop="remark" min-width="150">
            <template #default="scope">
              <el-input v-model="scope.row.remark" />
            </template>
          </el-table-column>
          <el-table-column :label="t('common.action')" width="100">
            <template #default="scope">
              <el-button link type="danger" @click="removeProduct(scope.$index)">
                {{ t('common.del') }}
              </el-button>
            </template>
          </el-table-column>
        </el-table>
        <el-button type="primary" plain @click="addProduct">
          <Icon class="mr-5px" icon="ep:plus" />
          {{ t('crm.order.addProduct') }}
        </el-button>
      </ContentWrap>
      <el-row :gutter="20">
        <el-col :span="8">
          <el-form-item :label="t('crm.order.totalProductPrice')" prop="totalProductPrice">
            <el-input
              disabled
              v-model="formData.totalProductPrice"
              :formatter="erpPriceInputFormatter"
            />
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item :label="t('crm.order.discountPercent')" prop="discountPercent">
            <el-input-number
              v-model="formData.discountPercent"
              :placeholder="t('crm.order.discountPercent')"
              controls-position="right"
              :min="0"
              :max="100"
              :precision="2"
              class="!w-1/1"
            />
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item :label="t('crm.order.totalPrice')" prop="totalPrice">
            <el-input
              disabled
              v-model="formData.totalPrice"
              :formatter="erpPriceInputFormatter"
            />
          </el-form-item>
        </el-col>
      </el-row>
    </el-form>
    <template #footer>
      <el-button :disabled="formLoading" type="primary" @click="submitForm">{{ t('common.save') }}</el-button>
      <el-button @click="dialogVisible = false">{{ t('common.cancel') }}</el-button>
    </template>
  </Dialog>
</template>
<script lang="ts" setup>
import * as CustomerApi from '@/api/crm/customer'
import * as OrderApi from '@/api/crm/order'
import * as UserApi from '@/api/system/user'
import * as ContractApi from '@/api/crm/contract'
import * as ProductApi from '@/api/crm/product'
import { erpPriceMultiply, erpPriceInputFormatter } from '@/utils'
import { useUserStore } from '@/store/modules/user'

const { t } = useI18n()
const message = useMessage()

const dialogVisible = ref(false)
const dialogTitle = ref('')
const formLoading = ref(false)
const formType = ref('')
const formData = ref({
  id: undefined,
  no: undefined,
  status: 0,
  customerId: undefined,
  contractId: undefined,
  ownerUserId: undefined,
  orderTime: undefined,
  discountPercent: 0,
  totalProductPrice: 0,
  totalPrice: 0,
  remark: undefined,
  products: []
})
const formRules = reactive({
  customerId: [{ required: true, message: t('crm.order.customerIdRequired'), trigger: 'blur' }],
  orderTime: [{ required: true, message: t('crm.order.orderTimeRequired'), trigger: 'blur' }],
  ownerUserId: [{ required: true, message: t('crm.order.ownerUserRequired'), trigger: 'blur' }]
})
const formRef = ref()
const userOptions = ref<UserApi.UserVO[]>([])
const customerList = ref([])
const contractList = ref<ContractApi.ContractVO[]>([])
const productList = ref<ProductApi.ProductVO[]>([])

watch(
  () => formData.value,
  (val) => {
    if (!val) return
    const totalProductPrice = val.products.reduce((prev, curr) => prev + (curr.totalPrice || 0), 0)
    const discountPrice = val.discountPercent != null
      ? erpPriceMultiply(totalProductPrice, val.discountPercent / 100.0)
      : 0
    const totalPrice = totalProductPrice - discountPrice
    formData.value.totalProductPrice = totalProductPrice
    formData.value.totalPrice = totalPrice
  },
  { deep: true }
)

const addProduct = () => {
  formData.value.products.push({
    id: undefined,
    productId: undefined,
    productPrice: 0,
    count: 1,
    totalPrice: 0,
    remark: undefined
  })
}

const removeProduct = (index: number) => {
  formData.value.products.splice(index, 1)
}

const handleProductChange = (row: any) => {
  const product = productList.value.find(p => p.id === row.productId)
  if (product) {
    row.productPrice = product.price
    calcTotalPrice(row)
  }
}

const calcTotalPrice = (row: any) => {
  row.totalPrice = erpPriceMultiply(row.productPrice, row.count)
  if (row.taxPercent != null && row.taxPercent > 0) {
    row.taxPrice = erpPriceMultiply(row.totalPrice, row.taxPercent / 100)
  } else {
    row.taxPrice = 0
  }
}

const handleCustomerChange = async () => {
  if (formData.value.customerId) {
    contractList.value = await ContractApi.getContractSimpleList(formData.value.customerId)
  }
}

const open = async (type: string, id?: number) => {
  dialogVisible.value = true
  dialogTitle.value = t('action.' + type)
  formType.value = type
  resetForm()
  if (id) {
    formLoading.value = true
    try {
      const data = await OrderApi.getOrder(id)
      formData.value = data
      if (data.products) {
        data.products.forEach(p => {
          const product = productList.value.find(prod => prod.id === p.productId)
          if (product) {
            p.productName = product.name
            p.productNo = product.no
          }
        })
      }
    } finally {
      formLoading.value = false
    }
  }
  customerList.value = await CustomerApi.getCustomerSimpleList()
  userOptions.value = await UserApi.getSimpleUserList()
  if (formType.value === 'create') {
    formData.value.ownerUserId = useUserStore().getUser.id
    formData.value.orderTime = Date.now()
  }
  productList.value = await ProductApi.getProductSimpleList()
}
defineExpose({ open })

const emit = defineEmits(['success'])
const submitForm = async () => {
  if (!formRef) return
  const valid = await formRef.value.validate()
  if (!valid) return
  formLoading.value = true
  try {
    const data = unref(formData.value) as unknown as OrderApi.OrderSaveReqVO
    if (formType.value === 'create') {
      await OrderApi.createOrder(data)
      message.success(t('common.createSuccess'))
    } else {
      await OrderApi.updateOrder(data)
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
    no: undefined,
    status: 0,
    customerId: undefined,
    contractId: undefined,
    ownerUserId: undefined,
    orderTime: undefined,
    discountPercent: 0,
    totalProductPrice: 0,
    totalPrice: 0,
    remark: undefined,
    products: []
  }
  formRef.value?.resetFields()
}
</script>