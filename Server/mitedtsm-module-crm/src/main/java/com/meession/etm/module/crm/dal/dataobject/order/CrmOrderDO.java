package com.meession.etm.module.crm.dal.dataobject.order;

import com.meession.etm.framework.mybatis.core.dataobject.BaseDO;
import com.meession.etm.module.crm.dal.dataobject.contract.CrmContractDO;
import com.meession.etm.module.crm.dal.dataobject.customer.CrmCustomerDO;
import com.meession.etm.module.crm.enums.common.CrmAuditStatusEnum;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@TableName("crm_order")
@KeySequence("crm_order_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CrmOrderDO extends BaseDO {

    @TableId
    private Long id;

    private String no;

    private Integer status;

    private Long customerId;

    private Long contractId;

    private Long ownerUserId;

    private LocalDateTime orderTime;

    private BigDecimal totalProductPrice;

    private BigDecimal discountPercent;

    private BigDecimal totalPrice;

    private String remark;

    private String processInstanceId;

}