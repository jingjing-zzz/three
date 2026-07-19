package com.meession.etm.module.bpm.service.oa;

import com.meession.etm.framework.common.pojo.PageResult;
import com.meession.etm.module.bpm.controller.admin.oa.vo.BpmOALoanCreateReqVO;
import com.meession.etm.module.bpm.controller.admin.oa.vo.BpmOALoanPageReqVO;
import com.meession.etm.module.bpm.dal.dataobject.oa.BpmOALoanDO;

import jakarta.validation.Valid;

public interface BpmOALoanService {

    Long createLoan(Long userId, @Valid BpmOALoanCreateReqVO createReqVO);

    void updateLoanStatus(Long id, Integer status);

    BpmOALoanDO getLoan(Long id);

    PageResult<BpmOALoanDO> getLoanPage(Long userId, BpmOALoanPageReqVO pageReqVO);

}