package com.meession.etm.module.crm.controller.admin.marketing.vo.campaign;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - 营销活动响应 VO")
@Data
public class MarketingCampaignRespVO {

    @Schema(description = "活动编号", example = "1024")
    private Long id;

    @Schema(description = "活动名称", example = "春节促销活动")
    private String name;

    @Schema(description = "活动类型", example = "1")
    private Integer type;

    @Schema(description = "活动状态", example = "1")
    private Integer status;

    @Schema(description = "开始时间", example = "2024-01-01 00:00:00")
    private LocalDateTime startTime;

    @Schema(description = "结束时间", example = "2024-01-31 23:59:59")
    private LocalDateTime endTime;

    @Schema(description = "目标客户筛选条件(JSON)", example = "{\"level\":1}")
    private String targetFilter;

    @Schema(description = "活动描述", example = "春节期间的促销活动")
    private String description;

    @Schema(description = "目标客户总数", example = "1000")
    private Integer totalTargetCount;

    @Schema(description = "已发送数量", example = "500")
    private Integer sendCount;

    @Schema(description = "成功数量", example = "450")
    private Integer successCount;

    @Schema(description = "失败数量", example = "50")
    private Integer failCount;

    @Schema(description = "创建时间", example = "2024-01-01 00:00:00")
    private LocalDateTime createTime;

}