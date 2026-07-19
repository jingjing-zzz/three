package com.meession.etm.module.bpm.service.oa;

import com.meession.etm.framework.common.pojo.PageResult;
import com.meession.etm.module.bpm.controller.admin.oa.vo.BpmOAWorkRequestCreateReqVO;
import com.meession.etm.module.bpm.controller.admin.oa.vo.BpmOAWorkRequestPageReqVO;
import com.meession.etm.module.bpm.dal.dataobject.oa.BpmOAWorkRequestDO;

import jakarta.validation.Valid;

public interface BpmOAWorkRequestService {

    Long createWorkRequest(Long userId, @Valid BpmOAWorkRequestCreateReqVO createReqVO);

    void updateWorkRequestStatus(Long id, Integer status);

    BpmOAWorkRequestDO getWorkRequest(Long id);

    PageResult<BpmOAWorkRequestDO> getWorkRequestPage(Long userId, BpmOAWorkRequestPageReqVO pageReqVO);

}