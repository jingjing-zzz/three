package com.meession.etm.module.crm.dal.dataobject.marketing;

import com.meession.etm.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.time.LocalDateTime;

@TableName("marketing_campaign")
@KeySequence("marketing_campaign_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MarketingCampaignDO extends BaseDO {

    @TableId
    private Long id;

    private Long tenantId;

    private String name;

    private Integer type;

    private Integer status;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private String targetFilter;

    private String description;

    private Integer totalTargetCount;

    private Integer sendCount;

    private Integer successCount;

    private Integer failCount;

}