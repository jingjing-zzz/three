package com.meession.etm.module.crm.controller.admin.business.vo.quotation;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "管理后台 - CRM 商机报价 Response VO")
@Data
public class CrmBusinessQuotationRespVO {
  @Schema(description = "编号")
  private Long id;
  @Schema(description = "商机编号")
  private Long businessId;
  @Schema(description = "报价编号")
  private String quotationNo;
  @Schema(description = "报价状态")
  private Integer status;
  @Schema(description = "状态名称")
  private String statusName;
  @Schema(description = "产品原价合计")
  private BigDecimal totalProductPrice;
  @Schema(description = "整单折扣")
  private BigDecimal discountPercent;
  @Schema(description = "报价总额")
  private BigDecimal totalPrice;
  @Schema(description = "确认人")
  private Long confirmedBy;
  @Schema(description = "确认人名称")
  private String confirmedByName;
  @Schema(description = "确认时间")
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  private LocalDateTime confirmedTime;
  @Schema(description = "备注")
  private String remark;
  @Schema(description = "创建时间")
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  private LocalDateTime createTime;
  @Schema(description = "报价产品项")
  private List<Item> items;

  @Data
  public static class Item {
    @Schema(description = "编号")
    private Long id;
    @Schema(description = "产品编号")
    private Long productId;
    @Schema(description = "产品名称")
    private String productName;
    @Schema(description = "产品编号")
    private String productNo;
    @Schema(description = "标准价")
    private BigDecimal standardPrice;
    @Schema(description = "实际售价")
    private BigDecimal actualPrice;
    @Schema(description = "数量")
    private BigDecimal count;
    @Schema(description = "行折扣")
    private BigDecimal discountPercent;
    @Schema(description = "行总价")
    private BigDecimal totalPrice;
    @Schema(description = "礼品")
    private String gift;
    @Schema(description = "备注")
    private String remark;
  }
}
