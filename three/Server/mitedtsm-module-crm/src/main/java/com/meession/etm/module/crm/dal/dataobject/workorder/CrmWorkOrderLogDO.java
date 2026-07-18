package com.meession.etm.module.crm.dal.dataobject.workorder;

import com.meession.etm.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

@TableName("crm_work_order_log")
@KeySequence("crm_work_order_log_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CrmWorkOrderLogDO extends BaseDO {

    @TableId
    private Long id;

    private Long workOrderId;

    private Integer statusBefore;

    private Integer statusAfter;

    private Long operatorId;

    private String operatorName;

    private String remark;

}