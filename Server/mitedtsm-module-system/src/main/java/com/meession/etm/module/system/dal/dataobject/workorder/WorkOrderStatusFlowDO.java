package com.meession.etm.module.system.dal.dataobject.workorder;

import com.meession.etm.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@TableName("work_order_status_flow")
@KeySequence("work_order_status_flow_seq")
@Data
@EqualsAndHashCode(callSuper = true)
public class WorkOrderStatusFlowDO extends BaseDO {

    private Long id;

    private Long orderId;

    private String orderNo;

    private Integer fromStatus;

    private Integer toStatus;

    private Long operatorId;

    private String operatorName;

    private String remark;

}
