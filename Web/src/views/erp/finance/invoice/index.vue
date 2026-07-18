<template>
  <FinanceRecordPage :type="10" title="发票" />
</template>

<script setup lang="ts">
import FinanceRecordPage from '../record/FinanceRecordPage.vue'

defineOptions({ name: 'ErpFinanceInvoice' })
</script>
