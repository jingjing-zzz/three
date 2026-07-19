package com.meession.etm.module.crm.job.receivable;

import com.meession.etm.framework.quartz.core.handler.JobHandler;
import com.meession.etm.module.crm.service.receivable.CrmReceivablePlanService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

/**
 * CRM 回款计划逾期检测任务。
 */
@Component
public class CrmReceivablePlanOverdueCheckJob implements JobHandler {

    @Resource
    private CrmReceivablePlanService receivablePlanService;

    @Override
    public String execute(String param) {
        int count = receivablePlanService.markOverdueReceivablePlans();
        return String.format("标记 %d 条逾期回款计划", count);
    }

}
