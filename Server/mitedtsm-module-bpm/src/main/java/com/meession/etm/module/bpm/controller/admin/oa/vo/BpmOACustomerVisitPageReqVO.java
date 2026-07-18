package com.meession.etm.module.bpm.controller.admin.oa.vo;

import com.meession.etm.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

import static com.meession.etm.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 客户拜访申请分页 Request VO")
@Data
public class BpmOACustomerVisitPageReqVO extends PageParam {

    @Schema(description = "状态")
    private Integer status;

    @Schema(description = "客户名称，模糊匹配")
    private String customerName;

    @Schema(description = "拜访目的，模糊匹配")
    private String purpose;

    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    @Schema(description = "申请时间")
    private LocalDateTime[] createTime;

}