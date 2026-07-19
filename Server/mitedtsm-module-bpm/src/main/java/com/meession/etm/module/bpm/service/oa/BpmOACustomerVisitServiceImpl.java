package com.meession.etm.module.bpm.service.oa;

import com.meession.etm.framework.common.pojo.PageResult;
import com.meession.etm.framework.common.util.object.BeanUtils;
import com.meession.etm.module.bpm.api.task.BpmProcessInstanceApi;
import com.meession.etm.module.bpm.api.task.dto.BpmProcessInstanceCreateReqDTO;
import com.meession.etm.module.bpm.controller.admin.oa.vo.BpmOACustomerVisitCreateReqVO;
import com.meession.etm.module.bpm.controller.admin.oa.vo.BpmOACustomerVisitPageReqVO;
import com.meession.etm.module.bpm.dal.dataobject.oa.BpmOACustomerVisitDO;
import com.meession.etm.module.bpm.dal.mysql.oa.BpmOACustomerVisitMapper;
import com.meession.etm.module.bpm.enums.task.BpmTaskStatusEnum;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.HashMap;
import java.util.Map;

@Service
@Validated
public class BpmOACustomerVisitServiceImpl implements BpmOACustomerVisitService {

    public static final String PROCESS_KEY = "oa_customer_visit";

    @Resource
    private BpmOACustomerVisitMapper customerVisitMapper;

    @Resource
    private BpmProcessInstanceApi processInstanceApi;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createCustomerVisit(Long userId, BpmOACustomerVisitCreateReqVO createReqVO) {
        BpmOACustomerVisitDO customerVisit = BeanUtils.toBean(createReqVO, BpmOACustomerVisitDO.class)
                .setUserId(userId).setStatus(BpmTaskStatusEnum.RUNNING.getStatus());
        customerVisitMapper.insert(customerVisit);

        Map<String, Object> processInstanceVariables = new HashMap<>();
        String processInstanceId = processInstanceApi.createProcessInstance(userId,
                new BpmProcessInstanceCreateReqDTO().setProcessDefinitionKey(PROCESS_KEY)
                        .setVariables(processInstanceVariables).setBusinessKey(String.valueOf(customerVisit.getId()))
                        .setStartUserSelectAssignees(createReqVO.getStartUserSelectAssignees()));

        customerVisitMapper.updateById(new BpmOACustomerVisitDO().setId(customerVisit.getId()).setProcessInstanceId(processInstanceId));
        return customerVisit.getId();
    }

    @Override
    public void updateCustomerVisitStatus(Long id, Integer status) {
        customerVisitMapper.updateById(new BpmOACustomerVisitDO().setId(id).setStatus(status));
    }

    @Override
    public BpmOACustomerVisitDO getCustomerVisit(Long id) {
        return customerVisitMapper.selectById(id);
    }

    @Override
    public PageResult<BpmOACustomerVisitDO> getCustomerVisitPage(Long userId, BpmOACustomerVisitPageReqVO pageReqVO) {
        return customerVisitMapper.selectPage(userId, pageReqVO);
    }

}