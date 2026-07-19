package com.meession.etm.module.bpm.framework.flowable.init;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.meession.etm.framework.common.util.json.JsonUtils;
import com.meession.etm.framework.tenant.core.context.TenantContextHolder;
import com.meession.etm.module.bpm.controller.admin.definition.vo.form.BpmFormSaveReqVO;
import com.meession.etm.module.bpm.controller.admin.definition.vo.model.BpmModelMetaInfoVO;
import com.meession.etm.module.bpm.controller.admin.definition.vo.model.BpmModelSaveReqVO;
import com.meession.etm.module.bpm.controller.admin.definition.vo.model.simple.BpmSimpleModelNodeVO;
import com.meession.etm.module.bpm.dal.dataobject.definition.BpmFormDO;
import com.meession.etm.module.bpm.enums.definition.BpmModelFormTypeEnum;
import com.meession.etm.module.bpm.enums.definition.BpmModelTypeEnum;
import com.meession.etm.module.bpm.enums.definition.BpmSimpleModelNodeTypeEnum;
import com.meession.etm.module.bpm.enums.definition.BpmUserTaskApproveTypeEnum;
import com.meession.etm.module.bpm.framework.flowable.core.enums.BpmTaskCandidateStrategyEnum;
import com.meession.etm.module.bpm.framework.flowable.core.util.BpmnModelUtils;
import com.meession.etm.module.bpm.framework.flowable.core.util.SimpleModelUtils;
import com.meession.etm.module.bpm.service.definition.BpmFormService;
import com.meession.etm.module.bpm.service.definition.BpmModelService;
import com.meession.etm.module.bpm.service.definition.BpmProcessDefinitionService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.flowable.bpmn.model.BpmnModel;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.repository.Model;
import org.flowable.engine.repository.ProcessDefinition;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Component
@Order(100)
@Slf4j
public class OaProcessDefinitionInitializer implements ApplicationRunner {

    @Resource
    private RepositoryService repositoryService;
    @Resource
    private BpmModelService modelService;
    @Resource
    private BpmProcessDefinitionService processDefinitionService;
    @Resource
    private BpmFormService bpmFormService;

    private static final List<ProcessDefinitionConfig> OA_PROCESS_CONFIGS = Arrays.asList(
            new ProcessDefinitionConfig("oa_leave", "OA请假流程", "OA", getLeaveFormConf(), getLeaveFormFields()),
            new ProcessDefinitionConfig("oa_business_trip", "OA出差流程", "OA", getBusinessTripFormConf(), getBusinessTripFormFields()),
            new ProcessDefinitionConfig("oa_loan", "OA借款流程", "OA", getLoanFormConf(), getLoanFormFields()),
            new ProcessDefinitionConfig("oa_customer_visit", "OA客户拜访流程", "OA", getCustomerVisitFormConf(), getCustomerVisitFormFields()),
            new ProcessDefinitionConfig("oa_work_request", "OA请示审批流程", "OA", getWorkRequestFormConf(), getWorkRequestFormFields())
    );

    @Override
    public void run(ApplicationArguments args) throws Exception {
        log.info("[run][开始初始化OA流程定义]");
        TenantContextHolder.setTenantId(1L);
        try {
            for (ProcessDefinitionConfig config : OA_PROCESS_CONFIGS) {
                try {
                    initProcessDefinition(config.getKey(), config.getName(), config.getCategory());
                } catch (Exception e) {
                    log.error("[run][初始化OA流程定义失败，key={}]", config.getKey(), e);
                }
            }
        } finally {
            TenantContextHolder.clear();
        }
        log.info("[run][OA流程定义初始化完成]");
    }

