package com.meession.etm.module.crm.controller.admin.marketing.vo.care;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Data
public class MarketingCustomerCareSaveReqVO {

    private Long id;

    @NotBlank(message = "规则名称不能为空")
    private String name;

    @NotNull(message = "规则类型不能为空")
    private Integer type;

    @NotNull(message = "触发类型不能为空")
    private Integer triggerType;

    private String triggerCondition;

    @NotNull(message = "发送渠道不能为空")
    private Integer sendChannel;

    private Long templateId;

    @NotBlank(message = "内容不能为空")
    private String content;

    private String subject;

    private Integer status;

    private String remark;

}
