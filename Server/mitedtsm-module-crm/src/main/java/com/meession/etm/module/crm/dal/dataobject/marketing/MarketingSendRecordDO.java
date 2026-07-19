package com.meession.etm.module.crm.dal.dataobject.marketing;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.meession.etm.framework.mybatis.core.dataobject.BaseDO;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

@TableName("marketing_send_record")
@Data
@EqualsAndHashCode(callSuper = true)
public class MarketingSendRecordDO extends BaseDO {

    @TableId
    private Long id;

    private Long tenantId;

    private Long batchId;

    private String campaignName;

    private String batchName;

    private Integer type;

    private String target;

    private String content;

    private Integer status;

    private String errorMessage;

    private Date sendTime;

    private String extData;

}
