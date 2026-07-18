<template>
  <FinanceRecordPage :type="20" title="报销" />
</template>

<script setup lang="ts">
import FinanceRecordPage from '../record/FinanceRecordPage.vue'

defineOptions({ name: 'ErpFinanceReimbursement' })
</script>
