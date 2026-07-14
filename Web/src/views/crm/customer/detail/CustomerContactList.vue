<template>
  <!-- 操作栏 -->
  <el-row class="mb-10px" justify="end">
    <el-button v-hasPermi="['crm:contact:create']" type="primary" @click="openContactForm">
      <Icon class="mr-5px" icon="ep:plus" />
      {{ t('common.add') }}
    </el-button>
  </el-row>
  <!-- 列表 -->
  <ContentWrap>
    <el-table v-loading="loading" :data="list" :show-overflow-tooltip="true" :stripe="true">
      <el-table-column align="center" :label="t('contact.name')" prop="name" min-width="120" />
      <el-table-column align="center" :label="t('contact.mobile')" prop="mobile" min-width="120" />
      <el-table-column align="center" :label="t('contact.telephone')" prop="telephone" min-width="120" />
      <el-table-column align="center" :label="t('contact.email')" prop="email" min-width="180" />
      <el-table-column align="center" :label="t('contact.post')" prop="post" min-width="100" />
      <el-table-column align="center" :label="t('contact.master')" prop="master" min-width="120">
        <template #default="scope">
          <dict-tag :type="DICT_TYPE.INFRA_BOOLEAN_STRING" :value="scope.row.master" />
        </template>
      </el-table-column>
      <el-table-column align="center" :label="t('contact.contactNextTime')" min-width="160">
        <template #default="scope">
          {{ formatDate(scope.row.contactNextTime) }}
        </template>
      </el-table-column>
      <el-table-column align="center" :label="t('contact.remark')" prop="remark" min-width="150" />
      <el-table-column align="center" fixed="right" :label="t('common.action')" min-width="150">
        <template #default="scope">
          <el-button
            v-hasPermi="['crm:contact:update']"
            link
            type="primary"
            @click="openContactForm(scope.row.id)"
          >
            {{ t('common.edit') }}
          </el-button>
          <el-button
            v-hasPermi="['crm:contact:delete']"
            link
            type="danger"
            @click="handleDelete(scope.row.id)"
          >
            {{ t('common.delete') }}
          </el-button>
        </template>
      </el-table-column>
    </el-table>
    <!-- 分页 -->
    <Pagination
      v-model:limit="queryParams.pageSize"
      v-model:page="queryParams.pageNo"
      :total="total"
      @pagination="getList"
    />
  </ContentWrap>

  <!-- 表单弹窗 -->
  <ContactForm ref="formRef" @success="getList" />
</template>
<script lang="ts" setup>
import { DICT_TYPE } from '@/utils/dict'
import { formatDate } from '@/utils/formatTime'
import * as ContactApi from '@/api/crm/contact'
import ContactForm from '@/views/crm/contact/ContactForm.vue'

defineOptions({ name: 'CrmCustomerContactList' })

const { t } = useI18n('crm') // 国际化
const message = useMessage()

const { customerId } = defineProps<{
  customerId: number // 客户编号
}>()

const loading = ref(false)
const total = ref(0)
const list = ref([])
const queryParams = reactive({
  pageNo: 1,
  pageSize: 10,
  customerId: customerId
})

/** 查询列表 */
const getList = async () => {
  loading.value = true
  try {
    const data = await ContactApi.getContactPage(queryParams)
    list.value = data.list
    total.value = data.total
  } finally {
    loading.value = false
  }
}

/** 添加/修改联系人 */
const formRef = ref()
const openContactForm = (id?: number) => {
  formRef.value?.open(id ? 'update' : 'create', id, customerId)
}

/** 删除 */
const handleDelete = async (id: number) => {
  try {
    await message.delConfirm()
    await ContactApi.deleteContact(id)
    message.success(t('common.delSuccess'))
    await getList()
  } catch {}
}

/** 初始化 */
onMounted(() => {
  getList()
})

/** 监听 customerId 变化 */
watch(() => customerId, (val) => {
  queryParams.customerId = val
  getList()
})
</script>
