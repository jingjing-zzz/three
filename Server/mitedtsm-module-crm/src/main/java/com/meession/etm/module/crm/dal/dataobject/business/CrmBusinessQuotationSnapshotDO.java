package com.meession.etm.module.crm.dal.dataobject.business;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@TableName("crm_business_quotation_snapshot")
@Data
public class CrmBusinessQuotationSnapshotDO {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("business_id")
    private Long businessId;

    @TableField("quotation_id")
    private Long quotationId;

    @TableField("quotation_no")
    private String quotationNo;

    @TableField("status")
    private Integer status;

    @TableField("total_product_price")
    private BigDecimal totalProductPrice;

    @TableField("discount_percent")
    private BigDecimal discountPercent;

    @TableField("total_price")
    private BigDecimal totalPrice;

    @TableField("confirmed_by")
    private Long confirmedBy;

    @TableField("confirmed_time")
    private LocalDateTime confirmedTime;

    @TableField("remark")
    private String remark;

    @TableField("creator")
    private String creator;

    @TableField("create_time")
    private LocalDateTime createTime;

    public static CrmBusinessQuotationSnapshotDO fromQuotation(CrmBusinessQuotationDO quotation) {
        CrmBusinessQuotationSnapshotDO snapshot = new CrmBusinessQuotationSnapshotDO();
        snapshot.setBusinessId(quotation.getBusinessId());
        snapshot.setQuotationId(quotation.getId());
        snapshot.setQuotationNo(quotation.getQuotationNo());
        snapshot.setStatus(quotation.getStatus());
        snapshot.setTotalProductPrice(quotation.getTotalProductPrice());
        snapshot.setDiscountPercent(quotation.getDiscountPercent());
        snapshot.setTotalPrice(quotation.getTotalPrice());
        snapshot.setConfirmedBy(quotation.getConfirmedBy());
        snapshot.setConfirmedTime(quotation.getConfirmedTime());
        snapshot.setRemark(quotation.getRemark());
        snapshot.setCreator(quotation.getCreator());
        snapshot.setCreateTime(LocalDateTime.now());
        return snapshot;
    }

}