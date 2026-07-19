package com.meession.etm.module.crm.controller.admin.order.vo.order;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "管理后台 - CRM 订单保存 Request VO")
@Data
public class CrmOrderSaveReqVO {

    @Schema(description = "编号", example = "1024")
    private Long id;

    @Schema(description = "客户编号", example = "1024")
    private Long customerId;

    @Schema(description = "合同编号", example = "2048")
    private Long contractId;

    @Schema(description = "负责人编号", example = "3072")
    private Long ownerUserId;

    @Schema(description = "下单时间")
    private LocalDateTime orderTime;

    @Schema(description = "整单折扣", example = "0")
    @DecimalMin(value = "0", message = "整单折扣不能小于0")
    private BigDecimal discountPercent;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "订单产品列表")
    private List<Product> products;

    @Schema(description = "订单产品")
    @Data
    public static class Product {

        @Schema(description = "编号", example = "1024")
        private Long id;

        @Schema(description = "产品编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "2048")
        @NotNull(message = "产品编号不能为空")
        private Long productId;

        @Schema(description = "产品单价", requiredMode = Schema.RequiredMode.REQUIRED, example = "100.00")
        @NotNull(message = "产品单价不能为空")
        @DecimalMin(value = "0.01", message = "产品单价必须大于0")
        private BigDecimal productPrice;

        @Schema(description = "数量", requiredMode = Schema.RequiredMode.REQUIRED, example = "10")
        @NotNull(message = "数量不能为空")
        @DecimalMin(value = "0.01", message = "数量必须大于0")
        private BigDecimal count;

        @Schema(description = "税率(%)", example = "13")
        @DecimalMin(value = "0", message = "税率不能小于0")
        private BigDecimal taxPercent;

        @Schema(description = "税额", example = "130.00")
        private BigDecimal taxPrice;

        @Schema(description = "备注")
        private String remark;

    }

}