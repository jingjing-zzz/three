package com.meession.etm.module.bpm.service.oa.listener;

import com.meession.etm.module.bpm.api.event.BpmProcessInstanceStatusEvent;
import com.meession.etm.module.bpm.api.event.BpmProcessInstanceStatusEventListener;
import com.meession.etm.module.bpm.service.oa.BpmOACustomerVisitService;
import com.meession.etm.module.bpm.service.oa.BpmOACustomerVisitServiceImpl;
import org.springframework.stereotype.Component;

import jakarta.annotation.Resource;

@Component
public class BpmOACustomerVisitStatusListener extends BpmProcessInstanceStatusEventListener {

    @Resource
    private BpmOACustomerVisitService customerVisitService;

    @Override
    protected String getProcessDefinitionKey() {
        return BpmOACustomerVisitServiceImpl.PROCESS_KEY;
    }

    @Override
    protected void onEvent(BpmProcessInstanceStatusEvent event) {
        customerVisitService.updateCustomerVisitStatus(Long.parseLong(event.getBusinessKey()), event.getStatus());
    }

}