package com.meession.etm.module.crm.service.marketing.impl;

import com.meession.etm.framework.common.exception.ServiceException;
import com.meession.etm.framework.common.pojo.PageResult;
import com.meession.etm.framework.common.util.object.BeanUtils;
import com.meession.etm.module.crm.controller.admin.marketing.vo.sms.MarketingSmsBatchPageReqVO;
import com.meession.etm.module.crm.controller.admin.marketing.vo.sms.MarketingSmsBatchSaveReqVO;
import com.meession.etm.module.crm.dal.dataobject.marketing.MarketingSmsBatchDO;
import com.meession.etm.module.crm.dal.mysql.marketing.MarketingSmsBatchMapper;
import com.meession.etm.module.crm.enums.ErrorCodeConstants;
import com.meession.etm.module.crm.service.marketing.MarketingSmsBatchService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MarketingSmsBatchServiceImpl implements MarketingSmsBatchService {

    @Resource
    private MarketingSmsBatchMapper smsBatchMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createSmsBatch(MarketingSmsBatchSaveReqVO createReqVO) {
        MarketingSmsBatchDO smsBatch = BeanUtils.toBean(createReqVO, MarketingSmsBatchDO.class);
        smsBatch.setStatus(createReqVO.getStatus() != null ? createReqVO.getStatus() : 1);
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

}
