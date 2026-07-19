package com.meession.etm.module.erp.controller.admin.sale.vo.out;

import com.meession.etm.framework.common.pojo.PageParam;
import com.meession.etm.framework.common.validation.InEnum;
import com.meession.etm.module.erp.enums.ErpAuditStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

import static com.meession.etm.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - ERP 销售出库分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ErpSaleOutPageReqVO extends PageParam {

    public static final Integer RECEIPT_STATUS_NONE = 0;
    public static final Integer RECEIPT_STATUS_PART = 1;
    public static final Integer RECEIPT_STATUS_ALL = 2;

    @Schema(description = "销售出库单号", example = "XSCK001")
    private String no;

    @Schema(description = "客户编号", example = "1724")
    private Long customerId;

    @Schema(description = "出库时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] outTime;

    @Schema(description = "状态", example = "10")
    @InEnum(ErpAuditStatus.class)
    private Integer status;

    @Schema(description = "备注", example = "随便")
    private String remark;

    @Schema(description = "创建者")
    private String creator;

    @Schema(description = "结算账户编号", example = "1")
    private Long accountId;

    @Schema(description = "销售订单号", example = "XSDD001")
    private String orderNo;

    @Schema(description = "收款状态", example = "1")
    private Integer receiptStatus;

    @Schema(description = "是否可收款", example = "true")
    private Boolean receiptEnable;

    @Schema(description = "产品编号", example = "1")
    private Long productId;

    @Schema(description = "仓库编号", example = "1")
    private Long warehouseId;

}