    private void initProcessDefinition(String key, String name, String category) {
        ProcessDefinition existingDefinition = repositoryService.createProcessDefinitionQuery()
                .processDefinitionKey(key)
                .active()
                .singleResult();
        
        if (existingDefinition != null) {
            log.info("[initProcessDefinition][流程定义已存在，跳过创建，key={}]", key);
            return;
        }

        ProcessDefinitionConfig config = OA_PROCESS_CONFIGS.stream()
                .filter(c -> c.getKey().equals(key))
                .findFirst()
                .orElse(null);

        Long formId = null;
        if (config != null) {
            formId = createOrGetForm(key, name + "表单", config.getFormConf(), config.getFormFields());
        }

        createAndDeployModel(key, name, category, formId);
    }

    private void createAndDeployModel(String key, String name, String category, Long formId) {
        log.info("[createAndDeployModel][开始创建流程模型，key={}, name={}, formId={}]", key, name, formId);

        String startUserId = UUID.randomUUID().toString();
        String approveId = UUID.randomUUID().toString();
        String endId = UUID.randomUUID().toString();

        BpmSimpleModelNodeVO startUserNode = new BpmSimpleModelNodeVO()
                .setId(startUserId)
                .setName("发起人")
                .setType(BpmSimpleModelNodeTypeEnum.START_USER_NODE.getType());

        BpmSimpleModelNodeVO approveNode = new BpmSimpleModelNodeVO()
                .setId(approveId)
                // 演示环境统一由管理员审批，保证发起后能在管理员待办中看到任务。
                .setName("管理员审批")
                .setType(BpmSimpleModelNodeTypeEnum.APPROVE_NODE.getType())
                .setApproveType(BpmUserTaskApproveTypeEnum.USER.getType())
                .setCandidateStrategy(BpmTaskCandidateStrategyEnum.USER.getStrategy())
                .setCandidateParam("1")
                .setApproveMethod(1);

        BpmSimpleModelNodeVO endNode = new BpmSimpleModelNodeVO()
                .setId(endId)
                .setName("结束")
                .setType(BpmSimpleModelNodeTypeEnum.END_NODE.getType());

        approveNode.setChildNode(endNode);
        startUserNode.setChildNode(approveNode);

        BpmnModel bpmnModel = SimpleModelUtils.buildBpmnModel(key, name, startUserNode);
        String bpmnXml = BpmnModelUtils.getBpmnXml(bpmnModel);
        String simpleJson = JsonUtils.toJsonString(startUserNode);

        BpmModelSaveReqVO modelVO = new BpmModelSaveReqVO();
        modelVO.setName(name);
        modelVO.setKey(key);
        modelVO.setCategory(category);
        modelVO.setType(BpmModelTypeEnum.SIMPLE.getType());
        modelVO.setFormType(BpmModelFormTypeEnum.CUSTOM.getType());
        
        String formCustomCreatePath = getFormCustomCreatePath(key);
        String formCustomViewPath = getFormCustomViewPath(key);
        modelVO.setFormCustomCreatePath(formCustomCreatePath);
        modelVO.setFormCustomViewPath(formCustomViewPath);
        
        modelVO.setBpmnXml(bpmnXml);
        modelVO.setSimpleModel(startUserNode);
        modelVO.setVisible(true);
        modelVO.setManagerUserIds(Collections.singletonList(1L));

        String modelId = modelService.createModel(modelVO);
        log.info("[createAndDeployModel][流程模型创建成功，modelId={}, key={}, formCustomCreatePath={}]", modelId, key, formCustomCreatePath);

        try {
            modelService.deployModel(1L, modelId);
            log.info("[createAndDeployModel][流程模型部署成功，key={}]", key);
        } catch (Exception e) {
            log.error("[createAndDeployModel][流程模型部署失败，key={}]", key, e);
        }
    }
    
    private String getFormCustomCreatePath(String key) {
        switch (key) {
            case "oa_leave":
                return "/bpm/oa/leave/create";
            case "oa_business_trip":
                return "/bpm/oa/business-trip/create";
            case "oa_loan":
                return "/bpm/oa/loan/create";
            case "oa_customer_visit":
                return "/bpm/oa/customer-visit/create";
            case "oa_work_request":
                return "/bpm/oa/work-request/create";
            default:
                return null;
        }
    }
    
