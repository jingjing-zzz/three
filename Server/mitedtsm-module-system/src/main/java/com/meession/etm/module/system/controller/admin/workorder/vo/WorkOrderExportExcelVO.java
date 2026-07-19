package com.meession.etm.module.system.controller.admin.workorder.vo;

import cn.idev.excel.annotation.ExcelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "工单 Excel 导出 VO")
@Data
public class WorkOrderExportExcelVO {

    @ExcelProperty("工单号")
    private String orderNo;

    @ExcelProperty("工单标题")
    private String title;

    @ExcelProperty("工单类型")
    private String typeName;

    @ExcelProperty("优先级")
    private String priorityName;

    @ExcelProperty("状态")
    private String statusName;

    @ExcelProperty("发起人")
    private String reporterName;

    @ExcelProperty("处理人")
    private String assigneeName;

    @ExcelProperty("客户")
    private String customerName;

    @ExcelProperty("工单内容")
    private String content;

    @ExcelProperty("处理备注")
    private String handleNote;

    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

}
