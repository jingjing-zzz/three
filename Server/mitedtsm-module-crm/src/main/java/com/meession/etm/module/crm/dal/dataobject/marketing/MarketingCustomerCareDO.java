package com.meession.etm.module.crm.dal.dataobject.marketing;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.meession.etm.framework.mybatis.core.dataobject.BaseDO;
import lombok.Data;
import lombok.EqualsAndHashCode;

@TableName("marketing_customer_care")
@Data
@EqualsAndHashCode(callSuper = true)
public class MarketingCustomerCareDO extends BaseDO {

    @TableId
    private Long id;

    private Long tenantId;

    private String name;

    private Integer type;

    private Integer triggerType;

    private String triggerCondition;

    private Integer sendChannel;

    private Long templateId;

    private String content;

    private String subject;

    private Integer status;

    private String remark;

}
