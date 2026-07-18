package com.meession.etm.module.crm.controller.admin.marketing.vo.sms;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Data
public class MarketingSmsBatchSaveReqVO {

    private Long id;

    private Long campaignId;

    private String campaignName;

    @NotNull(message = "模板ID不能为空")
    private Long templateId;

    private String templateName;

    @NotBlank(message = "内容不能为空")
    private String content;

    private Integer status;

    private String sendTime;

}
