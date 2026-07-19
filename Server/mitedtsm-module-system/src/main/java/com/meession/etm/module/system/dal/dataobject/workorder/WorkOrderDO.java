package com.meession.etm.module.system.dal.dataobject.workorder;

import com.meession.etm.framework.mybatis.core.dataobject.BaseDO;
import com.meession.etm.module.system.enums.workorder.WorkOrderPriorityEnum;
import com.meession.etm.module.system.enums.workorder.WorkOrderStatusEnum;
import com.meession.etm.module.system.enums.workorder.WorkOrderTypeEnum;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@TableName("work_order")
@KeySequence("work_order_seq")
@Data
@EqualsAndHashCode(callSuper = true)
public class WorkOrderDO extends BaseDO {

    private Long id;

    private String orderNo;

    private String title;

    private Integer type;

    private Integer priority;

    private Integer status;

    private String content;

    private Long reporterId;

    private String reporterName;

    private Long assigneeId;

    private String assigneeName;

    private Long deptId;

    private String deptName;

    @TableField("customer_id")
    private Long customerId;

    @TableField("customer_name")
    private String customerName;

    private String relatedModule;

    private Long relatedId;

    private String handleNote;

    private LocalDateTime handleTime;

    private LocalDateTime closeTime;

    private String rejectReason;

    private LocalDateTime rejectTime;

    private LocalDateTime expectedFinishTime;

    private LocalDateTime actualFinishTime;

}
