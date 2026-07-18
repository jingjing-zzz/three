package com.meession.etm.module.bpm.service.oa;

import com.meession.etm.framework.common.pojo.PageResult;
import com.meession.etm.framework.common.util.object.BeanUtils;
import com.meession.etm.module.bpm.api.task.BpmProcessInstanceApi;
import com.meession.etm.module.bpm.api.task.dto.BpmProcessInstanceCreateReqDTO;
import com.meession.etm.module.bpm.controller.admin.oa.vo.BpmOALoanCreateReqVO;
import com.meession.etm.module.bpm.controller.admin.oa.vo.BpmOALoanPageReqVO;
import com.meession.etm.module.bpm.dal.dataobject.oa.BpmOALoanDO;
import com.meession.etm.module.bpm.dal.mysql.oa.BpmOALoanMapper;
import com.meession.etm.module.bpm.enums.task.BpmTaskStatusEnum;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.HashMap;
import java.util.Map;

@Service
@Validated
public class BpmOALoanServiceImpl implements BpmOALoanService {

    public static final String PROCESS_KEY = "oa_loan";

    @Resource
    private BpmOALoanMapper loanMapper;

    @Resource
    private BpmProcessInstanceApi processInstanceApi;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createLoan(Long userId, BpmOALoanCreateReqVO createReqVO) {
        BpmOALoanDO loan = BeanUtils.toBean(createReqVO, BpmOALoanDO.class)
                .setUserId(userId).setStatus(BpmTaskStatusEnum.RUNNING.getStatus());
        loanMapper.insert(loan);

        Map<String, Object> processInstanceVariables = new HashMap<>();
        processInstanceVariables.put("amount", createReqVO.getAmount());
        String processInstanceId = processInstanceApi.createProcessInstance(userId,
                new BpmProcessInstanceCreateReqDTO().setProcessDefinitionKey(PROCESS_KEY)
                        .setVariables(processInstanceVariables).setBusinessKey(String.valueOf(loan.getId()))
                        .setStartUserSelectAssignees(createReqVO.getStartUserSelectAssignees()));

        loanMapper.updateById(new BpmOALoanDO().setId(loan.getId()).setProcessInstanceId(processInstanceId));
        return loan.getId();
    }

    @Override
    public void updateLoanStatus(Long id, Integer status) {
        loanMapper.updateById(new BpmOALoanDO().setId(id).setStatus(status));
    }

    @Override
    public BpmOALoanDO getLoan(Long id) {
        return loanMapper.selectById(id);
    }

    @Override
    public PageResult<BpmOALoanDO> getLoanPage(Long userId, BpmOALoanPageReqVO pageReqVO) {
        return loanMapper.selectPage(userId, pageReqVO);
    }

}