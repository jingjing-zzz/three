package com.meession.etm.module.crm.controller.admin.marketing.vo.approval;

import lombok.Data;

import java.util.Date;

@Data
public class MarketingApprovalRespVO {

    private Long id;

    private Long campaignId;

    private String campaignName;

    private Integer approvalType;

    private Long batchId;

    private Integer batchType;

    private Integer status;

    private String approver;

    private String approveRemark;

    private Date approveTime;

    private Date createTime;

}
