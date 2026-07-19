package com.meession.etm.module.crm.service.marketing.impl;

import com.meession.etm.framework.common.exception.ServiceException;
import com.meession.etm.framework.common.pojo.PageResult;
import com.meession.etm.framework.common.util.object.BeanUtils;
import com.meession.etm.module.crm.controller.admin.marketing.vo.approval.MarketingApprovalPageReqVO;
import com.meession.etm.module.crm.controller.admin.marketing.vo.approval.MarketingApprovalSaveReqVO;
import com.meession.etm.module.crm.dal.dataobject.marketing.MarketingApprovalDO;
import com.meession.etm.module.crm.dal.mysql.marketing.MarketingApprovalMapper;
import com.meession.etm.module.crm.enums.ErrorCodeConstants;
import com.meession.etm.module.crm.service.marketing.MarketingApprovalService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
public class MarketingApprovalServiceImpl implements MarketingApprovalService {

    @Resource
    private MarketingApprovalMapper approvalMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createApproval(MarketingApprovalSaveReqVO createReqVO) {
        MarketingApprovalDO approval = BeanUtils.toBean(createReqVO, MarketingApprovalDO.class);
        approval.setStatus(createReqVO.getStatus() != null ? createReqVO.getStatus() : 0); // 默认待审批
        approvalMapper.insert(approval);
        return approval.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateApproval(MarketingApprovalSaveReqVO updateReqVO) {
        MarketingApprovalDO approval = validateApproval(updateReqVO.getId());
        BeanUtils.copyProperties(updateReqVO, approval);
        approvalMapper.updateById(approval);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteApproval(Long id) {
        MarketingApprovalDO approval = validateApproval(id);
        approvalMapper.deleteById(approval.getId());
    }

    @Override
    public MarketingApprovalDO getApproval(Long id) {
        return approvalMapper.selectById(id);
    }

    @Override
    public MarketingApprovalDO validateApproval(Long id) {
        MarketingApprovalDO approval = getApproval(id);
        if (approval == null) {
            throw new ServiceException(ErrorCodeConstants.MARKETING_APPROVAL_NOT_EXISTS);
        }
        return approval;
    }

    @Override
    public PageResult<MarketingApprovalDO> getApprovalPage(MarketingApprovalPageReqVO pageReqVO) {
        return approvalMapper.selectPage(pageReqVO);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void approve(Long id, Integer status, String remark) {
        MarketingApprovalDO approval = validateApproval(id);
        approval.setStatus(status);
        approval.setApproveRemark(remark);
        approval.setApproveTime(new Date());
        approvalMapper.updateById(approval);
    }

}
