package com.meession.etm.module.crm.service.marketing.impl;

import com.meession.etm.framework.common.exception.ServiceException;
import com.meession.etm.framework.common.pojo.PageResult;
import com.meession.etm.framework.common.util.object.BeanUtils;
import com.meession.etm.module.crm.controller.admin.marketing.vo.care.MarketingCustomerCarePageReqVO;
import com.meession.etm.module.crm.controller.admin.marketing.vo.care.MarketingCustomerCareSaveReqVO;
import com.meession.etm.module.crm.dal.dataobject.marketing.MarketingCustomerCareDO;
import com.meession.etm.module.crm.dal.mysql.marketing.MarketingCustomerCareMapper;
import com.meession.etm.module.crm.enums.ErrorCodeConstants;
import com.meession.etm.module.crm.service.marketing.MarketingCustomerCareService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MarketingCustomerCareServiceImpl implements MarketingCustomerCareService {

    @Resource
    private MarketingCustomerCareMapper customerCareMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createCustomerCare(MarketingCustomerCareSaveReqVO createReqVO) {
        MarketingCustomerCareDO customerCare = BeanUtils.toBean(createReqVO, MarketingCustomerCareDO.class);
        customerCare.setStatus(createReqVO.getStatus() != null ? createReqVO.getStatus() : 1);
        customerCareMapper.insert(customerCare);
        return customerCare.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateCustomerCare(MarketingCustomerCareSaveReqVO updateReqVO) {
        MarketingCustomerCareDO customerCare = validateCustomerCare(updateReqVO.getId());
        BeanUtils.copyProperties(updateReqVO, customerCare);
        customerCareMapper.updateById(customerCare);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteCustomerCare(Long id) {
        MarketingCustomerCareDO customerCare = validateCustomerCare(id);
        customerCareMapper.deleteById(customerCare.getId());
    }

    @Override
    public MarketingCustomerCareDO getCustomerCare(Long id) {
        return customerCareMapper.selectById(id);
    }

    @Override
    public MarketingCustomerCareDO validateCustomerCare(Long id) {
        MarketingCustomerCareDO customerCare = getCustomerCare(id);
        if (customerCare == null) {
            throw new ServiceException(ErrorCodeConstants.MARKETING_CUSTOMER_CARE_NOT_EXISTS);
        }
        return customerCare;
    }

    @Override
    public PageResult<MarketingCustomerCareDO> getCustomerCarePage(MarketingCustomerCarePageReqVO pageReqVO) {
        return customerCareMapper.selectPage(pageReqVO);
    }

}
