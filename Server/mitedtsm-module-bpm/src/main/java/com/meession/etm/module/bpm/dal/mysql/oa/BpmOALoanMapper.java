package com.meession.etm.module.bpm.dal.mysql.oa;

import com.meession.etm.module.bpm.controller.admin.oa.vo.BpmOALoanPageReqVO;
import com.meession.etm.module.bpm.dal.dataobject.oa.BpmOALoanDO;
import com.meession.etm.framework.common.pojo.PageResult;
import com.meession.etm.framework.mybatis.core.mapper.BaseMapperX;
import com.meession.etm.framework.mybatis.core.query.LambdaQueryWrapperX;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface BpmOALoanMapper extends BaseMapperX<BpmOALoanDO> {

    default PageResult<BpmOALoanDO> selectPage(Long userId, BpmOALoanPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<BpmOALoanDO>()
                .eqIfPresent(BpmOALoanDO::getUserId, userId)
                .eqIfPresent(BpmOALoanDO::getStatus, reqVO.getStatus())
                .likeIfPresent(BpmOALoanDO::getReason, reqVO.getReason())
                .betweenIfPresent(BpmOALoanDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(BpmOALoanDO::getId));
    }

}