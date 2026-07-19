package com.meession.etm.module.system.controller.admin.workorder;

import com.meession.etm.framework.apilog.core.annotation.ApiAccessLog;
import com.meession.etm.framework.common.pojo.CommonResult;
import com.meession.etm.framework.common.pojo.PageResult;
import com.meession.etm.framework.common.util.object.BeanUtils;
import com.meession.etm.framework.excel.core.util.ExcelUtils;
import com.meession.etm.module.system.controller.admin.workorder.vo.*;
import com.meession.etm.module.system.dal.dataobject.workorder.WorkOrderDO;
import com.meession.etm.module.system.dal.dataobject.workorder.WorkOrderStatusFlowDO;
import com.meession.etm.module.system.dal.dataobject.workorder.WorkOrderTypeDO;
import com.meession.etm.module.system.dal.mysql.workorder.WorkOrderTypeMapper;
import com.meession.etm.module.system.enums.workorder.WorkOrderPriorityEnum;
import com.meession.etm.module.system.enums.workorder.WorkOrderStatusEnum;
import com.meession.etm.module.system.enums.workorder.WorkOrderTypeEnum;
import com.meession.etm.module.system.service.workorder.WorkOrderService;
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
import java.util.List;

import static com.meession.etm.framework.apilog.core.enums.OperateTypeEnum.EXPORT;
import static com.meession.etm.framework.common.pojo.CommonResult.success;
import static com.meession.etm.framework.common.pojo.PageParam.PAGE_SIZE_NONE;

@Tag(name = "管理后台 - 工单管理")
@RestController
@RequestMapping("/system/work-order")
@Validated
public class WorkOrderController {

    @Resource
    private WorkOrderService workOrderService;

    @Resource
    private WorkOrderTypeMapper workOrderTypeMapper;

    @PostMapping("/create")
    @Operation(summary = "创建工单")
    @PreAuthorize("@ss.hasPermission('crm:work-order:create')")
    public CommonResult<Long> createWorkOrder(@Valid @RequestBody WorkOrderSaveReqVO createReqVO) {
        Long orderId = workOrderService.createWorkOrder(createReqVO);
        return success(orderId);
    }

