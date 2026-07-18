package com.meession.etm.module.bpm.controller.admin.oa.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static com.meession.etm.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 客户拜访申请创建 Request VO")
@Data
public class BpmOACustomerVisitCreateReqVO {

    @Schema(description = "客户名称", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "客户名称不能为空")
    private String customerName;

    @Schema(description = "客户联系人")
    private String customerContact;

    @Schema(description = "联系电话")
    private String contactPhone;

    @Schema(description = "拜访时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "拜访时间不能为空")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime visitTime;

    @Schema(description = "拜访地点")
    private String visitLocation;

    @Schema(description = "拜访目的", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "拜访目的不能为空")
    private String purpose;

    @Schema(description = "发起人自选审批人 Map")
    private Map<String, List<Long>> startUserSelectAssignees;

}