package com.meession.etm.module.crm.service.marketing.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.meession.etm.framework.common.exception.ServiceException;
import com.meession.etm.framework.common.pojo.PageResult;
import com.meession.etm.framework.common.util.object.BeanUtils;
import com.meession.etm.module.crm.controller.admin.marketing.vo.sms.MarketingSmsBatchPageReqVO;
import com.meession.etm.module.crm.controller.admin.marketing.vo.sms.MarketingSmsBatchSaveReqVO;
import com.meession.etm.module.crm.dal.dataobject.marketing.MarketingSendRecordDO;
import com.meession.etm.module.crm.dal.dataobject.marketing.MarketingSmsBatchDO;
import com.meession.etm.module.crm.dal.mysql.marketing.MarketingSendRecordMapper;
import com.meession.etm.module.crm.dal.mysql.marketing.MarketingSmsBatchMapper;
import com.meession.etm.module.crm.enums.ErrorCodeConstants;
import com.meession.etm.module.crm.service.marketing.MarketingSmsBatchService;
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
public class MarketingSmsBatchServiceImpl implements MarketingSmsBatchService {

    @Resource
    private MarketingSmsBatchMapper smsBatchMapper;

    @Resource
    private MarketingSendRecordMapper sendRecordMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createSmsBatch(MarketingSmsBatchSaveReqVO createReqVO) {
        MarketingSmsBatchDO smsBatch = BeanUtils.toBean(createReqVO, MarketingSmsBatchDO.class);
        smsBatch.setStatus(createReqVO.getStatus() != null ? createReqVO.getStatus() : 0);
        smsBatch.setTotalCount(0);
        smsBatch.setSendCount(0);
        smsBatch.setSuccessCount(0);
        smsBatch.setFailCount(0);
        smsBatchMapper.insert(smsBatch);
        return smsBatch.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateSmsBatch(MarketingSmsBatchSaveReqVO updateReqVO) {
        MarketingSmsBatchDO smsBatch = validateSmsBatch(updateReqVO.getId());
        BeanUtils.copyProperties(updateReqVO, smsBatch);
        smsBatchMapper.updateById(smsBatch);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteSmsBatch(Long id) {
        MarketingSmsBatchDO smsBatch = validateSmsBatch(id);
        smsBatchMapper.deleteById(smsBatch.getId());
    }

    @Override
    public MarketingSmsBatchDO getSmsBatch(Long id) {
        return smsBatchMapper.selectById(id);
    }

    @Override
    public MarketingSmsBatchDO validateSmsBatch(Long id) {
        MarketingSmsBatchDO smsBatch = getSmsBatch(id);
        if (smsBatch == null) {
            throw new ServiceException(ErrorCodeConstants.MARKETING_SMS_BATCH_NOT_EXISTS);
        }
        return smsBatch;
    }

    @Override
    public PageResult<MarketingSmsBatchDO> getSmsBatchPage(MarketingSmsBatchPageReqVO pageReqVO) {
        return smsBatchMapper.selectPage(pageReqVO);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void sendSmsBatch(Long id) {
        // 1. 验证批次存在
        MarketingSmsBatchDO smsBatch = validateSmsBatch(id);
        // 2. 验证状态为待发送
        if (smsBatch.getStatus() != 0) {
            throw new ServiceException(ErrorCodeConstants.MARKETING_SMS_BATCH_CANNOT_SEND);
        }
        // 3. 更新状态为发送中
        smsBatch.setStatus(1);
        smsBatchMapper.updateById(smsBatch);
        // 4. 解析手机号列表
        List<String> phones = parseList(smsBatch.getPhoneList());
        int totalCount = phones.size();
        int successCount = 0;
        int failCount = 0;
        // 5. 逐条生成发送记录（模拟发送，90%成功）
        Random random = new Random();
        Date now = new Date();
        for (String phone : phones) {
            MarketingSendRecordDO record = new MarketingSendRecordDO();
            record.setBatchId(id);
            record.setCampaignName(smsBatch.getCampaignName());
            record.setBatchName(smsBatch.getTemplateName());
            record.setType(1); // 短信
            record.setTarget(phone);
            record.setContent(smsBatch.getContent());
            record.setSendTime(now);
            boolean isSuccess = random.nextDouble() < 0.9;
            if (isSuccess) {
                record.setStatus(1); // 成功
                successCount++;
            } else {
                record.setStatus(2); // 失败
                record.setErrorMessage("发送超时");
                failCount++;
            }
            sendRecordMapper.insert(record);
        }
        // 6. 更新批次统计数据
        smsBatch.setTotalCount(totalCount);
        smsBatch.setSendCount(totalCount);
        smsBatch.setSuccessCount(successCount);
        smsBatch.setFailCount(failCount);
        smsBatch.setSendRate(totalCount > 0 ? BigDecimal.valueOf(successCount * 100.0 / totalCount).setScale(2, RoundingMode.HALF_UP) : BigDecimal.ZERO);
        smsBatch.setStatus(2); // 已完成
        smsBatch.setSendTime(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        smsBatchMapper.updateById(smsBatch);
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
