package com.meession.etm.module.crm.dal.dataobject.marketing;

import com.baomidou.mybatisplus.annotation.*;
import com.meession.etm.framework.mybatis.core.dataobject.BaseDO;
import lombok.Data;
import lombok.EqualsAndHashCode;

@TableName("marketing_email_batch")
@Data
@EqualsAndHashCode(callSuper = true)
public class MarketingEmailBatchDO extends BaseDO {

    @TableId(type = IdType.INPUT)
    private Long id;

    private Long tenantId;

    private Long campaignId;

    private String campaignName;

    private Long templateId;

    private String templateName;

    private String emailList;

    private String subject;

    private String content;

    private Integer status;

    private String sendTime;

    private Integer totalCount;

    private Integer sendCount;

    private Integer successCount;

    private Integer failCount;

    private java.math.BigDecimal sendRate;

    private String remark;

    private String errorMessage;

}
