package com.meession.etm.module.crm.service.order;

import com.meession.etm.framework.common.biz.system.tenant.TenantCommonApi;
import com.meession.etm.framework.tenant.core.util.TenantUtils;
import com.meession.etm.module.bpm.dal.dataobject.definition.BpmProcessDefinitionInfoDO;
import com.meession.etm.module.bpm.dal.mysql.definition.BpmProcessDefinitionInfoMapper;
import com.meession.etm.module.bpm.enums.definition.BpmAutoApproveTypeEnum;
import com.meession.etm.module.bpm.enums.definition.BpmModelFormTypeEnum;
import com.meession.etm.module.bpm.enums.definition.BpmModelTypeEnum;
import com.meession.etm.module.bpm.framework.flowable.core.enums.BpmTaskCandidateStrategyEnum;
import com.meession.etm.module.bpm.framework.flowable.core.util.BpmnModelUtils;
import com.meession.etm.module.bpm.framework.flowable.core.util.FlowableUtils;
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
 * 初始化订单审批流程，避免首次使用订单审批时因缺少流程定义而失败。
 */
@Component
@Slf4j
public class CrmOrderBpmInitializer implements ApplicationRunner {

    private static final String PROCESS_NAME = "CRM订单审批流程";
    private static final String APPROVE_TASK_ID = "crmOrderApproveTask";

    @Resource
    private RepositoryService repositoryService;
    @Resource
    private BpmProcessDefinitionInfoMapper processDefinitionInfoMapper;
    @Resource
    private TenantCommonApi tenantApi;

    @Override
    public void run(ApplicationArguments args) {
        tenantApi.getTenantIdList().forEach(tenantId -> TenantUtils.execute(tenantId, this::deployIfAbsent));
    }

    private void deployIfAbsent() {
        String tenantId = FlowableUtils.getTenantId();
        ProcessDefinition definition = repositoryService.createProcessDefinitionQuery()
                .processDefinitionTenantId(tenantId)
                .processDefinitionKey(CrmOrderServiceImpl.PROCESS_KEY)
                .latestVersion()
                .active()
                .singleResult();
        if (definition == null) {
            repositoryService.createDeployment()
                    .key(CrmOrderServiceImpl.PROCESS_KEY)
                    .name(PROCESS_NAME)
                    .tenantId(tenantId)
                    .disableSchemaValidation()
                    .disableBpmnValidation()
                    .addBpmnModel(CrmOrderServiceImpl.PROCESS_KEY + ".bpmn", buildBpmnModel())
                    .deploy();
            definition = repositoryService.createProcessDefinitionQuery()
                    .processDefinitionTenantId(tenantId)
                    .processDefinitionKey(CrmOrderServiceImpl.PROCESS_KEY)
                    .latestVersion()
                    .active()
                    .singleResult();
            log.info("[deployIfAbsent][已部署订单审批流程，tenantId={}]", tenantId);
        }
        if (definition != null && processDefinitionInfoMapper.selectByProcessDefinitionId(definition.getId()) == null) {
            processDefinitionInfoMapper.insert(new BpmProcessDefinitionInfoDO()
                    .setProcessDefinitionId(definition.getId())
                    .setModelId(CrmOrderServiceImpl.PROCESS_KEY + "-auto-" + tenantId)
                    .setModelType(BpmModelTypeEnum.BPMN.getType())
                    .setCategory("CRM")
                    .setDescription(PROCESS_NAME)
                    .setFormType(BpmModelFormTypeEnum.CUSTOM.getType())
                    .setFormCustomCreatePath("/crm/order")
                    .setFormCustomViewPath("/crm/order")
                    .setVisible(true)
                    .setSort(0L)
                    .setAllowCancelRunningProcess(true)
                    .setAllowWithdrawTask(false)
                    .setAutoApprovalType(BpmAutoApproveTypeEnum.NONE.getType()));
        }
    }

    private BpmnModel buildBpmnModel() {
        BpmnModel model = new BpmnModel();
        model.setTargetNamespace(BpmnXMLConstants.BPMN2_NAMESPACE);
        Process process = new Process();
        process.setId(CrmOrderServiceImpl.PROCESS_KEY);
        process.setName(PROCESS_NAME);
        process.setExecutable(true);
        model.addProcess(process);

        StartEvent startEvent = new StartEvent();
        startEvent.setId("startEvent");
        startEvent.setName("开始");
        process.addFlowElement(startEvent);

        UserTask approveTask = new UserTask();
        approveTask.setId(APPROVE_TASK_ID);
        approveTask.setName("订单审批");
        // 演示环境由管理员处理审批，发起后可在管理员待办中看到任务。
        BpmnModelUtils.addCandidateElements(BpmTaskCandidateStrategyEnum.USER.getStrategy(), "1", approveTask);
        process.addFlowElement(approveTask);

        EndEvent endEvent = new EndEvent();
        endEvent.setId("endEvent");
        endEvent.setName("结束");
        process.addFlowElement(endEvent);

        process.addFlowElement(new SequenceFlow("startEvent", APPROVE_TASK_ID));
        process.addFlowElement(new SequenceFlow(APPROVE_TASK_ID, "endEvent"));
        return model;
    }
}
