package com.meession.etm.module.crm.controller.admin.order.vo.order;

import com.meession.etm.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - CRM 订单分页查询 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class CrmOrderPageReqVO extends PageParam {

    @Schema(description = "订单编号", example = "DD001")
    private String no;

    @Schema(description = "客户编号", example = "1024")
    private Long customerId;

    @Schema(description = "合同编号", example = "2048")
    private Long contractId;

    @Schema(description = "订单状态", example = "1")
    private Integer status;

    @Schema(description = "负责人编号", example = "3072")
    private Long ownerUserId;

    @Schema(description = "下单时间")
    private LocalDateTime orderTime;

    @Schema(description = "下单时间范围")
    private LocalDateTime[] orderTimeRange;

    @Schema(description = "创建时间范围")
    private LocalDateTime[] createTimeRange;

    @Schema(description = "数据权限场景类型", example = "1")
    private Integer sceneType;

}