    private String getFormCustomViewPath(String key) {
        switch (key) {
            case "oa_leave":
                return "/bpm/oa/leave/detail";
            case "oa_business_trip":
                return "/bpm/oa/business-trip/detail";
            case "oa_loan":
                return "/bpm/oa/loan/detail";
            case "oa_customer_visit":
                return "/bpm/oa/customer-visit/detail";
            case "oa_work_request":
                return "/bpm/oa/work-request/detail";
            default:
                return null;
        }
    }

    private Long createOrGetForm(String key, String name, String conf, List<String> fields) {
        List<BpmFormDO> existingForms = bpmFormService.getFormList();
        for (BpmFormDO form : existingForms) {
            if (name.equals(form.getName())) {
                log.info("[createOrGetForm][表单已存在，更新表单配置，name={}, id={}]", name, form.getId());
                BpmFormSaveReqVO formVO = new BpmFormSaveReqVO();
                formVO.setId(form.getId());
                formVO.setName(name);
                formVO.setConf(conf);
                formVO.setFields(fields);
                formVO.setStatus(1);
                bpmFormService.updateForm(formVO);
                return form.getId();
            }
        }

        log.info("[createOrGetForm][创建新表单，name={}]", name);
        BpmFormSaveReqVO formVO = new BpmFormSaveReqVO();
        formVO.setName(name);
        formVO.setConf(conf);
        formVO.setFields(fields);
        formVO.setStatus(1);
        return bpmFormService.createForm(formVO);
    }

    private static class ProcessDefinitionConfig {
        private final String key;
        private final String name;
        private final String category;
        private final String formConf;
        private final List<String> formFields;

        ProcessDefinitionConfig(String key, String name, String category, String formConf, List<String> formFields) {
            this.key = key;
            this.name = name;
            this.category = category;
            this.formConf = formConf;
            this.formFields = formFields;
        }

        public String getKey() {
            return key;
        }

        public String getName() {
            return name;
        }

        public String getCategory() {
            return category;
        }

        public String getFormConf() {
            return formConf;
        }

        public List<String> getFormFields() {
            return formFields;
        }
    }

    private static String getDefaultFormConf() {
        return "{\"form\":{\"inline\":false,\"hideRequiredAsterisk\":false,\"labelPosition\":\"right\",\"size\":\"small\",\"labelWidth\":\"125px\",\"showMessage\":true,\"inlineMessage\":false},\"submitBtn\":{\"show\":true,\"innerText\":\"提交\"},\"resetBtn\":{\"show\":false,\"innerText\":\"重置\"}}";
    }

    private static String getLeaveFormConf() {
        return getDefaultFormConf();
    }

