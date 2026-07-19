package com.meession.etm.module.crm.service.order.listener;

import com.meession.etm.module.bpm.api.event.BpmProcessInstanceStatusEvent;
import com.meession.etm.module.bpm.api.event.BpmProcessInstanceStatusEventListener;
import com.meession.etm.module.bpm.enums.task.BpmProcessInstanceStatusEnum;
import com.meession.etm.module.crm.enums.order.CrmOrderStatusEnum;
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
        Integer orderStatus = convertToOrderStatus(event.getStatus());
        if (orderStatus == null) {
            return;
        }
        orderService.updateOrderStatus(Long.parseLong(event.getBusinessKey()), orderStatus);
    }

    /** 将 BPM 审批结果转换为订单域状态，两个域的状态编码并不相同。 */
    private Integer convertToOrderStatus(Integer processStatus) {
        if (BpmProcessInstanceStatusEnum.APPROVE.getStatus().equals(processStatus)) {
            return CrmOrderStatusEnum.APPROVED.getStatus();
        }
        if (BpmProcessInstanceStatusEnum.REJECT.getStatus().equals(processStatus)) {
            return CrmOrderStatusEnum.REJECTED.getStatus();
        }
        if (BpmProcessInstanceStatusEnum.CANCEL.getStatus().equals(processStatus)) {
            return CrmOrderStatusEnum.CANCELLED.getStatus();
        }
        return null;
    }

}
