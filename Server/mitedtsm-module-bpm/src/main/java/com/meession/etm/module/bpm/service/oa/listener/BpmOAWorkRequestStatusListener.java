package com.meession.etm.module.bpm.service.oa.listener;

import com.meession.etm.module.bpm.api.event.BpmProcessInstanceStatusEvent;
import com.meession.etm.module.bpm.api.event.BpmProcessInstanceStatusEventListener;
import com.meession.etm.module.bpm.service.oa.BpmOAWorkRequestService;
import com.meession.etm.module.bpm.service.oa.BpmOAWorkRequestServiceImpl;
import org.springframework.stereotype.Component;

import jakarta.annotation.Resource;

@Component
public class BpmOAWorkRequestStatusListener extends BpmProcessInstanceStatusEventListener {

    @Resource
    private BpmOAWorkRequestService workRequestService;

    @Override
    protected String getProcessDefinitionKey() {
        return BpmOAWorkRequestServiceImpl.PROCESS_KEY;
    }

    @Override
    protected void onEvent(BpmProcessInstanceStatusEvent event) {
        workRequestService.updateWorkRequestStatus(Long.parseLong(event.getBusinessKey()), event.getStatus());
    }

}