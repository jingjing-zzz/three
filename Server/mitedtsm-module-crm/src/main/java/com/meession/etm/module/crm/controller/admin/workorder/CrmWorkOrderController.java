package com.meession.etm.module.crm.controller.admin.workorder;

import cn.hutool.core.collection.CollUtil;
import com.meession.etm.framework.apilog.core.annotation.ApiAccessLog;
import com.meession.etm.framework.common.pojo.CommonResult;
import com.meession.etm.framework.common.pojo.PageResult;
import com.meession.etm.framework.common.util.object.BeanUtils;
import com.meession.etm.framework.excel.core.util.ExcelUtils;
import com.meession.etm.module.crm.controller.admin.workorder.vo.*;
import com.meession.etm.module.crm.dal.dataobject.workorder.CrmWorkOrderDO;
import com.meession.etm.module.crm.dal.dataobject.workorder.CrmWorkOrderLogDO;
import com.meession.etm.module.crm.dal.mysql.workorder.CrmWorkOrderLogMapper;
import com.meession.etm.module.crm.enums.WorkOrderPriorityEnum;
import com.meession.etm.module.crm.enums.WorkOrderStatusEnum;
import com.meession.etm.module.crm.enums.WorkOrderTypeEnum;
import com.meession.etm.module.crm.service.workorder.CrmWorkOrderService;
import com.meession.etm.module.system.api.user.AdminUserApi;
import com.meession.etm.module.system.api.user.dto.AdminUserRespDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.meession.etm.framework.apilog.core.enums.OperateTypeEnum.EXPORT;
import static com.meession.etm.framework.common.pojo.CommonResult.success;
import static com.meession.etm.framework.common.pojo.PageParam.PAGE_SIZE_NONE;
import static com.meession.etm.framework.common.util.collection.CollectionUtils.convertList;
import static com.meession.etm.framework.common.util.collection.CollectionUtils.convertSet;
import static com.meession.etm.framework.security.core.util.SecurityFrameworkUtils.getLoginUserId;

@Tag(name = "管理后台 - CRM 工单")
@RestController
@RequestMapping("/crm/work-order")
@Validated
public class CrmWorkOrderController {

    @Resource
    private CrmWorkOrderService workOrderService;

    @Resource
    private CrmWorkOrderLogMapper workOrderLogMapper;

    @Resource
    private AdminUserApi adminUserApi;

    @PostMapping("/create")
    @Operation(summary = "创建工单")
    @PreAuthorize("@ss.hasPermission('crm:work-order:create')")
    public CommonResult<Long> createWorkOrder(@Valid @RequestBody CrmWorkOrderSaveReqVO createReqVO) {
        return success(workOrderService.createWorkOrder(createReqVO, getLoginUserId()));
    }

    @PutMapping("/update")
    @Operation(summary = "更新工单")
    @PreAuthorize("@ss.hasPermission('crm:work-order:update')")
    public CommonResult<Boolean> updateWorkOrder(@Valid @RequestBody CrmWorkOrderSaveReqVO updateReqVO) {
        workOrderService.updateWorkOrder(updateReqVO);
        return success(true);
    }

