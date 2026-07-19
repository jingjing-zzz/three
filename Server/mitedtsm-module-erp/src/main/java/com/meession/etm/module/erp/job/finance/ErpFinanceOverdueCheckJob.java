package com.meession.etm.module.erp.job.finance;

import com.meession.etm.framework.quartz.core.handler.JobHandler;
import com.meession.etm.module.erp.service.finance.ErpFinanceRecordService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

/**
 * ERP 财务单据逾期检测任务。
 */
@Component
public class ErpFinanceOverdueCheckJob implements JobHandler {

    @Resource
    private ErpFinanceRecordService financeRecordService;

    @Override
    public String execute(String param) {
        int count = financeRecordService.markOverdueRecords();
        return String.format("标记 %d 条逾期财务单据", count);
    }

}
