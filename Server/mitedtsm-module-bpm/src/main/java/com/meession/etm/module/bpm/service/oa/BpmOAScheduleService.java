package com.meession.etm.module.bpm.service.oa;

import com.meession.etm.framework.common.pojo.PageResult;
import com.meession.etm.module.bpm.controller.admin.oa.vo.BpmOAScheduleCreateReqVO;
import com.meession.etm.module.bpm.controller.admin.oa.vo.BpmOASchedulePageReqVO;
import com.meession.etm.module.bpm.dal.dataobject.oa.BpmOAScheduleDO;

import jakarta.validation.Valid;

public interface BpmOAScheduleService {

    Long createSchedule(Long userId, @Valid BpmOAScheduleCreateReqVO createReqVO);

    void updateSchedule(Long id, BpmOAScheduleDO updateReqVO);

    void deleteSchedule(Long id);

    BpmOAScheduleDO getSchedule(Long id);

    PageResult<BpmOAScheduleDO> getSchedulePage(Long userId, BpmOASchedulePageReqVO pageReqVO);

}