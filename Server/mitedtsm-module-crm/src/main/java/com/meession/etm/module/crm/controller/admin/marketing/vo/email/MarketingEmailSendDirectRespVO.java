package com.meession.etm.module.crm.controller.admin.marketing.vo.email;

import lombok.Data;

import java.util.List;

/**
 * 营销邮件 - 快速发送结果 VO
 *
 * @author 密讯
 */
@Data
public class MarketingEmailSendDirectRespVO {

    /**
     * 生成的邮件批次 ID
     */
    private Long batchId;

    /**
     * 总数
     */
    private Integer totalCount;

    /**
     * 成功数
     */
    private Integer successCount;

    /**
     * 失败数
     */
    private Integer failCount;

    /**
     * 失败的邮箱列表
     */
    private List<String> failedEmails;

}
