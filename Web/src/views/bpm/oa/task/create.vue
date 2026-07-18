<template>
  <ContentWrap :title="t('oa.task.title') + t('common.info')">
    <el-form
      ref="formRef"
      v-loading="formLoading"
      :model="formData"
      :rules="formRules"
      label-width="80px"
    >
      <el-form-item :label="t('oa.task.title')" prop="title">
        <el-input v-model="formData.title" :placeholder="t('oa.task.titlePlaceholder')" />
      </el-form-item>
      <el-form-item :label="t('oa.task.description')" prop="description">
        <el-input v-model="formData.description" :placeholder="t('oa.task.descriptionPlaceholder')" type="textarea" />
      </el-form-item>
      <el-form-item :label="t('oa.task.priority')" prop="priority">
        <el-select v-model="formData.priority" :placeholder="t('oa.task.priorityPlaceholder')">
          <el-option label="普通" :value="0" />
          <el-option label="重要" :value="1" />
          <el-option label="紧急" :value="2" />
        </el-select>
      </el-form-item>
      <el-form-item :label="t('oa.task.status')" prop="status">
        <el-select v-model="formData.status" :placeholder="t('oa.task.statusPlaceholder')">
          <el-option label="待处理" :value="0" />
          <el-option label="进行中" :value="1" />
          <el-option label="已完成" :value="2" />
          <el-option label="已取消" :value="3" />
        </el-select>
      </el-form-item>
      <el-form-item :label="t('oa.task.startTime')" prop="startTime">
        <el-date-picker
          v-model="formData.startTime"
          clearable
          :placeholder="t('common.selectTime')"
          type="datetime"
          value-format="x"
        />
      </el-form-item>
      <el-form-item :label="t('oa.task.endTime')" prop="endTime">
        <el-date-picker
          v-model="formData.endTime"
          clearable
          :placeholder="t('common.selectTime')"
          type="datetime"
          value-format="x"
        />
      </el-form-item>
      <el-form-item>
        <el-button :disabled="formLoading" type="primary" @click="submitForm">
          {{ t('common.ok') }}
        </el-button>
      </el-form-item>
    </el-form>
  </ContentWrap>
</template>
<script lang="ts" setup>
import * as TaskApi from '@/api/bpm/task'
import { useTagsViewStore } from '@/store/modules/tagsView'

defineOptions({ name: 'BpmOATaskCreate' })

const message = useMessage()
const { t } = useI18n('bpm')
const { delView } = useTagsViewStore()
const { push, currentRoute } = useRouter()
const { query } = useRoute()

const formLoading = ref(false)
const isEdit = ref(false)
const formData = ref({
  id: undefined,
  title: undefined,
  description: undefined,
  priority: 0,
  status: 0,
  startTime: undefined,
  endTime: undefined
})
const formRules = reactive({
  title: [{ required: true, message: t('oa.task.taskTitle') + t('common.notEmpty'), trigger: 'blur' }]
})
const formRef = ref()

const submitForm = async () => {
  if (!formRef) return
  const valid = await formRef.value.validate()
  if (!valid) return

  formLoading.value = true
  try {
    if (isEdit.value) {
      await TaskApi.updateTask(formData.value as TaskApi.TaskVO)
      message.success(t('common.updateSuccess'))
    } else {
      await TaskApi.createTask(formData.value as TaskApi.TaskVO)
      message.success(t('common.createSuccess'))
    }
    delView(unref(currentRoute))
    await push({ name: 'BpmOATask' })
  } finally {
    formLoading.value = false
  }
}

const getDetail = async (id: number) => {
  try {
    formLoading.value = true
    const data = await TaskApi.getTask(id)
    if (!data) {
      message.error(t('common.dataNotFound'))
      return
    }
    formData.value = {
      id: data.id,
      title: data.title,
      description: data.description,
      priority: data.priority,
      status: data.status,
      startTime: data.startTime,
      endTime: data.endTime
    }
    isEdit.value = true
  } finally {
    formLoading.value = false
  }
}

onMounted(async () => {
  if (query.id) {
    await getDetail(Number(query.id))
  }
})
</script>