    @PutMapping("/update-status")
    @Operation(summary = "更新工单状态")
    @PreAuthorize("@ss.hasPermission('crm:work-order:update')")
    public CommonResult<Boolean> updateWorkOrderStatus(@Valid @RequestBody CrmWorkOrderUpdateStatusReqVO updateStatusReqVO) {
        workOrderService.updateWorkOrderStatus(updateStatusReqVO, getLoginUserId());
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除工单")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('crm:work-order:delete')")
    public CommonResult<Boolean> deleteWorkOrder(@RequestParam("id") Long id) {
        workOrderService.deleteWorkOrder(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得工单")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('crm:work-order:query')")
    public CommonResult<CrmWorkOrderRespVO> getWorkOrder(@RequestParam("id") Long id) {
        CrmWorkOrderDO workOrder = workOrderService.getWorkOrder(id);
        return success(buildWorkOrderDetail(workOrder));
    }

    @GetMapping("/page")
    @Operation(summary = "获得工单分页")
    @PreAuthorize("@ss.hasPermission('crm:work-order:query')")
    public CommonResult<PageResult<CrmWorkOrderRespVO>> getWorkOrderPage(@Valid CrmWorkOrderPageReqVO pageVO) {
        PageResult<CrmWorkOrderDO> pageResult = workOrderService.getWorkOrderPage(pageVO);
        return success(new PageResult<>(buildWorkOrderDetailList(pageResult.getList()), pageResult.getTotal()));
    }

    @GetMapping("/statistics")
    @Operation(summary = "获得工单统计")
    @PreAuthorize("@ss.hasPermission('crm:work-order:query')")
    public CommonResult<CrmWorkOrderStatisticsRespVO> getWorkOrderStatistics() {
        return success(workOrderService.getWorkOrderStatistics());
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出工单 Excel")
    @PreAuthorize("@ss.hasPermission('crm:work-order:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportWorkOrderExcel(@Valid CrmWorkOrderPageReqVO pageVO,
                                     HttpServletResponse response) throws IOException {
        pageVO.setPageSize(PAGE_SIZE_NONE);
        PageResult<CrmWorkOrderDO> pageResult = workOrderService.getWorkOrderPage(pageVO);
        List<CrmWorkOrderRespVO> list = buildWorkOrderDetailList(pageResult.getList());
        ExcelUtils.write(response, "工单.xls", "数据", CrmWorkOrderRespVO.class, list);
    }

    private CrmWorkOrderRespVO buildWorkOrderDetail(CrmWorkOrderDO workOrder) {
        if (workOrder == null) {
            return null;
        }
        CrmWorkOrderRespVO respVO = BeanUtils.toBean(workOrder, CrmWorkOrderRespVO.class);

        respVO.setTypeName(WorkOrderTypeEnum.getName(workOrder.getType()));
        respVO.setPriorityName(WorkOrderPriorityEnum.getName(workOrder.getPriority()));
        respVO.setStatusName(WorkOrderStatusEnum.getName(workOrder.getStatus()));

        List<CrmWorkOrderLogDO> logs = workOrderLogMapper.selectListByWorkOrderId(workOrder.getId());
        if (CollUtil.isNotEmpty(logs)) {
            respVO.setLogs(convertList(logs, log -> {
                CrmWorkOrderRespVO.Log logVO = BeanUtils.toBean(log, CrmWorkOrderRespVO.Log.class);
                logVO.setStatusBeforeName(WorkOrderStatusEnum.getName(log.getStatusBefore()));
                logVO.setStatusAfterName(WorkOrderStatusEnum.getName(log.getStatusAfter()));
                return logVO;
            }));
        }

        return respVO;
    }

    private List<CrmWorkOrderRespVO> buildWorkOrderDetailList(List<CrmWorkOrderDO> list) {
        if (CollUtil.isEmpty(list)) {
            return Collections.emptyList();
        }

        Map<Long, AdminUserRespDTO> userMap = adminUserApi.getUserMap(convertList(list,
                order -> order.getOwnerUserId()));

        return convertList(list, workOrder -> {
            CrmWorkOrderRespVO respVO = BeanUtils.toBean(workOrder, CrmWorkOrderRespVO.class);

            respVO.setTypeName(WorkOrderTypeEnum.getName(workOrder.getType()));
            respVO.setPriorityName(WorkOrderPriorityEnum.getName(workOrder.getPriority()));
            respVO.setStatusName(WorkOrderStatusEnum.getName(workOrder.getStatus()));

            if (workOrder.getOwnerUserId() != null) {
                AdminUserRespDTO user = userMap.get(workOrder.getOwnerUserId());
                if (user != null) {
                    respVO.setOwnerUserName(user.getNickname());
                }
            }

            return respVO;
        });
    }

}