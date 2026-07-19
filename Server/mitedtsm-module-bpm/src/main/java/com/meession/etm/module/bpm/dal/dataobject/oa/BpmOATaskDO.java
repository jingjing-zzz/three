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

@TableName("bpm_oa_task")
@KeySequence("bpm_oa_task_seq")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BpmOATaskDO extends BaseDO {

    @TableId
    private Long id;

    private Long userId;

    private String title;

    private String description;

    private Integer priority;

    private Integer status;

    private Long assigneeId;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

}