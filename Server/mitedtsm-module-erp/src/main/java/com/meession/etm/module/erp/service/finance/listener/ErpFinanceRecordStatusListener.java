package com.meession.etm.module.erp.service.finance.listener;

import com.meession.etm.module.bpm.api.event.BpmProcessInstanceStatusEvent;
import com.meession.etm.module.bpm.api.event.BpmProcessInstanceStatusEventListener;
import com.meession.etm.module.erp.service.finance.ErpFinanceRecordService;
import com.meession.etm.module.erp.service.finance.ErpFinanceRecordServiceImpl;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

/**
 * 财务单据审批结果监听器。
 */
@Component
public class ErpFinanceRecordStatusListener extends BpmProcessInstanceStatusEventListener {

    @Resource
    private ErpFinanceRecordService financeRecordService;

    @Override
    protected String getProcessDefinitionKey() {
        return ErpFinanceRecordServiceImpl.REIMBURSEMENT_PROCESS_DEFINITION_KEY;
    }

    @Override
    protected void onEvent(BpmProcessInstanceStatusEvent event) {
        financeRecordService.updateFinanceRecordAuditStatus(Long.parseLong(event.getBusinessKey()), event.getStatus());
    }

    @Component
    public static class RefundStatusListener extends BpmProcessInstanceStatusEventListener {

        @Resource
        private ErpFinanceRecordService financeRecordService;

        @Override
        protected String getProcessDefinitionKey() {
            return ErpFinanceRecordServiceImpl.REFUND_PROCESS_DEFINITION_KEY;
        }

        @Override
        protected void onEvent(BpmProcessInstanceStatusEvent event) {
            financeRecordService.updateFinanceRecordAuditStatus(Long.parseLong(event.getBusinessKey()), event.getStatus());
        }

    }

}
