package com.meession.etm.module.crm.service.marketing.impl;

import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.meession.etm.framework.common.exception.ServiceException;
import com.meession.etm.framework.common.pojo.PageResult;
import com.meession.etm.framework.common.util.object.BeanUtils;
import com.meession.etm.module.crm.controller.admin.marketing.vo.email.MarketingEmailBatchPageReqVO;
import com.meession.etm.module.crm.controller.admin.marketing.vo.email.MarketingEmailBatchSaveReqVO;
import com.meession.etm.module.crm.controller.admin.marketing.vo.email.MarketingEmailSendDirectReqVO;
import com.meession.etm.module.crm.controller.admin.marketing.vo.email.MarketingEmailSendDirectRespVO;
import com.meession.etm.module.crm.dal.dataobject.marketing.MarketingEmailBatchDO;
import com.meession.etm.module.crm.dal.dataobject.marketing.MarketingSendRecordDO;
import com.meession.etm.module.crm.dal.mysql.marketing.MarketingEmailBatchMapper;
import com.meession.etm.module.crm.dal.mysql.marketing.MarketingSendRecordMapper;
import com.meession.etm.module.crm.enums.ErrorCodeConstants;
import com.meession.etm.module.crm.service.marketing.MarketingEmailBatchService;
import jakarta.annotation.Resource;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class MarketingEmailBatchServiceImpl implements MarketingEmailBatchService {

    @Resource
    private MarketingEmailBatchMapper emailBatchMapper;

    @Resource
    private MarketingSendRecordMapper sendRecordMapper;

    @Resource
    private JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createEmailBatch(MarketingEmailBatchSaveReqVO createReqVO) {
        MarketingEmailBatchDO emailBatch = BeanUtils.toBean(createReqVO, MarketingEmailBatchDO.class);
        emailBatch.setStatus(createReqVO.getStatus() != null ? createReqVO.getStatus() : 0);
        emailBatch.setTotalCount(0);
        emailBatch.setSendCount(0);
        emailBatch.setSuccessCount(0);
        emailBatch.setFailCount(0);
        emailBatchMapper.insert(emailBatch);
        return emailBatch.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateEmailBatch(MarketingEmailBatchSaveReqVO updateReqVO) {
        MarketingEmailBatchDO emailBatch = validateEmailBatch(updateReqVO.getId());
        BeanUtils.copyProperties(updateReqVO, emailBatch);
        emailBatchMapper.updateById(emailBatch);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteEmailBatch(Long id) {
        MarketingEmailBatchDO emailBatch = validateEmailBatch(id);
        emailBatchMapper.deleteById(emailBatch.getId());
    }

    @Override
    public MarketingEmailBatchDO getEmailBatch(Long id) {
        return emailBatchMapper.selectById(id);
    }

    @Override
    public MarketingEmailBatchDO validateEmailBatch(Long id) {
        MarketingEmailBatchDO emailBatch = getEmailBatch(id);
        if (emailBatch == null) {
            throw new ServiceException(ErrorCodeConstants.MARKETING_EMAIL_BATCH_NOT_EXISTS);
        }
        return emailBatch;
    }

    @Override
    public PageResult<MarketingEmailBatchDO> getEmailBatchPage(MarketingEmailBatchPageReqVO pageReqVO) {
        return emailBatchMapper.selectPage(pageReqVO);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void sendEmailBatch(Long id) {
        MarketingEmailBatchDO emailBatch = validateEmailBatch(id);
        if (emailBatch.getStatus() != 0) {
            throw new ServiceException(ErrorCodeConstants.MARKETING_EMAIL_BATCH_CANNOT_SEND);
        }
        emailBatch.setStatus(1);
        emailBatchMapper.updateById(emailBatch);
        List<String> emails = parseList(emailBatch.getEmailList());
        int totalCount = emails.size();
        int successCount = 0;
        int failCount = 0;
        Date now = new Date();
        for (String email : emails) {
            MarketingSendRecordDO record = new MarketingSendRecordDO();
            record.setBatchId(id);
            record.setCampaignName(emailBatch.getCampaignName());
            record.setBatchName(emailBatch.getTemplateName());
            record.setType(2); // 邮件
            record.setTarget(email);
            record.setContent(emailBatch.getContent());
            record.setSendTime(now);
            try {
                sendSingleEmail(email, emailBatch.getSubject(), emailBatch.getContent());
                record.setStatus(1); // 成功
                successCount++;
            } catch (Exception e) {
                log.error("[sendEmailBatch][发送邮件失败，收件人({})]", email, e);
                record.setStatus(2); // 失败
                record.setErrorMessage(StrUtil.sub(e.getMessage(), 0, 500));
                failCount++;
            }
            sendRecordMapper.insert(record);
        }
        emailBatch.setTotalCount(totalCount);
        emailBatch.setSendCount(totalCount);
        emailBatch.setSuccessCount(successCount);
        emailBatch.setFailCount(failCount);
        emailBatch.setSendRate(totalCount > 0 ? BigDecimal.valueOf(successCount * 100.0 / totalCount).setScale(2, RoundingMode.HALF_UP) : BigDecimal.ZERO);
        emailBatch.setStatus(2);
        emailBatch.setSendTime(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        emailBatchMapper.updateById(emailBatch);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public MarketingEmailSendDirectRespVO sendDirectEmail(MarketingEmailSendDirectReqVO reqVO) {
        List<String> emails = reqVO.getEmails();
        // 创建一条已完成的邮件批次记录（便于在"邮件群发批次"列表中追溯）
        MarketingEmailBatchDO emailBatch = new MarketingEmailBatchDO();
        emailBatch.setCampaignId(0L); // 快速发送无关联活动
        emailBatch.setCampaignName(StrUtil.nullToDefault(reqVO.getCampaignName(), "快速发送"));
        emailBatch.setTemplateId(0L); // 快速发送无关联模板（database NOT NULL，用 0 表示无）
        emailBatch.setTemplateName(StrUtil.nullToDefault(reqVO.getTemplateName(), "快速发送"));
        emailBatch.setSubject(reqVO.getSubject());
        emailBatch.setContent(reqVO.getContent());
        try {
            emailBatch.setEmailList(new ObjectMapper().writeValueAsString(emails));
        } catch (Exception ignored) {
            emailBatch.setEmailList("[]");
        }
        emailBatch.setStatus(2); // 直接标记为已完成
        emailBatch.setTotalCount(emails.size());
        emailBatch.setSendCount(emails.size());
        emailBatch.setSuccessCount(0);
        emailBatch.setFailCount(0);
        emailBatch.setSendTime(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        emailBatchMapper.insert(emailBatch);
        Long batchId = emailBatch.getId();

        // 逐个发送并记录
        int successCount = 0;
        int failCount = 0;
        List<String> failedEmails = new ArrayList<>();
        Date now = new Date();
        for (String email : emails) {
            MarketingSendRecordDO record = new MarketingSendRecordDO();
            record.setBatchId(batchId);
            record.setCampaignName(emailBatch.getCampaignName());
            record.setBatchName(emailBatch.getTemplateName());
            record.setType(2); // 邮件
            record.setTarget(email);
            record.setContent(reqVO.getContent());
            record.setSendTime(now);
            try {
                sendSingleEmail(email, reqVO.getSubject(), reqVO.getContent());
                record.setStatus(1);
                successCount++;
            } catch (Exception e) {
                log.error("[sendDirectEmail][发送邮件失败，收件人({})]", email, e);
                record.setStatus(2);
                record.setErrorMessage(StrUtil.sub(e.getMessage(), 0, 500));
                failCount++;
                failedEmails.add(email);
            }
            sendRecordMapper.insert(record);
        }

        // 更新批次统计
        emailBatch.setSuccessCount(successCount);
        emailBatch.setFailCount(failCount);
        emailBatch.setSendRate(emails.size() > 0
                ? BigDecimal.valueOf(successCount * 100.0 / emails.size()).setScale(2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO);
        emailBatchMapper.updateById(emailBatch);

        MarketingEmailSendDirectRespVO respVO = new MarketingEmailSendDirectRespVO();
        respVO.setBatchId(batchId);
        respVO.setTotalCount(emails.size());
        respVO.setSuccessCount(successCount);
        respVO.setFailCount(failCount);
        respVO.setFailedEmails(failedEmails);
        return respVO;
    }

    /**
     * 真实发送单封邮件
     *
     * @param toEmail  收件人邮箱
     * @param subject  邮件主题
     * @param content  邮件内容（支持 HTML）
     */
    private void sendSingleEmail(String toEmail, String subject, String content) {
        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, false, "UTF-8");
            helper.setFrom(fromEmail);
            helper.setTo(toEmail);
            helper.setSubject(StrUtil.nullToDefault(subject, "营销邮件"));
            helper.setText(content, true); // true 表示内容为 HTML
            javaMailSender.send(mimeMessage);
        } catch (MessagingException e) {
            throw new RuntimeException("邮件构造失败: " + e.getMessage(), e);
        }
    }

    private List<String> parseList(String json) {
        if (json == null || json.isEmpty()) {
            return List.of();
        }
        try {
            return new ObjectMapper().readValue(json, new TypeReference<List<String>>() {});
        } catch (Exception e) {
            return List.of();
        }
    }

}
