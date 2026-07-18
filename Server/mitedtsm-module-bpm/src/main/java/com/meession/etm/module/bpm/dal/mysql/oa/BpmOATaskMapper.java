package com.meession.etm.module.bpm.dal.mysql.oa;

import com.meession.etm.module.bpm.controller.admin.oa.vo.BpmOATaskPageReqVO;
import com.meession.etm.module.bpm.dal.dataobject.oa.BpmOATaskDO;
import com.meession.etm.framework.common.pojo.PageResult;
import com.meession.etm.framework.mybatis.core.mapper.BaseMapperX;
import com.meession.etm.framework.mybatis.core.query.LambdaQueryWrapperX;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface BpmOATaskMapper extends BaseMapperX<BpmOATaskDO> {

    default PageResult<BpmOATaskDO> selectPage(Long userId, BpmOATaskPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<BpmOATaskDO>()
                .eqIfPresent(BpmOATaskDO::getUserId, userId)
                .eqIfPresent(BpmOATaskDO::getAssigneeId, reqVO.getAssigneeId())
                .eqIfPresent(BpmOATaskDO::getStatus, reqVO.getStatus())
                .eqIfPresent(BpmOATaskDO::getPriority, reqVO.getPriority())
                .likeIfPresent(BpmOATaskDO::getTitle, reqVO.getTitle())
                .betweenIfPresent(BpmOATaskDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(BpmOATaskDO::getId));
    }

}