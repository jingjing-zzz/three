package com.meession.etm.module.crm.dal.dataobject.business;

import com.meession.etm.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.*;
import lombok.*;
import java.math.BigDecimal;

@TableName("crm_business_quotation_item")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CrmBusinessQuotationItemDO extends BaseDO {
    @TableId
    private Long id;
    private Long quotationId;
    private Long productId;
    private String productName;
    private String productNo;
    private BigDecimal standardPrice;
    private BigDecimal actualPrice;
    private BigDecimal count;
    private BigDecimal discountPercent;
    private BigDecimal totalPrice;
    private String gift;
    private String remark;
}
