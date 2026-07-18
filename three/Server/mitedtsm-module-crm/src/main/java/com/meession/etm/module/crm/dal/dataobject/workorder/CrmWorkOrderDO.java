package com.meession.etm.module.crm.dal.dataobject.workorder;

import com.meession.etm.framework.mybatis.core.dataobject.BaseDO;
import com.meession.etm.module.crm.dal.dataobject.customer.CrmCustomerDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.time.LocalDateTime;

@TableName("crm_work_order")
@KeySequence("crm_work_order_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CrmWorkOrderDO extends BaseDO {

    @TableId
    private Long id;

    private String title;

    private String content;

    private Integer type;

    private Integer priority;

    private Integer status;

    private Long customerId;

    private String customerName;

    private Long ownerUserId;

    private String ownerUserName;

    private LocalDateTime processTime;

    private LocalDateTime completeTime;

    private String result;

    private String rejectReason;

    private String creatorName;

}