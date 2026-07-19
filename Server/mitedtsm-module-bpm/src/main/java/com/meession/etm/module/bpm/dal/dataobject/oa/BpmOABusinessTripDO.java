package com.meession.etm.module.bpm.dal.dataobject.oa;

import com.meession.etm.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@TableName("bpm_oa_business_trip")
@KeySequence("bpm_oa_business_trip_seq")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BpmOABusinessTripDO extends BaseDO {

    @TableId
    private Long id;

    private Long userId;

    private String destination;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private Long day;

    private String reason;

    private Integer status;

    private String processInstanceId;

}