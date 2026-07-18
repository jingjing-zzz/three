package com.meession.etm.module.bpm.service.oa;

import com.meession.etm.framework.common.pojo.PageResult;
import com.meession.etm.framework.common.util.object.BeanUtils;
import com.meession.etm.module.bpm.api.task.BpmProcessInstanceApi;
import com.meession.etm.module.bpm.api.task.dto.BpmProcessInstanceCreateReqDTO;
import com.meession.etm.module.bpm.controller.admin.oa.vo.BpmOAWorkRequestCreateReqVO;
import com.meession.etm.module.bpm.controller.admin.oa.vo.BpmOAWorkRequestPageReqVO;
import com.meession.etm.module.bpm.dal.dataobject.oa.BpmOAWorkRequestDO;
import com.meession.etm.module.bpm.dal.mysql.oa.BpmOAWorkRequestMapper;
import com.meession.etm.module.bpm.enums.task.BpmTaskStatusEnum;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.HashMap;
import java.util.Map;

@Service
@Validated
public class BpmOAWorkRequestServiceImpl implements BpmOAWorkRequestService {

    public static final String PROCESS_KEY = "oa_work_request";

    @Resource
    private BpmOAWorkRequestMapper workRequestMapper;

    @Resource
    private BpmProcessInstanceApi processInstanceApi;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createWorkRequest(Long userId, BpmOAWorkRequestCreateReqVO createReqVO) {
        BpmOAWorkRequestDO workRequest = BeanUtils.toBean(createReqVO, BpmOAWorkRequestDO.class)
                .setUserId(userId).setStatus(BpmTaskStatusEnum.RUNNING.getStatus());
        workRequestMapper.insert(workRequest);

        Map<String, Object> processInstanceVariables = new HashMap<>();
        String processInstanceId = processInstanceApi.createProcessInstance(userId,
                new BpmProcessInstanceCreateReqDTO().setProcessDefinitionKey(PROCESS_KEY)
                        .setVariables(processInstanceVariables).setBusinessKey(String.valueOf(workRequest.getId()))
                        .setStartUserSelectAssignees(createReqVO.getStartUserSelectAssignees()));

        workRequestMapper.updateById(new BpmOAWorkRequestDO().setId(workRequest.getId()).setProcessInstanceId(processInstanceId));
        return workRequest.getId();
    }

    @Override
    public void updateWorkRequestStatus(Long id, Integer status) {
        workRequestMapper.updateById(new BpmOAWorkRequestDO().setId(id).setStatus(status));
    }

    @Override
    public BpmOAWorkRequestDO getWorkRequest(Long id) {
        return workRequestMapper.selectById(id);
    }

    @Override
    public PageResult<BpmOAWorkRequestDO> getWorkRequestPage(Long userId, BpmOAWorkRequestPageReqVO pageReqVO) {
        return workRequestMapper.selectPage(userId, pageReqVO);
    }

}