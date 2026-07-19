<template>
  <ContentWrap>
    <el-form
      ref="queryFormRef"
      :model="queryParams"
      class="-mb-15px"
      label-width="auto"
    >
      <el-row :gutter="20">
        <el-col :span="8">
          <el-form-item :label="t('oa.task.title')" prop="title">
            <el-input
              v-model="queryParams.title"
              class="!w-240px"
              clearable
              :placeholder="t('oa.task.titlePlaceholder')"
              @keyup.enter="handleQuery"
            />
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item :label="t('oa.task.status')" prop="status">
            <el-select
              v-model="queryParams.status"
              class="!w-240px"
              clearable
              :placeholder="t('oa.task.statusPlaceholder')"
            >
              <el-option label="待处理" :value="0" />
              <el-option label="进行中" :value="1" />
              <el-option label="已完成" :value="2" />
              <el-option label="已取消" :value="3" />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item :label="t('oa.task.priority')" prop="priority">
            <el-select
              v-model="queryParams.priority"
              class="!w-240px"
              clearable
              :placeholder="t('oa.task.priorityPlaceholder')"
            >
              <el-option label="普通" :value="0" />
              <el-option label="重要" :value="1" />
              <el-option label="紧急" :value="2" />
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row>
        <el-col :span="24">
          <el-form-item>
            <el-button @click="handleQuery">
              <Icon class="mr-5px" icon="ep:search" />
              {{ t('common.search') }}
            </el-button>
            <el-button @click="resetQuery">
              <Icon class="mr-5px" icon="ep:refresh" />
              {{ t('common.reset') }}
            </el-button>
            <el-button plain type="primary" @click="handleCreate()">
              <Icon class="mr-5px" icon="ep:plus" />
              {{ t('oa.task.createTask') }}
            </el-button>
          </el-form-item>
        </el-col>
      </el-row>
    </el-form>
  </ContentWrap>

  <ContentWrap>
    <el-table v-loading="loading" :data="list" :table-layout="'auto'">
      <el-table-column align="center" :label="t('oa.task.id')" prop="id" />
      <el-table-column align="center" :label="t('oa.task.taskTitle')" prop="title" />
      <el-table-column align="center" :label="t('oa.task.priority')" prop="priority">
        <template #default="scope">
          <el-tag v-if="scope.row.priority === 0">普通</el-tag>
          <el-tag v-else-if="scope.row.priority === 1" type="warning">重要</el-tag>
          <el-tag v-else type="danger">紧急</el-tag>
        </template>
      </el-table-column>
      <el-table-column align="center" :label="t('oa.task.status')" prop="status">
        <template #default="scope">
          <span v-if="scope.row.status === 0">待处理</span>
          <span v-else-if="scope.row.status === 1">进行中</span>
          <span v-else-if="scope.row.status === 2">已完成</span>
          <span v-else>已取消</span>
        </template>
      </el-table-column>
      <el-table-column
        :formatter="dateFormatter"
        align="center"
        :label="t('oa.task.startTime')"
        prop="startTime"
        min-width="180"
      />
      <el-table-column
        :formatter="dateFormatter"
        align="center"
        :label="t('oa.task.endTime')"
        prop="endTime"
        min-width="180"
      />
      <el-table-column
        :formatter="dateFormatter"
        align="center"
        :label="t('oa.task.createTime')"
        prop="createTime"
        min-width="180"
      />
      <el-table-column align="center" :label="t('common.operation')" min-width="200">
        <template #default="scope">
          <el-button
            v-hasPermi="['bpm:oa-task:update']"
            link
            type="primary"
            @click="handleEdit(scope.row)"
          >
            {{ t('common.edit') }}
          </el-button>
          <el-button
            v-hasPermi="['bpm:oa-task:delete']"
            link
            type="danger"
            @click="handleDelete(scope.row)"
          >
            {{ t('common.delete') }}
          </el-button>
        </template>
      </el-table-column>
    </el-table>
    <Pagination
      v-model:limit="queryParams.pageSize"
      v-model:page="queryParams.pageNo"
      :total="total"
      @pagination="getList"
    />
  </ContentWrap>
</template>
<script lang="ts" setup>
import { dateFormatter } from '@/utils/formatTime'
import * as TaskApi from '@/api/bpm/task'

defineOptions({ name: 'BpmOATask' })

const message = useMessage()
const router = useRouter()
const { t } = useI18n('bpm')

const loading = ref(true)
const total = ref(0)
const list = ref([])
const queryParams = reactive({
  pageNo: 1,
  pageSize: 10,
  status: undefined,
  priority: undefined,
  title: undefined,
  createTime: []
})
const queryFormRef = ref()

const getList = async () => {
  loading.value = true
  try {
    const data = await TaskApi.getTaskPage(queryParams)
    list.value = data.list
    total.value = data.total
  } finally {
    loading.value = false
  }
}

const handleQuery = () => {
  queryParams.pageNo = 1
  getList()
}

const resetQuery = () => {
  queryFormRef.value.resetFields()
  handleQuery()
}

const handleCreate = () => {
  router.push({ name: 'OATaskCreate' })
}

const handleEdit = (row: TaskApi.TaskVO) => {
  router.push({
    name: 'OATaskCreate',
    query: {
      id: row.id
    }
  })
}

const handleDelete = async (row: TaskApi.TaskVO) => {
  const confirm = await ElMessageBox.confirm(t('common.deleteConfirm'), t('common.deleteTitle'), {
    confirmButtonText: t('common.ok'),
    cancelButtonText: t('common.cancel'),
    type: 'warning'
  })
  if (confirm === 'confirm') {
    await TaskApi.deleteTask(row.id)
    message.success(t('common.deleteSuccess'))
    await getList()
  }
}

watch(
  () => router.currentRoute.value,
  () => {
    getList()
  }
)

onMounted(() => {
  getList()
})
</script>