    private static List<String> getLeaveFormFields() {
        return Arrays.asList(
                "{\"type\":\"select\",\"field\":\"type\",\"title\":\"请假类型\",\"info\":\"\",\"required\":true,\"validate\":[{\"required\":true,\"message\":\"请假类型不能为空\",\"trigger\":\"blur\"}],\"_fc_drag_tag\":\"select\",\"hidden\":false,\"display\":true,\"options\":[{\"label\":\"病假\",\"value\":1},{\"label\":\"事假\",\"value\":2},{\"label\":\"婚假\",\"value\":3},{\"label\":\"产假\",\"value\":4},{\"label\":\"陪产假\",\"value\":5},{\"label\":\"丧假\",\"value\":6},{\"label\":\"年假\",\"value\":7},{\"label\":\"调休\",\"value\":8},{\"label\":\"探亲假\",\"value\":9},{\"label\":\"工伤假\",\"value\":10},{\"label\":\"其他\",\"value\":11}]}",
                "{\"type\":\"datePicker\",\"field\":\"startTime\",\"title\":\"开始时间\",\"info\":\"\",\"required\":true,\"validate\":[{\"required\":true,\"message\":\"开始时间不能为空\",\"trigger\":\"blur\"}],\"_fc_drag_tag\":\"datePicker\",\"hidden\":false,\"display\":true,\"props\":{\"type\":\"datetime\",\"valueFormat\":\"yyyy-MM-dd HH:mm:ss\"}}",
                "{\"type\":\"datePicker\",\"field\":\"endTime\",\"title\":\"结束时间\",\"info\":\"\",\"required\":true,\"validate\":[{\"required\":true,\"message\":\"结束时间不能为空\",\"trigger\":\"blur\"}],\"_fc_drag_tag\":\"datePicker\",\"hidden\":false,\"display\":true,\"props\":{\"type\":\"datetime\",\"valueFormat\":\"yyyy-MM-dd HH:mm:ss\"}}",
                "{\"type\":\"textarea\",\"field\":\"reason\",\"title\":\"请假原因\",\"info\":\"\",\"required\":true,\"validate\":[{\"required\":true,\"message\":\"请假原因不能为空\",\"trigger\":\"blur\"}],\"_fc_drag_tag\":\"textarea\",\"hidden\":false,\"display\":true}"
        );
    }

    private static String getBusinessTripFormConf() {
        return getDefaultFormConf();
    }

    private static List<String> getBusinessTripFormFields() {
        return Arrays.asList(
                "{\"type\":\"input\",\"field\":\"destination\",\"title\":\"出差地点\",\"info\":\"\",\"required\":true,\"validate\":[{\"required\":true,\"message\":\"出差地点不能为空\",\"trigger\":\"blur\"}],\"_fc_drag_tag\":\"input\",\"hidden\":false,\"display\":true}",
                "{\"type\":\"datePicker\",\"field\":\"startTime\",\"title\":\"出发时间\",\"info\":\"\",\"required\":true,\"validate\":[{\"required\":true,\"message\":\"出发时间不能为空\",\"trigger\":\"blur\"}],\"_fc_drag_tag\":\"datePicker\",\"hidden\":false,\"display\":true,\"props\":{\"type\":\"datetime\",\"valueFormat\":\"yyyy-MM-dd HH:mm:ss\"}}",
                "{\"type\":\"datePicker\",\"field\":\"endTime\",\"title\":\"结束时间\",\"info\":\"\",\"required\":true,\"validate\":[{\"required\":true,\"message\":\"结束时间不能为空\",\"trigger\":\"blur\"}],\"_fc_drag_tag\":\"datePicker\",\"hidden\":false,\"display\":true,\"props\":{\"type\":\"datetime\",\"valueFormat\":\"yyyy-MM-dd HH:mm:ss\"}}",
                "{\"type\":\"textarea\",\"field\":\"reason\",\"title\":\"出差事由\",\"info\":\"\",\"required\":true,\"validate\":[{\"required\":true,\"message\":\"出差事由不能为空\",\"trigger\":\"blur\"}],\"_fc_drag_tag\":\"textarea\",\"hidden\":false,\"display\":true}"
        );
    }

    private static String getLoanFormConf() {
        return getDefaultFormConf();
    }

    private static List<String> getLoanFormFields() {
        return Arrays.asList(
                "{\"type\":\"inputNumber\",\"field\":\"amount\",\"title\":\"借款金额\",\"info\":\"单位：元\",\"required\":true,\"validate\":[{\"required\":true,\"message\":\"借款金额不能为空\",\"trigger\":\"blur\"}],\"_fc_drag_tag\":\"inputNumber\",\"hidden\":false,\"display\":true}",
                "{\"type\":\"textarea\",\"field\":\"reason\",\"title\":\"借款用途\",\"info\":\"\",\"required\":true,\"validate\":[{\"required\":true,\"message\":\"借款用途不能为空\",\"trigger\":\"blur\"}],\"_fc_drag_tag\":\"textarea\",\"hidden\":false,\"display\":true}",
                "{\"type\":\"datePicker\",\"field\":\"repaymentDate\",\"title\":\"还款日期\",\"info\":\"\",\"required\":true,\"validate\":[{\"required\":true,\"message\":\"还款日期不能为空\",\"trigger\":\"blur\"}],\"_fc_drag_tag\":\"datePicker\",\"hidden\":false,\"display\":true,\"props\":{\"type\":\"datetime\",\"valueFormat\":\"yyyy-MM-dd HH:mm:ss\"}}"
        );
    }

