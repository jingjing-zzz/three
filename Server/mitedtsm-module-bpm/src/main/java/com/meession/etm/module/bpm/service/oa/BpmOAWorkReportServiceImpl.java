package com.meession.etm.module.bpm.service.oa;

import com.meession.etm.framework.common.pojo.PageResult;
import com.meession.etm.framework.common.util.object.BeanUtils;
import com.meession.etm.module.bpm.api.task.BpmProcessInstanceApi;
import com.meession.etm.module.bpm.api.task.dto.BpmProcessInstanceCreateReqDTO;
import com.meession.etm.module.bpm.controller.admin.oa.vo.BpmOAWorkReportCreateReqVO;
import com.meession.etm.module.bpm.controller.admin.oa.vo.BpmOAWorkReportPageReqVO;
import com.meession.etm.module.bpm.dal.dataobject.oa.BpmOAWorkReportDO;
import com.meession.etm.module.bpm.dal.mysql.oa.BpmOAWorkReportMapper;
import com.meession.etm.module.bpm.enums.task.BpmTaskStatusEnum;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.HashMap;
import java.util.Map;

@Service
@Validated
public class BpmOAWorkReportServiceImpl implements BpmOAWorkReportService {

    public static final String PROCESS_KEY = "oa_work_report";

    @Resource
    private BpmOAWorkReportMapper workReportMapper;

    @Resource
    private BpmProcessInstanceApi processInstanceApi;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createWorkReport(Long userId, BpmOAWorkReportCreateReqVO createReqVO) {
        BpmOAWorkReportDO workReport = BeanUtils.toBean(createReqVO, BpmOAWorkReportDO.class)
                .setUserId(userId).setStatus(BpmTaskStatusEnum.RUNNING.getStatus());
        workReportMapper.insert(workReport);

        Map<String, Object> processInstanceVariables = new HashMap<>();
        processInstanceVariables.put("type", createReqVO.getType());
        String processInstanceId = processInstanceApi.createProcessInstance(userId,
                new BpmProcessInstanceCreateReqDTO().setProcessDefinitionKey(PROCESS_KEY)
                        .setVariables(processInstanceVariables).setBusinessKey(String.valueOf(workReport.getId()))
                        .setStartUserSelectAssignees(createReqVO.getStartUserSelectAssignees()));

        workReportMapper.updateById(new BpmOAWorkReportDO().setId(workReport.getId()).setProcessInstanceId(processInstanceId));
        return workReport.getId();
    }

    @Override
    public void updateWorkReportStatus(Long id, Integer status) {
        workReportMapper.updateById(new BpmOAWorkReportDO().setId(id).setStatus(status));
    }

    @Override
    public BpmOAWorkReportDO getWorkReport(Long id) {
        return workReportMapper.selectById(id);
    }

    @Override
    public PageResult<BpmOAWorkReportDO> getWorkReportPage(Long userId, BpmOAWorkReportPageReqVO pageReqVO) {
        return workReportMapper.selectPage(userId, pageReqVO);
    }

}