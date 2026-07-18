package com.meession.etm.module.crm.service.marketing.impl;

import com.meession.etm.framework.common.exception.ServiceException;
import com.meession.etm.framework.common.pojo.PageResult;
import com.meession.etm.framework.common.util.object.BeanUtils;
import com.meession.etm.module.crm.controller.admin.marketing.vo.email.MarketingEmailBatchPageReqVO;
import com.meession.etm.module.crm.controller.admin.marketing.vo.email.MarketingEmailBatchSaveReqVO;
import com.meession.etm.module.crm.dal.dataobject.marketing.MarketingEmailBatchDO;
import com.meession.etm.module.crm.dal.mysql.marketing.MarketingEmailBatchMapper;
import com.meession.etm.module.crm.enums.ErrorCodeConstants;
import com.meession.etm.module.crm.service.marketing.MarketingEmailBatchService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MarketingEmailBatchServiceImpl implements MarketingEmailBatchService {

    @Resource
    private MarketingEmailBatchMapper emailBatchMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createEmailBatch(MarketingEmailBatchSaveReqVO createReqVO) {
        MarketingEmailBatchDO emailBatch = BeanUtils.toBean(createReqVO, MarketingEmailBatchDO.class);
        emailBatch.setStatus(createReqVO.getStatus() != null ? createReqVO.getStatus() : 1);
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

}
