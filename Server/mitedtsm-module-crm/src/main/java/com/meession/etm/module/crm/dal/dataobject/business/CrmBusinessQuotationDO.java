package com.meession.etm.module.crm.dal.dataobject.business;

import com.meession.etm.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * CRM 商机报价 DO
 *
 * @author traebot
 */
@TableName("crm_business_quotation")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CrmBusinessQuotationDO extends BaseDO {
  @TableId
  private Long id;
  private Long businessId;
  private String quotationNo;
  private Integer status; // 0=DRAFT, 1=CONFIRMED, 2=VOID
  private BigDecimal totalProductPrice;
  private BigDecimal discountPercent;
  private BigDecimal totalPrice;
  private Long confirmedBy;
  private LocalDateTime confirmedTime;
  private String remark;
}
