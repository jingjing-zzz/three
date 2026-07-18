package com.meession.etm.module.crm.service.marketing;

import com.meession.etm.framework.common.pojo.PageResult;
import com.meession.etm.module.crm.controller.admin.marketing.vo.approval.MarketingApprovalPageReqVO;
import com.meession.etm.module.crm.controller.admin.marketing.vo.approval.MarketingApprovalSaveReqVO;
import com.meession.etm.module.crm.dal.dataobject.marketing.MarketingApprovalDO;

public interface MarketingApprovalService {

    Long createApproval(MarketingApprovalSaveReqVO createReqVO);

    void updateApproval(MarketingApprovalSaveReqVO updateReqVO);

    void deleteApproval(Long id);

    MarketingApprovalDO getApproval(Long id);

    MarketingApprovalDO validateApproval(Long id);

    PageResult<MarketingApprovalDO> getApprovalPage(MarketingApprovalPageReqVO pageReqVO);

    void approve(Long id, Integer status, String remark);

}
