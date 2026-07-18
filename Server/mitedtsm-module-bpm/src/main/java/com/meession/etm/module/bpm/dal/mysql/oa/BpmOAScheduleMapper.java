package com.meession.etm.module.bpm.dal.mysql.oa;

import com.meession.etm.module.bpm.controller.admin.oa.vo.BpmOASchedulePageReqVO;
import com.meession.etm.module.bpm.dal.dataobject.oa.BpmOAScheduleDO;
import com.meession.etm.framework.common.pojo.PageResult;
import com.meession.etm.framework.mybatis.core.mapper.BaseMapperX;
import com.meession.etm.framework.mybatis.core.query.LambdaQueryWrapperX;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface BpmOAScheduleMapper extends BaseMapperX<BpmOAScheduleDO> {

    default PageResult<BpmOAScheduleDO> selectPage(Long userId, BpmOASchedulePageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<BpmOAScheduleDO>()
                .eqIfPresent(BpmOAScheduleDO::getUserId, userId)
                .eqIfPresent(BpmOAScheduleDO::getStatus, reqVO.getStatus())
                .likeIfPresent(BpmOAScheduleDO::getTitle, reqVO.getTitle())
                .betweenIfPresent(BpmOAScheduleDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(BpmOAScheduleDO::getId));
    }

}