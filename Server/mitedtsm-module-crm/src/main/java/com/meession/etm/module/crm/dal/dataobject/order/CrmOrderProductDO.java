package com.meession.etm.module.crm.dal.dataobject.order;

import com.meession.etm.framework.mybatis.core.dataobject.BaseDO;
import com.meession.etm.module.crm.dal.dataobject.product.CrmProductDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.math.BigDecimal;

@TableName("crm_order_product")
@KeySequence("crm_order_product_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CrmOrderProductDO extends BaseDO {

    @TableId
    private Long id;

    private Long orderId;

    private Long productId;

    private BigDecimal productPrice;

    private BigDecimal count;

    private BigDecimal totalPrice;

    private BigDecimal taxPercent;

    private BigDecimal taxPrice;

    private String remark;

}