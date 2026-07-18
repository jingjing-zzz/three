<template>
  <ContentWrap :title="t('oa.schedule.title') + t('common.info')">
    <el-form
      ref="formRef"
      v-loading="formLoading"
      :model="formData"
      :rules="formRules"
      label-width="80px"
    >
      <el-form-item :label="t('oa.schedule.scheduleTitle')" prop="title">
        <el-input v-model="formData.title" :placeholder="t('oa.schedule.scheduleTitlePlaceholder')" />
      </el-form-item>
      <el-form-item :label="t('oa.schedule.description')" prop="description">
        <el-input v-model="formData.description" :placeholder="t('oa.schedule.descriptionPlaceholder')" type="textarea" />
      </el-form-item>
      <el-form-item :label="t('oa.schedule.startTime')" prop="startTime">
        <el-date-picker
          v-model="formData.startTime"
          clearable
          :placeholder="t('common.selectTime')"
          type="datetime"
          value-format="x"
        />
      </el-form-item>
      <el-form-item :label="t('oa.schedule.endTime')" prop="endTime">
        <el-date-picker
          v-model="formData.endTime"
          clearable
          :placeholder="t('common.selectTime')"
          type="datetime"
          value-format="x"
        />
      </el-form-item>
      <el-form-item :label="t('oa.schedule.location')" prop="location">
        <el-input v-model="formData.location" :placeholder="t('oa.schedule.locationPlaceholder')" />
      </el-form-item>
      <el-form-item :label="t('oa.schedule.remindTime')" prop="remindTime">
        <el-date-picker
          v-model="formData.remindTime"
          clearable
          :placeholder="t('common.selectTime')"
          type="datetime"
          value-format="x"
        />
      </el-form-item>
      <el-form-item :label="t('oa.schedule.status')" prop="status">
        <el-select v-model="formData.status" :placeholder="t('oa.schedule.statusPlaceholder')">
          <el-option label="未开始" :value="0" />
          <el-option label="进行中" :value="1" />
          <el-option label="已完成" :value="2" />
          <el-option label="已取消" :value="3" />
        </el-select>
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
import * as ScheduleApi from '@/api/bpm/schedule'
import { useTagsViewStore } from '@/store/modules/tagsView'

defineOptions({ name: 'BpmOAScheduleCreate' })

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
  startTime: undefined,
  endTime: undefined,
  location: undefined,
  remindTime: undefined,
  status: 0
})
const formRules = reactive({
  title: [{ required: true, message: t('oa.schedule.scheduleTitle') + t('common.notEmpty'), trigger: 'blur' }],
  startTime: [{ required: true, message: t('oa.schedule.startTime') + t('common.notEmpty'), trigger: 'change' }],
  endTime: [{ required: true, message: t('oa.schedule.endTime') + t('common.notEmpty'), trigger: 'change' }]
})
const formRef = ref()

const submitForm = async () => {
  if (!formRef) return
  const valid = await formRef.value.validate()
  if (!valid) return

  formLoading.value = true
  try {
    if (isEdit.value) {
      await ScheduleApi.updateSchedule(formData.value as ScheduleApi.ScheduleVO)
      message.success(t('common.updateSuccess'))
    } else {
      await ScheduleApi.createSchedule(formData.value as ScheduleApi.ScheduleVO)
      message.success(t('common.createSuccess'))
    }
    delView(unref(currentRoute))
    await push({ name: 'BpmOASchedule' })
  } finally {
    formLoading.value = false
  }
}

const getDetail = async (id: number) => {
  try {
    formLoading.value = true
    const data = await ScheduleApi.getSchedule(id)
    if (!data) {
      message.error(t('common.dataNotFound'))
      return
    }
    formData.value = {
      id: data.id,
      title: data.title,
      description: data.description,
      startTime: data.startTime,
      endTime: data.endTime,
      location: data.location,
      remindTime: data.remindTime,
      status: data.status
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