package com.meession.etm.module.crm.controller.admin.marketing.vo.record;

import lombok.Data;

import java.util.Date;

@Data
public class MarketingSendRecordRespVO {

    private Long id;

    private Long batchId;

    private String campaignName;

    private String batchName;

    private Integer type;

    private String target;

    private String content;

    private Integer status;

    private String errorMessage;

    private Date sendTime;

    private Date createTime;

}
