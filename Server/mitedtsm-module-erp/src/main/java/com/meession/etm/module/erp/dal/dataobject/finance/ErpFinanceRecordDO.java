package com.meession.etm.module.erp.dal.dataobject.finance;

import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.meession.etm.framework.mybatis.core.dataobject.BaseDO;
import com.meession.etm.module.erp.enums.ErpAuditStatus;
import com.meession.etm.module.erp.enums.finance.ErpFinanceRecordTypeEnum;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * ERP 财务单据 DO。
 */
@TableName("erp_finance_record")
@KeySequence("erp_finance_record_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ErpFinanceRecordDO extends BaseDO {

    @TableId
    private Long id;

    /**
     * 单据编号。
     */
    private String no;

    /**
     * 单据类型。
     *
     * 枚举 {@link ErpFinanceRecordTypeEnum}
     */
    private Integer type;

    /**
     * 审核状态。
     *
     * 枚举 {@link ErpAuditStatus}
     */
    private Integer status;

    /**
     * 业务发生时间。
     */
    private LocalDateTime recordTime;

    /**
     * 到期时间，用于发票/退款等逾期检测。
     */
    private LocalDateTime dueTime;

    /**
     * 是否逾期。
     */
    private Boolean overdue;

    private Long applicantUserId;

    private Long financeUserId;

    private Long accountId;

    private String subject;

    private String counterparty;

    private String invoiceNo;

    private BigDecimal amount;

    private BigDecimal taxAmount;

    private BigDecimal totalAmount;

    private String processInstanceId;

    private String remark;

}
