package com.meession.etm.module.bpm.dal.mysql.oa;

import com.meession.etm.module.bpm.controller.admin.oa.vo.BpmOAWorkRequestPageReqVO;
import com.meession.etm.module.bpm.dal.dataobject.oa.BpmOAWorkRequestDO;
import com.meession.etm.framework.common.pojo.PageResult;
import com.meession.etm.framework.mybatis.core.mapper.BaseMapperX;
import com.meession.etm.framework.mybatis.core.query.LambdaQueryWrapperX;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface BpmOAWorkRequestMapper extends BaseMapperX<BpmOAWorkRequestDO> {

    default PageResult<BpmOAWorkRequestDO> selectPage(Long userId, BpmOAWorkRequestPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<BpmOAWorkRequestDO>()
                .eqIfPresent(BpmOAWorkRequestDO::getUserId, userId)
                .eqIfPresent(BpmOAWorkRequestDO::getStatus, reqVO.getStatus())
                .likeIfPresent(BpmOAWorkRequestDO::getTitle, reqVO.getTitle())
                .betweenIfPresent(BpmOAWorkRequestDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(BpmOAWorkRequestDO::getId));
    }

}