    @PutMapping("/update")
    @Operation(summary = "修改工单")
    @PreAuthorize("@ss.hasPermission('crm:work-order:update')")
    public CommonResult<Boolean> updateWorkOrder(@Valid @RequestBody WorkOrderSaveReqVO updateReqVO) {
        workOrderService.updateWorkOrder(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除工单")
    @Parameter(name = "id", description = "工单编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('crm:work-order:delete')")
    public CommonResult<Boolean> deleteWorkOrder(@RequestParam("id") Long id) {
        workOrderService.deleteWorkOrder(id);
        return success(true);
    }

    @DeleteMapping("/delete-list")
    @Operation(summary = "批量删除工单")
    @Parameter(name = "ids", description = "工单编号列表", required = true)
    @PreAuthorize("@ss.hasPermission('crm:work-order:delete')")
    public CommonResult<Boolean> deleteWorkOrderList(@RequestParam("ids") List<Long> ids) {
        workOrderService.deleteWorkOrderList(ids);
        return success(true);
    }

    @GetMapping("/page")
    @Operation(summary = "获取工单列表")
    @PreAuthorize("@ss.hasPermission('crm:work-order:query')")
    public CommonResult<PageResult<WorkOrderRespVO>> getWorkOrderPage(@Validated WorkOrderPageReqVO pageReqVO) {
        PageResult<WorkOrderDO> pageResult = workOrderService.getWorkOrderPage(pageReqVO);
        List<WorkOrderRespVO> respVOList = BeanUtils.toBean(pageResult.getList(), WorkOrderRespVO.class);
        respVOList.forEach(this::fillEnumNames);
        return success(new PageResult<>(respVOList, pageResult.getTotal()));
    }

    @GetMapping("/get")
    @Operation(summary = "获得工单")
    @Parameter(name = "id", description = "工单编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('crm:work-order:query')")
    public CommonResult<WorkOrderRespVO> getWorkOrder(@RequestParam("id") Long id) {
        WorkOrderDO workOrder = workOrderService.getWorkOrder(id);
        WorkOrderRespVO respVO = BeanUtils.toBean(workOrder, WorkOrderRespVO.class);
        fillEnumNames(respVO);
        return success(respVO);
    }

    @PutMapping("/status/process")
    @Operation(summary = "处理工单")
    @PreAuthorize("@ss.hasPermission('crm:work-order:update')")
    public CommonResult<Boolean> processWorkOrder(@Valid @RequestBody WorkOrderUpdateStatusReqVO reqVO) {
        workOrderService.processWorkOrder(reqVO);
        return success(true);
    }

    @PutMapping("/status/complete")
    @Operation(summary = "完结工单")
    @PreAuthorize("@ss.hasPermission('crm:work-order:update')")
    public CommonResult<Boolean> completeWorkOrder(@Valid @RequestBody WorkOrderUpdateStatusReqVO reqVO) {
        workOrderService.completeWorkOrder(reqVO);
        return success(true);
    }

    @PutMapping("/status/reject")
    @Operation(summary = "退回工单")
    @PreAuthorize("@ss.hasPermission('crm:work-order:update')")
    public CommonResult<Boolean> rejectWorkOrder(@Valid @RequestBody WorkOrderUpdateStatusReqVO reqVO) {
        workOrderService.rejectWorkOrder(reqVO);
        return success(true);
    }

    @PutMapping("/status/update")
    @Operation(summary = "更新工单状态")
    @PreAuthorize("@ss.hasPermission('crm:work-order:update')")
    public CommonResult<Boolean> updateWorkOrderStatus(@Valid @RequestBody WorkOrderUpdateStatusReqVO reqVO) {
        workOrderService.updateWorkOrderStatus(reqVO);
        return success(true);
    }

    @GetMapping("/status-flow")
    @Operation(summary = "获取工单状态流转记录")
    @Parameter(name = "orderId", description = "工单编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('crm:work-order:query')")
    public CommonResult<List<WorkOrderStatusFlowRespVO>> getWorkOrderStatusFlow(@RequestParam("orderId") Long orderId) {
        List<WorkOrderStatusFlowDO> flowList = workOrderService.getWorkOrderStatusFlow(orderId);
        List<WorkOrderStatusFlowRespVO> respVOList = BeanUtils.toBean(flowList, WorkOrderStatusFlowRespVO.class);
        respVOList.forEach(this::fillFlowEnumNames);
        return success(respVOList);
    }

    @GetMapping("/type/list")
    @Operation(summary = "获取工单类型列表")
    @PreAuthorize("@ss.hasPermission('crm:work-order:query')")
    public CommonResult<List<WorkOrderTypeRespVO>> getWorkOrderTypeList() {
        List<WorkOrderTypeDO> typeList = workOrderTypeMapper.selectListByStatus(1);
        return success(BeanUtils.toBean(typeList, WorkOrderTypeRespVO.class));
    }

    @GetMapping("/status/enum")
    @Operation(summary = "获取工单状态枚举")
    @PreAuthorize("@ss.hasPermission('crm:work-order:query')")
    public CommonResult<List<EnumVO>> getWorkOrderStatusEnum() {
        List<EnumVO> list = java.util.Arrays.stream(WorkOrderStatusEnum.values())
                .map(e -> { EnumVO vo = new EnumVO(); vo.setValue(e.getValue()); vo.setLabel(e.getLabel()); return vo; })
                .collect(java.util.stream.Collectors.toList());
        return success(list);
    }

    @GetMapping("/priority/enum")
    @Operation(summary = "获取工单优先级枚举")
    @PreAuthorize("@ss.hasPermission('crm:work-order:query')")
    public CommonResult<List<EnumVO>> getWorkOrderPriorityEnum() {
        List<EnumVO> list = java.util.Arrays.stream(WorkOrderPriorityEnum.values())
                .map(e -> { EnumVO vo = new EnumVO(); vo.setValue(e.getValue()); vo.setLabel(e.getLabel()); return vo; })
                .collect(java.util.stream.Collectors.toList());
        return success(list);
    }

    @GetMapping("/type/enum")
    @Operation(summary = "获取工单类型枚举")
    @PreAuthorize("@ss.hasPermission('crm:work-order:query')")
    public CommonResult<List<EnumVO>> getWorkOrderTypeEnum() {
        List<EnumVO> list = java.util.Arrays.stream(WorkOrderTypeEnum.values())
                .map(e -> { EnumVO vo = new EnumVO(); vo.setValue(e.getValue()); vo.setLabel(e.getLabel()); return vo; })
                .collect(java.util.stream.Collectors.toList());
        return success(list);
    }

    @GetMapping("/statistics")
    @Operation(summary = "获取工单统计")
    @PreAuthorize("@ss.hasPermission('crm:work-order:query')")
    public CommonResult<WorkOrderStatisticsRespVO> getWorkOrderStatistics(WorkOrderStatisticsReqVO reqVO) {
        WorkOrderStatisticsRespVO statistics = workOrderService.getWorkOrderStatistics(reqVO);
        return success(statistics);
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出工单 Excel")
    @PreAuthorize("@ss.hasPermission('crm:work-order:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportWorkOrderExcel(@Validated WorkOrderPageReqVO pageReqVO,
                                     HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PAGE_SIZE_NONE); // 不分页，查询全部数据
        List<WorkOrderDO> list = workOrderService.getWorkOrderPage(pageReqVO).getList();
        // 转换为导出 VO，并填充枚举名称（DO 中无名称字段，需手动填充）
        List<WorkOrderExportExcelVO> exportList = BeanUtils.toBean(list, WorkOrderExportExcelVO.class);
        for (int i = 0; i < exportList.size(); i++) {
            WorkOrderExportExcelVO item = exportList.get(i);
            WorkOrderDO source = list.get(i);
            if (source.getType() != null) {
                WorkOrderTypeEnum typeEnum = WorkOrderTypeEnum.valueOf(source.getType());
                if (typeEnum != null) {
                    item.setTypeName(typeEnum.getLabel());
                }
            }
            if (source.getPriority() != null) {
                WorkOrderPriorityEnum priorityEnum = WorkOrderPriorityEnum.valueOf(source.getPriority());
                if (priorityEnum != null) {
                    item.setPriorityName(priorityEnum.getLabel());
                }
            }
            if (source.getStatus() != null) {
                WorkOrderStatusEnum statusEnum = WorkOrderStatusEnum.valueOf(source.getStatus());
                if (statusEnum != null) {
                    item.setStatusName(statusEnum.getLabel());
                }
            }
        }
        // 导出 Excel
        ExcelUtils.write(response, "工单.xls", "数据", WorkOrderExportExcelVO.class, exportList);
    }

    private void fillEnumNames(WorkOrderRespVO respVO) {
        if (respVO.getType() != null) {
            WorkOrderTypeEnum typeEnum = WorkOrderTypeEnum.valueOf(respVO.getType());
            if (typeEnum != null) {
                respVO.setTypeName(typeEnum.getLabel());
            }
        }
        if (respVO.getPriority() != null) {
            WorkOrderPriorityEnum priorityEnum = WorkOrderPriorityEnum.valueOf(respVO.getPriority());
            if (priorityEnum != null) {
                respVO.setPriorityName(priorityEnum.getLabel());
            }
        }
        if (respVO.getStatus() != null) {
            WorkOrderStatusEnum statusEnum = WorkOrderStatusEnum.valueOf(respVO.getStatus());
            if (statusEnum != null) {
                respVO.setStatusName(statusEnum.getLabel());
            }
        }
    }

    private void fillFlowEnumNames(WorkOrderStatusFlowRespVO respVO) {
        if (respVO.getFromStatus() != null) {
            WorkOrderStatusEnum statusEnum = WorkOrderStatusEnum.valueOf(respVO.getFromStatus());
            if (statusEnum != null) {
                respVO.setFromStatusName(statusEnum.getLabel());
            }
        }
        if (respVO.getToStatus() != null) {
            WorkOrderStatusEnum statusEnum = WorkOrderStatusEnum.valueOf(respVO.getToStatus());
            if (statusEnum != null) {
                respVO.setToStatusName(statusEnum.getLabel());
            }
        }
    }

    @lombok.Data
    public static class EnumVO {
        private Integer value;
        private String label;
    }

}
