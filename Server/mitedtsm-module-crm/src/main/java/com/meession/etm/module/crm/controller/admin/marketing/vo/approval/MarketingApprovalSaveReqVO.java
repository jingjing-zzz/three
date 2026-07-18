package com.meession.etm.module.crm.controller.admin.marketing.vo.approval;

import lombok.Data;

import jakarta.validation.constraints.NotNull;

@Data
public class MarketingApprovalSaveReqVO {

    private Long id;

    private Long campaignId;

    private String campaignName;

    @NotNull(message = "审批类型不能为空")
    private Integer type;

    @NotNull(message = "目标数量不能为空")
    private Integer targetCount;

    private String contentPreview;

    private Long batchId;

    private Integer status;

    private String approver;

    private String approveRemark;

}
