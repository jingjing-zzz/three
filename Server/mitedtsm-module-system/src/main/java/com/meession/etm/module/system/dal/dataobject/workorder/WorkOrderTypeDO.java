package com.meession.etm.module.system.dal.dataobject.workorder;

import com.meession.etm.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@TableName("work_order_type")
@KeySequence("work_order_type_seq")
@Data
@EqualsAndHashCode(callSuper = true)
public class WorkOrderTypeDO extends BaseDO {

    private Long id;

    private String code;

    private String name;

    private String description;

    private Integer sort;

    private Integer status;

}
