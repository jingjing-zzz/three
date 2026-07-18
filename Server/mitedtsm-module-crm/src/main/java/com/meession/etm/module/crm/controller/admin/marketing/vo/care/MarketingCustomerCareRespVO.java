package com.meession.etm.module.crm.controller.admin.marketing.vo.care;

import lombok.Data;

import java.util.Date;

@Data
public class MarketingCustomerCareRespVO {

    private Long id;

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

    private Date createTime;

}
