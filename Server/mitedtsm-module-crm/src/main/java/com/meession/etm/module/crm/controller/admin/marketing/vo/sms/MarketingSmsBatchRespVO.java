package com.meession.etm.module.crm.controller.admin.marketing.vo.sms;

import lombok.Data;

import java.util.Date;

@Data
public class MarketingSmsBatchRespVO {

    private Long id;

    private Long campaignId;

    private String campaignName;

    private Long templateId;

    private String templateName;

    private String content;

    private Integer status;

    private Integer totalCount;

    private Integer sendCount;

    private Integer successCount;

    private Integer failCount;

    private String sendTime;

    private String errorMessage;

    private Date createTime;

}
