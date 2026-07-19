package com.meession.etm.module.crm.controller.admin.order.vo.order;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "管理后台 - CRM 订单 Response VO")
@Data
public class CrmOrderRespVO {

    @Schema(description = "编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "17386")
    private Long id;

    @Schema(description = "订单编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "DD001")
    private String no;

    @Schema(description = "订单状态", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Integer status;

    @Schema(description = "客户编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1724")
    private Long customerId;
    @Schema(description = "客户名称", example = "密讯")
    private String customerName;

    @Schema(description = "合同编号", example = "2048")
    private Long contractId;
    @Schema(description = "合同名称", example = "年度采购合同")
    private String contractName;

    @Schema(description = "负责人编号", example = "1888")
    private Long ownerUserId;
    @Schema(description = "负责人姓名", example = "张三")
    private String ownerUserName;

    @Schema(description = "下单时间", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime orderTime;

    @Schema(description = "产品总金额", example = "12025")
    private BigDecimal totalProductPrice;

    @Schema(description = "整单折扣")
    private BigDecimal discountPercent;

    @Schema(description = "订单总金额", example = "12371")
    private BigDecimal totalPrice;

    @Schema(description = "备注", example = "随便")
    private String remark;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime createTime;

    @Schema(description = "创建者")
    private String creator;

    @Schema(description = "创建者姓名")
    private String creatorName;

    @Schema(description = "订单项列表")
    private List<Product> products;

    @Schema(description = "订单产品")
    @Data
    public static class Product {

        @Schema(description = "编号", example = "1024")
        private Long id;

        @Schema(description = "产品编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "2048")
        private Long productId;

        @Schema(description = "产品名称", example = "产品A")
        private String productName;

        @Schema(description = "产品编号", example = "P001")
        private String productNo;

        @Schema(description = "产品单位", example = "个")
        private String productUnit;

        @Schema(description = "产品单价", example = "100.00")
        private BigDecimal productPrice;

        @Schema(description = "数量", requiredMode = Schema.RequiredMode.REQUIRED, example = "10")
        private BigDecimal count;

        @Schema(description = "总价", example = "1000.00")
        private BigDecimal totalPrice;

        @Schema(description = "税率(%)", example = "13")
        private BigDecimal taxPercent;

        @Schema(description = "税额", example = "130.00")
        private BigDecimal taxPrice;

        @Schema(description = "备注")
        private String remark;

    }

}