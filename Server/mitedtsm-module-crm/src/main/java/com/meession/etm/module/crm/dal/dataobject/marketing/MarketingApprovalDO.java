package com.meession.etm.module.crm.dal.dataobject.marketing;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.meession.etm.framework.mybatis.core.dataobject.BaseDO;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

@TableName("marketing_approval")
@Data
@EqualsAndHashCode(callSuper = true)
public class MarketingApprovalDO extends BaseDO {

    @TableId
    private Long id;

    private Long tenantId;

    private Long campaignId;

    private String campaignName;

    private Integer type;

    private Integer targetCount;

    private String contentPreview;

    private Long batchId;

    private Integer status;

    private String processInstanceId;

    private String approver;

    private String approveRemark;

    private Date approveTime;

}
