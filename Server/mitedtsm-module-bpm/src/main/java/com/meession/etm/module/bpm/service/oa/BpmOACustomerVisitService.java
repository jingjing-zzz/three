package com.meession.etm.module.bpm.service.oa;

import com.meession.etm.framework.common.pojo.PageResult;
import com.meession.etm.module.bpm.controller.admin.oa.vo.BpmOACustomerVisitCreateReqVO;
import com.meession.etm.module.bpm.controller.admin.oa.vo.BpmOACustomerVisitPageReqVO;
import com.meession.etm.module.bpm.dal.dataobject.oa.BpmOACustomerVisitDO;

import jakarta.validation.Valid;

public interface BpmOACustomerVisitService {

    Long createCustomerVisit(Long userId, @Valid BpmOACustomerVisitCreateReqVO createReqVO);

    void updateCustomerVisitStatus(Long id, Integer status);

    BpmOACustomerVisitDO getCustomerVisit(Long id);

    PageResult<BpmOACustomerVisitDO> getCustomerVisitPage(Long userId, BpmOACustomerVisitPageReqVO pageReqVO);

}