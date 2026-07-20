package com.meession.etm.module.crm.controller.admin.marketing.vo.email;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

/**
 * 营销邮件 - 快速发送请求 VO
 *
 * 用于"邮件发送"子菜单：用户手动输入邮箱或从客户中选择邮箱后直接发送，
 * 不需要预先创建邮件批次。发送成功后会自动生成一条已完成的邮件批次记录。
 *
 * @author 密讯
 */
@Data
public class MarketingEmailSendDirectReqVO {

    /**
     * 收件人邮箱列表
     */
    @NotEmpty(message = "收件人邮箱不能为空")
    private List<String> emails;

    /**
     * 邮件主题
     */
    @NotBlank(message = "主题不能为空")
    private String subject;

    /**
     * 邮件内容（支持 HTML）
     */
    @NotBlank(message = "内容不能为空")
    private String content;

    /**
     * 活动名称（可选，默认"快速发送"）
     */
    private String campaignName;

    /**
     * 模板名称（可选，默认"快速发送"）
     */
    private String templateName;

}
