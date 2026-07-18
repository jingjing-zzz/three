package com.meession.etm.module.bpm.service.oa;

import com.meession.etm.framework.common.pojo.PageResult;
import com.meession.etm.module.bpm.controller.admin.oa.vo.BpmOATaskCreateReqVO;
import com.meession.etm.module.bpm.controller.admin.oa.vo.BpmOATaskPageReqVO;
import com.meession.etm.module.bpm.dal.dataobject.oa.BpmOATaskDO;

import jakarta.validation.Valid;

public interface BpmOATaskService {

    Long createTask(Long userId, @Valid BpmOATaskCreateReqVO createReqVO);

    void updateTask(Long id, BpmOATaskDO updateReqVO);

    void deleteTask(Long id);

    BpmOATaskDO getTask(Long id);

    PageResult<BpmOATaskDO> getTaskPage(Long userId, BpmOATaskPageReqVO pageReqVO);

}