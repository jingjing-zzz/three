package com.meession.etm.module.crm.service.order.listener;

import com.meession.etm.module.bpm.api.event.BpmProcessInstanceStatusEvent;
import com.meession.etm.module.bpm.api.event.BpmProcessInstanceStatusEventListener;
import com.meession.etm.module.crm.service.order.CrmOrderService;
import com.meession.etm.module.crm.service.order.CrmOrderServiceImpl;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

@Component
public class CrmOrderApprovalStatusListener extends BpmProcessInstanceStatusEventListener {

    @Resource
    private CrmOrderService orderService;

    @Override
    protected String getProcessDefinitionKey() {
        return CrmOrderServiceImpl.PROCESS_KEY;
    }

    @Override
    protected void onEvent(BpmProcessInstanceStatusEvent event) {
        orderService.updateOrderStatus(Long.parseLong(event.getBusinessKey()), event.getStatus());
    }

}