    private static String getCustomerVisitFormConf() {
        return getDefaultFormConf();
    }

    private static List<String> getCustomerVisitFormFields() {
        return Arrays.asList(
                "{\"type\":\"input\",\"field\":\"customerName\",\"title\":\"客户名称\",\"info\":\"\",\"required\":true,\"validate\":[{\"required\":true,\"message\":\"客户名称不能为空\",\"trigger\":\"blur\"}],\"_fc_drag_tag\":\"input\",\"hidden\":false,\"display\":true}",
                "{\"type\":\"input\",\"field\":\"customerContact\",\"title\":\"联系人\",\"info\":\"\",\"required\":true,\"validate\":[{\"required\":true,\"message\":\"联系人不能为空\",\"trigger\":\"blur\"}],\"_fc_drag_tag\":\"input\",\"hidden\":false,\"display\":true}",
                "{\"type\":\"input\",\"field\":\"contactPhone\",\"title\":\"联系电话\",\"info\":\"\",\"required\":false,\"_fc_drag_tag\":\"input\",\"hidden\":false,\"display\":true}",
                "{\"type\":\"datePicker\",\"field\":\"visitTime\",\"title\":\"拜访时间\",\"info\":\"\",\"required\":true,\"validate\":[{\"required\":true,\"message\":\"拜访时间不能为空\",\"trigger\":\"blur\"}],\"_fc_drag_tag\":\"datePicker\",\"hidden\":false,\"display\":true,\"props\":{\"type\":\"datetime\",\"valueFormat\":\"yyyy-MM-dd HH:mm:ss\"}}",
                "{\"type\":\"input\",\"field\":\"visitLocation\",\"title\":\"拜访地点\",\"info\":\"\",\"required\":false,\"_fc_drag_tag\":\"input\",\"hidden\":false,\"display\":true}",
                "{\"type\":\"textarea\",\"field\":\"purpose\",\"title\":\"拜访目的\",\"info\":\"\",\"required\":true,\"validate\":[{\"required\":true,\"message\":\"拜访目的不能为空\",\"trigger\":\"blur\"}],\"_fc_drag_tag\":\"textarea\",\"hidden\":false,\"display\":true}"
        );
    }

    private static String getWorkRequestFormConf() {
        return getDefaultFormConf();
    }

    private static List<String> getWorkRequestFormFields() {
        return Arrays.asList(
                "{\"type\":\"input\",\"field\":\"title\",\"title\":\"请示标题\",\"info\":\"\",\"required\":true,\"validate\":[{\"required\":true,\"message\":\"请示标题不能为空\",\"trigger\":\"blur\"}],\"_fc_drag_tag\":\"input\",\"hidden\":false,\"display\":true}",
                "{\"type\":\"textarea\",\"field\":\"content\",\"title\":\"请示内容\",\"info\":\"\",\"required\":true,\"validate\":[{\"required\":true,\"message\":\"请示内容不能为空\",\"trigger\":\"blur\"}],\"_fc_drag_tag\":\"textarea\",\"hidden\":false,\"display\":true}",
                "{\"type\":\"textarea\",\"field\":\"suggestion\",\"title\":\"个人建议\",\"info\":\"\",\"required\":false,\"_fc_drag_tag\":\"textarea\",\"hidden\":false,\"display\":true}"
        );
    }
}
