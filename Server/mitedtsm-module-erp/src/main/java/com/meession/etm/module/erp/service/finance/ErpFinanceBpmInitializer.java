package com.meession.etm.module.erp.service.finance;

import com.meession.etm.module.bpm.dal.dataobject.definition.BpmProcessDefinitionInfoDO;
import com.meession.etm.module.bpm.dal.mysql.definition.BpmProcessDefinitionInfoMapper;
import com.meession.etm.module.bpm.enums.definition.BpmAutoApproveTypeEnum;
import com.meession.etm.module.bpm.enums.definition.BpmModelFormTypeEnum;
import com.meession.etm.module.bpm.enums.definition.BpmModelTypeEnum;
import com.meession.etm.framework.tenant.core.util.TenantUtils;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.flowable.bpmn.constants.BpmnXMLConstants;
import org.flowable.bpmn.model.BpmnModel;
import org.flowable.bpmn.model.EndEvent;
import org.flowable.bpmn.model.Process;
import org.flowable.bpmn.model.SequenceFlow;
import org.flowable.bpmn.model.StartEvent;
import org.flowable.bpmn.model.UserTask;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.repository.ProcessDefinition;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * 发布财务域默认 BPM 流程，保证报销、退款单据创建后能进入审批。
 */
@Component
@Slf4j
public class ErpFinanceBpmInitializer implements ApplicationRunner {

    @Resource
    private RepositoryService repositoryService;
    @Resource
    private BpmProcessDefinitionInfoMapper processDefinitionInfoMapper;

    @Override
    public void run(ApplicationArguments args) {
        deployIfAbsent(ErpFinanceRecordServiceImpl.REIMBURSEMENT_PROCESS_DEFINITION_KEY,
                "ERP Reimbursement Approval", "/erp/finance/reimbursement");
        deployIfAbsent(ErpFinanceRecordServiceImpl.REFUND_PROCESS_DEFINITION_KEY,
                "ERP Refund Approval", "/erp/finance/refund");
    }

    private void deployIfAbsent(String key, String name, String formPath) {
        ProcessDefinition definition = repositoryService.createProcessDefinitionQuery()
                .processDefinitionKey(key)
                .latestVersion()
                .active()
                .singleResult();
        if (definition == null) {
            repositoryService.createDeployment()
                    .key(key)
                    .name(name)
                    .disableSchemaValidation()
                    .disableBpmnValidation()
                    .addBpmnModel(key + ".bpmn", buildBpmnModel(key, name))
                    .deploy();
            definition = repositoryService.createProcessDefinitionQuery()
                    .processDefinitionKey(key)
                    .latestVersion()
                    .active()
                    .singleResult();
            log.info("[deployIfAbsent][deployed finance BPM process: {}]", key);
        }
        if (definition != null) {
            ProcessDefinition finalDefinition = definition;
            TenantUtils.executeIgnore(() -> {
                if (processDefinitionInfoMapper.selectByProcessDefinitionId(finalDefinition.getId()) == null) {
                    processDefinitionInfoMapper.insert(new BpmProcessDefinitionInfoDO()
                            .setProcessDefinitionId(finalDefinition.getId())
                            .setModelId(key + "-auto")
                            .setModelType(BpmModelTypeEnum.BPMN.getType())
                            .setCategory("FINANCE")
                            .setDescription(name)
                            .setFormType(BpmModelFormTypeEnum.CUSTOM.getType())
                            .setFormCustomCreatePath(formPath)
                            .setFormCustomViewPath(formPath)
                            .setVisible(true)
                            .setSort(0L)
                            .setAllowCancelRunningProcess(true)
                            .setAllowWithdrawTask(false)
                            .setAutoApprovalType(BpmAutoApproveTypeEnum.NONE.getType()));
                }
            });
        }
    }

    private BpmnModel buildBpmnModel(String key, String name) {
        BpmnModel bpmnModel = new BpmnModel();
        bpmnModel.setTargetNamespace(BpmnXMLConstants.BPMN2_NAMESPACE);

        Process process = new Process();
        process.setId(key);
        process.setName(name);
        process.setExecutable(true);
        bpmnModel.addProcess(process);

        StartEvent startEvent = new StartEvent();
        startEvent.setId("startEvent");
        startEvent.setName("Start");
        process.addFlowElement(startEvent);

        UserTask approveTask = new UserTask();
        approveTask.setId("financeApproveTask");
        approveTask.setName("Finance Approval");
        approveTask.setAssignee("${initiator}");
        process.addFlowElement(approveTask);

        EndEvent endEvent = new EndEvent();
        endEvent.setId("endEvent");
        endEvent.setName("End");
        process.addFlowElement(endEvent);

        process.addFlowElement(new SequenceFlow("startEvent", "financeApproveTask"));
        process.addFlowElement(new SequenceFlow("financeApproveTask", "endEvent"));

        return bpmnModel;
    }

}
