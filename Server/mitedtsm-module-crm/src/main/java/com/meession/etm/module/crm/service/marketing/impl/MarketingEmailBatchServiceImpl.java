package com.meession.etm.module.crm.service.marketing.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.meession.etm.framework.common.exception.ServiceException;
import com.meession.etm.framework.common.pojo.PageResult;
import com.meession.etm.framework.common.util.object.BeanUtils;
import com.meession.etm.module.crm.controller.admin.marketing.vo.email.MarketingEmailBatchPageReqVO;
import com.meession.etm.module.crm.controller.admin.marketing.vo.email.MarketingEmailBatchSaveReqVO;
import com.meession.etm.module.crm.dal.dataobject.marketing.MarketingEmailBatchDO;
import com.meession.etm.module.crm.dal.dataobject.marketing.MarketingSendRecordDO;
import com.meession.etm.module.crm.dal.mysql.marketing.MarketingEmailBatchMapper;
import com.meession.etm.module.crm.dal.mysql.marketing.MarketingSendRecordMapper;
import com.meession.etm.module.crm.enums.ErrorCodeConstants;
import com.meession.etm.module.crm.service.marketing.MarketingEmailBatchService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Random;

@Service
public class MarketingEmailBatchServiceImpl implements MarketingEmailBatchService {

    @Resource
    private MarketingEmailBatchMapper emailBatchMapper;

    @Resource
    private MarketingSendRecordMapper sendRecordMapper;

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
        Random random = new Random();
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
            boolean isSuccess = random.nextDouble() < 0.9;
            if (isSuccess) {
                record.setStatus(3);
                successCount++;
            } else {
                record.setStatus(4);
                record.setErrorMessage("邮箱地址无效");
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
