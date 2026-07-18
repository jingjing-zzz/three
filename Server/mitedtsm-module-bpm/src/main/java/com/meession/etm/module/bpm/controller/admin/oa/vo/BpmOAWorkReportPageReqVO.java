package com.meession.etm.module.bpm.controller.admin.oa.vo;

import com.meession.etm.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

import static com.meession.etm.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 工作报告分页 Request VO")
@Data
public class BpmOAWorkReportPageReqVO extends PageParam {

    @Schema(description = "报告类型：1-日报，2-周报，3-月报")
    private Integer type;

    @Schema(description = "状态")
    private Integer status;

    @Schema(description = "报告标题，模糊匹配")
    private String title;

    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    @Schema(description = "创建时间")
    private LocalDateTime[] createTime;

}