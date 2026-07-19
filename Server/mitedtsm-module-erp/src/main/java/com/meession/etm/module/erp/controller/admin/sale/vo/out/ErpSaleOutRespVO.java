package com.meession.etm.module.erp.controller.admin.sale.vo.out;

import cn.idev.excel.annotation.ExcelIgnoreUnannotated;
import cn.idev.excel.annotation.ExcelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "管理后台 - ERP 销售出库 Response VO")
@Data
@ExcelIgnoreUnannotated
public class ErpSaleOutRespVO {

    @Schema(description = "编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "17386")
    @ExcelProperty("编号")
    private Long id;

    @Schema(description = "销售出库单号", requiredMode = Schema.RequiredMode.REQUIRED, example = "XSCK001")
    @ExcelProperty("销售出库单号")
    private String no;

    @Schema(description = "状态", requiredMode = Schema.RequiredMode.REQUIRED, example = "10")
    @ExcelProperty("状态")
    private Integer status;

    @Schema(description = "客户编号", example = "1724")
    private Long customerId;
    @Schema(description = "客户名称", example = "密讯")
    @ExcelProperty("客户名称")
    private String customerName;

    @Schema(description = "结算账户编号", example = "31189")
    private Long accountId;

    @Schema(description = "销售员编号", example = "1888")
    private Long saleUserId;

    @Schema(description = "出库时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("出库时间")
    private LocalDateTime outTime;

    @Schema(description = "销售订单编号", example = "17386")
    private Long orderId;
    @Schema(description = "销售订单号", example = "XSDD001")
    private String orderNo;

    @Schema(description = "合计数量", example = "15663")
    @ExcelProperty("合计数量")
    private BigDecimal totalCount;
    @Schema(description = "最终合计价格", example = "24906")
    @ExcelProperty("最终合计价格")
    private BigDecimal totalPrice;
    @Schema(description = "已收款金额，单位：元", example = "7127")
    private BigDecimal receiptPrice;

    @Schema(description = "合计产品价格，单位：元", example = "7127")
    private BigDecimal totalProductPrice;
    @Schema(description = "合计税额，单位：元", example = "7127")
    private BigDecimal totalTaxPrice;
    @Schema(description = "优惠率，百分比", example = "99.88")
    private BigDecimal discountPercent;
    @Schema(description = "优惠金额，单位：元", example = "7127")
    private BigDecimal discountPrice;
    @Schema(description = "其它金额，单位：元", example = "7127")
    private BigDecimal otherPrice;

    @Schema(description = "附件地址", example = "xxx")
    private String fileUrl;
    @Schema(description = "备注", example = "你猜")
    @ExcelProperty("备注")
    private String remark;

    @Schema(description = "创建人")
    private String creator;
    @Schema(description = "创建人名称")
    private String creatorName;
    @Schema(description = "创建时间")
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

    @Schema(description = "出库项列表")
    private List<Item> items;

    @Schema(description = "产品信息")
    @ExcelProperty("产品信息")
    private String productNames;

    @Data
    public static class Item {

        @Schema(description = "出库项编号", example = "11756")
        private Long id;

        @Schema(description = "销售订单项编号", example = "11756")
        private Long orderItemId;

        @Schema(description = "仓库编号", example = "3113")
        private Long warehouseId;

        @Schema(description = "产品编号", example = "3113")
        private Long productId;

        @Schema(description = "产品单位编号", example = "3113")
        private Long productUnitId;

        @Schema(description = "产品单价", example = "100.00")
        private BigDecimal productPrice;

        @Schema(description = "产品数量", example = "100.00")
        private BigDecimal count;

        @Schema(description = "税率，百分比", example = "99.88")
        private BigDecimal taxPercent;

        @Schema(description = "税额，单位：元", example = "100.00")
        private BigDecimal taxPrice;

        @Schema(description = "备注", example = "随便")
        private String remark;

        @Schema(description = "产品名称", example = "巧克力")
        private String productName;
        @Schema(description = "产品条码", example = "A9985")
        private String productBarCode;
        @Schema(description = "产品单位名称", example = "盒")
        private String productUnitName;
        @Schema(description = "库存数量", example = "100.00")
        private BigDecimal stockCount;

    }

}
